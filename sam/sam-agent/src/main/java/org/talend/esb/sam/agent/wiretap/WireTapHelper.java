package org.talend.esb.sam.agent.wiretap;

import org.apache.cxf.message.Message;

/**
 * Helper class to check if message content logging is disabled externally for SAM
 */
public class WireTapHelper {

    private static final String EXTERNAL_PROPERTY_NAME = "org.talend.esb.sam.agent.log.messageContent";

    /**
     * If property "org.talend.esb.sam.agent.log.messageContent" value
     * is "true" then log
     * is "false" then do not log
     * else fall back to global property "log.messageContent"
     *
     * @param message
     * @param logMessageContent
     * @return true if content logging is disable externally
     */
    public static boolean isMessageContentToBeLogged(final Message message, final boolean logMessageContent) {

        Object logMessageContentExtObj = message.getContextualProperty(EXTERNAL_PROPERTY_NAME);

        if (null == logMessageContentExtObj) {
            return logMessageContent;
        } else {
            String logMessageContentExtVal = (String) logMessageContentExtObj;

            if (logMessageContentExtVal.equalsIgnoreCase("true")) {
                return true;
            }
            if (logMessageContentExtVal.equalsIgnoreCase("false")) {
                return false;
            } else {
                /*
                 * if external property does not have either "true" or "false" then rely on global property value
                 */
                return logMessageContent;
            }
        }
    }
}
