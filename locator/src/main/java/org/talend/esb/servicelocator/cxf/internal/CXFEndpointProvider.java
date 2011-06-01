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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.MetadataType;
import org.apache.cxf.wsdl.WSAEndpointReferenceUtils;
import org.talend.esb.servicelocator.client.EndpointProvider;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.w3c.dom.Node;

public class CXFEndpointProvider implements EndpointProvider {

    private static final org.apache.cxf.ws.addressing.ObjectFactory
        WSA_OBJECT_FACTORY = new org.apache.cxf.ws.addressing.ObjectFactory();

    private static final org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory
        SL_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory();
    
    private QName sName;
    
    private EndpointReferenceType epr;

    public CXFEndpointProvider(QName serviceName, EndpointReferenceType endpointReference) {
        sName = serviceName;
        epr = endpointReference;
    }

    public CXFEndpointProvider(QName serviceName, String address, SLProperties properties) {
        this(serviceName, createEPR(address, properties));
    }

    public CXFEndpointProvider(Server server, String address, SLProperties properties) {
        this(getServiceName(server), createEPR(server, address, properties));
    }

    @Override
    public QName getServiceName() {
        return sName;
    }

    @Override
    public String getAddress() {
        return epr.getAddress().getValue();
    }

    @Override
    public void addEndpointReference(Node parent) {
        serializeEPR(epr, parent);
    }
    
    private void serializeEPR(EndpointReferenceType wsAddr, Node parent) {
        try {
            JAXBElement<EndpointReferenceType> ep =
                WSA_OBJECT_FACTORY.createEndpointReference(wsAddr);
            JAXBContext jc = JAXBContext.newInstance("org.apache.cxf.ws.addressing:org.talend.esb.servicelocator.client.internal.endpoint");
            Marshaller m = jc.createMarshaller();
            m.marshal(ep, parent);
        } catch( JAXBException jbe ){
            jbe.printStackTrace();
        }
    }

    private static EndpointReferenceType createEPR(String address, SLProperties props) {
        EndpointReferenceType epr = WSAEndpointReferenceUtils.getEndpointReference(address);
        if (props != null) {
            addProperties(epr, props);
        }
        return epr;
    }

    private static EndpointReferenceType createEPR(Server server, String address, SLProperties props) {
        EndpointReferenceType sourceEPR = server.getEndpoint().getEndpointInfo().getTarget();
        EndpointReferenceType targetEPR = WSAEndpointReferenceUtils.duplicate(sourceEPR);
        WSAEndpointReferenceUtils.setAddress(targetEPR, address);

        if (props != null) {
            addProperties(targetEPR, props);
        }
        return targetEPR;
    }

    private static void addProperties(EndpointReferenceType epr, SLProperties props) {
        MetadataType metadata = WSAEndpointReferenceUtils.getSetMetadata(epr);
        ServiceLocatorPropertiesType jaxbProps = SLPropertiesConverter.toServiceLocatorPropertiesType(props);

        JAXBElement<ServiceLocatorPropertiesType>
            slp = SL_OBJECT_FACTORY.createServiceLocatorProperties(jaxbProps);
        metadata.getAny().add(slp);
    }
    
    private static QName getServiceName(Server server) {
        EndpointInfo eInfo = server.getEndpoint().getEndpointInfo();
        ServiceInfo serviceInfo = eInfo.getService();
        return serviceInfo.getName();
    }

}
