/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.server;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;

public class Server {

    protected Server() throws Exception {
        System.out.println("Starting Server");
        
        createAndPublishUTPort();
        createAndPublishSAMLPort();
    }
    
    private void createAndPublishUTPort() {
        Endpoint ep = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, new GreeterImpl());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("ws-security.callback-handler",
                       "com.talend.examples.secure_greeter.PasswordCallback");
        properties.put("ws-security.signature.properties",
                       "/ws-secpol-wsdl/service.properties");
        properties.put(
            Endpoint.WSDL_PORT, 
            new QName("http://talend.com/examples/secure-greeter", "UTGreeterPort")
        );
        ep.setProperties(properties);
        ep.publish("http://localhost:9000/SecureUTGreeter");
    }
    
    private void createAndPublishSAMLPort() {
        Endpoint ep = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, new GreeterImpl());
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("ws-security.callback-handler",
                       "com.talend.examples.secure_greeter.PasswordCallback");
        properties.put("ws-security.encryption.properties",
                       "/ws-secpol-wsdl/client.properties");
        properties.put("ws-security.signature.properties",
                       "/ws-secpol-wsdl/service.properties");
        properties.put("ws-security.saml2.validator",
                       "demo.secure_greeter.server.ServerSamlValidator");
        properties.put(
            Endpoint.WSDL_PORT, 
            new QName("http://talend.com/examples/secure-greeter", "SAMLGreeterPort")
        );
        ep.setProperties(properties);
        ep.publish("http://localhost:9000/SecureSAMLGreeter");
    }

    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
