package org.sopera.monitoring.server.persistence;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

public class DBInitializer implements InitializingBean {

    DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleJdbcTemplate sjdbcTemplate = new SimpleJdbcTemplate(dataSource);
        Resource resource = new ClassPathResource("create.sql");
        SimpleJdbcTestUtils.executeSqlScript(sjdbcTemplate, resource, true);
    }

}
