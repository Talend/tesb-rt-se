/*
 * #%L
 * Service Activity Monitoring :: Datasource-derby
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
package org.talend.esb.datasource.derby;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ShutdownDBActivator implements BundleActivator {

    public void start(BundleContext context) throws Exception {

    }

    public void stop(BundleContext context) throws Exception {
        ServiceReference serviceRef = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin cfgAdmin = (ConfigurationAdmin)context.getService(serviceRef); 
        Configuration config = cfgAdmin.getConfiguration("org.talend.esb.datasource.derby");
        
        String serverName = (String)config.getProperties().get("datasource.server");
        int portNumber = Integer.parseInt((String)config.getProperties().get("datasource.port"));
        String dbName = (String)config.getProperties().get("datasource.database");
        String user = (String)config.getProperties().get("datasource.username");
        String password = (String)config.getProperties().get("datasource.password");
        
    	ClientConnectionPoolDataSource dataSource = new ClientConnectionPoolDataSource();
    	dataSource.setServerName(serverName);
    	dataSource.setPortNumber(portNumber);
    	dataSource.setDatabaseName(dbName);
    	dataSource.setShutdownDatabase("shutdown");
    	dataSource.setUser(user);
    	dataSource.setPassword(password);
    	dataSource.getConnection();

    }

}
