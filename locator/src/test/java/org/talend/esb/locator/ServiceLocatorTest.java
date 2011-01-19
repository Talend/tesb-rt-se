package org.talend.esb.locator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;
import static org.talend.esb.locator.ServiceLocator.PostConnectAction;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class ServiceLocatorTest {
	
	public static final QName SERVICE_NAME = new QName("http://example.com/services", "service1"); 

	public static final String ENDPOINT = "http://example.com/service1";

	public static final String SERVICE_PATH = ServiceLocator.LOCATOR_ROOT_PATH
	+ "/{http:%2F%2Fexample.com%2Fservices}service1";

	public static final String ENDPOINT_PATH = SERVICE_PATH + "/http:%2F%2Fexample.com%2Fservice1";

	final ZooKeeper zkMock = createMock(ZooKeeper.class);

	PostConnectAction pcaMock = createMock(PostConnectAction.class);

	@Test
	public void successfulConnect() throws Exception {
		ServiceLocator slc = createServiceLocator();

		pcaMock.process(slc);
		replay(pcaMock, zkMock);
		
		slc.setPostConnectAction(pcaMock);
		slc.connect();
		
		verify(pcaMock, zkMock);
	}

	@Test
	public void failingConnect() throws Exception {
		ServiceLocator slc = createServiceLocator(false);

		replay(pcaMock);
		
		slc.setConnectionTimeout(10);
		slc.setPostConnectAction(pcaMock);
		
		try {
			slc.connect();
			fail("A ServiceLocatorException should have been thrown.");
		} catch (ServiceLocatorException e) {			
		}
		
		verify(pcaMock);
	}

	@Test
	public void sucessfulRegisterServiceExistsEndPointDoesNotExist() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(new Stat());
		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
		
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_NAME, ENDPOINT);

		verify(zkMock);
	}

	@Test
	public void sucessfulRegisterServiceDoesNotExist() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(SERVICE_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.PERSISTENT))).andReturn(ENDPOINT_PATH);

		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
		
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_NAME, ENDPOINT);

		verify(zkMock);
	}

	@Test
	public void sucessfulRegisterServiceDoesNotExistButConcurrentlyCreatedByOtherClient() throws Exception {
		expect(zkMock.exists(SERVICE_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(SERVICE_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.PERSISTENT))).andThrow(new KeeperException.NodeExistsException());

		expect(zkMock.exists(ENDPOINT_PATH, false)).andReturn(null);
		expect(zkMock.create(eq(ENDPOINT_PATH), aryEq(new byte[0]), eq(Ids.OPEN_ACL_UNSAFE),
				eq(CreateMode.EPHEMERAL))).andReturn(ENDPOINT_PATH);
		
		replay(zkMock);

		ServiceLocator slc = createServiceLocator();
		slc.connect();
		slc.register(SERVICE_NAME, ENDPOINT);

		verify(zkMock);
	}

	private ServiceLocator createServiceLocator() {
		return createServiceLocator(true);
	}
	
	private ServiceLocator createServiceLocator(final boolean connectSuccessful) {
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
}
