package org.talend.esb.transport.jms.uri;

import org.apache.cxf.transport.jms.uri.JMSURIConstants;


/**
 * 
 */
public class JMSTopicEndpoint extends JMSEndpoint {
    /**
     * @param uri
     * @param subject
     */
    public JMSTopicEndpoint(String uri, String subject) {
        super(uri, JMSURIConstants.TOPIC, subject);
    }

}
