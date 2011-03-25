package org.talend.esb.sam.server.persistence;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.derby.drda.NetworkServerControl;

/**
 * Starting Derby server
 *
 */
public class StartDerbyThread extends Thread {

	@Override
	public void run(){
		NetworkServerControl server;
		try {
			server = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
			server.start(null);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
