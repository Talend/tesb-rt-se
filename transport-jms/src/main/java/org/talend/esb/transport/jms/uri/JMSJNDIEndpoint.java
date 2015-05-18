package org.talend.esb.transport.jms.uri;

import org.apache.cxf.transport.jms.uri.JMSURIConstants;


/**
 * 
 */
public class JMSJNDIEndpoint extends JMSEndpoint {

    /**
     * @param uri
     * @param subject
     */
    public JMSJNDIEndpoint(String uri, String subject) {
        super(uri, JMSURIConstants.JNDI, subject);
    }

}
