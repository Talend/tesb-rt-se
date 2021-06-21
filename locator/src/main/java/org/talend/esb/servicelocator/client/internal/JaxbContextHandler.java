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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * <code>JaxbContextHandler<code> manages the global {@link JAXBContext} used for WS-Addressing elements and
 * locator endpoint elements.
 * <p>
 * Methods to get the context are not synchronized because the rare case where the context is initialized
 * a second time is tolerable.
 */
public class JaxbContextHandler {

    private JaxbContextHandler() {
    }

    private static volatile JAXBContext endpointContext;

    private static volatile JAXBContext addressingContext;

    public static JAXBContext getEndpointContext() throws JAXBException{
        if (endpointContext == null) {
            endpointContext = JAXBContext.newInstance(
                    "org.talend.esb.servicelocator.client.internal.endpoint",
                    JaxbContextHandler.class.getClassLoader());
        }
        return endpointContext;
    }

    public static JAXBContext getAddressingContext() throws JAXBException{
        if (addressingContext == null) {
            addressingContext = JAXBContext.newInstance(
                    "org.talend.esb.servicelocator.client.ws.addressing",
                    JaxbContextHandler.class.getClassLoader());
        }
        return addressingContext;
    }
}
