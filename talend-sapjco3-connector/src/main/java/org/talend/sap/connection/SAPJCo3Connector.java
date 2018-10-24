/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.talend.sap.connection;

import java.lang.reflect.Method;
import java.util.Properties;

public class SAPJCo3Connector {

    private String connectionPoolName;
    private Properties properties;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setConnectionPoolName(String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
    }

    public void init() throws Exception {
        Class cls = Class.forName("org.hibersap.execution.jco.JCoEnvironment");
        Method regMethod = cls.getMethod("registerDestination", String.class, Properties.class);
        regMethod.invoke(cls, connectionPoolName, properties);
    }

    public void destroy() throws Exception {
        Class cls = Class.forName("org.hibersap.execution.jco.JCoEnvironment");
        Method regMethod = cls.getMethod("unregisterDestination", String.class);
        regMethod.invoke(cls, connectionPoolName);
    }

}