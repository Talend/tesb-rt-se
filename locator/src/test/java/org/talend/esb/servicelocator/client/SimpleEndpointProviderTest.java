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

package org.talend.esb.servicelocator.client;

import java.util.Collection;

import javax.xml.transform.dom.DOMResult;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.NAME_2;
import static org.talend.esb.servicelocator.TestValues.PROPERTIES;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

public class SimpleEndpointProviderTest {

    @Test
    public void getServiceName() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
    }

    @Test
    public void getAddress() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(ENDPOINT_1, epp.getAddress());
    }

    @Test
    public void getBinding() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(BindingType.SOAP11, epp.getBinding());
    }

    @Test
    public void getTransport() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(TransportType.HTTP, epp.getTransport());
    }

    @Test
    public void getLastTimeStarted() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(-1, epp.getLastTimeStarted());
    }

    @Test
    public void getLastTimeStopped() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        assertEquals(-1, epp.getLastTimeStopped());
    }

    @Test
    public void getPropertiesNoOneDefined() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);
        SLProperties props = epp.getProperties();
        
        Matcher<Collection<String>> emptyStringColl = empty();
        assertThat(props.getPropertyNames(), emptyStringColl);
    }
    
    @Test
    public void getPropertiesSomeDefined() {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        SLProperties props = epp.getProperties();
 
        assertThat(props.getPropertyNames(), containsInAnyOrder(NAME_1, NAME_2));
    }

    @Test
    public void writeEndpointReferenceTo() throws Exception {
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult);
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
        SimpleEndpointProvider epp = new SimpleEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);

        DOMResult domResult = new DOMResult();
        epp.writeEndpointReferenceTo(domResult);
        Document doc = (Document) domResult.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
                hasXPath("/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT));
    }
}
