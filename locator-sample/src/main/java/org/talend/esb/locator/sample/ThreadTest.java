package org.talend.esb.locator.sample;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.talend.esb.locator.ServiceLocator;
import org.talend.esb.locator.ServiceLocatorException;

/**
 * Very simple example to test lookup() and connect() methods in different threads.
 * @author rminko
 *
 */
public class ThreadTest {

	public static void main(String args[]) throws Exception {
		final ServiceLocator lc = new ServiceLocator();
		lc.setLocatorEndpoints(Constants.LOCATORENDPOINT);
		Thread th1 = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 100; i++) {
						lc.connect();
						lc.lookup(QName.valueOf("empty"));
					}
					System.out.println("Finished th #1");
				} catch (ServiceLocatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread th2 = new Thread() {
			public void run() {
				try {
					for (int i = 0; i < 100; i++) {
						lc.connect();
					}
					lc.lookup(QName.valueOf("empty"));
					System.out.println("Finished th #2");
				} catch (ServiceLocatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread th3 = new Thread() {
			public void run() {
				try {
					lc.connect();
					for (int i = 0; i < 100; i++) {
						lc.lookup(QName.valueOf("empty"));
					}
					System.out.println("Finished th #3");
				} catch (ServiceLocatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		th1.start();
		th2.start();
		th3.start();
		Thread.sleep(1000 * 60 * 100);
		lc.disconnect();
	}

}
