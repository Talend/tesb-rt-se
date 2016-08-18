package org.talend.esb.servicelocator.client.internal.zk;

import static org.talend.esb.servicelocator.client.internal.zk.ServiceLocatorACLs.LOCATOR_ACLS;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.factory.annotation.Value;
import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.NodePath;
import org.talend.esb.servicelocator.client.internal.RootNode;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorBackend;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;

@Named
@Singleton
public class ZKBackend implements ServiceLocatorBackend {

    public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");

    public static final Charset UTF8_CHAR_SET = Charset.forName("UTF-8");

    private static final Logger LOG = Logger.getLogger(ServiceLocatorImpl.class
            .getName());

    private static final byte[] EMPTY_CONTENT = new byte[0];

//    private static final PostConnectAction DO_NOTHING_ACTION = new PostConnectAction() {
//        @Override
//        public void process(ServiceLocator lc) {
//        }
//    };

    private Set<PostConnectAction> postConnectActions = new HashSet<PostConnectAction>();

//    private int sessionTimeout = 5000;

//    private int connectionTimeout = 5000;

    private boolean authentication;

    private LocatorSettings settings = new LocatorSettings();

    private volatile ZooKeeper zk;

    private RootNodeImpl rootNode = new RootNodeImpl(this);

    {
//        postConnectActionList.add(DO_NOTHING_ACTION);
        settings.setEndpoints("localhost:2181");
    }

