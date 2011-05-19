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
package org.talend.esb.sam.server.ui;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Default implementation of {@link UIProvider} based on {@link SimpleJdbcDaoSupport}
 * 
 * @author zubairov
 *
 */
public class UIProviderImpl extends SimpleJdbcDaoSupport implements UIProvider {

	@Override
	public JsonObject getEvents() throws SQLException {
		int rowCount = getJdbcTemplate().queryForInt("select count(*) from EVENTS");
		JsonObject obj = new JsonObject();
		obj.add("message", new JsonPrimitive("Spring sucks!"));
		obj.add("numberOfRows", new JsonPrimitive(rowCount));
		DatabaseMetaData metaData = getDataSource().getConnection().getMetaData();
		obj.add("db", new JsonPrimitive(metaData.getDatabaseProductName()));
		return obj;
	}
	
}
