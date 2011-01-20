package org.talend.esb.locator.sample;

import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.talend.esb.locator.sample.Constants;
import org.talend.esb.locator.EndpointResolver;
import org.talend.esb.sample.cxf.Greeter;

public class Consumer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create endpoint resolver
		EndpointResolver er = new EndpointResolver(
				Constants.SERVICENAME,
				Constants.LOCATORENDPOINT);
		Service service = null;
		Greeter client = null;
		for (int i = 0; i < 100; i++) {
			System.out.println("___________Begin__________________");
			String endpoint = er.selectEndpoint();

			if (endpoint == null) {
				System.out.println("Endpoint not found for service "
						+ Constants.SERVICENAME.toString());
			} else {
				System.out.println(er.selectEndpoint());
				// Create service
				service = Service.create(Constants.SERVICENAME);
				// Add endpoint from endpoint resolve
				service.addPort(Constants.PORTNAME,
						SOAPBinding.SOAP11HTTP_BINDING, er.selectEndpoint());

				client = service.getPort(Constants.PORTNAME,
						Greeter.class);
				System.out.println(client.greetMe("MyName"));
				System.out.println("___________End____________________");
				System.out.println();
			}
		}
	}

}
