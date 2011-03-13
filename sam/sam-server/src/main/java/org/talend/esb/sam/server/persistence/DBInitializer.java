package org.talend.esb.sam.server.persistence;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

public class DBInitializer implements InitializingBean {

    DataSource dataSource;
    boolean recreateDb;
    String createSql;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setRecreateDb(boolean recreateDb) {
        this.recreateDb = recreateDb;
    }

    public void setCreateSql(String createSql) {
		this.createSql = createSql;
	}

	@Override
    public void afterPropertiesSet() throws Exception {
        SimpleJdbcTemplate sjdbcTemplate = new SimpleJdbcTemplate(dataSource);
        if (recreateDb) {
        	Resource resource = new ClassPathResource(createSql);
        	SimpleJdbcTestUtils.executeSqlScript(sjdbcTemplate, resource, true);
        }
    }

}
