package org.talend.esb.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ServiceLocator {

	public static final NodePath LOCATOR_ROOT_PATH = new NodePath("cxf-locator");

	public static final byte[] EMPTY_CONTENT = new byte[0];
	
	public static final PostConnectAction DO_NOTHING = new PostConnectAction() {

		@Override
		public void process(ServiceLocator lc) {
		}
	};
	
	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;
	
	private int connectionTimeout = 5000;

	private PostConnectAction pca = DO_NOTHING;

	private volatile ZooKeeper zk;

	public void connect() throws IOException, InterruptedException, ServiceLocatorException  {
	    CountDownLatch connectionLatch = new CountDownLatch(1);
    	zk = createZooKeeper(connectionLatch);
		boolean connected = connectionLatch.await(connectionTimeout, TimeUnit.MILLISECONDS);
		
		if (!connected) {
			throw new ServiceLocatorException("Connection failed.");
		} else  {
			pca.process(this);
		}
	}

	public void disconnect() throws InterruptedException {
		if (zk != null) {
			zk.close();
		}
	}
	
	public void register(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException {
		NodePath serviceNodePath = LOCATOR_ROOT_PATH.child(serviceName.toString());
		ensurePathExists(serviceNodePath, CreateMode.PERSISTENT);
		
		NodePath endpointNodePath = serviceNodePath.child(endpoint);
		ensurePathExists(endpointNodePath, CreateMode.EPHEMERAL);
	}

	public List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException {
//		String serviceNodeName = encode(serviceName.toString());

		try {
			String providerPath = LOCATOR_ROOT_PATH.child(serviceName.toString()).toString();
			Stat s = zk.exists(providerPath, false);
			if (s != null) {
				return decode(zk.getChildren(providerPath, false));
			} else {
				System.out.println("Lookup for provider" + serviceName + " failed, provider not known.");
				return Collections.emptyList();
			}
		} catch (KeeperException e) {
			throw new ServiceLocatorException("The service locator server signaled an error.", e);
		}

	}

	public void setLocatorEndpoints(String endpoints) {
		locatorEndpoints = endpoints;
	}

	public void setSessionTimeout(int timeout) {
		sessionTimeout = timeout;
	}
	
	public void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
	}

	public void setPostConnectAction(PostConnectAction pca) {
		this.pca = pca;
	}

	private void ensurePathExists(NodePath path, CreateMode mode) throws ServiceLocatorException, InterruptedException {
		String nodePath = path.toString();
		Stat s = null; 
		try {
			s = zk.exists(nodePath, false);
		} catch (KeeperException e) {
			throw new ServiceLocatorException("The service locator server signaled an error.", e);
		}

		if (s == null) {
			try {
				zk.create(nodePath, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE,
						mode);
				System.out.println("Node " + nodePath + " created.");
			} catch(KeeperException e) {
				if (! e.code().equals(Code.NODEEXISTS)) {
					throw new ServiceLocatorException("The service locator server signaled an error.", e);
				} else {
					System.out.println("Some other client created " + nodePath + " concurrently.");
				}
			}
		} else {
			System.out.println("Node " + nodePath + " already exists.");
		}
	}

	private String decode(String encoded) {
		String raw = encoded.replace("%2F", "/");
		raw = raw.replace("%2A", "%");
		return raw;
	}

	private List<String> decode(List<String> encoded) {
		List<String> raw = new ArrayList<String>();

		for (String oneEncoded : encoded) {
			raw.add(decode(oneEncoded));
		}
		return raw;
	}

	protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch) throws IOException {
    	return new ZooKeeper(locatorEndpoints, sessionTimeout, new WatcherImpl(connectionLatch));
	}


	public class WatcherImpl implements Watcher {
		
		private CountDownLatch connectionLatch;
		public WatcherImpl(CountDownLatch connectionLatch) {
			this.connectionLatch = connectionLatch;
		}
		
		@Override
		public void process(WatchedEvent event) {
			System.out.println("Event with state " + event.getState() + " sent.");
			KeeperState eventState = event.getState(); 
			if (eventState == KeeperState.SyncConnected) {
				try {
					ensurePathExists(LOCATOR_ROOT_PATH, CreateMode.PERSISTENT);
				} catch (ServiceLocatorException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connectionLatch.countDown();
			} else if (eventState == KeeperState.Expired) {
				try {
					connect();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ServiceLocatorException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static interface PostConnectAction {
		void process(ServiceLocator lc);
	}
}
