/*
 * #%L
 * Abstract base class for Service Locator Selection Strategy
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

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public abstract class LocatorSelectionStrategy implements FailoverStrategy {

    protected static final Logger LOG = Logger.getLogger(LocatorSelectionStrategy.class.getName());

    protected Random random = new Random();

    private ServiceLocator serviceLocator;

    private SLPropertiesMatcher matcher = SLPropertiesMatcher.ALL_MATCHER;

    @Override
    public String selectAlternateAddress(List<String> alternates) {
        String alternateAddress = null;
        if (alternates != null && !alternates.isEmpty()) {
            int index = random.nextInt(alternates.size());
            alternateAddress = alternates.remove(index);
        }
        return alternateAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Endpoint> getAlternateEndpoints(Exchange exchange) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Endpoint selectAlternateEndpoint(List<Endpoint> alternates) {
        return null;
    }

    /**
     * 
     * @param exchange
     * @return
     */
    public abstract String getPrimaryAddress(Exchange exchange);

    public void setMatcher(SLPropertiesMatcher propertiesMatcher) {
        if (propertiesMatcher != null) {
            matcher = propertiesMatcher;
        }
    }

    public void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }

    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    protected QName getServiceName(Exchange exchange) {
        return exchange.getEndpoint().getService().getName();
    }

    protected List<String> getEndpoints(QName serviceName) {
        List<String> endpoints = Collections.emptyList();
        try {
            endpoints = serviceLocator.lookup(serviceName, matcher);
        } catch (ServiceLocatorException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Can not refresh list of endpoints due to ServiceLocatorException", e);
            }
        } catch (InterruptedException e) {
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE, "Can not refresh list of endpoints due to InterruptedException", e);
            }
        }
        return endpoints;
    }

}
