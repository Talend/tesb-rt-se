package org.talend.esb.sample;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.talend.esb.locator.LocatorRegistrar;
import org.talend.esb.locator.ServiceLocator;
import org.talend.esb.locator.ServiceLocatorException;

public class ServletContextListenerImpl implements ServletContextListener {

	ServiceLocator sl = null;
	
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("*****************Context Destroyed*********************");
		if(sl != null){
			try {
				sl.disconnect();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ServiceLocatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static public final String getHostName(ServletContext context)
	{
	    String host = null;
	    // use Reflection to invoke the getHostName method.
	    Object dirContext = context.getAttribute("org.apache.catalina.resources");
	    Class dirContextClass = dirContext.getClass();
	    try {
	      Method hostMethod = dirContextClass.getMethod("getHostName", null);
	      host = (String) hostMethod.invoke(dirContext, null);
	    } catch (Exception e) {

	    }
	    // if it is null the above method call failed for some reason.
	    // if it is localhost, we will assume there is only one
	    // instance on this machine and then we will try and get the hostname from
	    // the server.
	    try {
	      if (host == null || host.equals("localhost")) {
	        InetAddress local = InetAddress.getLocalHost();
	        host = local.getHostName();
	      }
	    } catch (UnknownHostException e) {
	      host = "UNKNOWN";
	    }
	    return host;
	  }


	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("*****************Context Created*********************");
		Bus bus = BusFactory.getDefaultBus();
		String serviceHost = arg0.getServletContext().getInitParameter("serviceHost");
		//serviceHost = "http://" + getHostName(arg0.getServletContext()) + ":8080/" + arg0.getServletContext().getServletContextName();

		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		List<Server> servers = serverRegistry.getServers();
		Server server = servers.get(0);
		String serviceAdressLocal = server.getEndpoint().getEndpointInfo().getAddress();
		if(!serviceAdressLocal.startsWith(serviceHost)) server.getEndpoint().getEndpointInfo().setAddress(serviceHost + serviceAdressLocal);
		
		System.out.println(serviceHost);
		
		sl = new ServiceLocator();
		sl.setLocatorEndpoints("localhost:2181");
        sl.setSessionTimeout(3000);
        sl.setConnectionTimeout(5000);
        try {
        	sl.connect();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}
        LocatorRegistrar lr = new LocatorRegistrar();
        lr.setLocatorClient(sl);
        lr.setBus(bus);
	}
}
