package org.talend.esb.servicelocator.client;
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


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.talend.esb.servicelocator.client.ws.addressing.AttributedURIType;
import org.talend.esb.servicelocator.client.ws.addressing.EndpointReferenceType;
import org.talend.esb.servicelocator.client.ws.addressing.MetadataType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SimpleEndpoint implements Endpoint {

    public static final Logger LOG = Logger.getLogger(SimpleEndpoint.class
            .getName());

    public static final org.talend.esb.servicelocator.client.ws.addressing.ObjectFactory
    WSA_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.ws.addressing.ObjectFactory();
    
    private String addr;
        
    private BindingType binding;
    
    private TransportType transport;
    
    private SLProperties props;

    private QName sName;

    public SimpleEndpoint(QName serviceName, String endpoint) {
        this(serviceName, endpoint, null);
    }

    public SimpleEndpoint(QName serviceName, String endpoint, SLProperties properties) {
        this(serviceName, endpoint, BindingType.SOAP11, TransportType.HTTP, properties);
    }

    public SimpleEndpoint(QName serviceName, String endpoint, BindingType bindingType,
            TransportType transportType, SLProperties properties) {
        sName = serviceName;
        addr = endpoint;
        binding = bindingType;
        transport = transportType;
        props = properties;
    }
    
    protected SimpleEndpoint() {}

    protected void init(QName serviceName, String endpoint, BindingType bindingType,
            TransportType transportType, SLProperties properties)  {
        sName = serviceName;
        addr = endpoint;
        props = properties;
        binding = bindingType;
        transport = transportType;
    }

    @Override
    public BindingType getBinding() {
        return binding;
    }

    @Override
    public TransportType getTransport() {
        return transport;
    }

    @Override
    public String getAddress() {
        return addr;
    }

    @Override
    public SLProperties getProperties() {
        return props;
    }

//    @Override
    public QName forService() {
        return sName;
    }

    @Override
    public QName getServiceName() {
        return forService();
    }

    @Override
    public void writeEndpointReferenceTo(Result result, PropertiesTransformer transformer)
    throws ServiceLocatorException {

        EndpointReferenceType epr = createEndpointReference(transformer);
        
        try {
        JAXBElement<EndpointReferenceType> ep =
            WSA_OBJECT_FACTORY.createEndpointReference(epr);
        ClassLoader cl = this.getClass().getClassLoader();
        JAXBContext jc = JAXBContext.newInstance(
                "org.talend.esb.servicelocator.client.ws.addressing:"
                + "org.talend.esb.servicelocator.client.internal.endpoint",
                cl);
        Marshaller m = jc.createMarshaller();
        m.marshal(ep, result);
    } catch (JAXBException e) {
        if (LOG.isLoggable(Level.SEVERE)) {
            LOG.log(Level.SEVERE,
                    "Failed to serialize endpoint data", e);
        }
        throw new ServiceLocatorException("Failed to serialize endpoint data", e);
    }        

    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void addEndpointReference(Node parent) throws ServiceLocatorException {
        EndpointReferenceType wsAddr = createEndpointReference(null);

        try {
            JAXBElement<EndpointReferenceType> ep =
                WSA_OBJECT_FACTORY.createEndpointReference(wsAddr);
            ClassLoader cl = this.getClass().getClassLoader();
            JAXBContext jc = JAXBContext.newInstance(
                    "org.talend.esb.servicelocator.client.ws.addressing:"
                    + "org.talend.esb.servicelocator.client.internal.endpoint",
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


    private EndpointReferenceType createEndpointReference(PropertiesTransformer transformer)  {
        AttributedURIType endpoint = new AttributedURIType();
        endpoint.setValue(addr);
        EndpointReferenceType epr = new EndpointReferenceType();
        epr.setAddress(endpoint);
        
        if (props != null) {
            DOMResult result = new DOMResult();
            transformer.writePropertiesTo(props, result);
            Document docResult = (Document)result.getNode();
            MetadataType metadata = new MetadataType();
            epr.setMetadata(metadata);
            
            metadata.getAny().add(docResult.getDocumentElement());
        }
                
        return epr;
    }
}
