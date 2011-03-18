package org.talend.esb.locator;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

public class ServiceLocatorMain {

	private ServiceLocator sl = new ServiceLocator();

	private PrintStream out;

	public ServiceLocatorMain() {
	}
	
	public void setLocatorEndpoints(String locatorEndpoints) {
		sl.setLocatorEndpoints(locatorEndpoints);
	}
	
	public void exec(OutputStream out) throws InterruptedException,
	ServiceLocatorException {
		this.out = new PrintStream(out);
	
		sl.connect();
		printServices();
		sl.disconnect();
	}

	private void printServices() throws InterruptedException,
			ServiceLocatorException {
		List<QName> services = sl.getServices();
		
		for(QName service : services) {
			out.println(service);
			printEndpoints(service);
		}
	}

	private void printEndpoints(QName service) throws InterruptedException,
	ServiceLocatorException {
		List<String> endpoints = sl.lookup(service);
		
		for (String endpoint : endpoints) {
			out.println(" |--" + endpoint);
		}
	}
	
	public static void main(String[] args) {
		
		ServiceLocatorMain main = new ServiceLocatorMain();
		if (! parseOptions(args, main)) {
			usage();
			return;
		}
		try {
			main.exec(System.out);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}

	}
	
	private static boolean parseOptions(String[] args, ServiceLocatorMain main) {
        List<String> argList = Arrays.asList(args);
        Iterator<String> argIter = argList.iterator();
        while (argIter.hasNext()) {
             String opt = argIter.next();
             try {
                 if (opt.equals("-endpoints")) {
                	 main.setLocatorEndpoints(argIter.next());
                 } else {
                     System.err.println("Error: unknown option " + opt);
                     return false;
                 }
             } catch (NoSuchElementException e){
                 System.err.println("Error: no argument found for option "
                         + opt);
                 return false;
             }
        }
        return true;
	}

    static void usage() {
        System.err.println("ServiceLocatorMain -endpoints host:port[,host:port]*");
    }
}

