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
package org.talend.esb.servicelocator.client.internal;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.*;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLEndpoint;

public class GetEndpointsTest extends AbstractServiceLocatorImplTest {

    @Test
    public void getEndpointsEndpointIsLive() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1);
        pathExists(ENDPOINT_PATH_11 + "/" + STATUS_NODE);
        getContent(ENDPOINT_PATH_11, CONTENT_ENDPOINT_1);

        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertTrue(endpoint.isLive());
        verifyAll();
    }

    @Test
    public void getEndpointsEndpointIsNotLive() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1); //, ENDPOINT_NODE_2);
        pathExistsNot(ENDPOINT_PATH_11 + "/" + STATUS_NODE);
        getContent(ENDPOINT_PATH_11, CONTENT_ENDPOINT_1);

        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertFalse(endpoint.isLive());
        verifyAll();
    }

    @Test
    public void getEndpoint() throws Exception {
        pathExists(ENDPOINT_PATH_11);
        pathExistsNot(ENDPOINT_PATH_11 + "/" + STATUS_NODE);
        getContent(ENDPOINT_PATH_11, CONTENT_ENDPOINT_1);

        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        SLEndpoint endpoint = slc.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        assertFalse(endpoint.isLive());
        assertEquals(SERVICE_QNAME_1, endpoint.forService());
        assertEquals(LAST_TIME_STARTED, endpoint.getLastTimeStarted());
        verifyAll();
    }

    @Test
    public void getEndpointExistsNot() throws Exception {
        pathExistsNot(ENDPOINT_PATH_11);
        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        SLEndpoint endpoint = slc.getEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        assertNull(endpoint);
        verifyAll();
    }

    protected void getContent(String path, byte[] content) throws KeeperException,
            InterruptedException {
        expect(zkMock.getData(eq(path), eq(false), (Stat) anyObject())).andReturn(content);
    }


}
