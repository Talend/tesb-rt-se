package org.talend.esb.job.controller.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import routines.system.api.ESBConsumer;

public class RuntimeESBConsumer implements ESBConsumer {

	private final QName serviceName;
	private final QName portName;
	final String operationName;

	public RuntimeESBConsumer(
			final QName serviceName,
			final QName portName,
			String operationName) {
		this.serviceName = serviceName;
		this.portName = portName;
		this.operationName = operationName;
	}

	@Override
	public Object invoke(Object payload) throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
		    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

		    javax.xml.ws.Service service = javax.xml.ws.Service.create(serviceName);
	    	javax.xml.ws.Dispatch<Source> disp =
	    		service.createDispatch(portName, Source.class, javax.xml.ws.Service.Mode.PAYLOAD);
	
	    	//service.getPort(portName, serviceEndpointInterface)
	
	    	return disp.invoke((Source)payload);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
			
		}
	}

}
