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



import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.junit.Test;
import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.NamespaceContextImpl;
import org.w3c.dom.Element;

import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.talend.esb.DomMother.newDocument;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_2;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_2;

public class CXFEndpointProviderTest {

    @Test
    public void getServiceName() {
        EndpointReferenceType epr = createEndpointReference(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);
        assertEquals(SERVICE_QNAME_1, epp.getServiceName());
    }

    @Test
    public void getAddress() {
        EndpointReferenceType epr = createEndpointReference(ENDPOINT_2);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_2, epr);
        assertEquals(ENDPOINT_2, epp.getAddress());
    }

    @Test
    public void addEndpointReference() {
        Element root = newDocument("EndpointData");

        EndpointReferenceType epr = createEndpointReference(ENDPOINT_1);
        CXFEndpointProvider epp = new CXFEndpointProvider(SERVICE_QNAME_1, epr);
        epp.addEndpointReference(root);
        
        DomMother.serialize(root, System.out);

        assertThat(root, hasXPath("/EndpointData/wsa:EndpointReference/wsa:Address",
            new NamespaceContextImpl("wsa" , "http://www.w3.org/2005/08/addressing")));
    }

    public static EndpointReferenceType createEndpointReference(String address) {
        org.apache.cxf.ws.addressing.ObjectFactory of = new org.apache.cxf.ws.addressing.ObjectFactory();
        
        AttributedURIType addr = of.createAttributedURIType();
        addr.setValue(address);
        
        EndpointReferenceType epr = of.createEndpointReferenceType();
        epr.setAddress(addr);
        return epr;
        
    }
}
