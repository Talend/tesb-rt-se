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
package org.talend.esb.sam.server.ui.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.server.persistence.dialects.DerbyDialect;
import org.talend.esb.sam.server.ui.CriteriaAdapter;
import org.talend.esb.sam.server.ui.UIProviderImpl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UIProviderTest extends TestCase {

	String sampleOne = "{" + "flowID: 'flowID'," + "timestamp: 1305737271353,"
			+ "type: 'REQ_OUT'" + "}";

	String sampleTwo = "{" + "flowID: 'flowID'," + "timestamp: 1305737271356,"
			+ "type: REQ_IN" + "}";

	List<JsonObject> objects = new ArrayList<JsonObject>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		JsonParser parser = new JsonParser();
		objects.add((JsonObject) parser.parse(sampleOne));
		objects.add((JsonObject) parser.parse(sampleTwo));
	}

	public void testEmptyCriterias() throws Exception {
		Capture<RowMapper<JsonObject>> mapper = new Capture<RowMapper<JsonObject>>();
		Capture<PreparedStatementCreator> creator = new Capture<PreparedStatementCreator>();
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("port", new String[] {"test"});
		JsonObject result = fetchResult(mapper, creator, params);
		@SuppressWarnings("unused")
		RowMapper<JsonObject> rowMapper = mapper.getValue();
		assertEquals(10, result.get("count").getAsInt());
		JsonArray aggregated = (JsonArray) result.get("aggregated");
		assertEquals(1, aggregated.size());
		JsonObject res = (JsonObject) aggregated.get(0);
		assertEquals(3, res.get("elapsed").getAsInt());
		assertEquals(2, res.get("types").getAsJsonArray().size());
		System.err.println(creator.getValue());
	}

	private JsonObject fetchResult(Capture<RowMapper<JsonObject>> mapper,
			Capture<PreparedStatementCreator> creator, Map<String, String[]> parameters) {
		UIProviderImpl provider = new UIProviderImpl();
		JdbcTemplate template = EasyMock.createMock(JdbcTemplate.class);
		provider.setJdbcTemplate(template);
		provider.setDialect(new DerbyDialect());

		// Expectations
		EasyMock.expect(template.queryForInt((String) EasyMock.anyObject()))
				.andReturn(10);
		EasyMock.expect(
				template.query(EasyMock.capture(creator),
						EasyMock.capture(mapper))).andReturn(
				objects);
		// Test
		EasyMock.replay(template);
		JsonObject result = provider.getEvents(0, "base", new CriteriaAdapter(0, 100,
				parameters));
		EasyMock.verify(template);
		return result;
	}

}
