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

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.talend.esb.job.controller.internal.util.ServiceHelper;
import org.talend.esb.sam.agent.feature.EventFeature;

class ESBProvider extends ESBProviderBase {

    private static final Logger LOG =
        Logger.getLogger(ESBProvider.class.getName());

    private final String publishedEndpointUrl;
    private final QName serviceName;
    private final QName portName;
    private final AbstractFeature serviceLocator;
    private Server server;

    public ESBProvider(String publishedEndpointUrl,
            final QName serviceName,
            final QName portName,
            final AbstractFeature serviceLocator,
            final EventFeature eventFeature) {
        this.publishedEndpointUrl = publishedEndpointUrl;
        this.serviceName = serviceName;
        this.portName = portName;
        this.serviceLocator = serviceLocator;
        this.eventFeature = eventFeature;
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
                ServiceHelper.removeOperation(si, "invoke");

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
        if (eventFeature != null) {
            features.add(eventFeature);
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

        ServiceHelper.addOperation(
            server.getEndpoint().getService().getServiceInfos().get(0),
            operationName, isRequestResponse);

        return esbProviderCallback;
    }

    @Override
    public boolean destroyESBProviderCallback(String operationName) {
        boolean destroyed = super.destroyESBProviderCallback(operationName);
        if (!destroyed) {
            ServiceHelper.removeOperation(
                server.getEndpoint().getService().getServiceInfos().get(0),
                operationName);
        } else {
            LOG.info("Web service '" + serviceName + "' stopped");
            server.destroy();
        }
        return destroyed;
    }

}
