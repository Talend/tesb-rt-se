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

import static org.talend.esb.sam.server.persistence.dialects.DatabaseDialect.SUBSTITUTION_STRING;

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

	private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS %%FILTER%%";

	private static final String SELECT_FLOW_QUERY = "select "
			+ "ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
			+ "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
			+ "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, MESSAGE_CONTENT "
			+ "from EVENTS where MI_FLOW_ID = :flowID";

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

	private RowMapper<JsonObject> mapper = new JsonRowMapper(); 
	
	@Override
	public JsonObject getEvents(long start, String baseURL,
			CriteriaAdapter criteria) {
		String countQuery = COUNT_QUERY;
		String whereClause = criteria.getWhereClause();
		if (whereClause != null && whereClause.length() > 0) {
			countQuery = countQuery.replaceAll(SUBSTITUTION_STRING, " WHERE " + whereClause);	
		} else {
			countQuery = countQuery.replaceAll(SUBSTITUTION_STRING, "");
		}
		int rowCount = getSimpleJdbcTemplate().queryForInt(countQuery, criteria);
		JsonObject result = new JsonObject();
		result.add("count", new JsonPrimitive(rowCount));
		if (start < rowCount) {
			String dataQuery = dialect.getDataQuery(criteria);
			List<JsonObject> objects = getSimpleJdbcTemplate().query(dataQuery,
					mapper, criteria);

			// Render RAW data
			Map<String, Long> flowLastTimestamp = new HashMap<String, Long>();
			Map<String, Set<String>> flowTypes = new HashMap<String, Set<String>>();
			for (JsonObject obj : objects) {
				String flowID = obj.get("flowID").getAsString();
				long timestamp = obj.get("timestamp").getAsLong();
				flowLastTimestamp.put(flowID, timestamp);
				if (!flowTypes.containsKey(flowID)) {
					flowTypes.put(flowID, new HashSet<String>());
				}
				flowTypes.get(flowID).add(obj.get("type").getAsString());
			}

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
							new JsonPrimitive(timestamp - endTime));
					newObj.remove("type");
					newObj.add("types", gson.toJsonTree(flowTypes.get(flowID)));
					newObj.add("details", new JsonPrimitive(baseURL + "flow/"
							+ flowID));
					aggregated.add(newObj);
				}
			}
			result.add("aggregated", aggregated);
		}
		return result;
	}

	@Override
	public JsonObject getFlowDetails(String flowID) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("flowID", flowID);
		JsonObject result = new JsonObject();
		JsonArray events = new JsonArray();
		List<JsonObject> list = getSimpleJdbcTemplate().query(
				SELECT_FLOW_QUERY, mapper, params);
		if (list.isEmpty()) {
			return null;
		} else {
			for (JsonObject obj : list) {
				events.add(obj);
			}
			result.add("events", events);
			return result;
		}
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
