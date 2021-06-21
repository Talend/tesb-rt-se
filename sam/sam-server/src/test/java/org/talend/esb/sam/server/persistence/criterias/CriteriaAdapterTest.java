/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.sam.service.criterias;

import java.util.HashMap;
import java.util.Map;

import org.talend.esb.sam.server.persistence.criterias.CriteriaAdapter;

import junit.framework.TestCase;

public class CriteriaAdapterTest extends TestCase {

	public void testWhereClause() throws Exception {
		Map<String, String[]> params = new HashMap<String, String[]>();
		assertEquals("", getWhereClause(params));
		params.put("ignore", new String[] {"ha ha"});
		assertEquals("", getWhereClause(params));
		params.put("port", new String[] {"port*"});
		assertEquals("(MI_PORT_TYPE LIKE :port)", getWhereClause(params));
		params.put("operation", new String[] {"blah"});
		assertEquals("(MI_OPERATION_NAME LIKE :operation) AND (MI_PORT_TYPE LIKE :port)", getWhereClause(params));
	}

	public void testSQLValuesProvider() throws Exception {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("ignore", new String[] {"ha ha"});
		params.put("port", new String[] {"port*"});
		params.put("operation", new String[] {"blah"});
		CriteriaAdapter adapter = new CriteriaAdapter(0, 100, params);
		assertTrue(adapter.hasValue("limit") && adapter.hasValue("offset"));
		assertTrue(adapter.hasValue("port") && adapter.hasValue("operation"));
	}

	private String getWhereClause(Map<String, String[]> params) {
		return new CriteriaAdapter(0, 100, params).getWhereClause();
	}

}
