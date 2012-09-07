/*
 * #%L
 * Talend :: ESB :: LOCATOR :: AUTH
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.locator.server.auth;

import java.lang.annotation.Retention;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.auth.AuthenticationProvider;

public class SLAuthenticationProvider implements AuthenticationProvider {

    public static String SL_READ = "SL_READ";

    public static String SL_MAINTAIN = "SL_MAINTAIN";

    public static String SL_ALL = "SL_ALL";

    private Charset utf8CharSet;

    public SLAuthenticationProvider() {
        utf8CharSet = Charset.forName("UTF-8");
        System.out.println("SLAuthenticationProvider created.");
    }

    @Override
    public String getScheme() {
        return "sl";
    }

    @Override
    public Code handleAuthentication(ServerCnxn cnxn, byte[] authData) {
        // Expect Id = "USER=PASSWORD,ROLE1,ROLE2,..."
        String id = new String(authData, utf8CharSet);
        String usedInfo[] = id.split("=");
        String user = "";
        String password = "";
        StringBuilder roles = new StringBuilder("");
        if (usedInfo.length >= 1) {
            user = usedInfo[0];
            String[] userData = usedInfo[1].split(",");
            if (userData.length >= 1) {
                password = userData[0];
                if (userData.length >= 2) {
                    for (int i = 1; i < userData.length; i++) {
                        roles.append(userData[i]);
                        if (i < userData.length - 1)
                            roles.append(",");
                    }
                }
            }
        } else {
            user = "anonymous";
        }
        System.out.println("User: " + user);
        System.out.println("Password: " + password);
        System.out.println("Roles: " + roles.toString());
        cnxn.getAuthInfo().add(new Id(getScheme(), roles.toString()));

        return KeeperException.Code.OK;
    }

    @Override
    public boolean matches(String id, String aclExpr) {
        String[] roles = id.split(",");
        for (int i = 0; i < roles.length; i++) {
            if(roles[i].equals(aclExpr)) return true;
        }
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean isValid(String id) {
        return id.length() > 0;
    }
}
