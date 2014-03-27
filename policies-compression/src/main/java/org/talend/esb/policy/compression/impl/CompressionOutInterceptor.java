package org.talend.esb.policy.compression.impl;

import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
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
			CompressionAssertion assertion = getAssertion(message);
			if (assertion != null) {
				this.setThreshold(assertion.getThreshold());
				this.setForce(assertion.isForced());
				super.handleMessage(message);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the assertion.
	 *
	 * @param message the message
	 * @return the assertion
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	protected CompressionAssertion getAssertion(Message message)
			throws SAXException, IOException, ParserConfigurationException {
		AssertionInfoMap aim = message.get(AssertionInfoMap.class);
		if (aim != null) {
			Collection<AssertionInfo> ais = aim
					.get(CompressionPolicyBuilder.COMPRESSION);

			if (ais == null) {
				return null;
			}

			for (AssertionInfo ai : ais) {
				if (ai.getAssertion() instanceof CompressionAssertion) {
					return (CompressionAssertion) ai.getAssertion();
				}
			}
		}

		return null;
	}
}
