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
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.MetadataType;
import org.apache.cxf.wsdl.WSAEndpointReferenceUtils;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.TransportType;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.cxf.internal.SLPropertiesConverter;


public class ContentHolder {
    public static final Logger LOG = Logger.getLogger(ContentHolder.class
            .getName());

    public static final org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory
    ENDPOINT_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory();

    private static final  String SERVICE_LOCATOR_PROPERTIES_NS = "http://talend.org/esb/serviceLocator/4.2";

    private static final String SERVICE_LOCATOR_PROPERTIES_LN = "ServiceLocatorProperties";
    
    private static final org.apache.cxf.ws.addressing.ObjectFactory WSA_OBJECT_FACTORY = 
        new org.apache.cxf.ws.addressing.ObjectFactory();
    
    protected EndpointDataType epd;
    
    protected EndpointReferenceType epr;
    
    private SLProperties props;

    public ContentHolder(EndpointDataType endpointData) {
        epd = endpointData;
    }
    
    public ContentHolder(byte[] content)  {
        if (content != null) {
            epd = toEndPointData(content);
            
            Element eprRoot = (Element) epd.getAny();
            epr =  toEndPointReference(eprRoot);

            props = extractProperties(epr);
        } else {
            throw new IllegalArgumentException("content must not be null.");
        }
    }

    public byte[] getContent() throws ServiceLocatorException {
        return serialize(epd);
    }

    public long getLastTimeStarted() {
        return epd.getLastTimeStarted();
    }

    public void  setLastTimeStarted(long lastTimeStarted) {
        epd.setLastTimeStarted(lastTimeStarted);       
    }

    public long getLastTimeStopped() {
        return  epd.getLastTimeStopped();       
    }

    public void  setLastTimeStopped(long lastTimeStopped) {
        epd.setLastTimeStopped(lastTimeStopped);       
    }

    public BindingType getBinding() {
        return BindingType.fromValue(epd.getBinding().value());
    }

    public TransportType getTransport() {
        return TransportType.fromValue(epd.getTransport().value());
    }

    public String getAddress() {
        return WSAEndpointReferenceUtils.getAddress(epr);
//        return epr.getAddress() != null
//            ?  epr.getAddress().getValue()
//            : null;
    }

    public void setAddress(String address) {
        WSAEndpointReferenceUtils.setAddress(epr, address);
    }

    public SLProperties getProperties() {
        return props;
    }

    private static byte[] serialize(EndpointDataType endpointData) throws ServiceLocatorException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(50000);
        try {
            JAXBElement<EndpointDataType> epd =
                ENDPOINT_OBJECT_FACTORY.createEndpointData(endpointData);
            ClassLoader cl = ContentHolder.class.getClassLoader();
            JAXBContext jc =
                JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint", cl);
            Marshaller m = jc.createMarshaller();
            m.marshal(epd, outputStream);
        } catch (JAXBException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to serialize endpoint data", e);
            }
            throw new ServiceLocatorException("Failed to serialize endpoint data", e);

        }
        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static EndpointDataType toEndPointData(byte[] content) {
        
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        try {
            ClassLoader cl = ContentHolder.class.getClassLoader();
            JAXBContext jc =
                JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint", cl);
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointDataType> slEndpoint = (JAXBElement<EndpointDataType>) um.unmarshal(is);

            return slEndpoint.getValue();
        } catch (JAXBException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to deserialize endpoint data", e);
            }
            EndpointDataType endpointData = ENDPOINT_OBJECT_FACTORY.createEndpointDataType();
            endpointData.setLastTimeStarted(-1);
            endpointData.setLastTimeStopped(-1);
            return endpointData;
        }
    }
    
    @SuppressWarnings("unchecked")
    private EndpointReferenceType toEndPointReference(Element root) {

        EndpointReferenceType epr = null;
        if (root != null) {

            try {
                ClassLoader cl = this.getClass().getClassLoader();
                JAXBContext jc = JAXBContext.newInstance("org.apache.cxf.ws.addressing", cl);
                Unmarshaller um = jc.createUnmarshaller();

                JAXBElement<EndpointReferenceType> eprElem =
                    (JAXBElement<EndpointReferenceType>) um.unmarshal(root);

                epr = eprElem.getValue();
            } catch (JAXBException e) {
                if (LOG.isLoggable(Level.SEVERE)) {
                    LOG.log(Level.SEVERE,
                            "Failed to deserialize endpoint reference", e);
                }
            }
        } else {
            LOG.log(Level.SEVERE, "No endpoint reference found in content");
        }
        return epr != null ? epr : WSA_OBJECT_FACTORY.createEndpointReferenceType();
    }

    private SLProperties extractProperties(EndpointReferenceType epr) {
        MetadataType metadata = epr.getMetadata();
        if (metadata != null) {
            List<Object> metaAny = metadata.getAny();
            for  (Object any : metaAny) {
                if (any instanceof Element) {
                    Element root = (Element) any;
                    if (isServiceLocatorProperties(root)) {
                        ServiceLocatorPropertiesType slp = toServiceLocatorProperties(root); 
                        return SLPropertiesConverter.toSLProperties(slp);
                    }
                }
            }
        }
        return new SLPropertiesImpl();
    }

    @SuppressWarnings("unchecked")
    private ServiceLocatorPropertiesType toServiceLocatorProperties(Element root) {

        try {
            ClassLoader cl = this.getClass().getClassLoader();
            JAXBContext jc =
                JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint", cl);
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<ServiceLocatorPropertiesType> slp =
                (JAXBElement<ServiceLocatorPropertiesType>) um.unmarshal(root);

            return slp.getValue();
        } catch (JAXBException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to deserialize service locator properties", e);
            }
            return ENDPOINT_OBJECT_FACTORY.createServiceLocatorPropertiesType();
        }
    }

    private boolean isServiceLocatorProperties(Element elem) {
        return
            SERVICE_LOCATOR_PROPERTIES_LN.equals(elem.getLocalName())
            && SERVICE_LOCATOR_PROPERTIES_NS.equals(elem.getNamespaceURI());
    }    
}
