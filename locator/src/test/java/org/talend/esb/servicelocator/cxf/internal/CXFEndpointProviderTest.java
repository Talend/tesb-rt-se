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
package org.talend.esb.servicelocator.cxf.internal;

import org.apache.cxf.ws.addressing.EndpointReferenceType;

import org.junit.Test;

import org.talend.esb.servicelocator.cxf.internal.CXFTestStubs;

import org.w3c.dom.Element;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.talend.esb.DomMother.newDocument;
import static org.talend.esb.servicelocator.NamespaceContextImpl.SL_NS;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.PROPERTIES;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;
import static org.talend.esb.servicelocator.cxf.internal.CXFTestStubs.SERVER_2;

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
    public void addEndpointReferenceWithEprGiven() {
        Element root = newDocument(SL_NS, "EndpointData");

        EndpointReferenceType epr = CXFTestStubs.createEPR(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);
        epp.addEndpointReference(root);
        
//        DomMother.serialize(root, System.out);

        assertThat(root,
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address", WSA_SL_NS_CONTEXT));
    }

    @Test
    public void addEndpointReferenceWithEndpointAndPropertiesGiven() {
        Element root = newDocument(SL_NS, "EndpointData");

        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, ENDPOINT_1, PROPERTIES);
        epp.addEndpointReference(root);

//       DomMother.serialize(root, System.out);
        
        assertThat(root, 
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
        assertThat(root, 
                hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
                    WSA_SL_NS_CONTEXT));
    }

    @Test
    public void addEndpointReferenceWithServerEndpointAndPropertiesGiven() {
        Element root = newDocument(SL_NS, "EndpointData");

        CXFEndpointProvider epp = new CXFEndpointProvider(SERVER_2, ENDPOINT_1, PROPERTIES);
        epp.addEndpointReference(root);

//       DomMother.serialize(root, System.out);
        
        assertThat(root, 
            hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Address/text()", WSA_SL_NS_CONTEXT,
                equalTo(ENDPOINT_1)));
//        assertThat(root, 
//                hasXPath("/sl:EndpointData/wsa:EndpointReference/wsa:Metadata/sl:ServiceLocatorProperties",
//                    WSA_SL_NS_CONTEXT));
    }
}
