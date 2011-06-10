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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.EndpointProvider;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.TransportType;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.NamespaceContextImpl.WSA_SL_NS_CONTEXT;
import static org.talend.esb.servicelocator.TestValues.*;

public class RegisterEndpointProviderTest extends AbstractServiceLocatorImplTest {

    @Test
    public void registerServiceExistsEndpointExists() throws Exception {
        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);
        createEndpointStatus(ENDPOINT_PATH_11);

        EndpointProvider eprProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(eprProvider);

        verifyAll();
    }

    @Test
    public void registerEndpointStatusExists() throws Exception {
        serviceExists(SERVICE_PATH_1);
        endpointExists(ENDPOINT_PATH_11);

        createEndpointStatusFails(ENDPOINT_PATH_11);

        EndpointProvider epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

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
        createEndpoint(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        EndpointProvider epProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(epProvider);

        verifyAll();
    }

    @Test
    public void registerServiceExistsNot() throws Exception {
        serviceExistsNot(SERVICE_PATH_1);
        createService(SERVICE_PATH_1);

        endpointExistsNot(ENDPOINT_PATH_11);
        createEndpoint(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        EndpointProvider epProvider = 
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
        createEndpoint(ENDPOINT_PATH_11);

        createEndpointStatus(ENDPOINT_PATH_11);

        EndpointProvider epProvider = 
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
        createEndpointStatus(ENDPOINT_PATH_11);

        EndpointProvider eprProvider = 
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1, BindingType.JAXRS, TransportType.HTTP);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(eprProvider);

        Document contentAsXML = capturedContentAsXML();

        assertThat(contentAsXML, hasXPath("/sl:EndpointData", WSA_SL_NS_CONTEXT));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStarted/text()", WSA_SL_NS_CONTEXT));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Binding/text()", WSA_SL_NS_CONTEXT));        
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:Transport/text()", WSA_SL_NS_CONTEXT));        
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/wsa:EndpointReference", WSA_SL_NS_CONTEXT));
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
        expect(zkMock.setData(eq(path), capture(contentCapture), eq(-1))).andReturn(new Stat());
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

    private void createEndpoint(String path) throws KeeperException, InterruptedException {
        expect(zkMock.create(eq(path), capture(contentCapture), eq(Ids.OPEN_ACL_UNSAFE), eq(PERSISTENT))).andReturn(path);
    }

    private void createEndpointStatus(String endpointPath)
        throws KeeperException, InterruptedException {
        String endpointStatusPath = endpointPath + "/" + STATUS_NODE;
        createNode(endpointStatusPath, EPHEMERAL);
    }

    private void createEndpointStatusFails(String endpointPath)
        throws KeeperException, InterruptedException {
        String endpointStatusPath = endpointPath + "/" + STATUS_NODE;
        createNode(endpointStatusPath, EPHEMERAL,new KeeperException.NodeExistsException());
    }

    private EndpointProvider createEPProviderStub(QName serviceName, String endpoint) throws Exception {
        return createEPProviderStub(serviceName, endpoint, BindingType.JAXRS, TransportType.HTTP);
    }
    private EndpointProvider createEPProviderStub(QName serviceName, String endpoint,
            BindingType bindingType, TransportType transportType) throws Exception {
        EndpointProvider eprProvider = createNiceMock(EndpointProvider.class);
        expect(eprProvider.getServiceName()).andStubReturn(serviceName);
        expect(eprProvider.getAddress()).andStubReturn(endpoint);
        expect(eprProvider.getBinding()).andStubReturn(bindingType);
        expect(eprProvider.getTransport()).andStubReturn(transportType);
        eprProvider.addEndpointReference( anyDOM());

        return eprProvider;
    }

    public static Node anyDOM() {
        EasyMock.reportMatcher(new AddChildElementMatcher());
        return null;
    }
    public static class AddChildElementMatcher implements IArgumentMatcher {

        @Override
        public boolean matches(Object argument) {
            if (argument != null && argument instanceof Node) {
                Node parent = (Node) argument;
                Document doc = (parent instanceof Document) ? (Document) parent : parent.getOwnerDocument();
                Element epr = doc.createElementNS("http://www.w3.org/2005/08/addressing", "EndpointReference");
                parent.appendChild(epr);
                return true;
            }
            return false;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            // TODO Auto-generated method stub
            
        }
        
    }
}
