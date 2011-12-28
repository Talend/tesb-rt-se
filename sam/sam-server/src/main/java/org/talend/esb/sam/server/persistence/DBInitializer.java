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
package org.talend.esb.sam.server.persistence;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

/**
 * The Class DBInitializer using for initializing persistence.
 */
public class DBInitializer implements InitializingBean {

	private static final Logger LOG = Logger.getLogger(DBInitializer.class.getName());
	
    private DataSource dataSource;
    private boolean recreateDb;
    private String createSql;

    /**
     * Sets the data source.
     *
     * @param dataSource the new data source
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sets the recreate db flag.
     *
     * @param recreateDb the recreateDb flag
     */
    public void setRecreateDb(boolean recreateDb) {
        this.recreateDb = recreateDb;
    }

    /**
     * Sets the sql.
     *
     * @param createSql the sql
     */
    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (recreateDb) {
        	if("create_oracle.sql".equals(createSql)) LOG.warning("Not recomended to use db.recreate=true parameter for Oracle database");
            Resource resource = new ClassPathResource(createSql);
            SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(dataSource), resource, true);
        }
    }

}
