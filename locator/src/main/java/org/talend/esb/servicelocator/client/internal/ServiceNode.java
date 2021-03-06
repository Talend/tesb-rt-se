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
package org.talend.esb.servicelocator.client.internal;

import java.util.List;

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.ServiceLocatorException;

public interface ServiceNode {

    QName getServiceName();

    boolean exists() throws ServiceLocatorException, InterruptedException;

    void ensureExists()  throws ServiceLocatorException, InterruptedException;

    void ensureRemoved() throws ServiceLocatorException, InterruptedException;

    EndpointNode getEndPoint(String endpoint);

    List<String> getEndpointNames() throws ServiceLocatorException, InterruptedException;

    List<EndpointNode> getEndPoints() throws ServiceLocatorException, InterruptedException;
}
