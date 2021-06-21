/*
 * #%L
 * Talend :: ESB :: LOCATOR :: AUTH
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
package org.talend.esb.locator.server.auth;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Configurator {

    private static final Logger LOG = Logger.getLogger(Configurator.class
            .getName());

    private static final String AUTHENTICATION_PROVIDER_CLASS = "org.talend.esb.locator.server.auth.SLAuthenticationProvider";

    private static final String AUTHENTICATION_PROVIDER_KEY = "zookeeper.authProvider.serviceLocator";

    public Configurator() {
        System.setProperty(AUTHENTICATION_PROVIDER_KEY,
                AUTHENTICATION_PROVIDER_CLASS);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE,
                    "Configurator for SLAuthenticationProvider set System property "
                            + AUTHENTICATION_PROVIDER_KEY + " to "
                            + AUTHENTICATION_PROVIDER_CLASS);
        }
    }
}
