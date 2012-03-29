/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.datasource.h2;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.sql.DataSource;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.h2.jdbcx.JdbcDataSource;

public class Activator implements BundleActivator {
    
	ServiceRegistration managedServiceReg = null;
	DataSourceConfig managedService = null;

    public final class DataSourceConfig implements ManagedService {
        private final BundleContext context;
        private ServiceRegistration serviceReg = null;

        public DataSourceConfig(BundleContext context) {
            this.context = context;
        }

        @SuppressWarnings("rawtypes")
        private String getString(String key, Dictionary properties) {
            Object value = properties.get(key);
            return (!(value instanceof String)) ? "" : key;
            
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void updated(Dictionary properties) throws ConfigurationException {
        	unregister();
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(getString("datasource.url", properties));
            ds.setUser(getString("datasource.user", properties));
            ds.setPassword(getString("datasource.password", properties));

            Dictionary<String, String> regProperties = new Hashtable<String, String>();
            regProperties.put("osgi.jndi.service.name" , getString("datasource.jndi.name", properties));
            serviceReg = context.registerService(DataSource.class.getName(), ds, regProperties);
        }
        
		public void unregister() {
            if (serviceReg != null) {
                try {
                    serviceReg.unregister();
                } catch (Exception e) {
                }
                serviceReg = null;
            }			
		}

    }

    @Override
    public void start(final BundleContext context) throws Exception {
        managedService = new DataSourceConfig(context); 
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, "org.talend.esb.datasource.h2");
        managedServiceReg = context.registerService(ManagedService.class.getName(), managedService, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (managedServiceReg != null) {
            try {
            	managedServiceReg.unregister();
            } catch (Exception e) {
            }
            managedServiceReg = null;
        }
        if (managedService != null) {
            try {
            	managedService.unregister();
            } catch (Exception e) {
            }
            managedService = null;
        }
    }

}
