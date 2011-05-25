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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.talend.esb.sam.server.persistence.criterias.Criteria;
import org.talend.esb.sam.server.persistence.criterias.EnumCriteria;
import org.talend.esb.sam.server.persistence.criterias.FlowTypeEnum;
import org.talend.esb.sam.server.persistence.criterias.PatternCriteria;
import org.talend.esb.sam.server.persistence.dialects.QueryFilter;

/**
 * Adapter that implements {@link SqlParameterSource} to be used to map HTTP URL
 * parameters to the SQL parameters
 * 
 * @author zubairov
 */
public class CriteriaAdapter implements SqlParameterSource, QueryFilter {

	private Logger log = LoggerFactory.getLogger(UIProviderImpl.class);

	private final Map<String, Criteria> criterias;

	private long start;

	private long limit;

	private static final Criteria[] FILTER_CRITERIAS = {
			new EnumCriteria("type", "EI_EVENT_TYPE", FlowTypeEnum.class),
			new PatternCriteria("transport", "MI_TRANSPORT_TYPE"),
			new PatternCriteria("port", "MI_PORT_TYPE"),
			new PatternCriteria("operation", "MI_OPERATION_NAME") };

	private static final String LIMIT_NAME = "limit";

	private static final String START_NAME = "start";

	public CriteriaAdapter(long start, long limit, Map<String, String> params) {
		this.start = start;
		this.limit = limit;
		this.criterias = getCriterias(params);

	}

	/**
	 * Reads filter parameters
	 * 
	 * @param req
	 * @return
	 */
	private Map<String, Criteria> getCriterias(Map<String, String> params) {
		Map<String, Criteria> result = new HashMap<String, Criteria>();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			for (Criteria criteria : FILTER_CRITERIAS) {
				if (criteria.getName().equals(key)) {
					try {
						String value = params.get(key);
						result.put(key, criteria.parseValue(value));
					} catch (Exception e) {
						// Exception happened during paring
						log.error("Error parsing parameter " + key, e);
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean hasValue(String paramName) {
		return criterias.containsKey(paramName) || LIMIT_NAME.equals(paramName)
				|| START_NAME.equals(paramName);
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		if (!hasValue(paramName)) {
			throw new IllegalArgumentException("Can't find criteria with name "
					+ paramName);
		}
		if (LIMIT_NAME.equals(paramName)) {
			return limit;
		}
		if (START_NAME.equals(paramName)) {
			return start;
		}
		return criterias.get(paramName).getValue();
	}

	@Override
	public int getSqlType(String paramName) {
		if (!hasValue(paramName)) {
			return TYPE_UNKNOWN;
		}
		Object value = getValue(paramName);
		return StatementCreatorUtils.javaTypeToSqlParameterType(value.getClass());
	}

	@Override
	public String getTypeName(String paramName) {
		return null;
	}

	@Override
	public String getWhereClause() {
		return "";
	}

}
