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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.talend.esb.DomMother.newDocument;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.TransportType;
import org.w3c.dom.Element;

final public class EndpointStubFactory {

    private EndpointStubFactory() {
    }

    public static Endpoint create(QName serviceName, String endpoint) throws Exception {
        return create(serviceName, endpoint, BindingType.JAXRS, TransportType.HTTP);
    }

    public static  Endpoint create(QName serviceName, String endpoint,
            BindingType bindingType, TransportType transportType)
        throws Exception {

        Endpoint eprProvider = createNiceMock(Endpoint.class);
        expect(eprProvider.getServiceName()).andStubReturn(serviceName);
        expect(eprProvider.getAddress()).andStubReturn(endpoint);
        expect(eprProvider.getBinding()).andStubReturn(bindingType);
        expect(eprProvider.getTransport()).andStubReturn(transportType);
        eprProvider.writeEndpointReferenceTo(anyDOMResult(), (Endpoint.PropertiesTransformer) EasyMock.anyObject());
        expectLastCall().asStub();

        replay(eprProvider);

        return eprProvider;
    }

    public static DOMResult anyDOMResult() {
        EasyMock.reportMatcher(new SetNodeMatcher());
        return null;
    }

    public static class SetNodeMatcher implements IArgumentMatcher {

        @Override
        public boolean matches(Object argument) {
            if (argument != null && argument instanceof DOMResult) {
                DOMResult result = (DOMResult) argument;
                Element epr = newDocument("http://www.w3.org/2005/08/addressing", "EndpointReference");

                result.setNode(epr.getOwnerDocument());
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
        }
    }
}
