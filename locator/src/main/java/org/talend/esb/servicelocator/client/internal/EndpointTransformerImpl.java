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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.Endpoint.PropertiesTransformer;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.endpoint.BindingType;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.client.internal.endpoint.TransportType;
import org.w3c.dom.Document;

public class EndpointTransformerImpl implements PropertiesTransformer, EndpointTransformer {
    
    public static final Logger LOG = Logger.getLogger(EndpointTransformerImpl.class
            .getName());

    public static final org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory
    ENDPOINT_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory();

    /**
     * {@inheritDoc
     */
    @Override
    public SLEndpointProvider toSLEndpoint(QName serviceName, byte[] content, boolean isLive) {
        EndpointDataType epd = toEndPointData(content);
        return new SLEndpointProvider(serviceName, epd, isLive);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public byte[] fromEndpoint(Endpoint endpoint, long lastTimeStarted, long lastTimeStopped)
    throws ServiceLocatorException {
        EndpointDataType endpointData =
            createEndpointData(endpoint, lastTimeStarted,  lastTimeStopped);
        return serialize(endpointData);
    }
    
    /**
     * {@inheritDoc
     */
    @Override
    public void writePropertiesTo(SLProperties props, Result result) {
        ServiceLocatorPropertiesType jaxbProps = SLPropertiesConverter.toServiceLocatorPropertiesType(props);

        try {
            ObjectFactory of = new ObjectFactory();

            JAXBElement<ServiceLocatorPropertiesType> elementProps =
                of.createServiceLocatorProperties(jaxbProps);
            ClassLoader cl = this.getClass().getClassLoader();
            JAXBContext jc =
                JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint", cl);
            Marshaller m = jc.createMarshaller();
            m.marshal(elementProps, result);
        } catch (JAXBException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to deserialize endpoint data", e);
            }
        }
    }
    
    private EndpointDataType createEndpointData(
            Endpoint eprProvider,
            long lastTimeStarted,
            long lastTimeStopped) throws ServiceLocatorException {
        ObjectFactory of = new ObjectFactory();
        EndpointDataType endpointData = of.createEndpointDataType();

        endpointData.setBinding(
            BindingType.fromValue(eprProvider.getBinding().getValue()));
        endpointData.setTransport(
                TransportType.fromValue(eprProvider.getTransport().getValue()));
        endpointData.setLastTimeStarted(lastTimeStarted);
        endpointData.setLastTimeStopped(lastTimeStopped);

        DOMResult result = new DOMResult();
        eprProvider.writeEndpointReferenceTo(result, this);
        Document  doc = (Document) result.getNode();
        endpointData.setEndpointReference(doc.getDocumentElement());
        
        return endpointData;
    }

    private byte[] serialize(EndpointDataType endpointData) throws ServiceLocatorException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(50000);
        try {
            ObjectFactory of = new ObjectFactory();

            JAXBElement<EndpointDataType> epd =
                of.createEndpointData(endpointData);
            ClassLoader cl = this.getClass().getClassLoader();
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
    public static EndpointDataType toEndPointData(byte[] content) {

        if (content != null) {

            ByteArrayInputStream is = new ByteArrayInputStream(content);
            try {
                ClassLoader cl = EndpointTransformerImpl.class.getClassLoader();
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
        } else {
            throw new IllegalArgumentException("content must not be null.");
        }
    }

/*
    private static byte[] serialize(EndpointDataType endpointData) throws ServiceLocatorException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(50000);
        try {
            JAXBElement<EndpointDataType> epd =
                ENDPOINT_OBJECT_FACTORY.createEndpointData(endpointData);
            ClassLoader cl = EndpointTransformerImpl.class.getClassLoader();
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
*/
}