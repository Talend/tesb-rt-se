/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.util.Map;

import javax.ws.rs.WebApplicationException;

import junit.framework.Assert;

import com.talend.camel.examples.springsecurity.common.HelloWorld;
import com.talend.camel.examples.springsecurity.common.User;
import com.talend.camel.examples.springsecurity.common.UserImpl;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;

/**
 * <p>
 * Uses a JAX-RS proxy to access a jaxrs server with several users and credentials.
 * Depending on the user's roles the operation should work or be denied.
 * </p>
 */
public final class RESTClient {

    private final Logger logger = Logger.getLogger(JaxWsClient.class);

    private String address;

    public RESTClient(int port) {
        address = "http://localhost:" + port + "/spring-security/HelloWorld";
    }

    public void sayHelloAsAdmin() throws Exception {
        HelloWorld service = createServiceProxy("jim", "jimspassword");
        System.out.println("Using HelloServiceRest with admin priviliges");

        System.out.println("Asking the service to add a new user and also say hi");
        try {
            System.out.println(service.sayHi("Barry"));
            System.out.println(service.sayHiToUser(new UserImpl("Barry")));
        } catch (WebApplicationException ex) {
            throw new RuntimeException("Should be able to sayHi", ex);
        }

        System.out.println("Getting the list of existing users");
        try {
            Map<Integer, User> users = service.getUsers();
            printUsers(users);
        } catch (WebApplicationException ex) {
            throw new RuntimeException("Admin should be able to invoke getUsers", ex);
        }
    }
    
    public void sayHelloAsUser() throws Exception {
        HelloWorld service = createServiceProxy("bob", "bobspassword");
        System.out.println("Using HelloServiceRest with user priviliges");
        System.out.println("Getting the list of existing users");
        try {
        	Map<Integer, User> users = service.getUsers();
            printUsers(users);
            throw new RuntimeException("Only admin should be able to invoke getUsers");
        } catch (WebApplicationException ex) {
            Assert.assertEquals("403 response code is expected", 403, ex.getResponse().getStatus());
            System.out.println("Access was denied for user bob");
        }

        System.out.println("Asking the service to add a new user Barry and also say hi");
        try {
            System.out.println(service.sayHi("Barry"));
            System.out.println(service.sayHiToUser(new UserImpl("Barry")));
        } catch (WebApplicationException ex) {
            throw new RuntimeException("Should be able to sayHi");
        }
    }
    
    public HelloWorld createServiceProxy(String username, String password) {
        HelloWorld service = JAXRSClientFactory.create(address, HelloWorld.class, username, password, null);
        WebClient.getConfig(service).getHttpConduit().getClient().setReceiveTimeout(100000000);
        WebClient.getConfig(service).getOutInterceptors().add(new LoggingOutInterceptor());
        return service;
	}

    private void printUsers(Map<Integer, User> users) {

        if (users.size() == 0) {
            System.out.println("No information about users is available");
        }

        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            System.out.println(entry.getValue().getName());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length == 2 && "http.port".equals(args[0]) 
                   ? Integer.valueOf(args[1]) : 8080;


    	System.out.println("Using CXF JAX-RS proxy to invoke on HelloWorld service");
        RESTClient client = new RESTClient(port);
        client.sayHelloAsAdmin();
        client.sayHelloAsUser();
    }
}
