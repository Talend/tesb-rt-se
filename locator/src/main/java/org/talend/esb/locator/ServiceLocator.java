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
 * This is the entry point for clients of the Service Locator. To access the Service Locator
 * clients have to first {@link #connect() connect} to the Service Locator to get a session
 * assigned. Once the connection is
 * established the client will periodically send heart beats to the server to keep the session
 * alive. 
 * <p>
 * The Service Locator provides the following operations.
 * <ul>
 *  <li>An endpoint for a specific service can be registered. If the client is destroyed,
 *   disconnect, or fails to successfully send the heartbeat for a period of timed defined by the 
 *   {@link #setSessionTimeout(int) session timeout parameter} the endpoint is removed from the
 *   Service Locator.
 *  <li>All endpoints for a specific service that were registered before by other clients can be
 *      looked up.
 * </ul>
 * 
 *
 */
public class ServiceLocator {

	private static final Logger LOG = Logger.getLogger(ServiceLocator.class.getName());

	public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");

	public static final byte[] EMPTY_CONTENT = new byte[0];
	
	public static final PostConnectAction DO_NOTHING = new PostConnectAction() {

		@Override
		public void process(ServiceLocator lc) {
		}
	};
	
	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;
	
	private int connectionTimeout = 5000;

	private PostConnectAction pca = DO_NOTHING;

	private volatile ZooKeeper zk;

	public void connect() throws IOException, InterruptedException, ServiceLocatorException  {
	    CountDownLatch connectionLatch = new CountDownLatch(1);
    	zk = createZooKeeper(connectionLatch);
		boolean connected = connectionLatch.await(connectionTimeout, TimeUnit.MILLISECONDS);
		
		if (!connected) {
			throw new ServiceLocatorException("Connection to Service Locator failed.");
		} else  {
			pca.process(this);
		}
	}

	public void disconnect() throws InterruptedException {
		if (zk != null) {
			zk.close(); 
		}
	}
	
	/**
	 * For a given service register the endpoint of a concrete provider of this service. 
	 * 
	 * @param serviceName
	 * @param endpoint
	 * @throws ServiceLocatorException
	 * @throws InterruptedException the current <code>Thread</code> was interrupted when waiting for
	 *                              a response of the ServiceLocator
	 */
	public void register(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Register endpoint " + endpoint + " for service " + serviceName + ".");
		}
		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName.toString());
		ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);
		
		NodePath endpointNodePath = serviceNodePath.child(endpoint);
		ensurePathExists(endpointNodePath, CreateMode.EPHEMERAL);
	}

	public List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Lookup endpoints of " + serviceName + " service.");
		}
		try {
			NodePath providerPath = LOCATOR_ROOT_PATH.child(serviceName.toString());
			if (nodeExists(providerPath)) {
				return decode(getChildren(providerPath));
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Lookup for provider" + serviceName + " failed, provider not known.");
				}
				return Collections.emptyList();
			}
		} catch (KeeperException e) {
			LOG.log(Level.SEVERE, "The service locator server signaled an error: " + e.getMessage()); 
			throw new ServiceLocatorException("The service locator server signaled an error.", e);
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

	private void ensurePathExists(NodePath path, CreateMode mode)
			throws ServiceLocatorException, InterruptedException {
		try {
			if (! nodeExists(path)) {
				createNode(path, mode);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " created.");
				}
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " already exists.");
				}
			}
		} catch(KeeperException e) {
			if (! e.code().equals(Code.NODEEXISTS)) {
				throw new ServiceLocatorException("The service locator server signaled an error.", e);
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Some other client created node" + path + " concurrently.");
				}
			}
		}
	}

	private boolean nodeExists(NodePath path) throws KeeperException, InterruptedException {
		return zk.exists(path.toString(), false) != null;
	}

	private void createNode(NodePath path, CreateMode mode) throws KeeperException, InterruptedException {
		zk.create(path.toString(), EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, mode);
	}
	
	private List<String> getChildren(NodePath path)  throws KeeperException, InterruptedException {
		return zk.getChildren(path.toString(), false);
	}

	private List<String> decode(List<String> encoded) {
		List<String> raw = new ArrayList<String>();

		for (String oneEncoded : encoded) {
			raw.add(NodePath.decode(oneEncoded));
		}
		return raw;
	}

	protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch) throws IOException {
    	return new ZooKeeper(locatorEndpoints, sessionTimeout, new WatcherImpl(connectionLatch));
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
					LOG.log(Level.SEVERE, "An IOException  was thrown when trying to connect to the ServiceLocator", e);
				}
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE, "An InterruptedException was thrown while waiting for an answer from the Service Locator", e);
				}
			} catch (ServiceLocatorException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE, "Failed to execute an request to Service Locator.", e);
				}
			}
		}
	}
	
	static interface PostConnectAction {
		void process(ServiceLocator lc);
	}
}
