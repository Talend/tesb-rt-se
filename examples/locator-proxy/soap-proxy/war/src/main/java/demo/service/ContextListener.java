package demo.service;

import javax.servlet.*;
import javax.xml.namespace.QName;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.schemas.esb._2011._09.locator.RegisterEndpointRequestType;
import org.talend.webservices.esb.locator_v1.InterruptedExceptionFault;
import org.talend.webservices.esb.locator_v1.LocatorServiceV10;
import org.talend.webservices.esb.locator_v1.ServiceLocatorFault;

public class ContextListener implements ServletContextListener {

	private ServletContext context = null;

	/*
	 * This method is invoked when the Web Application has been removed and is
	 * no longer able to accept requests
	 */

	public void contextDestroyed(ServletContextEvent event) {
		// Output a simple message to the server's console
		System.out.println("The Simple Web App. Has Been Removed");
		this.context = null;

	}

	// This method is invoked when the Web Application
	// is ready to service requests

	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();

		// Output a simple message to the server's console
		System.out.println("The Simple Web App. Is Ready");

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/client.xml");
		LocatorServiceV10 client = (LocatorServiceV10) context.getBean("locatorProxyService");

		String serviceHost = this.context.getInitParameter("serviceHost");

		try {
			RegisterEndpointRequestType registerEndpointRequestType = new RegisterEndpointRequestType();
			registerEndpointRequestType.setEndpointURL(serviceHost);
			registerEndpointRequestType.setServiceName(new QName("http://talend.org/esb/examples/", "GreeterService"));
			client.registerEndpoint(registerEndpointRequestType);
		 } catch (InterruptedExceptionFault e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (ServiceLocatorFault e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	}
}