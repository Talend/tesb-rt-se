package org.talend.esb.policy.compression.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.xml.sax.SAXException;

/**
 * The Class CompressionOutInterceptor.
 */
public class CompressionInInterceptor extends GZIPInInterceptor {

	/* (non-Javadoc)
	 * @see org.apache.cxf.transport.common.gzip.GZIPInInterceptor#handleMessage(org.apache.cxf.message.Message)
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			
			// Perform compression
			super.handleMessage(message);
			
			// Confirm policy processing
			AssertionInfo ai = CompressionPolicyBuilder.getAssertion(message);
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
