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

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

/**
 * Class to make Spring spaghetti.
 * 
 * @author zubairov
 *
 */
public abstract class AbstractDatabaseDialect implements DatabaseDialect {

    private DataFieldMaxValueIncrementer incrementer;

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.dialects.DatabaseDialect#getIncrementer()
     */
    @Override
    public DataFieldMaxValueIncrementer getIncrementer() {
        return incrementer;
    }

    /**
     * Injector method for Spring.
     *
     * @param incrementer the new incrementer
     */
    public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
        this.incrementer = incrementer;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.server.persistence.dialects.DatabaseDialect#getDataQuery(org.talend.esb.sam.server.persistence.dialects.QueryFilter)
     */
    @Override
    public String getDataQuery(QueryFilter filter) {
        String query = getQuery();
        String whereClause = filter.getWhereClause();
        String result = null;
        if (whereClause != null && whereClause.length() > 0) {
            result = query.replaceAll(SUBSTITUTION_STRING, " AND " + whereClause);	
        } else {
            result = query.replaceAll(SUBSTITUTION_STRING, "");
        }
        return result;
    }

    /**
     * This method should return a query string with {@link #SUBSTITUTION_STRING} placeholder
     * for where clause.
     *
     * @return the query
     */
    abstract String getQuery();

}
