package org.talend.esb.policy.compression.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.neethi.Assertion;
import org.xml.sax.SAXException;

/**
 * The Class CompressionOutInterceptor.
 */
public class CompressionOutInterceptor extends GZIPOutInterceptor {

	/* (non-Javadoc)
	 * @see org.apache.cxf.transport.common.gzip.GZIPOutInterceptor#handleMessage(org.apache.cxf.message.Message)
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			
			// Force is set to "true" for service consumer
			// and is set to "false" for service provider
			this.setForce(MessageUtils.isRequestor(message));

			// Load threshold value from policy assertion
			AssertionInfo ai = CompressionPolicyBuilder.getAssertion(message);
			if (ai != null){
				Assertion a = ai.getAssertion();
				if ( a instanceof CompressionAssertion) {
					this.setThreshold(((CompressionAssertion)a).getThreshold());
				}
			}
			
			// Perform compression
			super.handleMessage(message);
			
			// Confirm policy processing
			if (ai != null){
				ai.setAsserted(true);
			}

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
