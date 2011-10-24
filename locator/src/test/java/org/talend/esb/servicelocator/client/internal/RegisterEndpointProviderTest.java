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
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.TransportType;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.DomMother.newDocument;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.*;

public class RegisterEndpointProviderTest extends AbstractServiceLocatorImplTest {

    @Test
    public void registerServiceExistsEndpointExists() throws Exception {
        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        setData(ENDPOINT_PATH_11);
        createEndpointStatus(ENDPOINT_PATH_11);

        Endpoint eprProvider = createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(eprProvider);

        verifyAll();
    }

    @Test
    public void registerEndpointStatusExists() throws Exception {
        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        setData(ENDPOINT_PATH_11);

        createEndpointStatusFails(ENDPOINT_PATH_11);

        Endpoint epProvider = createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        try {
            slc.register(epProvider);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }
    }

    @Test
    public void registerServiceExistsEndpointExistsNot() throws Exception {
        serviceExists(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpointAndSetData(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        Endpoint epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider);

        verifyAll();
    }

    @Test
    public void registerEndpointPersistently() throws Exception {
        serviceExists(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpointAndSetData(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11, true);

        Endpoint epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider, true);

        verifyAll();
    }

    @Test
    public void registerEndpointWithProperties() throws Exception {
        serviceExists(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpointAndSetData(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11, true);

        Endpoint epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider, true);

        verifyAll();
    }

    @Test
    public void registerServiceExistsNot() throws Exception {
        serviceExistsNot(SERVICE_PATH_1);
        createService(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpointAndSetData(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        Endpoint epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider);

        verifyAll();
    }

    @Test
    public void registerServiceExistsNotButConcurrentlyCreated() throws Exception {
        serviceExistsNot(SERVICE_PATH_1);
        createServiceFails(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpointAndSetData(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        Endpoint epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider);

        verifyAll();
    }

    @Test
    public void registerWithEndpointReferenceProviderCheckContent() throws Exception {
        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        setData(ENDPOINT_PATH_11);
        createEndpointStatus(ENDPOINT_PATH_11);

        Endpoint eprProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1, BindingType.JAXRS,
                    TransportType.HTTP, LAST_TIME_STARTED, LAST_TIME_STOPPED);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(eprProvider);

        Document contentAsXML = capturedContentAsXML();

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
        verifyAll();
    }
    
    @Test
    public void unregister() throws Exception {
        endpointExists(ENDPOINT_PATH_11);
        deleteEndpointStatus(ENDPOINT_PATH_11);
        setData(ENDPOINT_PATH_11);

        Endpoint eprProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1, BindingType.JAXRS,
                    TransportType.HTTP, LAST_TIME_STARTED, LAST_TIME_STOPPED);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.unregister(eprProvider);

        Document contentAsXML = capturedContentAsXML();

        assertThat(contentAsXML, hasXPath("/sl:EndpointData", WSA_SL_NS_CONTEXT));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStarted/text()",
                WSA_SL_NS_CONTEXT, equalTo(Long.toString(LAST_TIME_STARTED))));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStopped/text()",
                WSA_SL_NS_CONTEXT, equalTo(Long.toString(LAST_TIME_STOPPED))));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Binding/text()",
                WSA_SL_NS_CONTEXT, equalTo(BindingType.JAXRS.getValue())));        
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Transport/text()",
                WSA_SL_NS_CONTEXT, equalTo(TransportType.HTTP.getValue())));        
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/wsa:EndpointReference",
                WSA_SL_NS_CONTEXT));

        verifyAll();
    }

    @Test
    public void unregisterEndpointExistsNot() throws Exception {
        endpointExistsNot(ENDPOINT_PATH_11);

        Endpoint eprProvider = createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.unregister(eprProvider);

        verifyAll();
    }
    
    @Test
    public void unregisterEndpointDeleteFails() throws Exception {
        endpointExists(ENDPOINT_PATH_11);
        delete(ENDPOINT_STATUS_PATH_11, new KeeperException.RuntimeInconsistencyException());

        Endpoint eprProvider = createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);
        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        try {
            slc.unregister(eprProvider);
            fail("A ServiceLocatorException should have been thrown.");
        } catch (ServiceLocatorException e) {
            ignore("Expected exception");
        }

        verifyAll();
    }

    private void serviceExists(String path) throws KeeperException, InterruptedException {
        pathExists(path);
    }

    private void serviceExistsNot(String path) throws KeeperException, InterruptedException {
        pathExistsNot(path);
    }

    private void endpointExists(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(new Stat());
//        expect(zkMock.setData(eq(path), capture(contentCapture), eq(-1))).andReturn(new Stat());
    }

    private void endpointExistsNot(String path) throws KeeperException, InterruptedException {
        expect(zkMock.exists(path, false)).andReturn(null);
    }

    private void createService(String path) throws KeeperException, InterruptedException {
        createNode(path, PERSISTENT);
    }

    private void createServiceFails(String path) throws KeeperException, InterruptedException {
        createNode(path, PERSISTENT, new KeeperException.NodeExistsException());
    }

    private void createEndpointAndSetData(String path) throws KeeperException, InterruptedException {
        expect(zkMock.create(eq(path),
                capture(contentCapture),
                eq(Ids.OPEN_ACL_UNSAFE),
                eq(PERSISTENT))).andReturn(path);
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

    protected Endpoint createEPProviderStub(QName serviceName, String endpoint) throws Exception {
        return createEPProviderStub(serviceName, endpoint, BindingType.JAXRS, TransportType.HTTP, -1, -1);
    }

    protected  Endpoint createEPProviderStub(QName serviceName, String endpoint,
            BindingType bindingType, TransportType transportType, long lastTimeStarted, long lastTimeStopped)
        throws Exception {
        
        Endpoint eprProvider = createNiceMock(Endpoint.class);
        expect(eprProvider.getServiceName()).andStubReturn(serviceName);
        expect(eprProvider.getAddress()).andStubReturn(endpoint);
        expect(eprProvider.getBinding()).andStubReturn(bindingType);
        expect(eprProvider.getTransport()).andStubReturn(transportType);
        expect(eprProvider.getLastTimeStarted()).andStubReturn(lastTimeStarted);
        expect(eprProvider.getLastTimeStopped()).andStubReturn(lastTimeStopped);
        eprProvider.writeEndpointReferenceTo(anyDOMResult(), (Endpoint.PropertiesTransformer) EasyMock.anyObject());
        expectLastCall().asStub();

        return eprProvider;
    }

    public static DOMResult anyDOMResult() {
        EasyMock.reportMatcher(new SetNodeMatcher());
        return null;
    }

    public static class SetNodeMatcher implements IArgumentMatcher {

        @Override
        public boolean matches(Object argument) {
            if (argument != null && argument instanceof DOMResult) {
                DOMResult result = (DOMResult) argument;
                Element epr = newDocument("http://www.w3.org/2005/08/addressing", "EndpointReference");

                result.setNode(epr.getOwnerDocument());
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
        }
    }
}
