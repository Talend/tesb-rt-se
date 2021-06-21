/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.servicelocator.client;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.w3c.dom.Node;

/**
 *
 * An <code>EndpointProvider</code> provides the necessary information to create an entry in the
 * service locator for an endpoint.
 *
 */
public interface Endpoint {

    /**
     * Return the name of the service the endpoint belongs to
     *
     * @return name of the service, must not be <code>null</code>.
     */
    QName getServiceName();

    /**
     * Return the address of the endpoint.
     *
     * @return url of the endpoint, must not be <code>null</code>.
     */
    String getAddress();

    BindingType getBinding();

    TransportType getTransport();

    SLProperties getProperties();

    void writeEndpointReferenceTo(Result result, PropertiesTransformer transformer)
        throws ServiceLocatorException;

    /**
     * Add a WS-Addressing endpoint reference to the given XML tree.
     *
     * @param parent the node where to add the endpoint reference, is not null and either an
     * {@link org.w3c.dom.Element} or a {@link org.w3c.dom.Document}.
     */
    @Deprecated
    void addEndpointReference(Node parent) throws ServiceLocatorException;

    interface PropertiesTransformer {

        void writePropertiesTo(SLProperties props, Result result);

    }

}
