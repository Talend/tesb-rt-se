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
package org.talend.esb.sam.server.persistence.dialects;

/**
 * Class to encapsulate difference between databases.
 *
 * @author zubairov
 *
 */
public class DerbyDialect extends AbstractDatabaseDialect {

    private static final String QUERY = "select "
            + "MI_FLOW_ID, EI_TIMESTAMP, EI_EVENT_TYPE, "
            + "MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, "
            + "ORIG_HOSTNAME,  ORIG_IP "
            + "from "
            + "EVENTS "
            + "where "
            + "MI_FLOW_ID in ("
            + "select MI_FLOW_ID from EVENTS WHERE (MI_FLOW_ID is not null) %%FILTER%% group by MI_FLOW_ID order by MAX(EI_TIMESTAMP) "
            + "DESC OFFSET :offset ROWS FETCH FIRST :limit ROWS ONLY"
            + ") order by EI_TIMESTAMP DESC";

/*    private static final String QUERY = "select MI_FLOW_ID, EI_TIMESTAMP, EI_EVENT_TYPE, "
            + "MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, "
            + "ORIG_HOSTNAME,  ORIG_IP "
            + "from "
            + "EVENTS E, "
            + "(select ID from FLOWS WHERE (ID is not null) "
            + "order by FI_TIMESTAMP DESC "
            + "OFFSET :offset ROWS FETCH FIRST :limit ROWS ONLY) F "
            + "where "
            + "E.MI_FLOW_ID = F.ID %%FILTER%%";*/

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.dialects.AbstractDatabaseDialect#getQuery()
     */
    @Override
    String getQuery() {
        return QUERY;
    }

	@Override
	public String getName() {
		return "derbyDialect";
	}
}
