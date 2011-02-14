package org.talend.esb.locator;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.talend.esb.locator.ServiceLocator.PostConnectAction;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.easymock.EasyMock;
import services.localhost.service1;

public class LocatorRegistrarTest {
	public static final QName SERVICE_NAME = new QName("http://localhost.services/", "service1");	
	public static final String ENDPOINT = "http://localhost:80/service1";	
	public static final String ENDPOINT_NODE_1 = "http:%2F%2Flocalhost:80%2Fservice1";	
	public static final String SERVICE_PATH = ServiceLocator.LOCATOR_ROOT_PATH
	+ "/{http:%2F%2Flocalhost.services%2F}service1";
	
	public static final String ENDPOINT_PATH = SERVICE_PATH + "/" + ENDPOINT_NODE_1;	
	private static ServiceLocator slc = createServiceLocator(true);
	
	final static ZooKeeper zkMock = createMock(ZooKeeper.class);
	PostConnectAction pcaMock = createMock(PostConnectAction.class);
	
	static LocatorRegistrar locatorRegistrar;
	static Server server; 

	@Test
	public void createLocatorRegistrar() {
		locatorRegistrar = new LocatorRegistrar();
		assert(locatorRegistrar != null);
	}

	@Test
	public void locatorRegistrarSucessfulStartServer() throws Exception {
		server = createServer();		
		slc = createServiceLocator();
		pcaMock.process(slc);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
		replay(pcaMock, zkMock);		
		slc.setPostConnectAction(pcaMock);
		slc.connect();
     	slc.register(SERVICE_NAME, ENDPOINT);
 
    	EasyMock.reset(zkMock);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);    	
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
				
		replay(zkMock);    	
		
        locatorRegistrar = new LocatorRegistrar();
        locatorRegistrar.setLocatorClient(slc);
		locatorRegistrar.startServer(server);
		verify(pcaMock, zkMock);    	
	}	
	
	@Test
	public void locatorRegistrarSucessfulStopServer() throws Exception {
    	EasyMock.reset(zkMock);		
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);		
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
		replay(zkMock);		
    	EasyMock.reset(zkMock);
    	
		List<String> lst = new ArrayList<String>();
		lst.add("ddd");
		expect(zkMock.getChildren(SERVICE_PATH, false)).andReturn(lst);
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.getState()).andReturn(ZooKeeper.States.CONNECTED);
		replay(zkMock);
		locatorRegistrar.stopServer(server);
		verify(zkMock);
	}	
	
	
	
	private ServiceLocator createServiceLocator() {
		return createServiceLocator(true);
	}
	
	private static ServiceLocator createServiceLocator(final boolean connectSuccessful) {
		return new ServiceLocator() {
			@Override
			protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch) throws IOException {
				if (connectSuccessful) {
					connectionLatch.countDown();
				}
				return zkMock;
			}
		};
	}	
	
	
		public class Service1Impl implements service1 {
		  public String sayHi(String text) {
		    return "Hello " + text;
		  }
		}

		private Server createServer() {		
			Service1Impl service1Impl = new Service1Impl();
		ServerFactoryBean svrFactory = new ServerFactoryBean();
		svrFactory.setServiceClass(service1.class);
		svrFactory.setAddress(ENDPOINT);
		svrFactory.setServiceBean(service1Impl);
		svrFactory.setEndpointName(SERVICE_NAME);
		Server svr = svrFactory.create();
		return svr;
		}

}
