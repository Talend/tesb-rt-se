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

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Intelligent mapper that tries to read all fields
 * from {@link ResultSet}
 * 
 * @author zubairov
 *
 */
public class JsonRowMapper implements RowMapper<JsonObject>{

	private Map<String, String> nameMapping = new HashMap<String, String>(); 
	
	
	private RowMapper<Map<String, Object>> nestedMapper = new ColumnMapRowMapper();
	
	final Gson gson;
	
	public JsonRowMapper() {
		nameMapping.put("MI_FLOW_ID", "flowID");
		nameMapping.put("EI_TIMESTAMP", "timestamp");
		nameMapping.put("EI_EVENT_TYPE", "type");
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Timestamp.class, new JsonSerializer<Timestamp>() {

			@Override
			public JsonElement serialize(Timestamp src, Type typeOfSrc,
					JsonSerializationContext context) {
				return new JsonPrimitive(src.getTime());
			}
		});
		gson = builder.create();
	}
	
	@Override
	public JsonObject mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> map = nestedMapper.mapRow(rs, rowNum);
		
		JsonObject row = new JsonObject();
		for (String key : map.keySet()) {
			String jsonName = nameMapping.get(key);
			if (jsonName == null) {
				jsonName = key;
			}
			row.add(jsonName, gson.toJsonTree(map.get(key)));
		}
		return row;
	}

}
