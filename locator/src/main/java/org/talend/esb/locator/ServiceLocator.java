package org.talend.esb.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * This is the entry point for clients of the Service Locator. To access the
 * Service Locator clients have to first {@link #connect() connect} to the
 * Service Locator to get a session assigned. Once the connection is established
 * the client will periodically send heart beats to the server to keep the
 * session alive.
 * <p>
 * The Service Locator provides the following operations.
 * <ul>
 * <li>An endpoint for a specific service can be registered.
 * <li>All endpoints for a specific service that were registered before by other
 * clients can be looked up.
 * </ul>
 * 
 */
public class ServiceLocator {

	private static final Logger LOG = Logger.getLogger(ServiceLocator.class
			.getName());

	public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");

	public static final byte[] EMPTY_CONTENT = new byte[0];

	// Count of register() and lookup() methods which performed in parallel in
	// this moment
	private int businessOperations = 0;

	// Is blocked by connect() or disconnect() method
	private boolean blockedByRunUpOperation = false;

	public static final PostConnectAction DO_NOTHING_ACTION = new PostConnectAction() {

		@Override
		public void process(ServiceLocator lc) {
		}
	};

	/**
	 * Callback interface to define actions that must be executed after a
	 * successful connect or reconnect.
	 */
	static interface PostConnectAction {
		/**
		 * Execute this after the connection to the Service Locator is
		 * established or re-established.
		 * 
		 * @param lc
		 *            the Service Locator client that just successfully
		 *            connected to the server, must not be <code>null</code>
		 */
		void process(ServiceLocator lc);
	}

	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;

	private int connectionTimeout = 5000;

	private int waitingTimeout = 3000;

	private PostConnectAction pca = DO_NOTHING_ACTION;

	private volatile ZooKeeper zk;

	/**
	 * Establish a connection to the Service Locator. After successful
	 * connection the specified {@link PostConnectAction} is run. If the session
	 * to the server expires because the server could not be reached within the
	 * {@link #setSessionTimeout(int) specified time}, a reconnect is
	 * automatically executed as soon as the server can be reached again.
	 * Because after a session time out all registered endpoints are removed it
	 * is important to specify a {@link PostConnectAction} that re-registers all
	 * endpoints.
	 * 
	 * @throws IOException
	 *             At least one of the endpoints does not represent a valid
	 *             address
	 * @throws InterruptedException
	 *             the current <code>Thread</code> was interrupted when waiting
	 *             for a successful connection to the ServiceLocator
	 * @throws ServiceLocatorException
	 *             the connect operation failed
	 */
	public void connect() throws IOException, InterruptedException,
			ServiceLocatorException {
		try {
			synchronized (this) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.FINE, "Start connect session");
				}
				blockedByRunUpOperation = true;

				connect(false);

				blockedByRunUpOperation = false;

				if (LOG.isLoggable(Level.FINER)) {
					LOG.log(Level.FINER, "End connect session");
				}

			}
		} catch (IOException e) {
			blockedByRunUpOperation = false;
			throw e;
		} catch (InterruptedException e) {
			blockedByRunUpOperation = false;
			throw e;
		} catch (ServiceLocatorException e) {
			blockedByRunUpOperation = false;
			throw e;
		} catch (Exception e) {
			blockedByRunUpOperation = false;
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, "Connect not passed: " + e.getMessage());
			}
		}

	}

	private void connect(boolean immediately) throws IOException,
			InterruptedException, ServiceLocatorException {
		disconnect(immediately);

		CountDownLatch connectionLatch = new CountDownLatch(1);
		zk = createZooKeeper(connectionLatch);
		boolean connected = connectionLatch.await(connectionTimeout,
				TimeUnit.MILLISECONDS);

		if (!connected) {
			throw new ServiceLocatorException(
					"Connection to Service Locator failed.");
		} else {
			pca.process(this);
		}

	}

	/**
	 * Disconnects from a Service Locator server. All endpoints that were
	 * registered before are removed from the server. To be able to communicate
	 * with a Service Locator server again the client has to {@link #connect()
	 * connect} again.
	 * 
	 * @throws InterruptedException
	 *             the current <code>Thread</code> was interrupted when waiting
	 *             for the disconnect to happen
	 * @throws ServiceLocatorException
	 */
	public void disconnect() throws InterruptedException,
			ServiceLocatorException {
		try {
			synchronized (this) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.FINE, "Start disconnect session");
				}

				blockedByRunUpOperation = true;

				disconnect(false);

				blockedByRunUpOperation = false;

				if (LOG.isLoggable(Level.FINER)) {
					LOG.log(Level.FINER, "End disconnect session");
				}
			}
		} catch (InterruptedException e) {
			blockedByRunUpOperation = false;
			throw e;
		} catch (ServiceLocatorException e) {
			blockedByRunUpOperation = false;
			throw e;
		} catch (Exception e) {
			blockedByRunUpOperation = false;
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, "Connect not passed: " + e.getMessage());
			}
		}

	}

	private void disconnect(boolean immediately)
			throws InterruptedException, ServiceLocatorException {
		if (businessOperations != 0 && !immediately) {
			try {
				int waiting = 0;
				 if (LOG.isLoggable(Level.FINE)) {
				 LOG.fine("Waiting "
				 + waitingTimeout
				 + " ms for some business operations to complete its work");
				 }
				while (businessOperations != 0 && waiting < waitingTimeout) {
					Thread.sleep(1);
					waiting++;
				}
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("In fact waiting was " + waiting + " ms ;)");
				}
				businessOperations = 0;
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"Thread sleeping, and the thread is interrupted, either before or during the activity: "
									+ e.getMessage());
				}
				throw new ServiceLocatorException("Thread is interrupted.", e);
			}
		} else {
			businessOperations = 0;
		}

		if (zk != null) {
			zk.close();
			zk = null;
		}
	}

	/**
	 * For a given service register the endpoint of a concrete provider of this
	 * service. If the client is destroyed, disconnected, or fails to
	 * successfully send the heartbeat for a period of time defined by the
	 * {@link #setSessionTimeout(int) session timeout parameter} the endpoint is
	 * removed from the Service Locator. To ensure that all available endpoints
	 * are re-registered when the client reconnects after a session expired a
	 * {@link PostConnectAction} should be
	 * {@link #setPostConnectAction(PostConnectAction) set} that registers all
	 * endpoints.
	 * 
	 * @param serviceName
	 *            the name of the service the endpoint is registered for, must
	 *            not be <code>null</code>
	 * @param endpoint
	 *            the endpoint to register, must not be <code>null</code>
	 * @throws ServiceLocatorException
	 *             the server returned an error
	 * @throws InterruptedException
	 *             the current <code>Thread</code> was interrupted when waiting
	 *             for a response of the ServiceLocator
	 */
	public void register(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		if (blockedByRunUpOperation) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.info("It seems that Service Locator performs connection operation. We are waiting for completion.");
			}
			synchronized (this) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.log(Level.FINEST, "Entering into synchronization block");
				}
			}
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.log(Level.FINEST, "Leaving the synchronization block");
			}
		}
		businessOperations++;
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Register endpoint " + endpoint
					+ " for service " + serviceName + ".");
		}
		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
				.toString());
		ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);

		NodePath endpointNodePath = serviceNodePath.child(endpoint);
		ensurePathExists(endpointNodePath, CreateMode.EPHEMERAL);
		if (businessOperations > 0)
			businessOperations--;
	}

	public void unregister(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		if (blockedByRunUpOperation) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.log(Level.INFO,
						"It seems that Service Locator performs connection operation. We are waiting for completion.");
			}
			synchronized (this) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.log(Level.FINEST, "Entering into synchronization block");
				}
			}
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.log(Level.FINEST, "Leaving the synchronization block");
			}
		}
		businessOperations++;
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Unregister endpoint " + endpoint + " for service "
					+ serviceName + ".");
		}

		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
				.toString());
		NodePath endpointNodePath = serviceNodePath.child(endpoint);

		try {
			deleteNode(endpointNodePath);
			deleteNode(serviceNodePath);
		} catch (KeeperException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"The service locator server signaled an error: "
								+ e.getMessage());
			}
			throw new ServiceLocatorException(
					"The service locator server signaled an error.", e);

		}

		if (businessOperations > 0)
			businessOperations--;
	}

	/**
	 * For the given service return all endpoints that currently registered at
	 * the Service Locator Service.
	 * 
	 * @param serviceName
	 *            the name of the service for which to get the endpoints, must
	 *            not be <code>null</code>
	 * @return a possibly empty list of endpoints
	 * @throws ServiceLocatorException
	 *             the server returned an error
	 * @throws InterruptedException
	 *             the current <code>Thread</code> was interrupted when waiting
	 *             for a response of the ServiceLocator
	 */
	public List<String> lookup(QName serviceName)
			throws ServiceLocatorException, InterruptedException {
		if (blockedByRunUpOperation) {
			if (LOG.isLoggable(Level.INFO)) {
				LOG.log(Level.INFO,
						"It seems that Service Locator performs connection operation. We are waiting for completion.");
			}
			synchronized (this) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.log(Level.FINEST, "Entering into synchronization block");
				}
			}
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.log(Level.FINEST, "Leaving the synchronization block");
			}
		}
		businessOperations++;
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Lookup endpoints of " + serviceName + " service.");
		}
		try {
			NodePath providerPath = LOCATOR_ROOT_PATH.child(serviceName
					.toString());
			List<String> children;
			if (nodeExists(providerPath)) {
				children = decode(getChildren(providerPath));
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Lookup for provider" + serviceName
							+ " failed, provider not known.");
				}
				children = Collections.emptyList();
			}
			if (businessOperations > 0)
				businessOperations--;

			return children;

		} catch (KeeperException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"The service locator server signaled an error: "
								+ e.getMessage());
			}
			throw new ServiceLocatorException(
					"The service locator server signaled an error.", e);
		}

	}

	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator endpoints set to " + locatorEndpoints);
		}
	}

	public void setSessionTimeout(int timeout) {
		sessionTimeout = timeout;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator session timeout set to: " + sessionTimeout);
		}
	}

	public void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator connection timeout set to: " + connectionTimeout);
		}
	}

	public void setPostConnectAction(PostConnectAction pca) {
		this.pca = pca;
	}

	public void setWaitingTimeout(int waitingTimeout) {
		this.waitingTimeout = waitingTimeout;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator wating timeout set to: " + waitingTimeout);
		}

	}

	private void ensurePathExists(NodePath path, CreateMode mode)
			throws ServiceLocatorException, InterruptedException {
		try {
			if (!nodeExists(path)) {
				createNode(path, mode);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " created.");
				}
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " already exists.");
				}
			}
		} catch (KeeperException e) {
			if (!e.code().equals(Code.NODEEXISTS)) {
				throw new ServiceLocatorException(
						"The service locator server signaled an error.", e);
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Some other client created node" + path
							+ " concurrently.");
				}
			}
		}
	}

	private boolean nodeExists(NodePath path) throws KeeperException,
			InterruptedException {
		return zk.exists(path.toString(), false) != null;
	}

	private void createNode(NodePath path, CreateMode mode)
			throws KeeperException, InterruptedException {
		zk.create(path.toString(), EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, mode);
	}

	private void deleteNode(NodePath path) throws KeeperException,
			InterruptedException {
		if (getChildren(path).isEmpty())
			zk.delete(path.toString(), -1);
	}

	private List<String> getChildren(NodePath path) throws KeeperException,
			InterruptedException {
		return zk.getChildren(path.toString(), false);
	}

	private List<String> decode(List<String> encoded) {
		List<String> raw = new ArrayList<String>();

		for (String oneEncoded : encoded) {
			raw.add(NodePath.decode(oneEncoded));
		}
		return raw;
	}

	protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch)
			throws IOException {
		return new ZooKeeper(locatorEndpoints, sessionTimeout, new WatcherImpl(
				connectionLatch));
	}

	public class WatcherImpl implements Watcher {

		private CountDownLatch connectionLatch;

		public WatcherImpl(CountDownLatch connectionLatch) {
			this.connectionLatch = connectionLatch;
		}

		@Override
		public void process(WatchedEvent event) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Event with state " + event.getState() + " sent.");
			}

			KeeperState eventState = event.getState();
			try {
				if (eventState == KeeperState.SyncConnected) {
					ensurePathExists(LOCATOR_ROOT_PATH, CreateMode.PERSISTENT);
					connectionLatch.countDown();
				} else if (eventState == KeeperState.Expired) {
					connect();
				}
			} catch (IOException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"An IOException  was thrown when trying to connect to the ServiceLocator",
							e);
				}
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"An InterruptedException was thrown while waiting for an answer from the Service Locator",
							e);
				}
			} catch (ServiceLocatorException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"Failed to execute an request to Service Locator.",
							e);
				}
			}
		}
	}
}
