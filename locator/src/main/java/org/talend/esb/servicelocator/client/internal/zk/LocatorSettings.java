/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.servicelocator.client.internal.zk;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorSettings {

    public static final Charset UTF8_CHAR_SET = Charset.forName("UTF-8");

    public static final String USER_PROP = "user";

    public static final String PWD_PROP = "password";

    private static final Logger LOG = Logger.getLogger(LocatorSettings.class
            .getName());

    private int sesTimeout = 5000;

    private int conTimeout = 5000;

    private String locatorEndpoints;

    private String userInEndpoint;

    private String pwdInEndpoint;

    private String userExplicit;

    private String pwdExplicit;

    public String getEndpoints() {
        return locatorEndpoints;
    }

    public void setEndpoints(String endpoints) {
        String[] parts = endpoints.split(";");
        locatorEndpoints = parts[0].trim();

        if (parts.length >= 2) {
            parseCredentials(parts[1]);
        }

        if (parts.length >= 3) {
            parseCredentials(parts[2]);
        }
    }

    public void setUser(String user) {
        userExplicit = "".equals(user) ? null : user;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("User for authentication to Service Locator set to: " + userExplicit);
        }
    }

    public String getUser() {
        return userExplicit != null ? userExplicit : userInEndpoint;
    }

    public void setPassword(String password) {
        pwdExplicit = "".equals(password) ? null : password;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Password for authentication to Service Locator set.");
        }

    }

    public String getPassword() {
        return pwdExplicit != null ? pwdExplicit : pwdInEndpoint;
    }

    public int getSessionTimeout() {
        return sesTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sesTimeout = sessionTimeout;

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator session timeout set to: " + sesTimeout);
        }
    }

    public int getConnectionTimeout() {
        return conTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        conTimeout = connectionTimeout;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator connection timeout set to: " + conTimeout);
        }

    }

    private void parseCredentials(String credential) {
        String[] credentialParts = credential.split("=");

        if (credentialParts.length != 2) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Unable to interprete credential parameter '" + credential + "', when parsing the endpoints property.");
            }
            return;
        }

        String property = credentialParts[0].trim();

        if (property.equals(USER_PROP)) {
            userInEndpoint = credentialParts[1].trim();

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("User for authentication to Service Locator set to: " + userExplicit + " via endpoints parameter");
            }
        } else if (property.equals(PWD_PROP)) {
            pwdInEndpoint = credentialParts[1].trim();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Password for authentication to Service Locator set via endpoints parameter");
            }
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Property " + property + " is not a valid credential property.");
            }
        }
    }

    public byte[] getAuthInfo() throws ServiceLocatorException {
        if (getUser() == null) {
            throw new ServiceLocatorException(
                    "Service Locator server requires authorization, but no user is defined.");
        }

        if (getPassword() == null) {
            throw new ServiceLocatorException(
                    "Service Locator server requires authorization, but no password is defined.");
        }

        return (getUser() + ":" + getPassword()).getBytes(UTF8_CHAR_SET);
    }
}
