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
package org.talend.esb.servicelocator.client.internal;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.MetadataType;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.cxf.internal.SLPropertiesConverter;
import org.w3c.dom.Element;

public class SLEndpointImpl implements SLEndpoint{

    private QName sName;
    
    private String address;
    
    private boolean isLive;
    
    private long lastTimeStarted = -1;
    
    private SLProperties props;
    
    public SLEndpointImpl(QName serviceName, byte[] content, boolean live) throws ServiceLocatorException{
        sName = serviceName;
        isLive = live;
        if (content != null) {
            EndpointDataType endpointData = toEndPointData(content);
            lastTimeStarted = endpointData.getLastTimeStarted();
            Element eprRoot = (Element) endpointData.getAny();
            EndpointReferenceType epr =  toEndPointReference(eprRoot);
            address = epr.getAddress().getValue();
            
            props = extractProperties(epr);
        } else {
            throw new IllegalArgumentException("content must not be null.");
        }
    }
    
    @Override
    public String getAddress() {
        return address;
    }


    @Override
    public QName forService() {
        return sName;
    }

    @Override
    public BindingType getBinding() {
        return BindingType.SOAP;
    }

    @Override
    public TransportType getTransport() {
        return TransportType.HTTP;
    }

    @Override
    public boolean isLive() {
        return isLive;
    }

    @Override
    public SLProperties getProperties() {
        return props;
    }

    @Override
    public long getLastTimeStarted() {
        return lastTimeStarted;
    }

    @Override
    public long getLastTimeStopped() {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("unchecked")
    private EndpointDataType toEndPointData(byte[] content) throws ServiceLocatorException {
        
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        try {
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointDataType> slEndpoint = (JAXBElement<EndpointDataType>) um.unmarshal(is);

            return slEndpoint.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private EndpointReferenceType toEndPointReference(Element root) throws ServiceLocatorException {

        try {
            JAXBContext jc = JAXBContext.newInstance("org.apache.cxf.ws.addressing");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointReferenceType> epr = (JAXBElement<EndpointReferenceType>) um.unmarshal(root);

            return epr.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }
    
    private SLProperties extractProperties(EndpointReferenceType epr) throws ServiceLocatorException {
        MetadataType metadata = epr.getMetadata();
        if (metadata != null) {
            List<Object> metaAny = metadata.getAny();
            for(Object any : metaAny) {
                if (any instanceof Element) {
                    Element root = (Element) any;
                    if (root.getLocalName().equals("ServiceLocatorProperties")) {
                        ServiceLocatorPropertiesType slp = toServiceLocatorProperties(root); 
                        return SLPropertiesConverter.toSLProperties(slp);
                    }
                }
            }
        }
        return new SLPropertiesImpl();
    }
    
    @SuppressWarnings("unchecked")
    private ServiceLocatorPropertiesType toServiceLocatorProperties(Element root) throws ServiceLocatorException {

        try {
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<ServiceLocatorPropertiesType> slp = (JAXBElement<ServiceLocatorPropertiesType>) um.unmarshal(root);

            return slp.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }
    
}
