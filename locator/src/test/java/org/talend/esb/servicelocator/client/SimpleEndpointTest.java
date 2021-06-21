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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.DomMother.newDocument;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestContent.createContent;
import static org.talend.esb.servicelocator.TestContent.createContentInvalidEPR;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STARTED;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STOPPED;
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.PROPERTIES;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.VALUE_1;
import static org.talend.esb.servicelocator.TestValues.VALUE_2;

import javax.xml.transform.dom.DOMResult;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.Endpoint.PropertiesTransformer;
import org.talend.esb.servicelocator.client.internal.EndpointTransformerImpl;
import org.talend.esb.servicelocator.client.internal.endpoint.BindingType;
import org.talend.esb.servicelocator.client.internal.endpoint.TransportType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleEndpointTest {

    private  byte[] content;

    private SLPropertiesImpl props;

    private SimpleEndpoint slEndpoint;

    @Before
    public void setUp() throws Exception {
        props = new SLPropertiesImpl();
        props.addProperty(NAME_1, VALUE_1, VALUE_2);

        content = createContent(
                ENDPOINT_1,
                LAST_TIME_STARTED,
                LAST_TIME_STOPPED,
                BindingType.JAXRS,
                TransportType.HTTPS,
                props);

        slEndpoint = new EndpointTransformerImpl().toSLEndpoint(SERVICE_QNAME_1, content, false);
    }

    @Test
    public void forService() {
        assertEquals(SERVICE_QNAME_1, slEndpoint.forService());
    }

    @Test
    public void getAddress() {
        assertEquals(ENDPOINT_1, slEndpoint.getAddress());
    }

    @Test
    public void getBinding() {
        assertEquals(org.talend.esb.servicelocator.client.BindingType.JAXRS, slEndpoint.getBinding());
    }

    @Test
    public void getBindingNoneExplicitlyDefined() {
        slEndpoint = new SimpleEndpoint(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(org.talend.esb.servicelocator.client.BindingType.SOAP11, slEndpoint.getBinding());
    }

    @Test
    public void getTransport() {
        assertEquals(org.talend.esb.servicelocator.client.TransportType.HTTPS, slEndpoint.getTransport());
    }

    @Test
    public void getTransportNoneExplicitlyDefined() {
        slEndpoint = new SimpleEndpoint(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(org.talend.esb.servicelocator.client.TransportType.HTTP, slEndpoint.getTransport());
    }

//    @Test
//    public void getLastTimeStarted() {
//        assertEquals(LAST_TIME_STARTED, slEndpoint.getLastTimeStarted());
//    }

//    @Test
//    public void getLastTimeStopped() {
//        assertEquals(LAST_TIME_STOPPED, slEndpoint.getLastTimeStopped());
//    }

    @Test
    public void getProperties() {
        SLProperties props = slEndpoint.getProperties();
        assertTrue(props.hasProperty(NAME_1));
        assertThat(props.getValues(NAME_1), containsInAnyOrder(VALUE_1, VALUE_2));
    }

    @Test
    public void getAddressInvalidContent() {
        slEndpoint = new EndpointTransformerImpl().toSLEndpoint(SERVICE_QNAME_1, createContentInvalidEPR(), true);

        assertNull(slEndpoint.getAddress());
    }

    @Test
    public void getPropertiesNotDefinedInContent() {
        content = createContent(ENDPOINT_1, LAST_TIME_STARTED, LAST_TIME_STOPPED, null);
        slEndpoint = new EndpointTransformerImpl().toSLEndpoint(SERVICE_QNAME_1, content, true);

        SLProperties properties = slEndpoint.getProperties();
        assertThat(properties.getPropertyNames(), hasSize(0));
    }

    @Test
    public void writeEndpointReferenceTo() throws Exception {
        PropertiesTransformer transformer = createNiceMock(PropertiesTransformer.class);
        replay(transformer);
        SimpleEndpoint epp = new SimpleEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult, transformer);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
            hasXPath("/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
        assertThat(root,
                not(hasXPath("/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT)));
    }

    @Test
    public void writeEndpointReferenceToWithPropertiesSpecified() throws Exception {
        PropertiesTransformer transformer = createMock(PropertiesTransformer.class);
        transformer.writePropertiesTo(eq(PROPERTIES), anyDOMResult());
        replay(transformer);

        SimpleEndpoint epp = new SimpleEndpoint(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult, transformer);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
                hasXPath("/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT));

        verify (transformer);
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
                Element epr = newDocument("http://talend.org/schemas/esb/locator/content/20011/11",
                        "ServiceLocatorProperties");

                result.setNode(epr.getParentNode());
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
        }
    }

}
