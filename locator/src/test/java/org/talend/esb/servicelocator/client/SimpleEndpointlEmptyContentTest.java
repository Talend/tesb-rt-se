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

import static org.junit.Assert.assertNull;
import static org.talend.esb.servicelocator.TestValues.EMPTY_CONTENT;
import static org.talend.esb.servicelocator.TestValues.SERVICE_QNAME_1;

import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.internal.EndpointTransformerImpl;

public class SimpleEndpointlEmptyContentTest {

    private SimpleEndpoint endpoint;

    EndpointTransformerImpl transf;

    @Before
    public void setUp() throws Exception {
        transf = new EndpointTransformerImpl();
        endpoint = transf.toSLEndpoint(SERVICE_QNAME_1, EMPTY_CONTENT, true);
    }

    @Test
    public void getAddress() throws Exception {
        assertNull(endpoint.getAddress());
    }

    @Test
    public void getBinding() throws Exception {
        assertNull(endpoint.getBinding());
    }

    @Test
    public void getTransport() throws Exception {
        assertNull(endpoint.getTransport());
    }

//    @Test
//    public void getLastTimeStarted() {
//        assertEquals(-1, endpoint.getLastTimeStarted());
//    }

//    @Test
//    public void getLastTimeStopped() {
//        assertEquals(-1, endpoint.getLastTimeStopped());
//    }
}
