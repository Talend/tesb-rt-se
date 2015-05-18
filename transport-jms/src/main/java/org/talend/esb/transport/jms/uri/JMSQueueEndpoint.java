package org.talend.esb.transport.jms.uri;

import org.apache.cxf.transport.jms.uri.JMSURIConstants;



/**
 * An endpoint for a JMS Queue which is also browsable
 *
 */
public class JMSQueueEndpoint extends JMSEndpoint {
    /**
     * @param uri
     * @param subject
     */
    public JMSQueueEndpoint(String uri, String subject) {
        super(uri, JMSURIConstants.QUEUE, subject);
    }
}
