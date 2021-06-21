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
 * Criteria based on enumeration.
 *
 * @author zubairov
 */
@SuppressWarnings("rawtypes")
public class EnumCriteria extends Criteria {

    private Class<? extends Enum> enumClass;

    protected Enum<?> value;

    /**
     * Instantiates a new enum criteria.
     *
     * @param name the name
     * @param colunmName the column name
     * @param clazz the clazz
     */
    public EnumCriteria(String name, String colunmName, Class<? extends Enum> clazz) {
        super(name, colunmName);
        this.enumClass = clazz;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#parseValue(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Criteria[] parseValue(String value) {
        EnumCriteria result = new EnumCriteria(this.name, this.columnName, this.enumClass);
        result.value = Enum.valueOf(enumClass, value);
        return new Criteria[] {result};
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getValue()
     */
    @Override
    public Object getValue() {
        return String.valueOf(value);
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.criterias.Criteria#getFilterClause()
     */
    @Override
    public StringBuilder getFilterClause() {
        StringBuilder builder = new StringBuilder();
        builder.append(columnName);
        builder.append(" =");
        builder.append(':').append(name);
        return builder;
    }

}
