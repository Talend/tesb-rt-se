package org.talend.esb.sam.server;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.esb.sam.server.persistence.DBInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class DBInitializerTest {

    private static EmbeddedDataSource ds;

    @BeforeClass
    public static void init() {
        System.setProperty("derby.stream.error.file", "target/derby.log");
        try {
            ds = new EmbeddedDataSource();
            ds.setDatabaseName("memory:myDB;create=true");
            ds.setCreateDatabase("create");
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }
    }

    @AfterClass
    public static void shutdown() {
        System.clearProperty("derby.stream.error.file");
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (Throwable t) {}
    }

    @Test
    public void  testInitialize() {
        DBInitializer dbi = new DBInitializer();
        dbi.setDataSource(ds);
        dbi.setDialect("derbyDialect");
        try {
            dbi.afterPropertiesSet();
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }

        try {
            Connection conn = ds.getConnection();
            String query = "SELECT * FROM events";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeQuery();
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }
    }
}