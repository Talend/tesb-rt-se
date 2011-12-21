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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;

/**
 * Default implementation of {@link UIProvider} based on.
 *
 * {@link SimpleJdbcDaoSupport}
 * @author zubairov
 */
public class UIProviderImpl extends SimpleJdbcDaoSupport implements UIProvider {

    private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS " +
            DatabaseDialect.SUBSTITUTION_STRING;

    private static final String SELECT_FLOW_QUERY = "select "
            + "EVENTS.ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
            + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
            + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, "
            + "CUST_KEY, CUST_VALUE "
            + "from EVENTS "
            + "left join EVENTS_CUSTOMINFO on EVENTS_CUSTOMINFO.EVENT_ID = EVENTS.ID "
            + "where MI_FLOW_ID = :flowID";

    private static final String SELECT_EVENT_QUERY = "select "
        + "ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
        + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
        + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, MESSAGE_CONTENT "
        + "from EVENTS where ID = :eventID";

    private DatabaseDialect dialect;

    private final RowMapper<JsonObject> mapper = new JsonRowMapper();

    private final UIProviderUtils utils = new UIProviderUtils();

    /**
     * Injector method for {@link DatabaseDialect}.
     *
     * @param dialect the new dialect
     */
    public void setDialect(DatabaseDialect dialect) {
        this.dialect = dialect;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.ui.UIProvider#getEvents(long, java.lang.String, org.talend.esb.sam.server.ui.CriteriaAdapter)
     */
    @Override
    public JsonObject getEvents(long offset, String baseURL,
            CriteriaAdapter criteria) {
        final String whereClause = criteria.getWhereClause();
        final String countQuery = COUNT_QUERY.replaceAll(DatabaseDialect.SUBSTITUTION_STRING,
                (whereClause != null && whereClause.length() > 0) ? " WHERE " + whereClause : "");
        int rowCount = getSimpleJdbcTemplate().queryForInt(countQuery, criteria);
        JsonObject result = new JsonObject();
        result.add("count", new JsonPrimitive(rowCount));
        // Aggregated data
        JsonArray aggregated = new JsonArray();
        if (offset < rowCount) {
            String dataQuery = dialect.getDataQuery(criteria);
            List<JsonObject> objects = getSimpleJdbcTemplate().query(dataQuery,
                    mapper, criteria);
            aggregated = utils.aggregateRawData(objects, baseURL);
        }
        result.add("aggregated", aggregated);
        return result;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.ui.UIProvider#getFlowDetails(java.lang.String, java.lang.String)
     */
    @Override
    public JsonObject getFlowDetails(String flowID, String baseURL) {
        List<JsonObject> list = getSimpleJdbcTemplate().query(
                SELECT_FLOW_QUERY, mapper, Collections.singletonMap("flowID", flowID));
        if (list.isEmpty()) {
            return null;
        } else {
            JsonObject result = new JsonObject();
            result.add("events", utils.aggregateFlowDetails(list, baseURL));
            return result;
        }
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.ui.UIProvider#getEventDetails(java.lang.String)
     */
    @Override
    public JsonObject getEventDetails(String eventID) {
        return getSimpleJdbcTemplate().queryForObject(
                SELECT_EVENT_QUERY, mapper, Collections.singletonMap("eventID", eventID));
    }

}
