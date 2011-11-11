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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

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

    public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");
    
    public static final  String LIVE = "live"; 

    public static final byte[] EMPTY_CONTENT = new byte[0];

    public static final PostConnectAction DO_NOTHING_ACTION = new PostConnectAction() {

        @Override
        public void process(ServiceLocator lc) {
        }
    };

    private static final Logger LOG = Logger.getLogger(ServiceLocatorImpl.class
            .getName());

    private static final NodePathBinder<NodePath> IDENTICAL_BINDER = new NodePathBinder<NodePath>() {
        @Override
        public NodePath bind(NodePath nodepath) {
            return nodepath;
        }
    };

    private static final NodePathBinder<String> TO_NAME_BINDER = new NodePathBinder<String>() {
        @Override
        public String bind(NodePath nodePath) {
            return nodePath.getNodeName();
        }
    };

    private static final NodePathBinder<QName> TO_SERVICENAME_BINDER = new NodePathBinder<QName>() {
        @Override
        public QName bind(NodePath nodePath) {
            return QName.valueOf(nodePath.getNodeName());
        }
    };

    private interface NodePathBinder<T> {
        T bind(NodePath nodepath) throws ServiceLocatorException, InterruptedException;

    }
    
    private String locatorEndpoints = "localhost:2181";

    private int sessionTimeout = 5000;

    private int connectionTimeout = 5000;

    private PostConnectAction postConnectAction = DO_NOTHING_ACTION;

    private volatile ZooKeeper zk;
    
    private EndpointTransformer transformer = new EndpointTransformerImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void connect() throws InterruptedException,
            ServiceLocatorException {
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
    public synchronized void disconnect() throws InterruptedException,
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
    public synchronized void register(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void register(QName serviceName, String endpoint, boolean persistent)
    throws ServiceLocatorException, InterruptedException {
    	register(new SimpleEndpoint(serviceName, endpoint), persistent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(QName serviceName, String endpoint, SLProperties properties)
        throws ServiceLocatorException, InterruptedException {

        register(new SimpleEndpoint(serviceName, endpoint, properties), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(QName serviceName, String endpoint,
            SLProperties properties, boolean persistent)
    throws ServiceLocatorException, InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint, properties), persistent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized  void register(Endpoint epProvider)
        throws ServiceLocatorException, InterruptedException {
        
        register(epProvider, false);
    }

    @Override
    public synchronized  void register(Endpoint epProvider, boolean persistent)
        throws ServiceLocatorException, InterruptedException {
        
        QName serviceName = epProvider.getServiceName();
        String endpoint = epProvider.getAddress();

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering endpoint " + endpoint + " for service "
                    + serviceName + "...");
        }
        checkConnection();

        long lastTimeStarted = System.currentTimeMillis();
        long lastTimeStopped = -1;
        
        NodePath serviceNodePath = ensureServiceExists(serviceName);
        NodePath endpointNodePath = serviceNodePath.child(endpoint);

        try {
            if (nodeExists(endpointNodePath)) {
                byte[] content = getContent(endpointNodePath);
                SLEndpoint oldEndpoint = transformer.toSLEndpoint(serviceName, content, false);
                lastTimeStopped = oldEndpoint.getLastTimeStopped();
            }
        } catch (KeeperException e) {
            throw locatorException(e);
        } 
        
        byte[] content = createContent(epProvider, lastTimeStarted, lastTimeStopped);
        endpointNodePath = 
            ensureEndpointExists(serviceNodePath, endpoint, content);

        createEndpointStatus(endpointNodePath, persistent);
    }

    @Override
    public synchronized void unregister(Endpoint epProvider)
        throws ServiceLocatorException, InterruptedException {

        QName serviceName = epProvider.getServiceName();
        String endpoint = epProvider.getAddress();

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering endpoint " + endpoint + " for service "
                    + serviceName + "...");
        }
        checkConnection();
        long lastTimeStarted = -1;
        long lastTimeStopped = System.currentTimeMillis();

        NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
                .toString());
        NodePath endpointNodePath = serviceNodePath.child(endpoint);

        try {            
            if (nodeExists(endpointNodePath)) {
                
                byte[] oldContent = getContent(endpointNodePath);
                SLEndpoint oldEndpoint = transformer.toSLEndpoint(serviceName, oldContent, false);
                lastTimeStarted = oldEndpoint.getLastTimeStarted();

                NodePath endpointStatusNodePath = endpointNodePath.child(LIVE);
        
                ensurePathDeleted(endpointStatusNodePath, false);

                byte[] content = createContent(epProvider, lastTimeStarted, lastTimeStopped);
                setNodeData(endpointNodePath, content);
            }
        } catch (KeeperException e) {
            throw locatorException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void unregister(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException {
        
        unregister(new SimpleEndpoint(serviceName, endpoint, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeEndpoint(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Removing endpoint " + endpoint + " for service "
                    + serviceName + "...");
        }

        checkConnection();

        NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
            .toString());
        NodePath endpointNodePath = serviceNodePath.child(endpoint);
        NodePath endpointStatusNodePath = endpointNodePath.child(LIVE);
        
        ensurePathDeleted(endpointStatusNodePath, false);
        ensurePathDeleted(endpointNodePath, false);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getServices() throws InterruptedException,
            ServiceLocatorException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Getting all services...");
        }
        checkConnection();

        try {
            return getChildren(LOCATOR_ROOT_PATH, TO_SERVICENAME_BINDER);
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
            public SLEndpoint bind(final NodePath nodePath)
                throws ServiceLocatorException, InterruptedException {
                
                try {
                    byte[] content = getContent(nodePath);
                    final boolean isLive = isLive(nodePath);
                    return transformer.toSLEndpoint(serviceName, content, isLive);
//                    return new SLEndpointImpl(serviceName, content, isLive);
                } catch (KeeperException e) {
                    throw locatorException(e);
                }
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
    public SLEndpoint getEndpoint(final QName serviceName, final String endpoint)
        throws ServiceLocatorException, InterruptedException {

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Get endpoint information for endpoint " + endpoint + " within service " + serviceName + "...");
        }

        checkConnection();
        try {
            NodePath servicePath = LOCATOR_ROOT_PATH.child(serviceName
                    .toString());
            NodePath endpointPath = servicePath.child(endpoint);
            if (nodeExists(endpointPath)) {
                byte[] content = getContent(endpointPath);
                final boolean isLive = isLive(endpointPath);

                return transformer.toSLEndpoint(serviceName, content, isLive);
            } else {
                return null;
            }
        } catch (KeeperException e) {
            throw locatorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getEndpointNames(QName serviceName)
        throws ServiceLocatorException, InterruptedException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Get all endpoint names of service " + serviceName + "...");
        }
        checkConnection();
        List<String> children;
        try {
            NodePath servicePath = LOCATOR_ROOT_PATH.child(serviceName
                    .toString());
            if (nodeExists(servicePath)) {
                children = getChildren(servicePath, TO_NAME_BINDER);
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
    public List<String> lookup(QName serviceName)
        throws ServiceLocatorException, InterruptedException {
        
        return lookup(serviceName, SLPropertiesMatcher.ALL_MATCHER);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> lookup(QName serviceName, SLPropertiesMatcher matcher)
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
                List<NodePath> childNodePaths = getChildren(providerPath,
                        IDENTICAL_BINDER);
                for (NodePath childNodePath : childNodePaths) {

                    if (isLive(childNodePath)) {
                        byte[] content = getContent(childNodePath);
                        SLEndpoint endpoint = transformer.toSLEndpoint(serviceName, content, true);
                        SLProperties props = endpoint.getProperties();

                        if (matcher.isMatching(props)) {
                            liveEndpoints.add(childNodePath.getNodeName());
                        }
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
     * Specify the endpoints of all the instances belonging to the service
     * locator ensemble this object might potentially be talking to when
     * {@link #connect() connecting}. The object will one by one pick an
     * endpoint (the order is non-deterministic) to connect to the service
     * locator until a connection is established.
     * 
     * @param endpoints
     *            comma separated list of endpoints,each corresponding to a
     *            service locator instance. Each endpoint is specified as a
     *            host:port pair. At least one endpoint must be specified. Valid
     *            exmaples are: "127.0.0.1:2181" or
     *            "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
     */
    public void setLocatorEndpoints(String endpoints) {
        locatorEndpoints = endpoints;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator endpoints set to " + locatorEndpoints);
        }
    }

    /**
     * Specify the time out of the session established at the server. The
     * session is kept alive by requests sent by this client object. If the
     * session is idle for a period of time that would timeout the session, the
     * client will send a PING request to keep the session alive.
     * 
     * @param sessionTimeout
     *            timeout in milliseconds, must be greater than zero and less
     *            than 60000.
     */
    public void setSessionTimeout(int timeout) {
        sessionTimeout = timeout;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator session timeout set to: " + sessionTimeout);
        }
    }

    /**
     * Specify the time this client waits {@link #connect() for a connection to
     * get established}.
     * 
     * @param connectionTimeout
     *            timeout in milliseconds, must be greater than zero
     */
    public void setConnectionTimeout(int timeout) {
        connectionTimeout = timeout;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator connection timeout set to: " + connectionTimeout);
        }
    }
    
    public void setEndpointTransformer(EndpointTransformer endpointTransformer) {
        transformer = endpointTransformer;
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

    private void checkConnection() throws ServiceLocatorException, InterruptedException {
        if (!isConnected()) {
            connect();
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

    private NodePath ensureServiceExists(QName serviceName) throws ServiceLocatorException,
    InterruptedException {
        NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName
                .toString());
        ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);
        return serviceNodePath;
    }

    private void createEndpointStatus(NodePath endpointNodePath, boolean persistent)
        throws ServiceLocatorException, InterruptedException {

        NodePath endpointStatusNodePath = endpointNodePath.child("live");
        try {
            CreateMode mode = persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL;
            createNode(endpointStatusNodePath, mode, EMPTY_CONTENT);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Node " + endpointStatusNodePath + " created.");
            }
        } catch (KeeperException e) {
            if (!e.code().equals(Code.NODEEXISTS)) {
                throw locatorException(e);
            } 
        }
    }

    private NodePath ensureEndpointExists(NodePath serviceNodePath, String endpoint, byte[] content)
        throws ServiceLocatorException, InterruptedException {
        NodePath endpointNodePath = serviceNodePath.child(endpoint);

        try {
            if (!nodeExists(endpointNodePath)) {
                createNode(endpointNodePath, CreateMode.PERSISTENT, content);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Endpoint " + endpoint + " created with data:");
                    LOG.fine(new String(content, "utf-8"));
                }
            } else {
                setNodeData(endpointNodePath, content);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + endpoint
                            + " already exists, updated content with data:");
                    LOG.fine(new String(content, "utf-8"));
                }
            }
        } catch (KeeperException e) {
            throw locatorException(e);
        } catch (UnsupportedEncodingException e) {
            throw locatorException(e);            
        }
        return endpointNodePath;
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
            if (deleteNode(path, canHaveChildren)) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + path + " deteted.");
                }
            } else {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + path + " cannot be deleted because it has children.");
                }
            }

        } catch (KeeperException e) {
            if (e.code().equals(Code.NONODE)) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node" + path + " already deleted.");
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

    private void setNodeData(NodePath path, byte[] content)
        throws KeeperException, InterruptedException {
        zk.setData(path.toString(), content, -1);
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

    private <T> List<T> getChildren(NodePath path, NodePathBinder<T> binder)
        throws ServiceLocatorException, KeeperException, InterruptedException {
        List<String> encoded = zk.getChildren(path.toString(), false);

        List<T> boundChildren = new ArrayList<T>();

        for (String oneEncoded : encoded) {
            T boundChild = binder.bind(path.child(oneEncoded, true));
            boundChildren.add(boundChild);
        }

        return boundChildren;
    }

    private byte[] getContent(NodePath path) throws KeeperException, InterruptedException {
        return zk.getData(path.toString(), false, null);
    }

    private boolean isLive(NodePath endpointPath) throws KeeperException,
            InterruptedException {
        NodePath liveNodePath = endpointPath.child("live");
        return nodeExists(liveNodePath);
    }

    private byte[] createContent(Endpoint eprProvider, long lastTimeStarted, long lastTimeStopped)
    throws ServiceLocatorException  {
        return transformer.fromEndpoint(eprProvider, lastTimeStarted, lastTimeStopped);
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
            return new ZooKeeper(locatorEndpoints, sessionTimeout,
                    new WatcherImpl(connectionLatch));
        } catch (IOException e) {
            throw new ServiceLocatorException("At least one of the endpoints "
                    + locatorEndpoints + " does not represent a valid address.");
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
                        "An InterruptedException was thrown while waiting for an answer from the"
                        + "Service Locator", e);
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
