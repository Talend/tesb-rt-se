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

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class SLEndpointImpl extends ContentHolder implements SLEndpoint{

    private QName sName;
    
    private boolean isLive;
    
    public SLEndpointImpl(QName serviceName, byte[] content, boolean live) throws ServiceLocatorException{
        super(content);
        sName = serviceName;
        isLive = live;
    }

    @Override
    public QName forService() {
        return sName;
    }

    @Override
    public BindingType getBinding() {
        return BindingType.SOAP11;
    }

    @Override
    public TransportType getTransport() {
        return TransportType.HTTP;
    }

    @Override
    public boolean isLive() {
        return isLive;
    }
}
