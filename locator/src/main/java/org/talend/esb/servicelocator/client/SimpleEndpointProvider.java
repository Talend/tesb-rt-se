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
package org.talend.esb.servicelocator.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.client.internal.ws.addressing.AttributedURIType;
import org.talend.esb.servicelocator.client.internal.ws.addressing.EndpointReferenceType;
import org.talend.esb.servicelocator.client.internal.ws.addressing.MetadataType;
import org.talend.esb.servicelocator.cxf.internal.SLPropertiesConverter;
import org.w3c.dom.Node;

public class SimpleEndpointProvider implements EndpointProvider {

    public static final Logger LOG = Logger.getLogger(SimpleEndpointProvider.class
            .getName());

    public static final org.talend.esb.servicelocator.client.internal.ws.addressing.ObjectFactory
    WSA_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.ws.addressing.ObjectFactory();

    public static final org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory
    SL_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory();

    private String addr;
    
    private QName sName;
    
    private SLProperties props;
    
    private BindingType binding = BindingType.SOAP11;
    
    private TransportType transport = TransportType.HTTP;

    public SimpleEndpointProvider(QName serviceName, String endpoint) {
        this(serviceName, endpoint, null);
    }

    public SimpleEndpointProvider(QName serviceName, String endpoint, SLProperties properties) {
        sName = serviceName;
        addr = endpoint;
        props = properties;
    }

    public QName getServiceName() {
        return sName;
    }

    public String getAddress() {
        return addr;
    }
    
    public BindingType getBinding() {
        return binding;
    }

    public TransportType getTransport() {
        return transport;
    }

    public long getLastTimeStarted() {
        return -1L;
    }

    public long getLastTimeStopped() {
        return -1L;        
    }

    public SLProperties getProperties() {
        return props != null  ? props : SLPropertiesImpl.EMPTY_PROPERTIES;    
    }


    public void writeEndpointReferenceTo(Result result) throws ServiceLocatorException {
        EndpointReferenceType epr = createEndpointReference();
        
        try {
        JAXBElement<EndpointReferenceType> ep =
            WSA_OBJECT_FACTORY.createEndpointReference(epr);
        ClassLoader cl = this.getClass().getClassLoader();
        JAXBContext jc = JAXBContext.newInstance(
                "org.talend.esb.servicelocator.client.internal.ws.addressing:"
                + "org.talend.esb.servicelocator.client.internal.endpoint",
                cl);
        Marshaller m = jc.createMarshaller();
        m.marshal(ep, result);
    } catch (JAXBException e) {
//        if (LOG.isLoggable(Level.SEVERE)) {
//            LOG.log(Level.SEVERE,
//                    "Failed to serialize endpoint data", e);
//        }
//        throw new ServiceLocatorException("Failed to serialize endpoint data", e);
    }        

    }
    
    @Override
    public void addEndpointReference(Node parent) throws ServiceLocatorException {
        EndpointReferenceType wsAddr = createEndpointReference();

        try {
            JAXBElement<EndpointReferenceType> ep =
                WSA_OBJECT_FACTORY.createEndpointReference(wsAddr);
            ClassLoader cl = this.getClass().getClassLoader();
            JAXBContext jc = JAXBContext.newInstance(
                    "org.apache.cxf.ws.addressing:org.talend.esb.servicelocator.client.internal.endpoint",
                    cl);
            Marshaller m = jc.createMarshaller();
            m.marshal(ep, parent);
        } catch (JAXBException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to serialize endpoint data", e);
            }
            throw new ServiceLocatorException("Failed to serialize endpoint data", e);
        }
    }

    private EndpointReferenceType createEndpointReference() {
        AttributedURIType endpoint = new AttributedURIType();
        endpoint.setValue(addr);
        EndpointReferenceType epr = new EndpointReferenceType();
        epr.setAddress(endpoint);
        
        if (props != null) {
            MetadataType metadata = new MetadataType();
            epr.setMetadata(metadata);
            
            ServiceLocatorPropertiesType jaxbProps = SLPropertiesConverter.toServiceLocatorPropertiesType(props);

            JAXBElement<ServiceLocatorPropertiesType>
                slp = SL_OBJECT_FACTORY.createServiceLocatorProperties(jaxbProps);
            metadata.getAny().add(slp);
        }
                
        return epr;
    }
}
