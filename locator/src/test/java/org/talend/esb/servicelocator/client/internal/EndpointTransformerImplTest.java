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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STARTED;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STOPPED;
import static org.talend.esb.servicelocator.TestValues.PROPERTIES_EMPTY;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

import javax.xml.transform.dom.DOMResult;

import org.junit.Test;
import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.TransportType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EndpointTransformerImplTest {

    @Test
    public void writePropertiesToPropertiesEmpty() throws Exception {

        EndpointTransformerImpl transformer = new EndpointTransformerImpl();
        DOMResult result = new DOMResult();
        transformer.writePropertiesTo(PROPERTIES_EMPTY, result);

        Document doc = (Document) result.getNode();
        Element root = doc.getDocumentElement();

        assertThat(root,
            hasXPath("/sl:ServiceLocatorProperties",
                WSA_SL_NS_CONTEXT));
        assertThat(root,
                hasXPath("/sl:ServiceLocatorProperties/node()",
                    WSA_SL_NS_CONTEXT, equalTo("")));
    }

    @Test
    public void fromEndpoint()  throws Exception {

            Endpoint endpoint = EndpointStubFactory.
                create(SERVICE_QNAME_1, ENDPOINT_1, BindingType.JAXRS,
                        TransportType.HTTP);

            EndpointTransformerImpl trans = new EndpointTransformerImpl();

            byte[] content = trans.fromEndpoint(endpoint, LAST_TIME_STARTED, LAST_TIME_STOPPED);
            Document contentAsXML = DomMother.parse(content);

            assertThat(contentAsXML, hasXPath("/sl:EndpointData", WSA_SL_NS_CONTEXT));
            assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStarted/text()",
                    WSA_SL_NS_CONTEXT, equalTo(Long.toString(LAST_TIME_STARTED))));
            assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStopped/text()",
                    WSA_SL_NS_CONTEXT, equalTo(Long.toString(LAST_TIME_STOPPED))));
            assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Binding/text()",
                    WSA_SL_NS_CONTEXT, equalTo(BindingType.JAXRS.getValue())));
            assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Transport/text()",
                    WSA_SL_NS_CONTEXT, equalTo(TransportType.HTTP.getValue())));
            assertThat(contentAsXML, hasXPath("/sl:EndpointData/wsa:EndpointReference", WSA_SL_NS_CONTEXT));
    }
}
