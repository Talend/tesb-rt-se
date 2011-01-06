package org.talend.esb.locator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.zookeeper.KeeperException;

public class CXFClient {

	private static String serverAddress = "localhost:2181";
	
	String asCommaSeparatedList(List<? extends Object > items) {
		StringBuffer result = new StringBuffer();
		Iterator<? extends Object> itemIter = items.iterator();
		
		while (itemIter.hasNext()) {
			result.append(itemIter.next());
			if (itemIter.hasNext()) {
				result.append(", ");
			}
		}
		return result.toString();
	}
	
	public static  void start(LocatorClient lc) throws IOException, KeeperException, InterruptedException{
		boolean next = true;
		while (next) {
			String line = nextLine();
			QName serviceName = QName.valueOf(line);
			if (! line.equals("q")) {
				List<String> endpoints = lc.lookup(serviceName);
				System.out.println("Endpoints for provider " + line + ": " + endpoints);
			} else {
				System.out.println("Stopped cxf client.");
				next = false;
			}
		}
	}
	
	private static String nextLine() throws IOException {
/*
		Console console = System.console();
		if (console != null) {
			return console.readLine("lookup: ");
		} else {
			throw new IllegalStateException("Cannot create a console for this application");
		}
*/
		System.out.print("lookup: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		return in.readLine();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException, ServiceLocatorException {
		if(args[0].equals("-s")) {
			serverAddress = args[1];
		}
		
		LocatorClient lc = new LocatorClient();
		lc.setLocatorEndpoints(serverAddress);

		lc.connect();

		start(lc);
	}
}


