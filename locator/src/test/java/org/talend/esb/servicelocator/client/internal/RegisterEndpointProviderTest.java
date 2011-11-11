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

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.TestContent;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.TransportType;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestValues.*;
import static org.talend.esb.servicelocator.client.internal.EndpointStubFactory.create;

public class RegisterEndpointProviderTest extends AbstractServiceLocatorImplTest {

    public static final byte[] OLD_DATA = TestContent.CONTENT_ANY_1;

    public static final byte[] NEW_DATA = TestContent.CONTENT_ANY_2;

    private EndpointTransformer trans = createMock(EndpointTransformer.class);

    private SLEndpoint slEndpointStub;
 
    @Before
    public void setUp() {
        super.setUp();
        
        slEndpointStub = createMock(SLEndpoint.class);
        expect(slEndpointStub.getLastTimeStopped()).andStubReturn(LAST_TIME_STOPPED);
        expect(slEndpointStub.getLastTimeStarted()).andStubReturn(LAST_TIME_STARTED);
    }

    @Test
    public void registerServiceExistsEndpointExistsCheckLastTimes() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);
        Capture<Long> lastTimeStartedCapture = new Capture<Long>();

        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        getData(ENDPOINT_PATH_11, OLD_DATA);
        data2Ep(SERVICE_QNAME_1, OLD_DATA);
        ep2Data(endpoint, lastTimeStartedCapture, LAST_TIME_STOPPED, NEW_DATA);
        setData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatus(ENDPOINT_PATH_11);
        
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);

        long beforeRegister = System.currentTimeMillis();
        slc.register(endpoint);
        long afterRegister = System.currentTimeMillis();

        verifyAll();
        long lastTimeStarted = lastTimeStartedCapture.getValue();
        assertTrue(beforeRegister <= lastTimeStarted && lastTimeStarted <= afterRegister);
    }

    @Test
    public void registerEndpointStatusExists() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);

        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        getData(ENDPOINT_PATH_11, OLD_DATA);
        data2Ep(SERVICE_QNAME_1, OLD_DATA);
        ep2Data(endpoint, NEW_DATA);
        setData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatusFails(ENDPOINT_PATH_11);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);

        slc.register(endpoint);

        verifyAll();
    }

    @Test
    public void registerServiceExistsEndpointExistsNot() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);

        serviceExists(SERVICE_PATH_1);
        endpointExistsNot(ENDPOINT_PATH_11);

        ep2Data(endpoint, NEW_DATA);
        createEndpointAndSetData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatus(ENDPOINT_PATH_11);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);
        slc.register(endpoint);

        verifyAll();
    }

    @Test
    public void registerEndpointPersistently() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);

        serviceExists(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        ep2Data(endpoint, NEW_DATA);
        createEndpointAndSetData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatus(ENDPOINT_PATH_11, true);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);
        slc.register(endpoint, true);

        verifyAll();
    }

    @Test
    public void registerServiceExistsNot() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);

        serviceExistsNot(SERVICE_PATH_1);
        createService(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        ep2Data(endpoint, NEW_DATA);
        createEndpointAndSetData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatus(ENDPOINT_PATH_11);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);
        slc.register(endpoint);

        verifyAll();
    }

    @Test
    public void registerServiceExistsNotButConcurrentlyCreated() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1);

        serviceExistsNot(SERVICE_PATH_1);
        createServiceFails(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        ep2Data(endpoint, NEW_DATA);
        createEndpointAndSetData(ENDPOINT_PATH_11, NEW_DATA);

        createEndpointStatus(ENDPOINT_PATH_11);


        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);
        slc.register(endpoint);

        verifyAll();
    }

    @Test
    public void unregister() throws Exception {
        Endpoint endpoint = create(SERVICE_QNAME_1, ENDPOINT_1, BindingType.JAXRS, TransportType.HTTP);

        Capture<Long> lastTimeStoppedCapture = new Capture<Long>();

        endpointExists(ENDPOINT_PATH_11);
        getData(ENDPOINT_PATH_11, OLD_DATA);
        data2Ep(SERVICE_QNAME_1, OLD_DATA);

        deleteEndpointStatus(ENDPOINT_PATH_11);
        ep2Data(endpoint, LAST_TIME_STARTED, lastTimeStoppedCapture, NEW_DATA);
        setData(ENDPOINT_PATH_11, NEW_DATA);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);

        long beforeUnregister = System.currentTimeMillis();
        slc.unregister(endpoint);
        long afterUnregister = System.currentTimeMillis();

        verifyAll();

        long lastTimeStopped = lastTimeStoppedCapture.getValue();
        assertTrue(beforeUnregister <= lastTimeStopped && lastTimeStopped <= afterUnregister);
    }

    @Test
    public void unregisterEndpointExistsNot() throws Exception {
        endpointExistsNot(ENDPOINT_PATH_11);

        Endpoint eprProvider = create(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);

        slc.unregister(eprProvider);

        verifyAll();
    }

    @Test
    public void unregisterEndpointDeleteFails() throws Exception {
        endpointExists(ENDPOINT_PATH_11);
        getData(ENDPOINT_PATH_11, OLD_DATA);
        data2Ep(SERVICE_QNAME_1, OLD_DATA);

        delete(ENDPOINT_STATUS_PATH_11, new KeeperException.RuntimeInconsistencyException());

        Endpoint eprProvider = create(SERVICE_QNAME_1, ENDPOINT_1);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorSuccess();
        slc.setEndpointTransformer(trans);

        try {
            slc.unregister(eprProvider);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    public void ep2Data(Endpoint endpoint, Capture<Long> lastTimeStartedCapture, long lastTimeStopped, byte[] content)
    throws ServiceLocatorException {
        expect(trans.fromEndpoint(eq(endpoint), capture(lastTimeStartedCapture), eq(lastTimeStopped)))
        .andReturn(content);
    }

    public void ep2Data(Endpoint endpoint, long lastTimeStarted, Capture<Long> lastTimeStoppedCapture, byte[] content)
    throws ServiceLocatorException {
        expect(trans.fromEndpoint(eq(endpoint), eq(lastTimeStarted), capture(lastTimeStoppedCapture) ))
        .andReturn(content);
    }

    public void ep2Data(Endpoint endpoint, byte[] content)
    throws ServiceLocatorException {
        expect(trans.fromEndpoint(eq(endpoint), anyLong(), anyLong()))
        .andReturn(content);
    }

    public void data2Ep(QName serviceName, byte[] content) {
        expect(trans.toSLEndpoint(serviceName, content, false)).andReturn(slEndpointStub);        
    }

    private void serviceExists(String path) throws KeeperException, InterruptedException {
        pathExists(path);
    }

    private void serviceExistsNot(String path) throws KeeperException, InterruptedException {
        pathExistsNot(path);
    }

    private void endpointExists(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(new Stat()).atLeastOnce();
    }

    private void endpointExistsNot(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(null).atLeastOnce();
    }

    private void createService(String path) throws KeeperException, InterruptedException {
        createNode(path, PERSISTENT);
    }

    private void createServiceFails(String path) throws KeeperException, InterruptedException {
        createNode(path, PERSISTENT, new KeeperException.NodeExistsException());
    }

    private void createEndpointAndSetData(String path, byte[] content) throws KeeperException, InterruptedException {
        expect(zkMock.create(path, content, Ids.OPEN_ACL_UNSAFE, PERSISTENT)).andReturn(path);
    }

    private void createEndpointStatus(String endpointPath)
        throws KeeperException, InterruptedException {
        createEndpointStatus(endpointPath, false);
    }

    private void createEndpointStatus(String endpointPath, boolean persistent)
        throws KeeperException, InterruptedException {
        String endpointStatusPath = endpointPath + "/" + STATUS_NODE;
        CreateMode mode = persistent ? PERSISTENT : EPHEMERAL; 

        createNode(endpointStatusPath, mode);
    }

    private void deleteEndpointStatus(String endpointPath)
        throws KeeperException, InterruptedException {
        String endpointStatusPath = endpointPath + "/" + STATUS_NODE;
        delete(endpointStatusPath);
    }   

    private void createEndpointStatusFails(String endpointPath)
        throws KeeperException, InterruptedException {
        String endpointStatusPath = endpointPath + "/" + STATUS_NODE;
        createNode(endpointStatusPath, EPHEMERAL, new KeeperException.NodeExistsException());
    }
}
