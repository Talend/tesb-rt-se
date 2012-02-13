/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */

package demo.interceptors.server;

import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxws.EndpointImpl;

import demo.interceptors.interceptor.DemoInterceptor;

public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");
        Object implementor = new GreeterImpl();
        String address = "http://localhost:8080/services/InterceptorExample";
        Endpoint endpoint = Endpoint.create(implementor);

        // Add the DemoInterceptor's to print the contents
        DemoInterceptor.addInterceptors(((EndpointImpl)endpoint).getServer().getEndpoint().getBinding());
        endpoint.publish(address);
    }

    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
