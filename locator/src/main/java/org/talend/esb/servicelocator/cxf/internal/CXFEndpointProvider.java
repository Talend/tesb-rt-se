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

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.ObjectFactory;
import org.talend.esb.servicelocator.client.EndpointProvider;
import org.w3c.dom.Node;

public class CXFEndpointProvider implements EndpointProvider {

    private QName sName;
    
    private EndpointReferenceType epr;

    public CXFEndpointProvider(QName serviceName, EndpointReferenceType endpointReference) {
        sName = serviceName;
        epr = endpointReference;
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
            ObjectFactory of = new ObjectFactory();

            JAXBElement<EndpointReferenceType> ep =
                of.createEndpointReference(wsAddr);
            JAXBContext jc = JAXBContext.newInstance("org.apache.cxf.ws.addressing:org.talend.esb.servicelocator.client.internal.endpoint");
            Marshaller m = jc.createMarshaller();
            m.marshal(ep, parent);
        } catch( JAXBException jbe ){
            jbe.printStackTrace();
        }
    }
}
