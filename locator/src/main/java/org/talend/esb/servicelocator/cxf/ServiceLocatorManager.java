/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.servicelocator.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;

public interface ServiceLocatorManager {


    void listenForAllServers(Bus anotherBus);

    void registerServer(Server server, Bus anotherBus);

    void registerServer(Server server, SLProperties props, Bus anotherBus);

    void listenForAllClients(Bus bus);

    void listenForAllClients(Bus bus, String selectionStrategy);

    void enableClient(Client client);

    void enableClient(final Client client, SLPropertiesMatcher matcher);

    void enableClient(final Client client, SLPropertiesMatcher matcher, String selectionStrategy);

    void enableClient(ClientConfiguration clientConf);

    void enableClient(final ClientConfiguration clientConf, SLPropertiesMatcher matcher);

    void enableClient(final ClientConfiguration clientConfiguration,
            SLPropertiesMatcher matcher,
            String selectionStrategy);
}
