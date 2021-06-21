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
package org.talend.esb.sam.server.persistence.criterias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.talend.esb.sam.server.persistence.dialects.QueryFilter;

/**
 * Adapter that implements {@link SqlParameterSource} to be used to map HTTP URL
 * parameters to the SQL parameters.
 *
 * @author zubairov
 */
public class CriteriaAdapter implements SqlParameterSource, QueryFilter {

    private static final Logger LOG = Logger.getLogger(CriteriaAdapter.class.getName());

    private static final String CONSUMER_EVENT_TYPES =
            "(EI_EVENT_TYPE = 'REQ_OUT' or EI_EVENT_TYPE = 'RESP_IN')";

    private static final String PROVIDER_EVENT_TYPES =
            "(EI_EVENT_TYPE = 'REQ_IN' or EI_EVENT_TYPE = 'RESP_OUT')";

    private static final Criteria[] FILTER_CRITERIAS = {
        new PatternCriteria("transport", "MI_TRANSPORT_TYPE"),
        new PatternCriteria("port", "MI_PORT_TYPE"),
        new PatternCriteria("operation", "MI_OPERATION_NAME"),
        new DateCriteria("timestamp_before", "EI_TIMESTAMP"),
        new DateCriteria("timestamp_after", "EI_TIMESTAMP"),
        new DateCriteria("timestamp_on", "EI_TIMESTAMP"),
        new PatternCriteria("flowID", "MI_FLOW_ID"),
        new PatternCriteria("consumerIP", "ORIG_IP", CONSUMER_EVENT_TYPES),
        new PatternCriteria("consumerHost", "ORIG_HOSTNAME", CONSUMER_EVENT_TYPES),
        new PatternCriteria("providerIP", "ORIG_IP", PROVIDER_EVENT_TYPES),
        new PatternCriteria("providerHost", "ORIG_HOSTNAME", PROVIDER_EVENT_TYPES)
    };

    private static final String LIMIT_NAME = "limit";

    private static final String OFFSET_NAME = "offset";

    private final Map<String, Criteria> criterias;

    private final long offset;

    private final long limit;

    /**
     * Instantiates a new criteria adapter.
     *
     * @param offset the offset
     * @param limit the limit
     * @param params the CriteriaAdapter params
     */
    public CriteriaAdapter(long offset, long limit, Map<String, String[]> params) {
        this.offset = offset;
        this.limit = limit;
        this.criterias = getCriterias(params);
    }

    /**
     * Reads filter parameters.
     *
     * @param params the params
     * @return the criterias
     */
    private Map<String, Criteria> getCriterias(Map<String, String[]> params) {
        Map<String, Criteria> result = new HashMap<String, Criteria>();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            for (Criteria criteria : FILTER_CRITERIAS) {
                if (criteria.getName().equals(param.getKey())) {
                    try {
                        Criteria[] parsedCriterias = criteria.parseValue(param.getValue()[0]);
                        for (Criteria parsedCriteria : parsedCriterias) {
                            result.put(parsedCriteria.getName(), parsedCriteria);
                        }
                    } catch (Exception e) {
                        // Exception happened during paring
                        LOG.log(Level.SEVERE, "Error parsing parameter " + param.getKey(), e);
                    }
                    break;
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.namedparam.SqlParameterSource#hasValue(java.lang.String)
     */
    @Override
    public boolean hasValue(String paramName) {
        return criterias.containsKey(paramName) || LIMIT_NAME.equals(paramName)
                || OFFSET_NAME.equals(paramName);
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.namedparam.SqlParameterSource#getValue(java.lang.String)
     */
    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        if (!hasValue(paramName)) {
            throw new IllegalArgumentException("Can't find criteria with name " + paramName);
        }
        if (LIMIT_NAME.equals(paramName)) {
            return limit;
        }
        if (OFFSET_NAME.equals(paramName)) {
            return offset;
        }
        return criterias.get(paramName).getValue();
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.namedparam.SqlParameterSource#getSqlType(java.lang.String)
     */
    @Override
    public int getSqlType(String paramName) {
        if (!hasValue(paramName)) {
            return TYPE_UNKNOWN;
        }
        Object value = getValue(paramName);
        return StatementCreatorUtils.javaTypeToSqlParameterType(value.getClass());
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.namedparam.SqlParameterSource#getTypeName(java.lang.String)
     */
    @Override
    public String getTypeName(String paramName) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.dialects.QueryFilter#getWhereClause()
     */
    @Override
    public String getWhereClause() {
        StringBuilder result = new StringBuilder();
        List<String> names = new ArrayList<String>(criterias.keySet());
        Collections.sort(names);
        for (String key : names) {
            Criteria criteria = criterias.get(key);
            if (result.length() > 0) {
                result.append(" AND ");
            }
            result.append('(').append(criteria.getFilterClause()).append(')');
        }
        return result.toString();
    }

}
