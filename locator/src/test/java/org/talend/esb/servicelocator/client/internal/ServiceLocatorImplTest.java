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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.easymock.Capture;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.talend.esb.servicelocator.TestContent;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.*;

public class ServiceLocatorImplTest extends AbstractServiceLocatorImplTest {

    @Test
    public void connect() throws Exception {
        ServiceLocatorImpl slc = createServiceLocator(true);

        pcaMock.process(slc);
        replayAll();

        slc.setPostConnectAction(pcaMock);
        slc.connect();

        verifyAll();
    }

    @Test
    public void connectFailing() throws Exception {
        ServiceLocatorImpl slc = createServiceLocator(false);

        replayAll();

        slc.setConnectionTimeout(10);
        slc.setPostConnectAction(pcaMock);

        try {
            slc.connect();
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    @Test
    public void failureWhenRegisteringService() throws Exception {
        pathExists(SERVICE_PATH_1, new KeeperException.RuntimeInconsistencyException());

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        try {
            slc.register(SERVICE_QNAME_1, ENDPOINT_1);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    @Test
    public void unregisterEndpoint() throws Exception {
        long currentTime = System.currentTimeMillis();
        Capture<byte[]> contentCapture;

        byte[] content = TestContent.createContent(ENDPOINT_1, 12345678L, -1L);
        expect(zkMock.getData(ENDPOINT_PATH_11, false, null)).andReturn(content);
        delete(ENDPOINT_STATUS_PATH_11);

        contentCapture = new Capture<byte[]>();
        expect(zkMock.setData(eq(ENDPOINT_PATH_11), capture(contentCapture), eq(-1))).andReturn(new Stat());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        slc.unregister(SERVICE_QNAME_1, ENDPOINT_1);
        
        
        byte[] updatedContent = contentCapture.getValue();
        ContentHolder holder = new ContentHolder(updatedContent);
        long newLastTimeStopped = Long.parseLong(holder.getEndpointData().getLastTimeStopped());

        assertThat(newLastTimeStopped, greaterThan(currentTime));
        verifyAll();
    }

    @Test
    public void unregisterEndpointDeleteFails() throws Exception {
        delete(ENDPOINT_STATUS_PATH_11, new KeeperException.RuntimeInconsistencyException());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        try {
            slc.unregister(SERVICE_QNAME_1, ENDPOINT_1);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    @Test
    public void lookupServiceKnownEndpointsAvailable() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1, ENDPOINT_NODE_2);

        pathExists(ENDPOINT_STATUS_PATH_11);
        pathExistsNot(ENDPOINT_STATUS_PATH_12);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

        assertThat(endpoints, hasItem(ENDPOINT_1));
        verifyAll();
    }

    @Test
    public void lookupServiceNotKnown() throws Exception {
        pathExistsNot(SERVICE_PATH_1);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

        Matcher<Iterable<String>> emptyStringIterable = emptyIterable();
        assertThat(endpoints, emptyStringIterable);
        verifyAll();
    }

    @Test
    public void getServicesSuccessful() throws Exception {
        getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(), SERVICE_NAME_1, SERVICE_NAME_2);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<QName> services = slc.getServices();

        assertThat(services, containsInAnyOrder(SERVICE_QNAME_1, SERVICE_QNAME_2));
        verifyAll();
    }

    @Test
    public void failureWhenGettingServices() throws Exception {
        getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(),
                new KeeperException.RuntimeInconsistencyException());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        try {
            slc.getServices();
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }
        verifyAll();
    }

    @Test
    public void getEndpointNames() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1, ENDPOINT_NODE_2);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<String> endpoints = slc.getEndpointNames(SERVICE_QNAME_1);

        assertThat(endpoints, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
        verifyAll();
    }
}
