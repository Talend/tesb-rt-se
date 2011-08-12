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

import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.tools.common.extensions.soap.SoapOperation;
import org.apache.cxf.tools.util.SOAPBindingUtil;
import org.apache.cxf.wsdl.WSDLManager;
import org.talend.esb.sam.common.handler.impl.CustomInfoHandler;

class ESBProvider extends ESBProviderBase {

    private static final Logger LOG = Logger.getLogger(ESBProvider.class.getName());
    private static final QName XSD_ANY_TYPE =
        new QName("http://www.w3.org/2001/XMLSchema", "anyType");

    private final String publishedEndpointUrl;
    private final QName serviceName;
    private final QName portName;
    private final AbstractFeature serviceLocator;
    private final AbstractFeature serviceActivityMonitoring;

    private Server server;

    public ESBProvider(String publishedEndpointUrl,
            final QName serviceName,
            final QName portName,
            final AbstractFeature serviceLocator,
            final AbstractFeature serviceActivityMonitoring,
            final CustomInfoHandler customInfoHandler) {
        this.publishedEndpointUrl = publishedEndpointUrl;
        this.serviceName = serviceName;
        this.portName = portName;
        this.serviceLocator = serviceLocator;
        this.serviceActivityMonitoring = serviceActivityMonitoring;
        setCustomInfoHandler(customInfoHandler);
    }

    public String getPublishedEndpointUrl() {
        return publishedEndpointUrl;
    }

    public void run(final Bus bus) {
        final JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean() {
            @Override
            protected Endpoint createEndpoint() throws BusException,
                EndpointException {
                final Endpoint endpoint = super.createEndpoint();

                final ServiceInfo si =
                    endpoint.getService().getServiceInfos().get(0);
                // remove default operation
                removeOperation(si, "invoke");

                // set portType = serviceName
                InterfaceInfo ii = si.getInterface();
                ii.setName(serviceName);
                return endpoint;
            }
        };
        sf.setServiceName(serviceName);
        sf.setEndpointName(portName);
        sf.setAddress(publishedEndpointUrl);
        sf.setServiceBean(this);
        List<AbstractFeature> features = new ArrayList<AbstractFeature>();
        if (serviceLocator != null) {
            features.add(serviceLocator);
        }
        if (serviceActivityMonitoring != null) {
            features.add(serviceActivityMonitoring);
        }
        sf.setFeatures(features);
        sf.setBus(bus);

        server = sf.create();

        LOG.info("Web service '" + serviceName + "' published at endpoint '"
            + publishedEndpointUrl + "'");
    }

    @Override
    public RuntimeESBProviderCallback createESBProviderCallback(
            String operationName, boolean isRequestResponse) {
        RuntimeESBProviderCallback esbProviderCallback =
            super.createESBProviderCallback(operationName, isRequestResponse);

        addOperation(server.getEndpoint().getService().getServiceInfos().get(0),
                operationName, isRequestResponse);

        return esbProviderCallback;
    }

    @Override
    public boolean destroyESBProviderCallback(String operationName) {
        boolean destroyed = super.destroyESBProviderCallback(operationName);
        if (!destroyed) {
            removeOperation(
                server.getEndpoint().getService().getServiceInfos().get(0),
                operationName);
        } else {
            LOG.info("Web service '" + serviceName + "' stopped");
            server.destroy();
        }
        return destroyed;
    }

    public static void addOperation(final ServiceInfo si,
            String operationName, boolean isRequestResponse) {
        final InterfaceInfo ii = si.getInterface();
        final String namespace = ii.getName().getNamespaceURI();

        final OperationInfo oi = ii.addOperation(
            new QName(namespace, operationName));
        final MessageInfo mii = oi.createMessage(
            new QName(namespace, operationName + "Request"),
            MessageInfo.Type.INPUT);
        oi.setInput(operationName + "Request", mii);
        MessagePartInfo mpi = mii.addMessagePart("request");
        mpi.setTypeQName(XSD_ANY_TYPE);
        if (isRequestResponse) {
            MessageInfo mio = oi.createMessage(
                new QName(namespace, operationName + "Response"),
                MessageInfo.Type.OUTPUT);
            oi.setOutput(operationName + "Response", mio);
            mpi = mio.addMessagePart("response");
            mpi.setTypeQName(XSD_ANY_TYPE);
        }

        final BindingInfo bi = si.getBindings().iterator().next();
        final BindingOperationInfo boi = new BindingOperationInfo(bi, oi);
        bi.addOperation(boi);
        if (bi instanceof SoapBindingInfo) {
//            SoapOperationInfo soi = new SoapOperationInfo();
//            soi.setAction(operationName);
//            boi.addExtensor(soi);

            // org.apache.cxf.binding.soap.SoapBindingFactory
            final SoapBindingInfo sbi = (SoapBindingInfo)bi;
            Bus bs = org.apache.cxf.bus.spring.SpringBusFactory.getDefaultBus();
            WSDLManager m = bs.getExtension(WSDLManager.class);
            ExtensionRegistry extensionRegistry = m.getExtensionRegistry();
            boolean isSoap12 = sbi.getSoapVersion() instanceof Soap12;
            try {
                SoapOperation soapOperation =
                    SOAPBindingUtil.createSoapOperation(extensionRegistry, isSoap12);
                soapOperation.setSoapActionURI(operationName/*soi.getAction()*/);
//                soapOperation.setStyle(soi.getStyle());
                boi.addExtensor(soapOperation);
            } catch (WSDLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void removeOperation(final ServiceInfo si, String operationName) {
        InterfaceInfo ii = si.getInterface();

        final String namespace = ii.getName().getNamespaceURI();
        OperationInfo oi = ii.getOperation(
            new QName(namespace, operationName));
        ii.removeOperation(oi);

        BindingInfo bi = si.getBindings().iterator().next();
        BindingOperationInfo boi = bi.getOperation(oi);
        bi.removeOperation(boi);
    }

}
