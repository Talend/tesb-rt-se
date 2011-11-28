/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.server.listener;

import java.net.InetAddress;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.derby.drda.NetworkServerControl;

public class DerbyStarterContextListener implements ServletContextListener {

    private final boolean startDerby;
    private NetworkServerControl server;

    public DerbyStarterContextListener() {
        startDerby = "TRUE".equalsIgnoreCase(System.getProperty("org.talend.esb.sam.server.embedded"));
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        if (startDerby) {
            try {
                server.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void contextInitialized(ServletContextEvent arg0) {
        if (startDerby) {
            try {
                server = new NetworkServerControl(
                        InetAddress.getByName("localhost"), 1527);
                server.start(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}