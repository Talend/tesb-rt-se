package demo.service;

import javax.servlet.*;
import javax.xml.namespace.QName;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.LocatorService;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;

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
		LocatorService client = (LocatorService) context.getBean("locatorProxyService");

		String serviceHost = this.context.getInitParameter("serviceHost");

		try {
			client.registerEndpoint(new QName("http://talend.org/esb/examples/", "GreeterService"), serviceHost, null);
		 } catch (InterruptedExceptionFault e) {
			 e.printStackTrace();
		 } catch (ServiceLocatorFault e) {
			 e.printStackTrace();
		 }
	}
}