    @Override
    public RootNode connect() throws InterruptedException,
            ServiceLocatorException {

        if (!isConnected()) {
            disconnect();

            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Start connect session");
            }

            CountDownLatch connectionLatch = new CountDownLatch(1);
            zk = createZooKeeper(connectionLatch);

            if (authentication) {
                authenticate();
            }

            boolean connected = connectionLatch.await(settings.getConnectionTimeout(),
                    TimeUnit.MILLISECONDS);

            if (!connected) {
                throw new ServiceLocatorException(
                        "Connection to Service Locator failed.");
            }

            for (PostConnectAction postConnectAction : postConnectActions) {
                postConnectAction.process(null);
            }

            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "End connect session");
            }
        }

        return rootNode;
    }

    @PreDestroy
    @Override
    public void disconnect() throws InterruptedException,
            ServiceLocatorException {
        if (zk != null) {
            zk.close();
            zk = null;
            if (LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Disconnected service locator session.");
            }
        }
    }

    public boolean isConnected() {
        return (zk != null) && zk.getState().equals(ZooKeeper.States.CONNECTED);
    }

    public RootNode getRootNode() throws InterruptedException,
            ServiceLocatorException {
        connect();
        return rootNode;
    }

    public boolean nodeExists(NodePath path) throws ServiceLocatorException,
            InterruptedException {
        try {
            return zk.exists(path.toString(), false) != null;
        } catch (KeeperException e) {
            throw locatorException(e);
        }
    }

    public void createNode(NodePath path, CreateMode mode, byte[] content)
            throws KeeperException, InterruptedException {
        zk.create(path.toString(), content, getACLs(), mode);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Node " + path + " created as" + mode
                    + "in ZooKeeper with content "
                    + new String(content, UTF8_CHAR_SET));
        }

    }

    public void setNodeData(NodePath path, byte[] content)
            throws ServiceLocatorException, InterruptedException {
        try {
            zk.setData(path.toString(), content, -1);
        } catch (KeeperException e) {
            throw locatorException(e);
        }
    }

    public boolean deleteNode(NodePath path, boolean canHaveChildren)
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

    public <T> List<T> getChildren(NodePath path, NodeMapper<T> mapper)
            throws ServiceLocatorException, InterruptedException {
        List<String> encoded;
        try {
            encoded = zk.getChildren(path.toString(), false);
        } catch (KeeperException e) {
            throw locatorException(e);
        }

        List<T> boundChildren = new ArrayList<T>(encoded.size());

        for (String oneEncoded : encoded) {
            String notEncoded = NodePath.decode(oneEncoded);
            T boundChild = mapper.map(notEncoded);
            boundChildren.add(boundChild);
        }

        return boundChildren;
    }

    public byte[] getContent(NodePath path) throws ServiceLocatorException,
            InterruptedException {
        try {
            byte[] content = zk.getData(path.toString(), false, null);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Retrieved the following content for node " + path);
                LOG.fine(new String(content, UTF8_CHAR_SET));
            }
            return content;
        } catch (KeeperException e) {
            throw locatorException(e);
        }
    }

    public void ensurePathExists(NodePath path, CreateMode mode)
            throws ServiceLocatorException, InterruptedException {
        ensurePathExists(path, mode, EMPTY_CONTENT);
    }

    public void ensurePathExists(NodePath path, CreateMode mode, byte[] content)
            throws ServiceLocatorException, InterruptedException {
        try {
            if (!nodeExists(path)) {
                createNode(path, mode, content);
            } else {
                if (mode.isEphemeral()) {
                    deleteNode(path, false);
                    createNode(path, mode, content);
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Ephemeral node " + path + " was recreated.");
                    }
                } else if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + path + " already exists.");
                }
                if (!Arrays.equals(EMPTY_CONTENT, content) && (content.length != 0)) setNodeData(path, content);
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

    /**
     * @param path            Path to the node to be removed
     * @param canHaveChildren If <code>false</code> method throws an exception in case we
     *                        have {@link KeeperException} with code
     *                        {@link KeeperException.Code.NOTEMPTY NotEmpty}. If
     *                        <code>true</code>, node just not be deleted in case we have
     *                        Keeper {@link KeeperException.NotEmptyException
     *                        NotEmptyException}.
     * @throws ServiceLocatorException
     * @throws InterruptedException
     */
    public void ensurePathDeleted(NodePath path, boolean canHaveChildren)
            throws ServiceLocatorException, InterruptedException {
        try {
            if (deleteNode(path, canHaveChildren)) {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + path + " deteted.");
                }
            } else {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Node " + path
                            + " cannot be deleted because it has children.");
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

    @Override
    public void addPostConnectAction(PostConnectAction postConnectAction) {
        postConnectActions.add(postConnectAction);
    }

    @Override
    public void removePostConnectAction(PostConnectAction postConnectAction) {
        postConnectActions.remove(postConnectAction);
    }

    /**
     * Specify the endpoints of all the instances belonging to the service
     * locator ensemble this object might potentially be talking to when
     * {@link #connect() connecting}. The object will one by one pick an
     * endpoint (the order is non-deterministic) to connect to the service
     * locator until a connection is established.
     *
     * @param endpoints comma separated list of endpoints,each corresponding to a
     *                  service locator instance. Each endpoint is specified as a
     *                  host:port pair. At least one endpoint must be specified. Valid
     *                  exmaples are: "127.0.0.1:2181" or
     *                  "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
     */
    @Value("${locator.endpoints}")
    public void setLocatorEndpoints(String endpoints) {
        settings.setEndpoints(endpoints);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator endpoints set to " + settings.getEndpoints());
        }
    }

    /**
     * Specify the time out of the session established at the server. The
     * session is kept alive by requests sent by this client object. If the
     * session is idle for a period of time that would timeout the session, the
     * client will send a PING request to keep the session alive.
     *
     * @param timeout timeout in milliseconds, must be greater than zero and less
     *                than 60000.
     */
    @Value("${session.timeout}")
    public void setSessionTimeout(int timeout) {
        settings.setSessionTimeout(timeout);
    }

    /**
     * Specify the time this client waits {@link #connect() for a connection to
     * get established}.
     *
     * @param timeout timeout in milliseconds, must be greater than zero
     */
    @Value("${connection.timeout}")
    public void setConnectionTimeout(int timeout) {
        settings.setConnectionTimeout(timeout);
    }

    @Value("${authentication.name}")
    public void setUserName(String userName) {
        settings.setUser(userName);
    }

    @Value("${authentication.password}")
    public void setPassword(String passWord) {
        settings.setPassword(passWord);
    }

    private void initializeRootNode() throws ServiceLocatorException,
            InterruptedException {
        rootNode.ensureExists();
        authentication = rootNode.isAuthenticationEnabled();
    }

    private void authenticate() throws ServiceLocatorException {
        if (settings.getUser() == null) {
            throw new ServiceLocatorException(
                    "Service Locator server requires authentication, but no user is defined.");
        }
        zk.addAuthInfo("sl", settings.getAuthInfo());
    }

    private List<ACL> getACLs() {
        return authentication ? LOCATOR_ACLS : Ids.OPEN_ACL_UNSAFE;
    }

    protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch)
            throws ServiceLocatorException {
        try {
            return new ZooKeeper(settings.getEndpoints(), settings.getSessionTimeout(),
                    new WatcherImpl(connectionLatch));
        } catch (IOException e) {
            throw new ServiceLocatorException(
                    "A network failure occured when connecting to the ZooKeeper server",
                    e);
        }
    }

    private ServiceLocatorException locatorException(Exception e) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "The service locator server signaled an error",
                    e);
        }
        return new ServiceLocatorException(
                "The service locator server signaled an error.", e);
    }

    public interface NodeMapper<T> {
        T map(String nodeName) throws ServiceLocatorException,
                InterruptedException;
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
                    try {
                        initializeRootNode();
                    } catch (ServiceLocatorException e) {
                        KeeperException zke = (KeeperException) e.getCause();
                        if (zke.code().equals(KeeperException.Code.NOAUTH)) {
                            authenticate();
                            initializeRootNode();
                        }
                    }
                    //fix for TESB-9642
//                    for (PostConnectAction postConnectAction : postConnectActionList) {
//                        postConnectAction.process(null);
//                    }
                    //moved to connect() method
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
                            "Failed to execute an request to Service Locator.",
                            e);
                }
            }
        }
    }

}
