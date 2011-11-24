package org.talend.esb.sam.server.activator;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import org.talend.esb.sam.server.ui.servlets.ListServlet;

public class SamServerActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
		if (sRef != null){
			HttpService httpService = (HttpService) context.getService(sRef);
			ListServlet listServlet = new ListServlet();
			//Dictionary initParam = new Properties();
			//initParam.put("contextConfigLocation", "classpath:META-INF/spring/server-osgi.xml");
			httpService.registerServlet("/api/v1.0/list", listServlet, null, httpService.createDefaultHttpContext());
			
		}

		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
		if (sRef != null){
			HttpService httpService = (HttpService) context.getService(sRef);
			httpService.unregister("/api/v1.0/list");

		}
		
	}

}
