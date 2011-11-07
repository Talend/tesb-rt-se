package org.talend.esb.servicelocator.client.internal;
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


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.TransportType;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.client.ws.addressing.EndpointReferenceType;
import org.talend.esb.servicelocator.client.ws.addressing.MetadataType;
import org.w3c.dom.Element;

public class SLEndpointProvider extends SimpleEndpoint implements SLEndpoint {

    public static final Logger LOG = Logger.getLogger(SLEndpointProvider.class
            .getName());

    public static final org.talend.esb.servicelocator.client.ws.addressing.ObjectFactory
    WSA_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.ws.addressing.ObjectFactory();

    public static final org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory
    SL_OBJECT_FACTORY = new org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory();

    private static final  String SERVICE_LOCATOR_PROPERTIES_NS = "http://talend.org/schemas/esb/locator/content/20011/11";

    private static final String SERVICE_LOCATOR_PROPERTIES_LN = "ServiceLocatorProperties";

    private Element eprRoot;

    private long lastTimeStarted = -1;

    private long lastTimeStopped = -1;

    private boolean isLive;

    public SLEndpointProvider(QName serviceName, EndpointDataType endpointData, boolean live) {
        eprRoot = (Element) endpointData.getEndpointReference();
        EndpointReferenceType epr =  toEndPointReference(eprRoot);

        init(serviceName,
                extractAddress(epr),
                extractBinding(endpointData),
                extractTransport(endpointData),
                extractProperties(epr)); 

        lastTimeStarted = endpointData.getLastTimeStarted();
        lastTimeStopped = endpointData.getLastTimeStopped();
        isLive = live;
    }

    @Override
    public long getLastTimeStarted() {
        return lastTimeStarted;
    }

    @Override
    public long getLastTimeStopped() {
        return  lastTimeStopped;       
    }

    @Override
    public boolean isLive() {
        return isLive;
    }

    @SuppressWarnings("unchecked")
    private EndpointReferenceType toEndPointReference(Element root) {

        EndpointReferenceType epr = null;
        if (root != null) {

            try {
                ClassLoader cl = this.getClass().getClassLoader();
                JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.ws.addressing", cl);
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
        return epr != null ? epr : new EndpointReferenceType();
    }

    private String extractAddress(EndpointReferenceType epr) {
        return epr.getAddress() != null
            ?  epr.getAddress().getValue()
            : null;        
    }

    private BindingType extractBinding(EndpointDataType epd) {
        return epd.getBinding() != null
            ?  BindingType.fromValue(epd.getBinding().value())
            : null;        
    }

    private TransportType extractTransport(EndpointDataType epd) {
        return epd.getTransport() != null
            ?  TransportType.fromValue(epd.getTransport().value())
            : null;        
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
            return SL_OBJECT_FACTORY.createServiceLocatorPropertiesType();
        }
    }

    private boolean isServiceLocatorProperties(Element elem) {
        return
            SERVICE_LOCATOR_PROPERTIES_LN.equals(elem.getLocalName())
            && SERVICE_LOCATOR_PROPERTIES_NS.equals(elem.getNamespaceURI());
    }
}
