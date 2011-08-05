package demo.service;

import java.util.Enumeration;

import javax.servlet.*;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.locator.proxy.service.InterruptedExceptionFault;
import org.talend.esb.locator.proxy.service.LocatorProxyService;
import org.talend.esb.locator.proxy.service.ServiceLocatorFault;
import org.talend.esb.locator.proxy.service.types.RegisterEndpointRequestType;

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
		LocatorProxyService client = (LocatorProxyService) context.getBean("locatorProxyService");

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