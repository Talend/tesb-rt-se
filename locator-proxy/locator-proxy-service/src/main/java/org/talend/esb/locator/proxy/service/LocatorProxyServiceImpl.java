/*
 * #%L
 * Service Locator :: Proxy
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.locator.proxy.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.cxf.internal.LocatorRegistrar;

public class LocatorProxyServiceImpl implements LocatorProxyService {

    private static final Logger LOG = Logger.getLogger(LocatorProxyServiceImpl.class.getPackage().getName());
	
	private ServiceLocator locatorClient;

    public void setServiceLocator(ServiceLocator locatorClient) {
        this.locatorClient = locatorClient;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set.");
        }
    }
	
	@Override
	public void registerEndpoint(QName serviceName, String endpointURL) {
		try {
			locatorClient.register(serviceName, endpointURL);
		} catch (ServiceLocatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public W3CEndpointReference lookupEndpoint(QName serviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EndpointReferenceListType lookupEndpoints(QName serviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unregisterEnpoint(QName serviceName, String endpointURL) {
		try {
			locatorClient.unregister(serviceName, endpointURL);
			return true;
		} catch (ServiceLocatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
