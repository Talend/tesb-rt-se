package org.talend.esb.locator.sample;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.locator.sample.Constants;
import org.talend.esb.locator.EndpointResolver;
import org.talend.esb.sample.cxf.Greeter;

import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

public class Consumer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		final Logger LOG = Logger.getLogger(Consumer.class
				.getName());

		EndpointResolver er = new EndpointResolver(Constants.SERVICENAME,
				Constants.LOCATORENDPOINT);
		Greeter client = null;
		for (int i = 0; i < 10; i++) {
			LOG.log(Level.INFO, "------------ BEGIN ---------");
			try {
				client = er.getPort(Constants.PORTNAME,
						SOAPBinding.SOAP11HTTP_BINDING, Greeter.class);
				LOG.log(Level.INFO, client.greetMe("MyName"));

			} catch (java.net.SocketException se) {
				LOG.log(Level.WARNING, "Can not process due to SocketException. Will refresh list of endpoints");
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				er.refreshEndpointsList();
			}
			LOG.log(Level.INFO, "------------ END -----------");

		}
	}

}
