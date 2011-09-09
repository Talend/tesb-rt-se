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
package org.talend.esb.locator.rest.proxy.service;

import java.util.List;

import javax.ws.rs.core.Response;

import org.talend.esb.locator.rest.proxy.service.types.EndpointReferenceListType;
import org.talend.esb.locator.rest.proxy.service.types.RegisterEndpointRequestType;
import org.w3._2005._08.addressing.EndpointReferenceType;

public class LocatorProxyServiceImpl implements LocatorProxyService {

	@Override
	public EndpointReferenceType lookupEndpoint(String arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EndpointReferenceListType lookupEndpoints(String arg0,
			List<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response registerEndpoint(RegisterEndpointRequestType arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response unregisterEndpoint(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
