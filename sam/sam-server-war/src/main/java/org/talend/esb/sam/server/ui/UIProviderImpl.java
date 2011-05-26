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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Default implementation of {@link UIProvider} based on
 * {@link SimpleJdbcDaoSupport}
 * 
 * @author zubairov
 * 
 */
public class UIProviderImpl extends SimpleJdbcDaoSupport implements UIProvider {

	private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS";

	private JsonParser parser = new JsonParser();

	private Gson gson = new Gson();

	private DatabaseDialect dialect;
	
	/**
	 * Injector method for {@link DatabaseDialect}
	 * 
	 * @param dialect
	 */
	public void setDialect(DatabaseDialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Result mapper
	 * 
	 * @author zubairov
	 * 
	 */
	private class Mapper implements RowMapper<JsonObject> {

		@Override
		public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
			JsonObject row = new JsonObject();
			row.add("flowID", new JsonPrimitive(rs.getString("MI_FLOW_ID")));
			row.add("timestamp",
					new JsonPrimitive(rs.getTimestamp("EI_TIMESTAMP").getTime()));
			row.add("type", new JsonPrimitive(rs.getString("EI_EVENT_TYPE")));
			row.add("port", new JsonPrimitive(rs.getString("MI_PORT_TYPE")));
			row.add("operation",
					new JsonPrimitive(rs.getString("MI_OPERATION_NAME")));
			row.add("transport",
					new JsonPrimitive(rs.getString("MI_TRANSPORT_TYPE")));
			row.add("host", new JsonPrimitive(rs.getString("ORIG_HOSTNAME")));
			row.add("ip", new JsonPrimitive(rs.getString("ORIG_IP")));
			return row;
		}

	}

	@Override
	public JsonObject getEvents(long start, String baseURL, CriteriaAdapter criteria) {
		int rowCount = getSimpleJdbcTemplate().queryForInt(COUNT_QUERY);
		JsonObject result = new JsonObject();
		result.add("count", new JsonPrimitive(rowCount));
		if (start < rowCount) {
			String dataQuery = dialect.getDataQuery(criteria);
			List<JsonObject> objects = getSimpleJdbcTemplate().query(dataQuery, new Mapper(), criteria);
			
			// Render RAW data
			Map<String, Long> flowLastTimestamp = new HashMap<String, Long>();
			Map<String, Set<String>> flowTypes = new HashMap<String, Set<String>>();
			JsonArray rawData = new JsonArray();
			for (JsonObject obj : objects) {
				String flowID = obj.get("flowID").getAsString();
				long timestamp = obj.get("timestamp").getAsLong();
				rawData.add(copy(obj));
				flowLastTimestamp.put(flowID, timestamp);
				if (!flowTypes.containsKey(flowID)) {
					flowTypes.put(flowID, new HashSet<String>());
				}
				flowTypes.get(flowID).add(obj.get("type").getAsString());
			}
			result.add("raw", rawData);

			// Aggregated data
			JsonArray aggregated = new JsonArray();
			for (JsonObject obj : objects) {
				String flowID = obj.get("flowID").getAsString();
				long timestamp = obj.get("timestamp").getAsLong();
				Long endTime = flowLastTimestamp.get(flowID);
				if (endTime != null) {
					flowLastTimestamp.remove(flowID);
					JsonObject newObj = copy(obj);
					newObj.add("elapsed",
							new JsonPrimitive(endTime - timestamp));
					newObj.remove("type");
					newObj.add("types", gson.toJsonTree(flowTypes.get(flowID)));
					newObj.add("details", new JsonPrimitive(baseURL + "flow/" + flowID));
					aggregated.add(newObj);
				}
			}
			result.add("aggregated", aggregated);
		}
		return result;
	}

	/**
	 * Creates a copy of {@link JsonObject}
	 * 
	 * @param obj
	 * @return
	 */
	private JsonObject copy(JsonObject obj) {
		return (JsonObject) parser.parse(obj.toString());
	}

}
