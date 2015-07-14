/*
 * #%L
 * Service Activity Monitoring :: Derby Starter
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.derby.starter;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.drda.NetworkServerControl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkActivator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkActivator.class);

    private NetworkServerControl server;

    public void start(BundleContext context) throws Exception {
        LOG.info("Starting internal Derby DB...");

        server = new NetworkServerControl(InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }), 1527);
        server.start(null);

        // if it already exists nothing should happen
        DriverManager.getConnection(getDerbyJDBC_Create("db"));
    }

    public void stop(BundleContext context) throws Exception {
        LOG.info("Stopping internal Derby DB...");

        try {
            DriverManager.getConnection(getDerbyJDBC_Shutdown("db"));
        } catch (SQLException e) {
            if (!"08006".equals(e.getSQLState())) {
                LOG.error("Exception during shutting db down.", e);
            }
        }

        server.shutdown();
    }

    private static String getDerbyJDBC_Create(String databaseName) {
        return "jdbc:derby:" + databaseName + ";create=true";
    }

    private static String getDerbyJDBC_Shutdown(String databaseName) {
        return "jdbc:derby:" + databaseName + ";shutdown=true";
    }

}
