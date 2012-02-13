/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package demo.secure_greeter.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import com.talend.examples.secure_greeter.SecureGreeterPortType;
import com.talend.examples.secure_greeter.SecureGreeterService;


public final class Client {

    private static final QName SERVICE_NAME =
        new QName("http://talend.com/examples/secure-greeter", "SecureGreeterService");
    private static final QName UT_PORT_NAME =
        new QName("http://talend.com/examples/secure-greeter", "UTGreeterPort");
    private static final QName SAML_PORT_NAME =
        new QName("http://talend.com/examples/secure-greeter", "SAMLGreeterPort");

    
    URL wsdl;
    SecureGreeterPortType utGreeter;
    SecureGreeterPortType samlGreeter;
    
    public Client(
        SecureGreeterPortType utGreeter,
        SecureGreeterPortType samlGreeter
    ) throws Exception {
        this.utGreeter = utGreeter;
        this.samlGreeter = samlGreeter;
        doWork();
    }
    
    public Client() throws Exception {
        this(new String[0]);
    }
    
    public Client(String args[]) throws Exception {
        if (args.length == 0) {
            wsdl = Client.class.getResource("/ws-secpol-wsdl/greeter.wsdl");
        }
        getUTGreeter();
        getSAMLGreeter();
        doWork();
    }
    
    public final void doWork() {
        System.out.println("Invoking greetMe using UsernameToken...");
        System.out.println("server responded with: " + utGreeter.greetMe(System.getProperty("user.name")));
        System.out.println();
        
        System.out.println("Invoking greetMe using SAML...");
        System.out.println("server responded with: " + samlGreeter.greetMe(System.getProperty("user.name")));
        System.out.println();
    }

    public SecureGreeterPortType getUTGreeter() {
        if (utGreeter == null) {
            SecureGreeterService service = new SecureGreeterService(wsdl, SERVICE_NAME);
            utGreeter = service.getPort(UT_PORT_NAME, SecureGreeterPortType.class);

            ((BindingProvider)utGreeter).getRequestContext()
                .put("ws-security.username", "abcd");
            ((BindingProvider)utGreeter).getRequestContext()
                .put("ws-security.callback-handler", 
                     "com.talend.examples.secure_greeter.PasswordCallback");
            ((BindingProvider)utGreeter).getRequestContext()
                .put("ws-security.encryption.properties", "/ws-secpol-wsdl/service.properties");
        }
        return utGreeter;
    }
    
    public SecureGreeterPortType getSAMLGreeter() {
        if (samlGreeter == null) {
            SecureGreeterService service = new SecureGreeterService(wsdl, SERVICE_NAME);
            samlGreeter = service.getPort(SAML_PORT_NAME, SecureGreeterPortType.class);

            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.signature.username", "alice");
            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.encryption.username", "bob");
            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.callback-handler", 
                     "com.talend.examples.secure_greeter.PasswordCallback");
            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.saml-callback-handler", new SamlCallbackHandler());
            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.encryption.properties", "/ws-secpol-wsdl/service.properties");
            ((BindingProvider)samlGreeter).getRequestContext()
                .put("ws-security.signature.properties", "/ws-secpol-wsdl/client.properties");
        }
        return samlGreeter;
    }
    
    public static void main(String[] args) throws Exception {
        new Client(args);
        System.exit(0);
    }
}
