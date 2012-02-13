/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.interceptors.client;

import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import com.talend.examples.interceptors.Greeter;

import demo.interceptors.interceptor.DemoInterceptor;

import org.apache.cxf.frontend.ClientProxy;

public final class Client {

    private static final QName SERVICE_NAME = new QName("http://talend.com/examples/interceptors",
                                                        "GreeterService");
    private static final QName PORT_NAME = new QName("http://talend.com/examples/interceptors", "GreeterPort");

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(Client.class.getResourceAsStream("/interceptors/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    public Client() throws Exception {
        this(new String[0]);
    }

    public Client(String args[]) throws Exception {
        
        final String address = "http://localhost:" + getPort() + "/services/InterceptorExample";

        Service service = Service.create(SERVICE_NAME);
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, address);

        Greeter greeter = service.getPort(Greeter.class);
        
        // Use CXF API's to grab the underlying Client object and add
        // the DemoInterceptor's to it
        org.apache.cxf.endpoint.Client client = ClientProxy.getClient(greeter);
        DemoInterceptor.addInterceptors(client.getEndpoint().getBinding());

        System.out.println("Invoking greetMe...");
        System.out.println("server responded with: " + greeter.greetMe(System.getProperty("user.name")));
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        new Client(args);
        System.exit(0);
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
