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

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.talend.esb.locator.proxy.service.types.EndpointReferenceListType;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

@WebService(targetNamespace = "http://service.proxy.locator.esb.talend.org/", name = "LocatorProxyService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface LocatorProxyService {

    @Oneway
    @WebMethod(action = "registerEndpoint")
    public void registerEndpoint(
        @WebParam(partName = "serviceName", name = "serviceName", targetNamespace = "")
        javax.xml.namespace.QName serviceName,
        @WebParam(partName = "endpointURL", name = "endpointURL", targetNamespace = "")
        java.lang.String endpointURL
    );

    @WebResult(name = "EndpointReferenceType", targetNamespace = "http://service.proxy.locator.esb.talend.org/types", partName = "return")
    @WebMethod(action = "lookupEndpoint")
    public W3CEndpointReference lookupEndpoint(
        @WebParam(partName = "serviceName", name = "serviceName", targetNamespace = "")
        javax.xml.namespace.QName serviceName
    );

    @WebResult(name = "EndpointReferenceListType", targetNamespace = "http://service.proxy.locator.esb.talend.org/types", partName = "body")
    @WebMethod(action = "lookupEndpoints")
    public EndpointReferenceListType lookupEndpoints(
        @WebParam(partName = "serviceName", name = "serviceName", targetNamespace = "")
        javax.xml.namespace.QName serviceName
    );

    @WebResult(name = "succes", targetNamespace = "", partName = "succes")
    @WebMethod(action = "unregisterEnpoint")
    public boolean unregisterEnpoint(
        @WebParam(partName = "serviceName", name = "serviceName", targetNamespace = "")
        javax.xml.namespace.QName serviceName,
        @WebParam(partName = "endpointURL", name = "endpointURL", targetNamespace = "")
        java.lang.String endpointURL
    );
}
