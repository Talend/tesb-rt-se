/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/
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

	private PostConnectAction postConnectAction = DO_NOTHING_ACTION;

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
	 * @throws InterruptedException
	 *             the current <code>Thread</code> was interrupted when waiting
	 *             for a successful connection to the ServiceLocator
	 * @throws ServiceLocatorException 
	 *             the connect operation failed
	 */
	synchronized public void connect() throws
			InterruptedException, ServiceLocatorException {
		disconnect();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.log(Level.FINE, "Start connect session");
		}

		CountDownLatch connectionLatch = new CountDownLatch(1);
		zk = createZooKeeper(connectionLatch);

		boolean connected = connectionLatch.await(connectionTimeout,
				TimeUnit.MILLISECONDS);

		if (!connected) {
			throw new ServiceLocatorException(
					"Connection to Service Locator failed.");
		} else {
//			notifyAll();
			postConnectAction.process(this);
		}

		if (LOG.isLoggable(Level.FINER)) {
			LOG.log(Level.FINER, "End connect session");
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
	synchronized public void disconnect() throws InterruptedException,
			ServiceLocatorException {

		if (zk != null) {
			zk.close();
			zk = null;
			if (LOG.isLoggable(Level.FINER)) {
				LOG.log(Level.FINER, "Disconnected service locator session.");
			}
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
	synchronized public void register(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, "Registering endpoint " + endpoint
					+ " for service " + serviceName + "...");
		}
		checkConnection();
		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
				.toString());
		ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);

		NodePath endpointNodePath = serviceNodePath.child(endpoint);
		ensurePathExists(endpointNodePath, CreateMode.EPHEMERAL);
	}

	synchronized public void unregister(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Unregistering endpoint " + endpoint + " for service "
					+ serviceName + "...");
		}

		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
				.toString());
		NodePath endpointNodePath = serviceNodePath.child(endpoint);

		checkConnection();
		ensurePathDeleted(endpointNodePath, false);
		ensurePathDeleted(serviceNodePath, true);
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
	synchronized public List<String> lookup(QName serviceName)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.INFO)) {
			LOG.info("Looking up endpoints of service " + serviceName );
		}
		try {
			checkConnection();
			NodePath providerPath = LOCATOR_ROOT_PATH.child(serviceName
					.toString());
			List<String> children;
			if (nodeExists(providerPath)) {
				children = decode(getChildren(providerPath));
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Lookup of service " + serviceName
							+ " failed, service is not known.");
				}
				children = Collections.emptyList();
			}

			return children;

		} catch (KeeperException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE,
						"The service locator server signaled an error", e);
			}
			throw new ServiceLocatorException(
					"The service locator server signaled an error.", e);
		}
	}

	/**
	 * Specify the endpoints of all the instances belonging to the service locator ensemble this
	 * object might potentially be talking to when {@link #connect() connecting}. The object
	 * will one by one pick an endpoint (the order is non-deterministic) to connect to the service
	 * locator until a connection is established.
	 * 
	 * @param endpoints comma separated list of endpoints,each corresponding to a service locator
	 *           instance. Each endpoint is specified as a host:port pair. At least one endpoint
	 *           must be specified. Valid exmaples are: "127.0.0.1:2181" or
	 *           "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
	 */
	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator endpoints set to " + locatorEndpoints);
		}
	}

	/**
	 * Specify the time out of the session established at the server. The session is kept alive by
	 * requests sent by this client object. If the session is idle for a period of time that would
	 * timeout the session, the client will send a PING request to keep the session alive.
	 * 
	 * @param sessionTimeout timeout in milliseconds, must be greater than zero and less than 60000. 
	 */
	public void setSessionTimeout(int timeout) {
		sessionTimeout = timeout;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator session timeout set to: " + sessionTimeout);
		}
	}

	/**
	 * Specify the time this client waits {@link #connect() for a connection to get established}.
	 * 
	 * @param connectionTimeout timeout in milliseconds, must be greater than zero
	 */
	public void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Locator connection timeout set to: " + connectionTimeout);
		}
	}

	public void setPostConnectAction(PostConnectAction postConnectAction) {
		this.postConnectAction = postConnectAction;
	}

	private boolean isConnected() {
		return (zk != null) && zk.getState().equals(ZooKeeper.States.CONNECTED);
	}

	private void checkConnection() throws InterruptedException, ServiceLocatorException {
		if (!isConnected()) {
/*
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING,
						"The connection to Service Locator was not established yet. Waiting for "
								+ connectionTimeout + " ms");
			}
			wait(connectionTimeout);
			if (!isConnected()) {
*/
				throw new ServiceLocatorException(
						"The connection to Service Locator was not established.");
//			}
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

	/**
	 * 
	 * @param path
	 *            Path to the node to be removed
	 * @param canHaveChildren
	 *            If <code>false</code> method throws an exception in case we
	 *            have {@link KeeperException} with code
	 *            {@link KeeperException.Code.NOTEMPTY NotEmpty}. If
	 *            <code>true</code>, node just not be deleted in case we have
	 *            Keeper {@link KeeperException.NotEmptyException
	 *            NotEmptyException}.
	 * @throws ServiceLocatorException
	 * @throws InterruptedException
	 */
	private void ensurePathDeleted(NodePath path, boolean canHaveChildren)
			throws ServiceLocatorException, InterruptedException {
		try {
			if (nodeExists(path) && deleteNode(path, canHaveChildren)) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " deteted.");
				}
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + path + " has already been deleted.");
				}
			}

		} catch (KeeperException e) {
			if (e.code().equals(Code.NONODE)) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Some other client deleted node" + path
							+ " concurrently.");
				}
			} else {
				throw new ServiceLocatorException(
						"The service locator server signaled an error.", e);
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

	private boolean deleteNode(NodePath path, boolean canHaveChildren)
			throws KeeperException, InterruptedException {
		try {
			if (getChildren(path).isEmpty())
				zk.delete(path.toString(), -1);
			return true;
		} catch (KeeperException e) {
			if (e.code().equals(Code.NOTEMPTY) && canHaveChildren) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Some other client created children nodes in the node"
							+ path
							+ " concurrently. Therefore, we can not delete it.");
				}
				return false;
			} else {
				throw e;
			}
		}
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
	throws ServiceLocatorException {
		try {
			return new ZooKeeper(locatorEndpoints, sessionTimeout, new WatcherImpl(connectionLatch));
		} catch (IOException e) {
			throw new ServiceLocatorException("At least one of the endpoints " + locatorEndpoints +
					" does not represent a valid address.");			
		}
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
			} catch (InterruptedException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"An InterruptedException was thrown while waiting for an answer from the Service Locator",
							e);
				}
			} catch (ServiceLocatorException e) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE,
							"Failed to execute an request to Service Locator.", e);
				}
			}
		}
	}
}
