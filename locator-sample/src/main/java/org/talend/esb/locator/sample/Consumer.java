package org.talend.esb.locator.sample;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.locator.sample.Constants;
//import org.talend.esb.locator.EndpointResolver;
import org.talend.esb.locator.ServiceLocatorException;
import org.talend.esb.sample.cxf.Greeter;

import javax.xml.ws.WebServiceException;

public class Consumer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
/*
		final Logger LOG = Logger.getLogger(Consumer.class.getName());

		EndpointResolver er = null;
		String response = null;
		Greeter client = null;
		try {
		er = new EndpointResolver(Constants.SERVICENAME,
				Constants.LOCATORENDPOINT);
		if (!er.isEmptyList()) {
			for (int i = 0; i < 10; i++) {
//				LOG.log(Level.INFO, "------------ BEGIN ---------");
				System.out.println("BEGIN...");
					client = er.getPort(Constants.PORTNAME, Greeter.class);
					response = client.greetMe("MyName#" + i);
					System.out.println("Response from the service: ");
					System.out.println(response);

				}
			}
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
				} catch (ServiceLocatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//				LOG.log(Level.INFO, "------------ END -----------");
				System.out.println("END.");

*/
			}
		}
