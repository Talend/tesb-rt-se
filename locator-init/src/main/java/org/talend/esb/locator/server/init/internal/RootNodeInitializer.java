package org.talend.esb.locator.server.init.internal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
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

    private static final String ROOT_NODE_PATH = "/cxf-locator";

    private static final Logger LOG = Logger.getLogger(RootNodeInitializer.class.getName());
    
    private String locatorEndpoints = "localhost:2181";

    private String version = "5.2.0";
    
    private boolean withAuthentication;

    private ZooKeeper zk; 

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
    
    public void setAuthentication(boolean authentication) {
        withAuthentication = authentication;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("authentication is " + authentication);
        }
    }

    public void initialize() {        
        try {
            zk = new ZooKeeper(locatorEndpoints, 5000, this);            
        } catch (IOException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Failed to create ZooKeeper client", e);
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        KeeperState eventState = event.getState();
//        try {
            if (eventState == KeeperState.SyncConnected) {
                createRootNode();
            } else {
                if (LOG.isLoggable(Level.SEVERE)) {
                    LOG.log(Level.SEVERE, "Connect to ZooKeeper failed. ZooKeeper client returned state "
                + eventState);
                }

            }        
    }

    private void createRootNode() {
        try {

            Stat stat = zk.exists(ROOT_NODE_PATH, false);
            
            if (stat == null) {
                zk.create(ROOT_NODE_PATH, getContent(), getLocatorRootACLs(), CreateMode.PERSISTENT);
            } else {
                zk.setData(ROOT_NODE_PATH, getContent(), -1);
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
        
    }
    
    private byte [] getContent() {
        String contentAsStr = version + "," + Boolean.toString(withAuthentication);
        return contentAsStr.getBytes(UTF8_CHAR_SET);
    }

    private List<ACL> getLocatorRootACLs() {
        return withAuthentication ? LOCATOR_ROOT_ACLS : Ids.OPEN_ACL_UNSAFE;
    }


/*
    private List<ACL> getZKRootACLs() {
        return withAuthentication ? ZK_ROOT_ACLS : Ids.OPEN_ACL_UNSAFE;
    }
*/
}
