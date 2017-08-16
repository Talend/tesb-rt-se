package org.talend.esb.sam.server.persistence.dialects;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DerbyMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.SqlServerMaxValueIncrementer;

public class DialectFactory {

    private DerbyMaxValueIncrementer derbyIncrementer = new DerbyMaxValueIncrementer();

    private DB2SequenceMaxValueIncrementer db2Incrementer = new DB2SequenceMaxValueIncrementer();

    private MySQLMaxValueIncrementer mysqlIncrementer = new MySQLMaxValueIncrementer();

    private H2SequenceMaxValueIncrementer h2Incrementer = new H2SequenceMaxValueIncrementer();

    private OracleSequenceMaxValueIncrementer oracleIncrementer = new OracleSequenceMaxValueIncrementer();

    private SqlServerMaxValueIncrementer sqlServerIncrementer = new SqlServerMaxValueIncrementer();

    private PostgreSQLSequenceMaxValueIncrementer postgreSQLIncrementer = new PostgreSQLSequenceMaxValueIncrementer();

    private DatabaseDialect derbyDialect = new DerbyDialect();

    private DatabaseDialect db2Dialect = new DB2Dialect();

    private DatabaseDialect mysqlDialect = new MySQLDialect();

    private DatabaseDialect h2Dialect = new H2Dialect();

    private DatabaseDialect oracleDialect = new OracleDialect();

    private DatabaseDialect sqlServerDialect = new SqlServerDialect();

    private DatabaseDialect postgreSQLDialect = new PostgreSQLDialect();

    public DialectFactory(DataSource dataSource) {
        derbyIncrementer.setDataSource(dataSource);
        derbyIncrementer.setIncrementerName("TAB_SEQUENCE");
        derbyIncrementer.setColumnName("ID_VALUE");
        derbyDialect.setIncrementer(derbyIncrementer);

        db2Incrementer.setDataSource(dataSource);
        db2Incrementer.setIncrementerName("EVENT_ID");
        db2Dialect.setIncrementer(db2Incrementer);

        mysqlIncrementer.setDataSource(dataSource);
        mysqlIncrementer.setIncrementerName("SEQUENCE");
        mysqlIncrementer.setColumnName("SEQ_COUNT");
        mysqlDialect.setIncrementer(mysqlIncrementer);

        h2Incrementer.setDataSource(dataSource);
        h2Incrementer.setIncrementerName("ID_SEQUENCE");
        h2Dialect.setIncrementer(h2Incrementer);

        oracleIncrementer.setDataSource(dataSource);
        oracleIncrementer.setIncrementerName("EVENT_ID");
        oracleDialect.setIncrementer(oracleIncrementer);

        sqlServerIncrementer.setDataSource(dataSource);
        sqlServerIncrementer.setIncrementerName("SEQUENCE");
        sqlServerIncrementer.setColumnName("SEQ_COUNT");
        sqlServerDialect.setIncrementer(sqlServerIncrementer);

        postgreSQLIncrementer.setDataSource(dataSource);
        postgreSQLIncrementer.setIncrementerName("EVENT_ID");
        postgreSQLDialect.setIncrementer(postgreSQLIncrementer);
    }

    public DatabaseDialect getDialect(String dialectName) {
        if ("derbyDialect".equals(dialectName)) {
            return derbyDialect;
        } else if ("DB2Dialect".equals(dialectName)){
            return db2Dialect;
        } else if ("mysqlDialect".equals(dialectName)){
            return mysqlDialect;
        } else if ("h2Dialect".equals(dialectName)){
            return h2Dialect;
        } else if ("oracleDialect".equals(dialectName)){
            return oracleDialect;
        } else if ("sqlServerDialect".equals(dialectName)){
            return sqlServerDialect;
        } else if ("postgresqlDialect".equals(dialectName)){
            return postgreSQLDialect;
        } else {
            return null;
        }
    }

}
