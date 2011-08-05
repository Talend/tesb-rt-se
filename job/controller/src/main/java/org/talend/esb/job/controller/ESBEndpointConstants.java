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

    String PUBLISHED_ENDPOINT_URL = "publishedEndpointUrl";
    String DEFAULT_OPERATION_NAME = "defaultOperationName";
    String SERVICE_NAME = "serviceName";
    String PORT_NAME = "portName";
    String COMMUNICATION_STYLE = "COMMUNICATION_STYLE";
    String USE_SERVICE_LOCATOR = "useServiceLocator";
    String USE_SERVICE_ACTIVITY_MONITOR = "useServiceActivityMonitor";

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

        public static OperationStyle fromString(String value) {
            for (OperationStyle style : OperationStyle.values()) {
                if (style.style.equalsIgnoreCase(value)) {
                    return style;
                }
            }
            throw new IllegalArgumentException(
                    "Unsupported communication style: " + value);
        }
    }

}
