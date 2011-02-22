package demo.service;


import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.talend.esb.locator.LocatorFeature;
import demo.common.Greeter;

public class Client {

	public static void main(String[] args) throws Exception {

		String response = null;
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		LocatorFeature locatorFeature = new LocatorFeature();
		locatorFeature.setLocatorEndpoints("localhost:2181");

		factory.getFeatures().add(locatorFeature);
		factory.setServiceClass(Greeter.class);
		factory.setAddress("locator://more_useful_information");
//		factory.setAddress("http://example.com");
		Greeter client = (Greeter) factory.create();

		for (int i = 0; i < 10; i++) {
			System.out.println("BEGIN...");

			response = client.greetMe("MyName#" + i);
			System.out.println("Response from the service: ");
			System.out.println(response);
			
			System.out.println("END...");

		}

		System.out.println("END.");

	}
}
