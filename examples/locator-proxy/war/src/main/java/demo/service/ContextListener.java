package demo.service;

import java.util.Enumeration;

import javax.servlet.*;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.locator.proxy.service.InterruptedExceptionFault;
import org.talend.esb.locator.proxy.service.LocatorProxyService;
import org.talend.esb.locator.proxy.service.ServiceLocatorFault;

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
		System.out.println("****" + serviceHost + this.context.getContextPath());

		// W3CEndpointReference endpointReference;
		// try {
		// endpointReference = client.lookupEndpoint(new
		// QName("http://services.talend.org/CRMService",
		// "CRMServiceProvider"));
		// System.out.println(endpointReference.toString());
		// } catch (InterruptedExceptionFault e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ServiceLocatorFault e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}