/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.servicelocator.client.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.ServiceLocator;

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
public class ServiceLocatorImpl implements ServiceLocator {

	private static final Logger LOG = Logger.getLogger(ServiceLocatorImpl.class
			.getName());

	public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");

	public static final byte[] EMPTY_CONTENT = new byte[0];

	private static final NodePathBinder<NodePath> IDENTICAL_BINDER =
		new NodePathBinder<NodePath>() {
			@Override
			public NodePath bind(NodePath nodepath) {
				return nodepath;
			}
		};	

	private static final NodePathBinder<String> TO_NAME__BINDER =
		new NodePathBinder<String>() {
			@Override
			public String bind(NodePath nodePath) {
				return nodePath.getNodeName();
			}
		};

			private static final NodePathBinder<QName> TO_SERVICENAME__BINDER =
		new NodePathBinder<QName>() {
			@Override
			public QName bind(NodePath nodePath) {
				return QName.valueOf(nodePath.getNodeName());
			}
		};


	public static final PostConnectAction DO_NOTHING_ACTION = new PostConnectAction() {

		@Override
		public void process(ServiceLocator lc) {
		}
	};
	
	private static interface NodePathBinder<T> {
		T bind(NodePath nodepath) throws KeeperException, InterruptedException;
	}


	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;

	private int connectionTimeout = 5000;

	private PostConnectAction postConnectAction = DO_NOTHING_ACTION;

	private volatile ZooKeeper zk;

