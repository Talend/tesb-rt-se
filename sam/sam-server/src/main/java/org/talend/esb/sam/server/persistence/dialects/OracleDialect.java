/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
 * The Class OracleDialect.
 */
public class OracleDialect extends AbstractDatabaseDialect{

    private static final String QUERY = 
        "SELECT " + 
        "EVENTS.MI_FLOW_ID, EI_TIMESTAMP, EI_EVENT_TYPE, " + 
        "MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, " + 
        "ORIG_HOSTNAME,  ORIG_IP " +
        "FROM " +
            "EVENTS, " +
            "(SELECT MI_FLOW_ID FROM( " +
                "select MI_FLOW_ID, rownum as rn from( " +
                    "select MI_FLOW_ID, max(EI_TIMESTAMP) as TSTAMP from events where (MI_FLOW_ID is not null) %%FILTER%% group by MI_FLOW_ID order by max(EI_TIMESTAMP) desc " +
                 ") where rownum <= (%%LIMIT%% + %%OFFSET%%)) " +
                "WHERE rn > %%OFFSET%% ) SUBQ " +
        "WHERE EVENTS.MI_FLOW_ID = SUBQ.MI_FLOW_ID ORDER BY EI_TIMESTAMP DESC ";

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.dialects.AbstractDatabaseDialect#getQuery()
     */
    @Override
    String getQuery() {
        return QUERY;
    }

}
