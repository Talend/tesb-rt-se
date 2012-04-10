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

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.apache.derby.jdbc.ClientDataSource;

public class DataSourceConfigurator implements ManagedService{
	private ClientDataSource dataSource;

	public void setDataSource(ClientDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties == null) return;
		if (dataSource != null){
			dataSource.setServerName(getString(properties, "datasource.server"));
			dataSource.setPortNumber(getInt(properties, "datasource.port"));
			dataSource.setDatabaseName(getString(properties, "datasource.database"));
			dataSource.setUser(getString(properties, "datasource.user"));
			dataSource.setPassword(getString(properties, "datasource.password"));
		}
	}

	@SuppressWarnings("rawtypes")
    private String getString(Dictionary properties, String key) {
        Object value = properties.get(key);
        return (!(value instanceof String)) ? "" : (String)value;
    }

	@SuppressWarnings("rawtypes")
    private int getInt(Dictionary properties, String key) {
        return Integer.parseInt(properties.get(key).toString());
    }
}
