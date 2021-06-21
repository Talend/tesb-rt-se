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

/**
 * Criteria for patterns.
 *
 * @author zubairov
 */
public class PatternCriteria extends Criteria {

    private String pattern;

    private String condition;

    /**
     * Instantiates a new pattern criteria.
     *
     * @param name the name
     * @param colunmName the column name
     */
    public PatternCriteria(String name, String colunmName) {
        super(name, colunmName);
    }

    /**
     * Conditional pattern criteria.
     *
     * @param name the name
     * @param columnName the column name
     * @param condition condition that will be concatenated to the pattern condition
     */
    public PatternCriteria(String name, String columnName, String condition) {
        super(name, columnName);
        this.condition = condition;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#parseValue(java.lang.String)
     */
    @Override
    public Criteria[] parseValue(String attribute) {
        PatternCriteria result = new PatternCriteria(this.name, this.columnName, this.condition);
        result.pattern = toSQLPattern(attribute);
        return new Criteria[] {result};
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getValue()
     */
    @Override
    public Object getValue() {
        return pattern;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getFilterClause()
     */
    @Override
    public StringBuilder getFilterClause() {
        StringBuilder builder = new StringBuilder();
        builder.append(columnName);
        builder.append(" LIKE ");
        builder.append(':').append(name);
        if (condition != null) {
            builder.append(" AND ");
            builder.append(condition);
        }
        return builder;
    }

    /**
     * To sql pattern.
     *
     * @param attribute the attribute
     * @return the string
     */
    private String toSQLPattern(String attribute) {
        String pattern = attribute.replace("*", "%");
        if (!pattern.startsWith("%")) {
            pattern = "%" + pattern;
        }
        if (!pattern.endsWith("%")) {
            pattern = pattern.concat("%");
        }
        return pattern;
    }

}
