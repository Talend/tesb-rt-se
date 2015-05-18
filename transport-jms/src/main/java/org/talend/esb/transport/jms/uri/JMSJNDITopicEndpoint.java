package org.talend.esb.transport.jms.uri;

import org.apache.cxf.transport.jms.uri.JMSURIConstants;


/**
 * 
 */
public class JMSJNDITopicEndpoint extends JMSEndpoint {

    /**
     * @param uri
     * @param subject
     */
    public JMSJNDITopicEndpoint(String uri, String subject) {
        super(uri, JMSURIConstants.JNDI_TOPIC, subject);
    }

}
