/*
 * #%L
 * Service Activity Monitoring :: Datasource-mysql
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
package org.talend.esb.datasource.db2;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.ibm.db2.jcc.DB2SimpleDataSource;

public class DataSourceConfigurator implements ManagedService {
    private DB2SimpleDataSource dataSource;

    public void setDataSource(DB2SimpleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        if (dataSource != null) {
            dataSource.setServerName(getString(properties, "datasource.servername"));
            dataSource.setDatabaseName(getString(properties, "datasource.databasename"));
            dataSource.setPortNumber(Integer.parseInt(getString(properties, "datasource.portnumber")));
            dataSource.setDriverType(Integer.parseInt(getString(properties, "datasource.drivertype")));
            dataSource.setUser(getString(properties, "datasource.user"));
            dataSource.setPassword(getString(properties, "datasource.password"));
            dataSource.setSecurityMechanism(com.ibm.db2.jcc.DB2BaseDataSource.CLEAR_TEXT_PASSWORD_SECURITY);
        }

    }

    @SuppressWarnings("rawtypes")
    private String getString(Dictionary properties, String key) {
        Object value = properties.get(key);
        return (!(value instanceof String)) ? "" : (String) value;
    }
}
