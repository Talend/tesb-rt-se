/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package service;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");
        
        Endpoint ep = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, new DoubleItPortTypeImpl());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("ws-security.callback-handler",
                       "service.ServiceKeystorePasswordCallback");
        properties.put("ws-security.signature.properties",
                       "serviceKeystore.properties");
        properties.put(
            Endpoint.WSDL_PORT, 
            new QName("http://www.example.org/contract/DoubleIt", "DoubleItPort")
        );
        ep.setProperties(properties);
        ep.publish("http://localhost:9000/doubleit/services/doubleit");
    }
    
    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
