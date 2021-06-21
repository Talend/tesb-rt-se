/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.wiretap;

import org.apache.cxf.message.Message;

/**
 * Helper class to check if message content logging is disabled externally for SAM
 */
public class WireTapHelper {

    public static final String EXTERNAL_PROPERTY_NAME = "org.talend.esb.sam.agent.log.messageContent";

    public static final String CONTENT_LOGGING_IS_DISABLED = "[CONTENT LOGGING IS DISABLED]";

    /**
     * If the "org.talend.esb.sam.agent.log.messageContent" property value is "true" then log the message content
     * If it is "false" then skip the message content logging
     * Else fall back to global property "log.messageContent"
     *
     * @param message
     * @param logMessageContent
     * @param logMessageContentOverride
     * @return
     */
    public static boolean isMessageContentToBeLogged(final Message message, final boolean logMessageContent,
            boolean logMessageContentOverride) {

        /*
         * If controlling of logging behavior is not allowed externally
         * then log according to global property value
         */
        if (!logMessageContentOverride) {
            return logMessageContent;
        }

        Object logMessageContentExtObj = message.getContextualProperty(EXTERNAL_PROPERTY_NAME);

        if (null == logMessageContentExtObj) {

            return logMessageContent;

        } else if (logMessageContentExtObj instanceof Boolean) {

            return ((Boolean) logMessageContentExtObj).booleanValue();

        } else if (logMessageContentExtObj instanceof String) {

            String logMessageContentExtVal = (String) logMessageContentExtObj;

            if (logMessageContentExtVal.equalsIgnoreCase("true")) {

                return true;

            } else if (logMessageContentExtVal.equalsIgnoreCase("false")) {

                return false;

            } else {

                return logMessageContent;
            }
        } else {

            return logMessageContent;
        }
    }
}
