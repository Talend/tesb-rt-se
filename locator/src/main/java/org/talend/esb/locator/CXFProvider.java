package org.talend.esb.locator;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.zookeeper.KeeperException;

public class CXFProvider {

	private static String serverAddress = "localhost:2181";

	private static QName providerName = QName.valueOf("{http://example.com/provider_ns}provider1");

	private static String endpoint = "http://localhost:8080/prov1";

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException, ServiceLocatorException {
		int i = 0;
		while(i < args.length) {
			if(args[i].equals("-s")) {
				i++;
				serverAddress = args[i];
				i++;
			} else if(args[i].equals("-p")) {
				i++;
				providerName = QName.valueOf(args[i]);
				i++;
			} else if(args[i].equals("-e")) {
				i++;
				endpoint = args[i];
				i++;
			}
		}
		
		LocatorClient lc = new LocatorClient();
		lc.setLocatorEndpoints(serverAddress);

		lc.connect();
		lc.register(providerName, endpoint);
		
		synchronized (lc) {
			try {
				lc.wait();
			} catch (InterruptedException e) {
			}
		}
	}
}


