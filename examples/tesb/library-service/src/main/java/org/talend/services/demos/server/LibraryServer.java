package org.talend.services.demos.server;

import javax.xml.ws.Endpoint;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.talend.services.demos.library._1_0.Library;

public class LibraryServer {

	protected LibraryServer() throws Exception {
        System.out.println("Starting Server");
        Library implementor = new LibraryServerImpl();
        EndpointImpl ep = (EndpointImpl)Endpoint.publish(
        		"http://localhost:9090/LibraryPort", implementor);

        // Adding logging for incoming and outgoing messages
        ep.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
        ep.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
    }

    public static void main(String args[]) throws Exception {
        new LibraryServer();
        System.out.println("Server ready...");
        Thread.sleep(5 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }

}
