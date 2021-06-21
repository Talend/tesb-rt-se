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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorSettingsTest {

    public static final String ENDPOINTS = "host1:2189,host2:2188,host3:2187";

    public static final String USERNAME_PWD = "user=test;password=test";

    public static final String USER_1 = "user1";

    public static final String USER_2 = "user2";

    public static final String PWD_1 = "passw1";

    public static final String PWD_2 = "passw2";

    public static final String ENDPOINTS_USER_PWD = ENDPOINTS + " ;user = " + USER_1 + "; password = " + PWD_1;

    public static final String ENDPOINTS_PWD = ENDPOINTS + "; password = " + PWD_1;

    public static final String ENDPOINTS_PWD_USER = ENDPOINTS + "; password = " + PWD_1 + " ;user = " + USER_1;

    public static final String ENDPOINTS_INCL_USER = ENDPOINTS + "; user=" ;

    @Test
    public void noEndpointsSet() {
        LocatorSettings settings = new LocatorSettings();
        assertThat(settings.getEndpoints(), nullValue());
    }

    @Test
    public void pureEndpointsSet() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS);
        assertThat(settings.getEndpoints(), equalTo(ENDPOINTS));
    }

    @Test
    public void endpointsWithUserNamePasswordSet() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_USER_PWD);
        assertThat(settings.getEndpoints(), equalTo(ENDPOINTS));
    }

    @Test
    public void getUserWhereEncodedInEndpoints() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_USER_PWD);

        assertThat(settings.getUser(), equalTo(USER_1));
    }

    @Test
    public void getPasswordWhereEncodedInEndpoints() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_USER_PWD);

        assertThat(settings.getPassword(), equalTo(PWD_1));
    }

    @Test
    public void getPasswordWhereOnlyPasswordEncodedInEndpoints() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_PWD);

        assertThat(settings.getPassword(), equalTo(PWD_1));
        assertThat(settings.getUser(), nullValue());
    }

    @Test
    public void getUserWhereUserSecondEncodedInEndpoints() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_PWD_USER);

        assertThat(settings.getUser(), equalTo(USER_1));
        assertThat(settings.getPassword(), equalTo(PWD_1));
    }

    @Test
    public void invalidUserParameterAddedToEndpoints() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_INCL_USER);

        assertThat(settings.getEndpoints(), equalTo(ENDPOINTS));
        assertThat(settings.getUser(), nullValue());
        assertThat(settings.getPassword(), nullValue());
    }

    @Test
    public void getUserUserExplicitlySet() {
        LocatorSettings settings = new LocatorSettings();
        settings.setUser(USER_2);

        assertThat(settings.getUser(), equalTo(USER_2));
    }

    @Test
    public void getUserEmptyUserExplicitlySet() {
        LocatorSettings settings = new LocatorSettings();
        settings.setUser("");

        assertThat(settings.getUser(), nullValue());
    }

    @Test
    public void getUserUserExplicitlySetAndThroughEndpoint() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_PWD_USER);
        settings.setUser(USER_2);

        assertThat(settings.getUser(), equalTo(USER_2));
    }

    @Test
    public void getPwdPwdExplicitlySetAndThroughEndpoint() {
        LocatorSettings settings = new LocatorSettings();
        settings.setEndpoints(ENDPOINTS_PWD_USER);
        settings.setPassword(PWD_2);

        assertThat(settings.getPassword(), equalTo(PWD_2));
    }

    @Test
    public void getPwdEmptyPwdExplicitlySet() {
        LocatorSettings settings = new LocatorSettings();
        settings.setPassword("");

        assertThat(settings.getPassword(), nullValue());
    }

    @Test
    public void getAuthInfo() throws Exception {
        LocatorSettings settings = new LocatorSettings();
        settings.setUser(USER_2);
        settings.setPassword(PWD_2);

        byte[] expectedAuthInfo = (USER_2 + ":" + PWD_2).getBytes("UTF-8");

        assertThat(settings.getAuthInfo(), equalTo(expectedAuthInfo));
    }

    @Test
    public void getAuthInfoNoUserDefined() throws Exception {
        LocatorSettings settings = new LocatorSettings();
        settings.setPassword(PWD_2);

        try {
            settings.getAuthInfo();
            fail("A ServiceLocatorException was expected to be thrown.");
        } catch (ServiceLocatorException e) {
            noop();
        }
    }

    @Test
    public void getAuthInfoNoPwdDefined() throws Exception {
        LocatorSettings settings = new LocatorSettings();
        settings.setUser(USER_2);

        try {
            settings.getAuthInfo();
            fail("A ServiceLocatorException was expected to be thrown.");
        } catch (ServiceLocatorException e) {
            noop();
        }
    }

    private void noop() {}
}

