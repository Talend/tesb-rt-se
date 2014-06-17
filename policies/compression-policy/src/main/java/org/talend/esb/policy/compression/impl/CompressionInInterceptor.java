package org.talend.esb.policy.compression.impl;

import java.util.List;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;

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
			List<AssertionInfo> aiList = CompressionPolicyBuilder.getAssertions(message);
			
			for (AssertionInfo ai : aiList) {
				if (ai != null){
					ai.setAsserted(true);
				}				
			}

		}catch (RuntimeException e) {
			throw e;
		}catch (Exception e) {
			throw new Fault(e);
		}
	}

}
