package org.talend.esb.locator.zookeeper.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

public class ZookeeperServerImpl implements ZookeeperServer {

    ZookeeperServer serverInner;

    private ZookeeperServerImpl() {
    }

    public static ZookeeperServer getZookeeperServer(Properties props) throws Exception {

        QuorumPeerConfig config = new QuorumPeerConfig();
        config.parseProperties(props);

        if (config.getServers().size() > 0) {
            return new MyQuorumPeerMain(config);
        } else {
            return new MyZooKeeperServerMain(config);
        }
    }

    public void startup() throws IOException {
        serverInner.startup();
    }

    public void shutdown() {
        serverInner.shutdown();
    }


    static class MyQuorumPeerMain extends QuorumPeerMain implements ZookeeperServer {

        private final QuorumPeerConfig config;

        MyQuorumPeerMain(QuorumPeerConfig config) {
            this.config = config;
        }

        public void startup() throws IOException {
            runFromConfig(config);
        }

        public void shutdown() {
            if(null != quorumPeer) {
                quorumPeer.shutdown();
            }
        }
    }


    static class MyZooKeeperServerMain extends ZooKeeperServerMain implements ZookeeperServer {

        private final QuorumPeerConfig config;

        MyZooKeeperServerMain(QuorumPeerConfig config) {
            this.config = config;
        }

        public void startup() throws IOException {
             ServerConfig serverConfig = new ServerConfig();
             serverConfig.readFrom(config);
             runFromConfig(serverConfig);
        }

        public void shutdown() {
            super.shutdown();
        }
    }
}
