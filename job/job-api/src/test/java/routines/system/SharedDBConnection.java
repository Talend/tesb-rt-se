/*
 * #%L
 * Talend :: ESB :: Job :: API
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
// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package routines.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A buffer to keep all the DB connections, make it reusable between the different jobs.
 */
public class SharedDBConnection {

    private static boolean DEBUG = false;

    private static SharedDBConnection instance = null;

    private Map<String, Connection> sharedConnections = new HashMap<String, java.sql.Connection>();

    private SharedDBConnection() {

    }

    private static synchronized SharedDBConnection getInstance() {
        if (instance == null) {
            instance = new SharedDBConnection();
        }
        return instance;
    }

    private synchronized Connection getConnection(String dbDriver, String url, String userName, String password,
            String dbConnectionName) throws ClassNotFoundException, SQLException {

        if (DEBUG) {
            Set<String> keySet = sharedConnections.keySet();
            System.out.print("SharedDBConnection, current shared connections list is:"); //$NON-NLS-1$
            for (String key : keySet) {
                System.out.print(" " + key); //$NON-NLS-1$
            }
            System.out.println();
        }

        Connection connection = sharedConnections.get(dbConnectionName);
        if (connection == null) {
            if (DEBUG) {
                System.out.println("SharedDBConnection, can't find the key:" + dbConnectionName + " " //$NON-NLS-1$ //$NON-NLS-2$
                        + "so create a new one and share it."); //$NON-NLS-1$
            }
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(url, userName, password);
            sharedConnections.put(dbConnectionName, connection);
        } else if (connection.isClosed()) {
            if (DEBUG) {
                System.out.println("SharedDBConnection, find the key: " + dbConnectionName + " " //$NON-NLS-1$ //$NON-NLS-2$
                        + "But it is closed. So create a new one and share it."); //$NON-NLS-1$
            }
            connection = DriverManager.getConnection(url, userName, password);
            sharedConnections.put(dbConnectionName, connection);
        } else {
            if (DEBUG) {
                System.out.println("SharedDBConnection, find the key: " + dbConnectionName + " " + "it is OK."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return connection;
    }

    private synchronized Connection getConnection(String dbDriver, String url, String dbConnectionName)
            throws ClassNotFoundException, SQLException {

        if (DEBUG) {
            Set<String> keySet = sharedConnections.keySet();
            System.out.print("SharedDBConnection, current shared connections list is:"); //$NON-NLS-1$
            for (String key : keySet) {
                System.out.print(" " + key); //$NON-NLS-1$
            }
            System.out.println();
        }

        Connection connection = sharedConnections.get(dbConnectionName);
        if (connection == null) {
            if (DEBUG) {
                System.out.println("SharedDBConnection, can't find the key:" + dbConnectionName + " " //$NON-NLS-1$ //$NON-NLS-2$
                        + "so create a new one and share it."); //$NON-NLS-1$
            }
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(url);
            sharedConnections.put(dbConnectionName, connection);
        } else if (connection.isClosed()) {
            if (DEBUG) {
                System.out.println("SharedDBConnection, find the key: " + dbConnectionName + " " //$NON-NLS-1$ //$NON-NLS-2$
                        + "But it is closed. So create a new one and share it."); //$NON-NLS-1$
            }
            connection = DriverManager.getConnection(url);
            sharedConnections.put(dbConnectionName, connection);
        } else {
            if (DEBUG) {
                System.out.println("SharedDBConnection, find the key: " + dbConnectionName + " " + "it is OK."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return connection;
    }

    /**
     * If there don't exist the connection or it is closed, create and store it.
     * 
     * @param dbDriver
     * @param url
     * @param userName
     * @param password
     * @param dbConnectionName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getDBConnection(String dbDriver, String url, String userName, String password,
            String dbConnectionName) throws ClassNotFoundException, SQLException {
        SharedDBConnection instanceLocal = getInstance();
        Connection connection = instanceLocal.getConnection(dbDriver, url, userName, password, dbConnectionName);
        return connection;
    }

    /**
     * If there don't exist the connection or it is closed, create and store it.
     * 
     * @param dbDriver
     * @param url
     * @param dbConnectionName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getDBConnection(String dbDriver, String url, String dbConnectionName) throws ClassNotFoundException,
            SQLException {
        SharedDBConnection instanceLocal = getInstance();
        Connection connection = instanceLocal.getConnection(dbDriver, url, dbConnectionName);
        return connection;
    }

    /**
     * Set the buffer as null, make it recyclable.
     */
    public static void clear() {
        instance = null;
    }

    public static void setDebugMode(boolean debug) {
        DEBUG = debug;
    }
}
