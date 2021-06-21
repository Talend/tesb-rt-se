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
package org.talend.esb.servicelocator.cxf.internal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.DomMother.newDocument;
import static org.talend.esb.servicelocator.NamespaceContextImpl.SL_NS;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.NAME_2;
import static org.talend.esb.servicelocator.TestValues.PROPERTIES;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;
import static org.talend.esb.servicelocator.client.BindingType.JAXRS;
import static org.talend.esb.servicelocator.client.BindingType.SOAP11;
import static org.talend.esb.servicelocator.client.BindingType.SOAP12;
import static org.talend.esb.servicelocator.client.TransportType.HTTP;
import static org.talend.esb.servicelocator.cxf.internal.CXFEndpointProvider.JAXRS_BINDING_ID;
import static org.talend.esb.servicelocator.cxf.internal.CXFEndpointProvider.SOAP11_BINDING_ID;
import static org.talend.esb.servicelocator.cxf.internal.CXFEndpointProvider.SOAP12_BINDING_ID;
import static org.talend.esb.servicelocator.cxf.internal.CXFEndpointProvider.SOAP_HTTP_TRANSPORT_ID;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_2;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.createJAXRSServerStub;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.createServerStub;

import java.util.Collection;

import javax.xml.transform.dom.DOMResult;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.junit.Test;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.TransportType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CXFEndpointProviderTest {

    @Test
    public void getServiceName() {
        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
    }

    @Test
    public void getAddress() {
        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_2);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_2, epr);
        assertEquals(ENDPOINT_2, epp.getAddress());
    }

    @Test
    public void getBinding() {
        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_2);
        CXFEndpointProvider epp =
            new CXFEndpointProvider(SERVICE_QNAME_2, SOAP11_BINDING_ID, SOAP_HTTP_TRANSPORT_ID,  epr);
        assertEquals(BindingType.SOAP11, epp.getBinding());
    }

    @Test
    public void getTransport() {
        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_2);
        CXFEndpointProvider epp =
            new CXFEndpointProvider(SERVICE_QNAME_2, SOAP11_BINDING_ID, SOAP_HTTP_TRANSPORT_ID, epr);
        assertEquals(TransportType.HTTP, epp.getTransport());
    }

    @Test
    public void getProperties() {
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        SLProperties properties = epp.getProperties();

        assertTrue(properties.hasProperty(NAME_1));
        assertTrue(properties.hasProperty(NAME_2));
    }

    @Test
    public void getPropertiesNotDefined() {
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, null);

        SLProperties properties = epp.getProperties();
        Collection<String> keys = properties.getPropertyNames();
        assertThat(keys, empty());
    }


    @Test
    public void writeEndpointReferenceToWithEprGiven() throws Exception {
        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult, null);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();
        assertThat(root,
            hasXPath("/wsa:EndpointReference/wsa:Address", WSA_SL_NS_CONTEXT));
    }

    @Test
    public void writeEndpointReferenceToWithEndpointAndPropertiesGiven() throws Exception {
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult, null);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
            hasXPath("/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
        assertThat(root,
                hasXPath("/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT));
    }

    @Test
    public void writeEndpointReferenceToWithServerEndpointAndPropertiesGiven()  throws Exception {
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVER_2, ENDPOINT_1, PROPERTIES);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult, null);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
            hasXPath("/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
    }

    @Test
    public void addEndpointReferenceWithEprGiven() throws Exception {
        Element root = newDocument(SL_NS, "EndpointData");

        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);
        epp.addEndpointReference(root);

        assertThat(root,
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address", WSA_SL_NS_CONTEXT));
    }

    @Test
    public void addEndpointReferenceWithEndpointAndPropertiesGiven() throws Exception {
        Element root = newDocument(SL_NS, "EndpointData");

        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        epp.addEndpointReference(root);

        assertThat(root,
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
        assertThat(root,
                hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT));
    }

    @Test
    public void addEndpointReferenceWithServerEndpointAndPropertiesGiven()  throws Exception {
        Element root = newDocument(SL_NS, "EndpointData");

        CXFEndpointProvider epp = new CXFEndpointProvider(SERVER_2, ENDPOINT_1, PROPERTIES);
        epp.addEndpointReference(root);

        assertThat(root,
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
    }

    @Test
    public void addServerWithNameGivenInJaxrsStyle()  throws Exception {
        Server server = createJAXRSServerStub(SERVICE_QNAME_2, ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(SERVICE_QNAME_2, epp.getServiceName());
    }

    @Test
    public void addServerWithSOAP11BindingGiven()  throws Exception {
        Server server =
            createServerStub(SERVICE_QNAME_2, ENDPOINT_1, SOAP11_BINDING_ID, SOAP_HTTP_TRANSPORT_ID);
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(SOAP11, epp.getBinding());
    }

    @Test
    public void addServerWithSOAP12BindingGiven()  throws Exception {
        Server server =
            createServerStub(SERVICE_QNAME_2, ENDPOINT_1, SOAP12_BINDING_ID, SOAP_HTTP_TRANSPORT_ID);
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(SOAP12, epp.getBinding());
    }

    @Test
    public void addServerWithJAXRSBindingGiven()  throws Exception {
        Server server =
            createServerStub(SERVICE_QNAME_2, ENDPOINT_1, JAXRS_BINDING_ID, SOAP_HTTP_TRANSPORT_ID);
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(JAXRS, epp.getBinding());
    }

    @Test
    public void addServerWithHTTPTransportGiven()  throws Exception {
        Server server =
            createServerStub(SERVICE_QNAME_2, ENDPOINT_1, SOAP11_BINDING_ID, SOAP_HTTP_TRANSPORT_ID);
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(HTTP, epp.getTransport());
    }

    @Test
    public void addServerWithUnknownTransportGiven()  throws Exception {
        Server server = createServerStub(SERVICE_QNAME_2, ENDPOINT_1, SOAP11_BINDING_ID, "unknown");
        CXFEndpointProvider epp = new CXFEndpointProvider(server, ENDPOINT_1, PROPERTIES);

        assertEquals(TransportType.OTHER, epp.getTransport());
    }

    @Test
    public void equalsWhenAddressAndServiceEquivalent() {
        CXFEndpointProvider endpoint = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        CXFEndpointProvider other = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        assertTrue(endpoint.equals(other));
    }

    @Test
    public void nonEqualsWhenAddressNonEquivalent() {
        CXFEndpointProvider endpoint = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        CXFEndpointProvider other = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_2, PROPERTIES);

        assertFalse(endpoint.equals(other));
    }

    @Test
    public void nonEqualsWhenServiceNonEquivalent() {
        CXFEndpointProvider endpoint = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        CXFEndpointProvider other = new CXFEndpointProvider(SERVICE_QNAME_2, ENDPOINT_1, PROPERTIES);

        assertFalse(endpoint.equals(other));
    }

    @Test
    public void nonEqualsWhenOtherNull() {
        CXFEndpointProvider endpoint = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        assertFalse(endpoint.equals(null));
    }

    @Test
    public void nonEqualsWhenOtherDifferentType() {
        CXFEndpointProvider endpoint = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        assertFalse(endpoint.equals(new Object()));
    }
}
