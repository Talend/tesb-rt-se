package org.talend.esb.sam.agent.wiretap;

import org.apache.cxf.message.Message;

/**
 * Helper class to check if message content logging is disabled externally for SAM
 */
public class WireTapHelper {

    private static final String EXTERNAL_PROPERTY_NAME = "org.talend.esb.sam.agent.log.messageContent";

    /**
     * If the "org.talend.esb.sam.agent.log.messageContent" property value is "true" then log the message content
     * If it is "false" then skip the message content logging
     * Else fall back to global property "log.messageContent"
     *
     * @param message
     * @param logMessageContent
     * @return
     */
    public static boolean isMessageContentToBeLogged(final Message message, final boolean logMessageContent) {

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
