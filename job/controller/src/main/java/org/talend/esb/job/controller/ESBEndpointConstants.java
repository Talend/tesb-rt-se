/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller;

public interface ESBEndpointConstants {

    // keys for ESBEndpointInfo properties
    String PUBLISHED_ENDPOINT_URL = "publishedEndpointUrl";
    String DEFAULT_OPERATION_NAME = "defaultOperationName";
    String SERVICE_NAME = "serviceName";
    String PORT_NAME = "portName";
    String COMMUNICATION_STYLE = "COMMUNICATION_STYLE";
    String USE_SERVICE_LOCATOR = "useServiceLocator";
    String USE_SERVICE_ACTIVITY_MONITOR = "useServiceActivityMonitor";
    String ESB_SECURITY = "esbSecurity";
    String USERNAME = "username";
    String PASSWORD = "password";

    // request constants
    String REQUEST_PAYLOAD = "PAYLOAD";
    String REQUEST_SAM_PROPS = "SAM-PROPS";
    String REQUEST_SL_PROPS = "SL-PROPS";

    // policy id
    String ID_POLICY_TOKEN = "org.talend.esb.job.token.policy";
    String ID_POLICY_SAML = "org.talend.esb.job.saml.policy";

    enum OperationStyle {
        REQUEST_RESPONSE("request-response"),
        ONE_WAY("one-way");

        String style;

        OperationStyle(String style) {
            this.style = style;
        }

        public static boolean isRequestResponse(String value) {
            return REQUEST_RESPONSE.equals(fromString(value));
        }

        private static OperationStyle fromString(String value) {
            for (OperationStyle style : OperationStyle.values()) {
                if (style.style.equals(value)) {
                    return style;
                }
            }
            throw new IllegalArgumentException(
                    "Unsupported communication style: " + value);
        }
    }

    enum EsbSecurity {
        NO("NO"),
        TOKEN("TOKEN"),
        SAML("SAML");

        String esbSecurity;

        EsbSecurity(String esbSecurity) {
            this.esbSecurity = esbSecurity;
        }

        public static EsbSecurity fromString(String value) {
            if (null == value) {
                return NO;
            }
            for (EsbSecurity esbSecurity : EsbSecurity.values()) {
                if (esbSecurity.esbSecurity.equals(value)) {
                    return esbSecurity;
                }
            }
            throw new IllegalArgumentException(
                    "Unsupported secutity value: " + value);
        }
    }
}
