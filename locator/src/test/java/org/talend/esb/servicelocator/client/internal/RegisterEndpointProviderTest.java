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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.NamespaceContextImpl;
import org.talend.esb.servicelocator.client.EndpointProvider;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.CreateMode.PERSISTENT;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.talend.esb.servicelocator.TestValues.*;

public class RegisterEndpointProviderTest extends AbstractServiceLocatorImplTest {

    private Capture<byte[]> contentCapture;

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
            createEPProviderStub(SERVICE_QNAME_1, ENDPOINT_1);

        replayAll();

        ServiceLocatorImpl slc = createServiceLocatorAndConnect();
        slc.register(eprProvider);

        Document contentAsXML = capturedContentAsXML();
        DomMother.serialize(contentAsXML, System.out);

        NamespaceContext edpNamespaceContext = 
            new NamespaceContextImpl("sl", "http://talend.org/esb/serviceLocator/4.2")
            .add("wsa", "http://www.w3.org/2005/08/addressing");


        assertThat(contentAsXML, hasXPath("/sl:EndpointData", edpNamespaceContext));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/sl:LastTimeStarted/text()", edpNamespaceContext));
        assertThat(contentAsXML, hasXPath("/sl:EndpointData/wsa:EndpointReference", edpNamespaceContext));
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
        contentCapture = new Capture<byte[]>();
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
        contentCapture = new Capture<byte[]>();
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

    private Document capturedContentAsXML() {
        byte[] content = contentCapture.getValue();
        return DomMother.parse(content);
    }

    private EndpointProvider createEPProviderStub(QName serviceName, String endpoint) {
        EndpointProvider eprProvider = createNiceMock(EndpointProvider.class);
        expect(eprProvider.getServiceName()).andStubReturn(serviceName);
        expect(eprProvider.getAddress()).andStubReturn(endpoint);
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
