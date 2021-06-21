/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_3;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_4;
import static org.talend.esb.servicelocator.TestValues.REL_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_3;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_4;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.Binding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.wsdl.WSAEndpointReferenceUtils;
import org.easymock.EasyMock;

public class CXFTestStubs {
    public static final Server SERVER_1 = createServerStub(SERVICE_QNAME_1, ENDPOINT_1);

    public static final Server SERVER_2 = createServerStub(SERVICE_QNAME_2, ENDPOINT_2);

    public static final Server SERVER_3 = createServerStub(SERVICE_QNAME_3, ENDPOINT_3);

    public static final Server SERVER_4 = createServerStub(SERVICE_QNAME_4, ENDPOINT_4);

    public static final Server REL_SERVER_1 = createServerStub(SERVICE_QNAME_1, REL_ENDPOINT_1);

    public static Server createServerStub(QName serviceName, String endpointName) {
        return createServerStub(serviceName, endpointName, null, null);
    }

    public static Server createServerStub(QName serviceName, String endpointName, String bindingId, String transportId) {
        EndpointInfo endpointInfo = createEndpointInfoStub(serviceName, endpointName, transportId);
        Binding binding = createBindingStub(bindingId);
        Endpoint endpoint = createEndpointStub(endpointInfo, binding);

        return createServerStub(endpoint);
    }

    public static Server createServerStub(Endpoint endpoint) {
        Server server = EasyMock.createNiceMock(Server.class);
        expect(server.getEndpoint()).andStubReturn(endpoint);

        EasyMock.replay(server);
        return server;
    }

    public static Server createJAXRSServerStub(QName serviceName, String endpointName) {
        EndpointInfo endpointInfo = createJAXRSEndpointInfoStub(serviceName, endpointName);
        Binding binding = createBindingStub(CXFEndpointProvider.JAXRS_BINDING_ID);
        Endpoint endpoint = createEndpointStub(endpointInfo, binding);

        return createServerStub(endpoint);
    }

    public static Endpoint createEndpointStub(EndpointInfo endpointInfo, Binding binding) {
        Endpoint endpoint = EasyMock.createNiceMock(Endpoint.class);
        expect(endpoint.getEndpointInfo()).andStubReturn(endpointInfo);
        expect(endpoint.getBinding()).andStubReturn(binding);

        EasyMock.replay(endpoint);

        return endpoint;
    }

    public static EndpointInfo createEndpointInfoStub(QName serviceName, String endpointName, String transportId) {
        EndpointReferenceType epr = createEPR(endpointName);
        ServiceInfo serviceInfo = EasyMock.createNiceMock(ServiceInfo.class);
        expect(serviceInfo.getName()).andStubReturn(serviceName);

        EndpointInfo endpointInfo = EasyMock.createNiceMock(EndpointInfo.class);
        expect(endpointInfo.getAddress()).andStubReturn(endpointName);
        expect(endpointInfo.getService()).andStubReturn(serviceInfo);
        expect(endpointInfo.getTarget()).andStubReturn(epr);
        expect(endpointInfo.getTransportId()).andStubReturn(transportId);

        EasyMock.replay(serviceInfo, endpointInfo);

        return endpointInfo;
    }

    public static EndpointInfo createJAXRSEndpointInfoStub(QName serviceName, String endpointName) {
        EndpointReferenceType epr = createEPR(endpointName);

        EndpointInfo endpointInfo = EasyMock.createNiceMock(EndpointInfo.class);
        expect(endpointInfo.getName()).andStubReturn(serviceName);
        expect(endpointInfo.getAddress()).andStubReturn(endpointName);
        expect(endpointInfo.getTarget()).andStubReturn(epr);

        EasyMock.replay(endpointInfo);

        return endpointInfo;
    }

    public static Binding createBindingStub(String bindingId) {
        BindingInfo bindingInfo = EasyMock.createNiceMock(BindingInfo.class);
        expect(bindingInfo.getBindingId()).andStubReturn(bindingId);

        Binding binding = EasyMock.createNiceMock(Binding.class);
        expect(binding.getBindingInfo()).andStubReturn(bindingInfo);
        expect(binding.getBindingInfo()).andStubReturn(bindingInfo);

        EasyMock.replay(bindingInfo, binding);

        return binding;
    }

    public static EndpointReferenceType createEPR(String address) {
        EndpointReferenceType epr =
            WSAEndpointReferenceUtils.getEndpointReference(address);

        return epr;
    }

}
