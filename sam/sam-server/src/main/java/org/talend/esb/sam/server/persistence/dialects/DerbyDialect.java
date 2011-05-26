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
package org.talend.esb.sam.server.persistence.dialects;

/**
 * Class to encapsulate difference between databases.
 * 
 * @author zubairov
 * 
 */
public class DerbyDialect extends AbstractDatabaseDialect {

	private static final String SUBSTITUTION_STRING = "%%FILTER%%";

	private static final String QUERY = "select "
			+ "MI_FLOW_ID, EI_TIMESTAMP, EI_EVENT_TYPE, "
			+ "MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, "
			+ "ORIG_HOSTNAME,  ORIG_IP "
			+ "from "
			+ "EVENTS "
			+ "where "
			+ "MI_FLOW_ID in ("
			+ "select MI_FLOW_ID from EVENTS %%FILTER%% group by MI_FLOW_ID order by MIN(EI_TIMESTAMP) OFFSET :start ROWS FETCH FIRST :limit ROWS ONLY"
			+ ") order by EI_TIMESTAMP";

	@Override
	public String getDataQuery(QueryFilter filter) {
		String whereClause = filter.getWhereClause();
		String result = null;
		if (whereClause != null && whereClause.length() > 0) {
			result = QUERY.replaceAll(SUBSTITUTION_STRING, " WHERE " + whereClause);	
		} else {
			result = QUERY.replaceAll(SUBSTITUTION_STRING, "");
		}
		return result;
	}

}