	/**
	 * {@inheritDoc}
	 */
	@Override
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
			postConnectAction.process(this);
		}

		if (LOG.isLoggable(Level.FINER)) {
			LOG.log(Level.FINER, "End connect session");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
	synchronized public void register(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		register(serviceName, endpoint, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(QName serviceName, String endpoint,
			SLProperties properties) throws ServiceLocatorException,
			InterruptedException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Registering endpoint " + endpoint
					+ " for service " + serviceName + "...");
		}
		checkConnection();
		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
				.toString());
		ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);

		NodePath endpointNodePath = serviceNodePath.child(endpoint);
		ensureEndpointExists(endpointNodePath, properties);

		NodePath endpointStatusNodePath = endpointNodePath.child("live");
		ensurePathExists(endpointStatusNodePath, CreateMode.EPHEMERAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	synchronized public void unregister(QName serviceName, String endpoint)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Unregistering endpoint " + endpoint + " for service "
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
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> getServices() throws InterruptedException, ServiceLocatorException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Getting all services...");
		}
		checkConnection();

		try {
			return getChildren(LOCATOR_ROOT_PATH, TO_SERVICENAME__BINDER);
		} catch (KeeperException e) {
			throw locatorException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SLEndpoint> getEndpoints(final QName serviceName)
			throws ServiceLocatorException, InterruptedException {
		
		NodePathBinder<SLEndpoint> slEndpointBinder = new NodePathBinder<SLEndpoint>() {
			
			@Override
			public SLEndpoint bind(final NodePath nodePath) throws KeeperException, InterruptedException {
				final long olderTime = 23678782200l;
				final long laterTime = olderTime + 24 * 60 * 60* 1000;

				final boolean isLive = isLive(nodePath);
			
				SLEndpoint endpoint = new SLEndpoint() {
					@Override
					public String getAddress() {
						return nodePath.getNodeName();
					}

					@Override
					public boolean isLive() {
							return isLive;
					}
					
					@Override
					public String getBinding() {
						return "HTTP/SOAP";
					}

					@Override
					public SLProperties getProperties() {
						return new SLPropertiesImpl();
					}

					@Override
					public long getLastTimeStarted() {
						return isLive ? laterTime : olderTime;
					}

					@Override
						public long getLastTimeStopped() {
						return isLive ? olderTime  :laterTime;
					}

					@Override
					public QName forService() {
						return serviceName;
					}
						
				};
				return endpoint;
			}
		};
		
		checkConnection();
		try {
			NodePath servicePath = LOCATOR_ROOT_PATH.child(serviceName
					.toString());
			if (nodeExists(servicePath)) {
				return getChildren(servicePath, slEndpointBinder);
			} else {
				return Collections.emptyList();
			}
		} catch (KeeperException e) {
			throw locatorException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	synchronized public List<String> getEndpointNames(QName serviceName)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Get all endpoints of service " + serviceName + "...");
		}
		checkConnection();
		List<String> children;
		try {
			NodePath servicePath = LOCATOR_ROOT_PATH.child(serviceName
					.toString());
			if (nodeExists(servicePath)) {
				children = getChildren(servicePath, TO_NAME__BINDER);
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Lookup of service " + serviceName
							+ " failed, service is not known.");
				}
				children = Collections.emptyList();
			}
		} catch (KeeperException e) {
			throw locatorException(e);
		}
		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	synchronized public List<String> lookup(QName serviceName)
			throws ServiceLocatorException, InterruptedException {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Looking up endpoints of service " + serviceName + "...");
		}
		checkConnection();
		
		List<String> liveEndpoints;
		try {
			NodePath providerPath = LOCATOR_ROOT_PATH.child(serviceName
					.toString());
			if (nodeExists(providerPath)) {
				liveEndpoints = new ArrayList<String>();
				List<NodePath> childNodePaths = getChildren(providerPath, IDENTICAL_BINDER);
				for (NodePath childNodePath : childNodePaths) {
					
					if (isLive(childNodePath)) {
						liveEndpoints.add(childNodePath.getNodeName());
					}
				}
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Lookup of service " + serviceName
							+ " failed, service is not known.");
				}
				liveEndpoints = Collections.emptyList();
			}
		} catch (KeeperException e) {
			throw locatorException(e);
		}
		return liveEndpoints;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPostConnectAction(PostConnectAction postConnectAction) {
		this.postConnectAction = postConnectAction;
	}

	private boolean isConnected() {
		return (zk != null) && zk.getState().equals(ZooKeeper.States.CONNECTED);
	}

	private void checkConnection() throws ServiceLocatorException {
		if (!isConnected()) {
				throw new ServiceLocatorException(
						"The connection to Service Locator was not established.");
		}
	}

	private void ensurePathExists(NodePath path, CreateMode mode)
			throws ServiceLocatorException, InterruptedException {
		try {
			if (!nodeExists(path)) {
				createNode(path, mode, EMPTY_CONTENT);
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
				throw locatorException(e);
			} else {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Some other client created node" + path
							+ " concurrently.");
				}
			}
		}
	}

	private void ensureEndpointExists(NodePath endpoint, SLProperties props)
			throws ServiceLocatorException, InterruptedException {
		byte[] content = serializePropertis(props);
		try {
			if (!nodeExists(endpoint)) {
				createNode(endpoint, CreateMode.PERSISTENT, content);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Endpoint " + endpoint + " created.");
				}
			} else {
				zk.setData(endpoint.toString(), content, -1);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Node " + endpoint + " already exists, updated content.");
				}
			}
		} catch (KeeperException e) {
			throw locatorException(e);
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
			if (/*nodeExists(path) && */ deleteNode(path, canHaveChildren)) {
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
				throw locatorException(e);
			}
		}
	}

	private boolean nodeExists(NodePath path) throws KeeperException,
			InterruptedException {
		return zk.exists(path.toString(), false) != null;
	}

	private void createNode(NodePath path, CreateMode mode, byte[] content)
			throws KeeperException, InterruptedException {
		zk.create(path.toString(), content, Ids.OPEN_ACL_UNSAFE, mode);
	}

	private boolean deleteNode(NodePath path, boolean canHaveChildren)
			throws KeeperException, InterruptedException {
		try {
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

	private <T> List<T> getChildren(NodePath path, NodePathBinder<T> binder) throws KeeperException,
	InterruptedException {
		List<String> encoded = zk.getChildren(path.toString(), false);

		List<T> boundChildren = new ArrayList<T>();

		for (String oneEncoded : encoded) {
			T boundChild = binder.bind(path.child(oneEncoded, true));
			boundChildren.add(boundChild);
		}
		
		return boundChildren;
	}

	private boolean isLive(NodePath endpointPath) throws KeeperException, InterruptedException {
		NodePath liveNodePath = endpointPath.child("live");
		return nodeExists(liveNodePath);
	}
	
	private byte[] serializePropertis(SLProperties props) throws ServiceLocatorException {
		if (props != null) {
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);

				objectStream.writeObject(props);
				objectStream.close();
				return byteStream.toByteArray();
			} catch(IOException e) {
				throw new ServiceLocatorException("Failed to serialize properties: " + props, e);
			}
		} else {
			return EMPTY_CONTENT;
		}
	}

	private ServiceLocatorException locatorException(Exception e) {
		if (LOG.isLoggable(Level.SEVERE)) {
			LOG.log(Level.SEVERE,
					"The service locator server signaled an error", e);
		}
		return new ServiceLocatorException(
				"The service locator server signaled an error.", e);
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
