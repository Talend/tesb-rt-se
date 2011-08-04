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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.esb.sam.common.event.EventTypeEnum;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * A utility class to extract complex methods
 * to simplify testing
 *
 * @author zubairov
 *
 */
public class UIProviderUtils {

	private JsonParser parser = new JsonParser();

	private Gson gson = new Gson();

	public JsonArray aggregateFlowDetails(List<JsonObject> objects, String baseURL) {
		Map<Long, Map<String, String>> customInfo = new HashMap<Long, Map<String, String>>();
		Set<Long> allEvents = new HashSet<Long>();
		for (JsonObject obj : objects) {
			long eventID = obj.get("id").getAsLong();
			allEvents.add(eventID);
			String custKey = obj.get("custKey").isJsonNull() ? null : obj.get("custKey").getAsString();
			String custValue = obj.get("custValue").isJsonNull() ? null : obj.get("custValue").getAsString();
			if (custKey != null) {
				if (!customInfo.containsKey(eventID)) {
					customInfo.put(eventID, new HashMap<String, String>());
				}
				customInfo.get(eventID).put(custKey, custValue);
			}
		}
		JsonArray result = new JsonArray();
		for (JsonObject obj : objects) {
			long eventID = obj.get("id").getAsLong();
			if (allEvents.contains(eventID)) {
				allEvents.remove(eventID);
				JsonObject newObj = copy(obj);
				if (customInfo.containsKey(eventID)) {
					newObj.add("customInfo", gson.toJsonTree(customInfo.get(eventID)));
				}
				newObj.remove("custKey");
				newObj.remove("custValue");

				newObj.add("details", new JsonPrimitive(baseURL + "event/" + newObj.get("id")));
				result.add(newObj);
			}
		}

		return result;
	}

	public JsonArray aggregateRawData(List<JsonObject> objects, String baseURL) {
		// Render RAW data
		Map<String, Long> flowLastTimestamp = new HashMap<String, Long>();
		Map<String, String> flowProviderIP = new HashMap<String, String>();
		Map<String, String> flowProviderHost = new HashMap<String, String>();
		Map<String, String> flowConsumerIP = new HashMap<String, String>();
		Map<String, String> flowConsumerHost = new HashMap<String, String>();
		Map<String, Set<String>> flowTypes = new HashMap<String, Set<String>>();
		for (JsonObject obj : objects) {
			if (null == obj.get("flowID") || obj.get("flowID").isJsonNull()){
				continue;
			}
			String flowID = obj.get("flowID").getAsString();
			long timestamp = obj.get("timestamp").getAsLong();
			flowLastTimestamp.put(flowID, timestamp);
			if (!flowTypes.containsKey(flowID)) {
				flowTypes.put(flowID, new HashSet<String>());
			}
			String eventType = obj.get("type").getAsString();
			flowTypes.get(flowID).add(eventType);
			EventTypeEnum typeEnum = EventTypeEnum.valueOf(eventType);
			boolean isConsumer = typeEnum == EventTypeEnum.REQ_OUT || typeEnum == EventTypeEnum.RESP_IN;
			boolean isProvider = typeEnum == EventTypeEnum.REQ_IN || typeEnum == EventTypeEnum.RESP_OUT;
			String host = obj.get("host").getAsString();
			String ip = obj.get("ip").getAsString();
			if (isConsumer) {
				flowConsumerIP.put(flowID, ip);
				flowConsumerHost.put(flowID, host);
			}
			if (isProvider) {
				flowProviderIP.put(flowID, ip);
				flowProviderHost.put(flowID, host);
			}
		}
		JsonArray result = new JsonArray();
		for (JsonObject obj : objects) {
			if (null == obj.get("flowID") || obj.get("flowID").isJsonNull()){
				continue;
			}
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
				newObj.remove("host");
				newObj.remove("ip");
				if (flowConsumerHost.containsKey(flowID)) {
					newObj.add("consumer_host", new JsonPrimitive(flowConsumerHost.get(flowID)));
					newObj.add("consumer_ip", new JsonPrimitive(flowConsumerIP.get(flowID)));
				}
				if (flowProviderHost.containsKey(flowID)) {
					newObj.add("provider_host", new JsonPrimitive(flowProviderHost.get(flowID)));
					newObj.add("provider_ip", new JsonPrimitive(flowProviderIP.get(flowID)));
				}
				result.add(newObj);
			}
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
