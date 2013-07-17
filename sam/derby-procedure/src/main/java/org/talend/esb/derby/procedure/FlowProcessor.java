/*
 * #%L
 * Service Activity Monitoring :: Derby Procedure
 * %%
 * Copyright (C) 2011 - 2013 Talend Inc.
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
package org.talend.esb.derby.procedure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class FlowProcessor {

    public static void InsertOrUpdateFlowsTable(String flowID, Timestamp timestamp) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:default:connection");

        PreparedStatement ps = connection.prepareStatement("SELECT ID FROM FLOWS WHERE ID = ?");
        ps.setString(1, flowID);
        ResultSet rs = ps.executeQuery();
        Statement st = connection.createStatement();
        if (rs.next()) {
            st.execute("update FLOWS set FI_TIMESTAMP='" + timestamp + "' where ID = '" + flowID + "'");
        } else {
            st.execute("insert into FLOWS (ID, FI_TIMESTAMP) values ('" + flowID + "','" + timestamp + "')");
        }

        rs.close();
        ps.close();
        st.close();
        connection.close();
    }

}
