package org.talend.esb.locator;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.ServerRegistry;

import org.junit.Test;
import org.talend.esb.locator.ServiceLocator.PostConnectAction;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;

public class LocatorRegistrarTest {
	
	public static final QName SERVICE_NAME = new QName(
			"http://example.com/", "service1");
	public static final String ENDPOINT = "http://example.com/service1";
	public static final String ENDPOINT_NODE_1 = "http:%2F%2Fexample.com%2Fservice1";
	public static final String SERVICE_PATH = ServiceLocator.LOCATOR_ROOT_PATH
			+ "/{http:%2F%2Fexample.com%2F}service1";

	public static final String ENDPOINT_PATH = SERVICE_PATH + "/"
			+ ENDPOINT_NODE_1;

	private ServiceLocator slc = createServiceLocator(true);

	final static ZooKeeper zkMock = createMock(ZooKeeper.class);
	PostConnectAction pcaMock = createMock(PostConnectAction.class);

	LocatorRegistrar locatorRegistrar;
	Server server = createMock(Server.class);
	Endpoint endpoint = createMock(Endpoint.class);
	Endpoint endpoint1 = createMock(Endpoint.class);
	EndpointInfo endpointInfo = createMock(EndpointInfo.class);
	ServiceInfo serviceInfo = createMock(ServiceInfo.class);
	private Bus bus = BusFactory.getDefaultBus();

	@Test
	public void createLocatorRegistrar() {
		locatorRegistrar = new LocatorRegistrar();
		assert (locatorRegistrar != null);
	}

	@Test
	public void locatorRegistrarUnsucessfulStartServerWithoutLocatorSet()
			throws Exception {
		server = createServer();
		locatorRegistrar = new LocatorRegistrar();
		try {
			locatorRegistrar.startServer(server);
			fail("A NullPointerException should have been thrown.");
		} catch (NullPointerException ne) {
		}
	}

	@Test
	public void locatorRegistrarUnsucessfulNullLocatorSetFail()
			throws Exception {
		locatorRegistrar = new LocatorRegistrar();
		try {
			locatorRegistrar.setLocatorClient(null);
			fail("A NullPointerException should have been thrown.");
		} catch (NullPointerException ne) {
		}
	}

	@Test
	public void locatorRegistrarUnSucessfulInitNullBus() throws Exception {
		slc = createServiceLocator();
		pcaMock.process(slc);
		EasyMock.reset(zkMock);
		zkMock.close();	
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		replay(pcaMock, zkMock);
		slc.setPostConnectAction(pcaMock);
		slc.connect();
		slc.register(SERVICE_NAME, ENDPOINT);

		locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setLocatorClient(slc);
		locatorRegistrar.setBus(null);
		try {
			locatorRegistrar.init();
			fail("A NullPointerException should have been thrown.");
		} catch (NullPointerException ne) {
		}
		verify(pcaMock, zkMock);
	}

	@Test
	public void locatorRegistrarSucessfulStartAndStopServer() throws Exception {
		server = createServer();
		slc = createServiceLocator();
		pcaMock.process(slc);
		EasyMock.reset(zkMock);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		EasyMock.expectLastCall().times(3);
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		EasyMock.expectLastCall().times(3);
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		EasyMock.expectLastCall().times(3);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		EasyMock.expectLastCall().times(2);
		List<String> lst = new ArrayList<String>();
		lst.add("");
		expect(zkMock.getChildren(SERVICE_PATH, false)).andReturn(lst);
		replay(pcaMock, zkMock);
		slc.setPostConnectAction(pcaMock);
		slc.connect();
		slc.register(SERVICE_NAME, ENDPOINT);
		locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setLocatorClient(slc);
		locatorRegistrar.setEndpointPrefix("");		
		locatorRegistrar.startServer(server);
		server = createServer();
		locatorRegistrar.stopServer(server);
		verify(pcaMock, zkMock, server);
	}

	@Test
	public void locatorRegistrarSucessfulInit() throws Exception {
		EasyMock.reset(zkMock);
		server = createServerForInit();
		slc = createServiceLocator();
		pcaMock.process(slc);
		zkMock.close();	
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		EasyMock.expectLastCall().times(2);		
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		EasyMock.expectLastCall().times(2);
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);		
		EasyMock.expectLastCall().times(2);
		expect(
				zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]),
						eq(Ids.OPEN_ACL_UNSAFE), eq(CreateMode.EPHEMERAL)))
				.andReturn(ENDPOINT_PATH);
		EasyMock.expectLastCall().times(2);
		replay(pcaMock, zkMock);
		slc.setPostConnectAction(pcaMock);
		slc.connect();

		locatorRegistrar = new LocatorRegistrar();
		locatorRegistrar.setLocatorClient(slc);
		ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);
		serverRegistry.getServers().add(server);
		locatorRegistrar.setBus(bus);
//		locatorRegistrar.setEndpointPrefix("http://");		
		locatorRegistrar.init();
	}

	private ServiceLocator createServiceLocator() {
		return createServiceLocator(true);
	}

	private static ServiceLocator createServiceLocator(
			final boolean connectSuccessful) {
		return new ServiceLocator() {
			@Override
			protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch)
					throws IOException {
				if (connectSuccessful) {
					connectionLatch.countDown();
				}
				return zkMock;
			}
		};
	}

	private Server createServer() {
		EasyMock.reset(serviceInfo, endpointInfo, endpoint, server);
		expect(serviceInfo.getName()).andReturn(SERVICE_NAME);
		expect(endpointInfo.getService()).andReturn(serviceInfo);
		expect(endpointInfo.getAddress()).andReturn(ENDPOINT);
		expect(endpoint.getEndpointInfo()).andReturn(endpointInfo);
		expect(server.getEndpoint()).andReturn(endpoint);
		replay(serviceInfo, endpointInfo, endpoint, server);
		return server;
	}
	
	private Server createServerForInit() {
		EasyMock.reset(serviceInfo, endpointInfo, endpoint, server);
		expect(serviceInfo.getName()).andReturn(SERVICE_NAME);
		EasyMock.expectLastCall().times(2);		
		expect(endpointInfo.getService()).andReturn(serviceInfo);
		EasyMock.expectLastCall().times(2);		
		expect(endpointInfo.getAddress()).andReturn(ENDPOINT);
		EasyMock.expectLastCall().times(2);		
		expect(endpoint.getEndpointInfo()).andReturn(endpointInfo);
		EasyMock.expectLastCall().times(2);		
		expect(server.getEndpoint()).andReturn(endpoint);
		EasyMock.expectLastCall().times(2);		
		replay(serviceInfo, endpointInfo, endpoint, server);
		return server;
	}
	
}
