package org.talend.esb.locator.zookeeper.server;

import java.io.IOException;

import org.apache.zookeeper.server.admin.AdminServer.AdminServerException;

public interface ZookeeperServer {

    void startup() throws IOException, AdminServerException;

    void shutdown();
}
