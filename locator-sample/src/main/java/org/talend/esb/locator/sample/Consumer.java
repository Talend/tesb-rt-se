package org.talend.esb.locator.sample;

import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.locator.sample.Constants;
import org.talend.esb.locator.EndpointResolver;
import org.talend.esb.sample.cxf.Greeter;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

public class Consumer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final Logger LOG = Logger.getLogger(Consumer.class.getName());

		EndpointResolver er = null;
		er = new EndpointResolver(Constants.SERVICENAME,
				Constants.LOCATORENDPOINT);
		if (er.isReady()) {
			Greeter client = null;
			for (int i = 0; i < 10; i++) {
				LOG.log(Level.INFO, "------------ BEGIN ---------");
				try {
					client = er.getPort(Constants.PORTNAME,
							 Greeter.class);
					LOG.log(Level.INFO, client.greetMe("MyName"));

				} catch (WebServiceException se) {
					if (se.getCause().getClass().equals(SocketException.class)) {
						LOG.log(Level.WARNING,
								"Can not process due to SocketException. Will refresh list of endpoints after 5 sec");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}
						er.refreshEndpointsList();
					} else {
						se.printStackTrace();
					}
				}
				LOG.log(Level.INFO, "------------ END -----------");

			}
		}
	}
}
