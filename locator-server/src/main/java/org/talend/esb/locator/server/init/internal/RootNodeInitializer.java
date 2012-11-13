package org.talend.esb.locator.server.init.internal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import static org.talend.esb.locator.server.init.internal.RootNodeACLs.LOCATOR_ROOT_ACLS;
import static org.talend.esb.locator.server.init.internal.RootNodeACLs.ZK_ROOT_ACLS;

public class RootNodeInitializer implements Watcher {
    
    private static final Charset UTF8_CHAR_SET = Charset.forName("UTF-8");

    private static final String ZK_ROOT_NODE_PATH = "/";

    private static final String ROOT_NODE_PATH = "/cxf-locator";

    private static final Logger LOG = Logger.getLogger(RootNodeInitializer.class.getName());

    private int timeout = 10000;

    private String locatorEndpoints = "localhost:2181";

    private String version = "5.2.0";

    private boolean authentication;

    private ZooKeeper zk;

    private CountDownLatch finishedLatch = new CountDownLatch(1);

    public void setLocatorEndpoints(String endpoints) {
        locatorEndpoints = endpoints;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator endpoints set to " + locatorEndpoints);
        }
    }

    public void setLocatorPort(String port) {
        locatorEndpoints = "localhost:" + port;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator endpoint set to " + locatorEndpoints);
        }
    }

    public void setVersion(String versionNumber) {
        version = versionNumber;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Version set to " + version);
        }
    }
    
    public void setAuthentication(boolean auth) {
        authentication = auth;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("authentication is " + authentication);
        }
    }

    public void initialize() throws InterruptedException {        
        try {
            zk = new ZooKeeper(locatorEndpoints, 5000, this);
            boolean finished = finishedLatch.await(timeout,
                    TimeUnit.MILLISECONDS);
            if (! finished && LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Failed to connect to ZooKeeper and initialize the root node within " + timeout + " ms.");               
            }
        } catch (IOException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Failed to create ZooKeeper client", e);
            }
        } finally {
            stop();
        }
    }
    
    public void stop() throws InterruptedException {
        if (zk != null) {
            zk.close();
            zk = null;
        }
    }

    @Override
    public void process(WatchedEvent event) {
        KeeperState eventState = event.getState();
            if (eventState == KeeperState.SyncConnected) {
                createRootNode();
            } else {
                if (LOG.isLoggable(Level.INFO)) {
                    LOG.log(Level.INFO, "Connect to ZooKeeper failed. ZooKeeper client returned state "
                + eventState);
                }

            }        
    }

    private void createRootNode() {
        try {
            Stat stat = zk.exists(ROOT_NODE_PATH, false);
            
            if (stat == null) {
                zk.create(ROOT_NODE_PATH, getContent(), getLocatorRootACLs(), CreateMode.PERSISTENT);
                zk.setACL(ZK_ROOT_NODE_PATH, getZKRootACLs(), -1);
            } else {
                try {
                    byte[] oldContent = zk.getData(ROOT_NODE_PATH, false, new Stat());
                    if (contentNeedsUpdate(oldContent)) {
                        zk.setData(ROOT_NODE_PATH, getContent(), -1);
                        zk.setACL(ROOT_NODE_PATH, getLocatorRootACLs(), -1);
                        zk.setACL(ZK_ROOT_NODE_PATH, getZKRootACLs(), -1);
                    }
                } catch (KeeperException e) {
                    if (e.code().equals(KeeperException.Code.NOAUTH)) {
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.log(Level.INFO,
                            "Service Locator already requires authentication. Configuration settings"
                            + " for the Service Locator root cannot be applied anymore.");
                        } else {
                            throw e;
                        }
                    }
                }
            }
        } catch (KeeperException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Failed to create RootNode", e);
            }
        } catch (InterruptedException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Thread got interrupted when wating for root node to be created.", e);
            }
        }
        finishedLatch.countDown();
    }
    
    private byte [] getContent() {
        String contentAsStr = version + "," + Boolean.toString(authentication);
        return contentAsStr.getBytes(UTF8_CHAR_SET);
    }
    
    private boolean contentNeedsUpdate(byte[] oldContent) {
       
        String oldVersion= null;
        boolean oldAuthentication = false;
        
        String contentStr = new String(oldContent, UTF8_CHAR_SET);
        String[] parts = contentStr.split(",");

        if (parts.length == 2) {
            oldVersion = parts[0];
            oldAuthentication = Boolean.parseBoolean(parts[1]);

            return (! oldAuthentication && authentication) || ! oldVersion.equals(version);
        } else {
            return false;
        }
    }

    private List<ACL> getLocatorRootACLs() {
        return authentication ? LOCATOR_ROOT_ACLS : Ids.OPEN_ACL_UNSAFE;
    }

    private List<ACL> getZKRootACLs() {
        return authentication ? ZK_ROOT_ACLS : Ids.OPEN_ACL_UNSAFE;
    }
}
