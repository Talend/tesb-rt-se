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

import java.sql.Timestamp;

/**
 * Criteria for date values.
 *
 * @author zubairov
 */
public class DateCriteria extends Criteria {

    /**
     * Number of milliseconds in day without one millisecond
     */
    private static final long MILLS_IN_DAY = (24 * 60 * 60 * 1000) - 1;

    private Timestamp value;

    /**
     * Instantiates a new date criteria.
     *
     * @param name the name
     * @param colunmName the colunm name
     */
    public DateCriteria(String name, String colunmName) {
        super(name, colunmName);
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#parseValue(java.lang.String)
     */
    @Override
    public Criteria[] parseValue(String attribute) {
        long attributeValue = Long.parseLong(attribute);
        if (name.endsWith("_on")) {
            // We have timestamp_on case
            DateCriteria after = new DateCriteria(name + "_after", columnName);
            after.value = new Timestamp(attributeValue - MILLS_IN_DAY/2);
            DateCriteria before = new DateCriteria(name + "_before", columnName);
            before.value = new Timestamp(attributeValue + MILLS_IN_DAY/2);
            return new Criteria[] {after, before};
        } else {
            DateCriteria result = new DateCriteria(name, columnName);
            result.value = new Timestamp(attributeValue);
            return new Criteria[] {result};
        }
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getValue()
     */
    @Override
    public Object getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getFilterClause()
     */
    @Override
    public StringBuilder getFilterClause() {
        StringBuilder builder = new StringBuilder();
        builder.append(columnName);
        if (name.lastIndexOf('_') > 0) {
            String suffix = name.substring(name.lastIndexOf('_') + 1);
            if ("before".equals(suffix)) {
                builder.append(" < ");
            } else if ("after".equals(suffix)) {
                builder.append(" > ");
            } else {
                builder.append(" = ");
            }
        } else {
            builder.append(" = ");
        }
        builder.append(':').append(name);
        return builder;
    }

}
