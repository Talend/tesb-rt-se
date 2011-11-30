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

public class OracleDialect extends AbstractDatabaseDialect{

    private static final String QUERY = "select "
        + "MI_FLOW_ID, EI_TIMESTAMP, EI_EVENT_TYPE, "
        + "MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, "
        + "ORIG_HOSTNAME,  ORIG_IP "
        + "from "
        + "EVENTS "
        + "where "
        + "MI_FLOW_ID in ("
        + "select MI_FLOW_ID from ("
        + "select E.MI_FLOW_ID, rownum rn from (select MI_FLOW_ID from EVENTS %%FILTER%% group by MI_FLOW_ID "
        + "order by MAX(EI_TIMESTAMP) DESC) E WHERE rownum <= (:offset + :limit)"
        + ") where rn > :offset "
        + ") order by EI_TIMESTAMP DESC";

    @Override
    String getQuery() {
        return QUERY;
    }

}
