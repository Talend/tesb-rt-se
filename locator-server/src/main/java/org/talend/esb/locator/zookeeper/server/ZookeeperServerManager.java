package org.talend.esb.locator.zookeeper.server;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.aries.blueprint.compendium.cm.CmPropertyPlaceholder;

public class ZookeeperServerManager implements ZookeeperServer {

    private static final Logger LOG = Logger.getLogger(ZookeeperServerManager.class.getName());

    private ZookeeperServer main;
    private Thread zkMainThread;

    private Dictionary<?, ?> properties;
    public void setProperties(Dictionary<?, ?> properties) {
        this.properties = properties;
    }

//    CmPropertyPlaceholder cmPropertyPlaceholder;
//    public void setCmPropertyPlaceholder(CmPropertyPlaceholder cmPropertyPlaceholder) {
//        this.cmPropertyPlaceholder = cmPropertyPlaceholder;
//    }

    public void startup() {

//        Dictionary<?, ?> dict = cmPropertyPlaceholder.getConfigAdmin()
//                .getConfiguration(cmPropertyPlaceholder.getPersistentId()).getProperties();
        Dictionary<?, ?> dict = properties;
        // System.out.println("### ZOOKEEPER :: dictionary : " + dict);

        LOG.info("Staring up ZooKeeper server");

        if (dict == null) {
            LOG.info("Ignoring configuration update because updated configuration is empty.");
            shutdown();
            return;
        }


        if (main != null) {
            // stop the current instance
            shutdown();
            // then reconfigure and start again.
        }

        if (dict.get("clientPort") == null) {
            LOG.info("Ignoring configuration update because required property 'clientPort' isn't set.");
            return;
        }

        Properties props = new Properties();
        for (Enumeration<?> e = dict.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            props.put(key, dict.get(key));
        }

        try {
            main = ZookeeperServerImpl.getZookeeperServer(props);

            zkMainThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        main.startup();
                    } catch (IOException e) {
                        LOG.log(Level.SEVERE, "Problem running ZooKeeper server.", e);
                    }
                }
            });
            zkMainThread.start();

            LOG.info("Applied configuration update :" + props);
        } catch (Exception th) {
            LOG.log(Level.SEVERE, "Problem applying configuration update: " + props, th);
        }
    }

    public synchronized void shutdown() {
        if (main != null) {
            LOG.info("Shutting down ZooKeeper server");
            main.shutdown();
            try {
                zkMainThread.join();
            } catch (InterruptedException e) {
                // ignore
            }
            main = null;
            zkMainThread = null;
        }
    }

}
