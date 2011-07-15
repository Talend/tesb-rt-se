/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.saaj.SAAJFactoryResolver;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

import routines.system.api.ESBConsumer;

@javax.jws.WebService()
public class RuntimeESBConsumer implements ESBConsumer {
    private static final Logger LOG = Logger.getLogger(RuntimeESBConsumer.class.getName());
	
    private final QName serviceName;
    private final QName portName;
    private final String operationName;
    private final String publishedEndpointUrl;
    private final boolean isRequestResponse;
    private final AbstractFeature serviceLocator;
    private final AbstractFeature serviceActivityMonitoring;
    private final CustomInfoHandler customPropertiesHandler;
    private final Bus bus;
    private Client client;

    public RuntimeESBConsumer(
            final QName serviceName,
            final QName portName,
            String operationName,
            String publishedEndpointUrl,
            boolean isRequestResponse,
            final AbstractFeature serviceLocator,
            final AbstractFeature serviceActivityMonitoring,
            final CustomInfoHandler customPropertiesHandler,
            final Bus bus) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.operationName = operationName;
        this.publishedEndpointUrl = publishedEndpointUrl;
        this.isRequestResponse = isRequestResponse;
        this.serviceLocator = serviceLocator;
        this.serviceActivityMonitoring = serviceActivityMonitoring;
        this.customPropertiesHandler = customPropertiesHandler;
        this.bus = bus;
    }

    @Override
    public Object invoke(Object payload) throws Exception {
        if (payload instanceof org.dom4j.Document) {
            return sendDocument((org.dom4j.Document)payload);
        } else if (payload instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map =
                (java.util.Map<String, Object>)payload;
            if (serviceActivityMonitoring != null) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, String> samProps =
                    (java.util.Map<String, String>)map.get(ESBProvider.REQUEST_SAM_PROPS);
                if (samProps != null) {
				    LOG.info("SAM custom properties received: " + samProps);
                    customPropertiesHandler.setCustomInfo(samProps);
                }
            }
            return sendDocument(
                (org.dom4j.Document)map.get(ESBProvider.REQUEST_PAYLOAD));
        } else {
            throw new RuntimeException(
                "Consumer try to send incompatible object: " + payload.getClass().getName());
        }
    }

    private Object sendDocument(org.dom4j.Document doc) throws Exception {
        Client client = createClient();
        try {
            Object[] result = client.invoke(operationName,
                new org.dom4j.io.DocumentSource(doc));
            if (result != null) {
                org.dom4j.io.DocumentResult docResult = new org.dom4j.io.DocumentResult();
                javax.xml.transform.TransformerFactory.newInstance().
                    newTransformer().transform((Source)result[0], docResult);
                return docResult.getDocument();
            }
        } catch (org.apache.cxf.binding.soap.SoapFault e) {
            // org.apache.cxf.jaxws.JaxWsClientProxy
            SOAPFault soapFault = createSoapFault(e);
            if (soapFault == null) {
                throw new WebServiceException(e);
            }
            SOAPFaultException exception = new SOAPFaultException(soapFault);
            if (e instanceof Fault && e.getCause() != null) {
                exception.initCause(e.getCause());
            } else {
                exception.initCause(e);
            }
            throw exception;
        }
        return null;
    }

    private Client createClient() throws BusException, EndpointException {
        if (client != null) {
            return client;
        }
        final JaxWsClientFactoryBean cf = new JaxWsClientFactoryBean();
        cf.setServiceName(serviceName);
        cf.setEndpointName(portName);
        String endpointUrl =
            (serviceLocator == null)
                ? publishedEndpointUrl
                : "locator://" + serviceName.getLocalPart();
        cf.setAddress(endpointUrl);
        cf.setServiceClass(this.getClass());
        cf.setBus(bus);
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        if(serviceLocator != null) {
            features.add(serviceLocator);
        }
        if(serviceActivityMonitoring != null) {
            features.add(serviceActivityMonitoring);
        }
        cf.setFeatures(features);

        client = cf.create();
        
        final Service service = client.getEndpoint().getService();
        service.setDataBinding(new SourceDataBinding());

        final ServiceInfo si = service.getServiceInfos().get(0);
        // set portType = serviceName
        InterfaceInfo ii = si.getInterface();
        ii.setName(serviceName);

        ESBProvider.addOperation(si,
                operationName, isRequestResponse);

        return client;
    }

    static SOAPFault createSoapFault(Exception ex) throws SOAPException {
        SOAPFault soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault(); 
        if (ex instanceof SoapFault) {
            if (!soapFault.getNamespaceURI().equals(((SoapFault)ex).getFaultCode().getNamespaceURI())
                && SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE
                    .equals(((SoapFault)ex).getFaultCode().getNamespaceURI())) {
                //change to 1.1
                try {
                    soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault();
                } catch (Throwable t) {
                    //ignore
                }
            }
            soapFault.setFaultString(((SoapFault)ex).getReason());
            soapFault.setFaultCode(((SoapFault)ex).getFaultCode());
            soapFault.setFaultActor(((SoapFault)ex).getRole());
            if (((SoapFault)ex).getSubCode() != null) {
                soapFault.appendFaultSubcode(((SoapFault)ex).getSubCode());
            }

            if (((SoapFault)ex).hasDetails()) {
                org.w3c.dom.Node nd = soapFault.getOwnerDocument().importNode(((SoapFault)ex).getDetail(),
                                                                  true);
                nd = nd.getFirstChild();
                soapFault.addDetail();
                while (nd != null) {
                    org.w3c.dom.Node next = nd.getNextSibling();
                    soapFault.getDetail().appendChild(nd);
                    nd = next;
                }
            }
        } else {
            String msg = ex.getMessage();
            if (msg != null) {
                soapFault.setFaultString(msg);
            }
        }      
        return soapFault;
    }

}
