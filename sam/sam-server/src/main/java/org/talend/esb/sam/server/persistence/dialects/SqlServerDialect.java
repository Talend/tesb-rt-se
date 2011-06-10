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
 * Class to encapsulate difference between databases
 *
 * @author pvasilchenko
 *
 */
public class SqlServerDialect extends AbstractDatabaseDialect {
	private static final String QUERY = "select "
	+ "[MI_FLOW_ID], [EI_TIMESTAMP], [EI_EVENT_TYPE], "
	+ "[MI_PORT_TYPE], [MI_OPERATION_NAME], [MI_TRANSPORT_TYPE], "
	+ "[ORIG_HOSTNAME], [ORIG_IP] "
	+ "FROM "
	+ "[EVENTS] "
	+ "WHERE "
	+ "[MI_FLOW_ID] in ( "
	+ "select [MI_FLOW_ID] from ( "
	+ "select [MI_FLOW_ID], rn from "
	+ "( "
	+ "select [MI_FLOW_ID], ROW_NUMBER() over(order by MAX([EI_TIMESTAMP]) DESC) as rn "
	+ "from [EVENTS] as subq "
	+ "%%FILTER%% "
	+ "group by [MI_FLOW_ID] "
	+ ") as subq1 where rn <= :offset + :limit "
	+ ") as subq2 where rn > :offset "
	+ ") "
	+ "order by [EI_TIMESTAMP] DESC";

	@Override
	public String getQuery() {
		return QUERY;
	}

}
