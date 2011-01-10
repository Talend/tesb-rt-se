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
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class LocatorClient {
	
	public static final String LOCATOR_ROOT = "/cxf-locator";
	
	public static final byte[] EMPTY_CONTENT = new byte[0];
	
	public static final PostConnectAction DO_NOTHING = new PostConnectAction() {

		@Override
		public void process(LocatorClient lc) {
		}
	};
	
	private String locatorEndpoints = "localhost:2181";

	private int sessionTimeout = 5000;
	
	private int connectionTimeout = 5000;

	private PostConnectAction pca = DO_NOTHING;

	private volatile ZooKeeper zk;

	public void connect() throws IOException, InterruptedException, ServiceLocatorException  {
	    CountDownLatch connectionLatch = new CountDownLatch(1);
    	zk = new ZooKeeper(locatorEndpoints, sessionTimeout, new WatcherImpl(connectionLatch));

    	System.out.println("ZooKeeper state after creating client proxy: " + zk.getState());
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

	public void register(QName serviceName, String endpoint) throws KeeperException, InterruptedException {
		String serviceNodeName = encode(serviceName.toString());
		String endpointNodeName = encode(endpoint);

		ensureProviderExists(serviceNodeName);
		registerEndpoint(serviceNodeName, endpointNodeName);
	}


	public List<String> lookup(QName serviceName) throws KeeperException, InterruptedException {
		String serviceNodeName = encode(serviceName.toString());

		String providerPath = LOCATOR_ROOT + "/" + serviceNodeName;
		Stat s = zk.exists(providerPath, false);
		if (s != null) {
			return decode(zk.getChildren(providerPath, false));
		} else {
			System.out.println("Lookup for provider" + serviceNodeName + " failed, provider not known.");
			return Collections.emptyList();
		}
	}
	
	private void ensureRootExists() throws KeeperException, InterruptedException {
		Stat s = zk.exists(LOCATOR_ROOT, false);
		if (s == null) {
			zk.create(LOCATOR_ROOT, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
			System.out.println("Node " + LOCATOR_ROOT + " created.");
		} else {
			System.out.println("Node " + LOCATOR_ROOT + " already exists.");
		}
		
	}

	private void ensureProviderExists(String serviceNodeName) throws KeeperException, InterruptedException {
		String nodePath = LOCATOR_ROOT + "/"+ serviceNodeName;
		Stat s = zk.exists(nodePath, false);
		if (s == null) {
			zk.create(nodePath, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
			System.out.println("Node " + nodePath + " created.");
		} else {
			System.out.println("Node " + nodePath + " already exists.");
		}
	}

	private void registerEndpoint(String serviceNodeName, String endpointNodeName) throws KeeperException, InterruptedException {
		String nodePath = LOCATOR_ROOT + "/"+ serviceNodeName  + "/" + endpointNodeName;
		Stat s = zk.exists(nodePath, false);
		if (s == null) {
			zk.create(nodePath, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
			System.out.println("Node " + nodePath + " created.");
		} else {
			System.out.println("Node " + nodePath + " already exists.");
		}
	}

	private String encode(String raw) {
		String encoded = raw.replace("%", "%2A");
		encoded = encoded.replace("/", "%2F");
		return encoded;
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

	public class WatcherImpl implements Watcher {
		
		private CountDownLatch connectionLatch;
		public WatcherImpl(CountDownLatch connectionLatch) {
			this.connectionLatch = connectionLatch;
		}
		
		@Override
		public void process(WatchedEvent event) {
			System.out.println("Event " + event + " sent.");
			KeeperState eventState = event.getState(); 
			if (eventState == KeeperState.SyncConnected) {
				try {
					ensureRootExists();
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connectionLatch.countDown();
				System.out.println("ZooKeeper state after connected event: " + zk.getState());
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
		void process(LocatorClient lc);
	}
}
