package org.talend.esb.locator.zookeeper.server;

import java.io.IOException;

public interface ZookeeperServer {

    void startup() throws IOException;

    void shutdown();
}
