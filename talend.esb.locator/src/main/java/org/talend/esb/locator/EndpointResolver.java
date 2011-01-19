package org.talend.esb.locator;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.KeeperException;

public class EndpointResolver {

	private List<String> endpointsList;
	private LocatorClient lc;
	private QName serviceName;

	public EndpointResolver(QName serviceName) {
		this.serviceName = serviceName;
		lc = new LocatorClient();
		//TODO Set LOCATOR_ROOT for LocatorClient
		endpointsList = receiveEndpointsList();
	}

	private List<String> receiveEndpointsList() {
		List<String> endpointsList = null;

		try {
			endpointsList = lc.lookup(this.serviceName);
		} catch (KeeperException e) {
			System.err
					.println("Can not receive list of endpoints due to Keeper exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err
					.println("Can not receive list of endpoints due to Interrupted exception");
			e.printStackTrace();
		}

		return endpointsList;
	}

	public String selectEndpoint() {
		if (endpointsList.isEmpty()) {
			System.err.println("List of endpoints is empty");
		} else {
			return endpointsList.get(0);
		}
		
		return null;
	}

	public List<String> getEndpointsList() {
		return endpointsList;
	}

	public void refreshEndpointsList() {
		try {
			List<String> el = receiveEndpointsList();
			if (el == null) {
				System.err.println("Can not receive list of endpoint");
			}
		} catch(Exception e) {
			System.err.println("Can not refresh list of endpoints due to unknown exception");
			e.printStackTrace();
		}
	}
}
