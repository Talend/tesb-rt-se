/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.util.Map;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;

import common.authorization.HelloWorld;
import common.authorization.User;
import common.authorization.UserImpl;

/**
 * Example showing JAX-RS and JAX-WS proxies making calls to JAX-RS and JAX-WS
 * services by relying on the same shared code making remote invocations.
 */
public final class RESTClient {

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(RESTClient.class.getResourceAsStream("/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    int port;

    public RESTClient() {
        this(getPort());
    }

    public RESTClient(int port) {
        this.port = port;
    }

    public void sayHelloRest() throws Exception {
        final String address = "http://localhost:" + port + "/services/hello-rest";

        System.out.println("Using CXF JAX-RS proxy to invoke on HelloWorld service");

        // Admin
        HelloWorld service = JAXRSClientFactory.create(address, HelloWorld.class, "admin", "admin", null);
        WebClient.getConfig(service).getHttpConduit().getClient().setReceiveTimeout(100000000);
        useHelloServiceRest(service, "Barry", true);

        // User
        service = JAXRSClientFactory.create(address, HelloWorld.class, "user", "user", null);

        useHelloServiceRest(service, "Barry", false);
    }

    public void sayHelloSoap() throws Exception {
        HelloWorld hw = createSoapService("admin", "admin");

        // Admin
        useHelloServiceSoap(hw, "Fred", true);

        hw = createSoapService("user", "user");

        // User
        useHelloServiceSoap(hw, "Fred", false);

    }

    private HelloWorld createSoapService(String name, String password) throws Exception {
        final QName serviceName = new QName("http://hello.com", "HelloWorld");
        final QName portName = new QName("http://hello.com", "HelloWorldPort");
        final String address = "http://localhost:" + port + "/services/hello-soap";

        System.out.println("Using JAX-WS proxy to invoke on HelloWorld service");

        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, address);

        HelloWorld hw = service.getPort(HelloWorld.class);

        ((BindingProvider)hw).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, name);
        ((BindingProvider)hw).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

        return hw;
    }

    private void useHelloServiceRest(HelloWorld service, String user, boolean admin) {

        System.out.println("Using HelloServiceRest with " + (admin ? "admin" : "user") + " priviliges");

        System.out.println("Getting the list of existing users");
        try {
            printUsers(service.getUsers());
            if (!admin) {
                throw new RuntimeException("Only Admin can invoke getUsers");
            }
        } catch (WebApplicationException ex) {
            if (admin) {
                throw new RuntimeException("Admin can invoke getUsers, status " + ex.getResponse().getStatus());
            }
            if (ex.getResponse().getStatus() != 403) {
                throw new RuntimeException("403 response code is expected");
            }
            System.out.println("Access Denied : 403");
        }

        System.out.println("Asking the service to add a new user " + user + " and also say hi");

        try {
            System.out.println(service.sayHi(user));
            System.out.println(service.sayHiToUser(new UserImpl(user)));
        } catch (WebApplicationException ex) {
            throw new RuntimeException("Everyone can invoke sayHi and sayHiToUser");
        }

        System.out.println("Getting the list of existing users");

        try {
            Map<Integer, User> users = service.getUsers();
            printUsers(users);
            if (!admin) {
                throw new RuntimeException("Only Admin can invoke getUsers");
            }
            System.out.println("Echoing the list of existing users");
            printUsers(service.echoUsers(users));
            if (!admin) {
                throw new RuntimeException("Only Admin can invoke echoUsers");
            }
        } catch (WebApplicationException ex) {
            if (admin) {
                throw new RuntimeException("Admin can invoke getUsers and echoUsers");
            }
            if (ex.getResponse().getStatus() != 403) {
                throw new RuntimeException("403 response code is expected");
            }
            System.out.println("Access Denied : 403");
        }

    }

    private void useHelloServiceSoap(HelloWorld service, String user, boolean admin) {

        System.out.println("Using HelloServiceSoap with " + (admin ? "admin" : "user") + " priviliges");

        System.out.println("Getting the list of existing users");
        try {
            printUsers(service.getUsers());
            if (!admin) {
                throw new RuntimeException("Only Admin can invoke getUsers");
            }
        } catch (SOAPFaultException ex) {
            if (admin) {
                throw new RuntimeException("Admin can invoke getUsers");
            }
            if (!"Unauthorized".equals(ex.getMessage())) {
                throw new RuntimeException("Unauthorized message is expected");
            }
            System.out.println("Access Denied : Unauthorized");
        }

        System.out.println("Asking the service to add a new user " + user + " and also say hi");

        try {
            System.out.println(service.sayHi(user));
            System.out.println(service.sayHiToUser(new UserImpl(user)));
        } catch (Exception ex) {
            throw new RuntimeException("Everyone can invoke sayHi and sayHiToUser");
        }

        System.out.println("Getting the list of existing users");

        try {
            Map<Integer, User> users = service.getUsers();
            printUsers(users);

            if (!admin) {
                throw new RuntimeException("Only Admin can invoke getUsers");
            }

            System.out.println("Echoing the list of existing users");

            printUsers(service.echoUsers(users));
            if (!admin) {
                throw new RuntimeException("Only Admin can invoke echoUsers");
            }
        } catch (SOAPFaultException ex) {
            if (admin) {
                throw new RuntimeException("Admin can invoke getUsers and echoUsers");
            }
            if (!"Unauthorized".equals(ex.getMessage())) {
                throw new RuntimeException("Unauthorized message is expected");
            }
            System.out.println("Access Denied : Unauthorized");
        }

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

        RESTClient client = new RESTClient();

        // uses CXF JAX-RS Proxy
        client.sayHelloRest();

        System.out.println();

        // uses JAX-WS Client
        client.sayHelloSoap();
    }

    private static int getPort() {
        try {
            return Integer.valueOf(HTTP_PORT);
        } catch (NumberFormatException ex) {
            // ignore
        }
        return DEFAULT_PORT_VALUE;
    }
}
