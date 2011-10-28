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
import org.hamcrest.Matcher;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestContent.createContent;
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

        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        try {
            slc.register(SERVICE_QNAME_1, ENDPOINT_1);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    @Test
    public void removeEndpoint() throws Exception {
        delete(ENDPOINT_STATUS_PATH_11);
        delete(ENDPOINT_PATH_11);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.removeEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        verifyAll();
    }

    @Test
    public void removeEndpointNotLiving() throws Exception {
        delete(ENDPOINT_STATUS_PATH_11, new KeeperException.NoNodeException());
        delete(ENDPOINT_PATH_11);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.removeEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        verifyAll();
    }

    @Test
    public void removeEndpointNotExistingAtAll() throws Exception {
        delete(ENDPOINT_STATUS_PATH_11, new KeeperException.NoNodeException());
        delete(ENDPOINT_PATH_11, new KeeperException.NoNodeException());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.removeEndpoint(SERVICE_QNAME_1, ENDPOINT_1);

        verifyAll();
    }

    @Test
    public void removeEndpointFails() throws Exception {
        delete(ENDPOINT_STATUS_PATH_11);
        delete(ENDPOINT_PATH_11, new KeeperException.RuntimeInconsistencyException());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        try {
            slc.removeEndpoint(SERVICE_QNAME_1, ENDPOINT_1);
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
        getData(ENDPOINT_PATH_11, createContent(PROPERTIES_1));

        pathExistsNot(ENDPOINT_STATUS_PATH_12);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

        assertThat(endpoints, hasItem(ENDPOINT_1));
        verifyAll();
    }

    @Test
    public void lookupServiceKnownEndpointsAvailableWithProperties() throws Exception {
        SLPropertiesMatcher matcher = new SLPropertiesMatcher();
        matcher.addAssertion(NAME_1, VALUE_2);
        
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1, ENDPOINT_NODE_2);

        pathExists(ENDPOINT_STATUS_PATH_11);
        getData(ENDPOINT_PATH_11, createContent(PROPERTIES_1));
        
        pathExists(ENDPOINT_STATUS_PATH_12);
        getData(ENDPOINT_PATH_12, createContent(PROPERTIES_2));

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        List<String> endpoints = slc.lookup(SERVICE_QNAME_1, matcher);
        
        assertThat(endpoints, containsInAnyOrder(ENDPOINT_1));
        verifyAll();
    }

    @Test
    public void lookupServiceNotKnown() throws Exception {
        pathExistsNot(SERVICE_PATH_1);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        List<String> endpoints = slc.lookup(SERVICE_QNAME_1);

        Matcher<Iterable<String>> emptyStringIterable = emptyIterable();
        assertThat(endpoints, emptyStringIterable);
        verifyAll();
    }

    @Test
    public void getServicesSuccessful() throws Exception {
        getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(), SERVICE_NAME_1, SERVICE_NAME_2);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        List<QName> services = slc.getServices();

        assertThat(services, containsInAnyOrder(SERVICE_QNAME_1, SERVICE_QNAME_2));
        verifyAll();
    }

    @Test
    public void failureWhenGettingServices() throws Exception {
        getChildren(ServiceLocatorImpl.LOCATOR_ROOT_PATH.toString(),
                new KeeperException.RuntimeInconsistencyException());
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();

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

        ServiceLocatorImpl slc = createServiceLocatorSuccess();

        List<String> endpoints = slc.getEndpointNames(SERVICE_QNAME_1);

        assertThat(endpoints, containsInAnyOrder(ENDPOINT_1, ENDPOINT_2));
        verifyAll();
    }
}
