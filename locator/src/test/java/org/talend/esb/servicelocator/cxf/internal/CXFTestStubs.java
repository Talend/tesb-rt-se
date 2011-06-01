/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf.internal;

import static org.easymock.EasyMock.expect;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.WSAEndpointReferenceUtils;
import org.easymock.EasyMock;

public class CXFTestStubs {
    public static final Server SERVER_1 = createServerStub(SERVICE_QNAME_1, ENDPOINT_1);

    public static final Server SERVER_2 = createServerStub(SERVICE_QNAME_2, ENDPOINT_2);

    public static Server createServerStub(QName serviceName, String endpointName) {
        EndpointReferenceType epr = createEPR(endpointName);
        ServiceInfo serviceInfo = EasyMock.createNiceMock(ServiceInfo.class);
        expect(serviceInfo.getName()).andStubReturn(serviceName);

        EndpointInfo endpointInfo = EasyMock.createNiceMock(EndpointInfo.class);
        expect(endpointInfo.getAddress()).andStubReturn(endpointName);
        expect(endpointInfo.getService()).andStubReturn(serviceInfo);
        expect(endpointInfo.getTarget()).andStubReturn(epr);

        Endpoint endpoint = EasyMock.createNiceMock(Endpoint.class);
        expect(endpoint.getEndpointInfo()).andStubReturn(endpointInfo);

        Server server = EasyMock.createNiceMock(Server.class);
        expect(server.getEndpoint()).andStubReturn(endpoint);

        EasyMock.replay(serviceInfo, endpointInfo, endpoint, server);
        return server;
    }
    
    public static EndpointReferenceType createEPR (String address) {
        EndpointReferenceType epr =
            WSAEndpointReferenceUtils.getEndpointReference(address);
        
        return epr;
    }

}
