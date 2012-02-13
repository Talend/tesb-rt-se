/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;

/**
 * Demonstrates how the forward and backward compatibility between
 * the old and new SOAP and REST clients is achieved with the help
 * of the CXF STAX Transform Feature
 */

public class SOAPClient {

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(SOAPClient.class.getResourceAsStream("/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    int port;

    public SOAPClient() {
        port = getPort();
    }

    /**
     * Old SOAP client uses old SOAP service 
     */
    public void useOldSOAPService() throws Exception {
        URL wsdlURL = getClass().getResource("/CustomerService.wsdl");
        com.example.customerservice.CustomerServiceService service = 
            new com.example.customerservice.CustomerServiceService(wsdlURL);
        
        com.example.customerservice.CustomerService customerService = 
            service.getCustomerServicePort();

        System.out.println("Using old SOAP CustomerService with old client");
        
        customer.v1.Customer customer = createOldCustomer("Barry Old SOAP");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry Old SOAP");
        printOldCustomerDetails(customer);
    }
    
    /**
     * New SOAP client uses new SOAP service.
     */
    public void useNewSOAPService(boolean direct) throws Exception {
        URL wsdlURL = getClass().getResource("/CustomerServiceNew.wsdl");
        org.customer.service.CustomerServiceService service = 
            new org.customer.service.CustomerServiceService(wsdlURL);
        
        org.customer.service.CustomerService customerService = 
            direct ? service.getCustomerServicePort() : service.getCustomerServiceNewPort();

        System.out.println("Using new SOAP CustomerService with new client");
        
        customer.v2.Customer customer = createNewCustomer("Barry New SOAP");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry New SOAP");
        printNewCustomerDetails(customer);
    }

    /**
     * Old SOAP client uses new SOAP service 
     */
    public void useNewSOAPServiceWithOldClient() throws Exception {
        
        URL wsdlURL = getClass().getResource("/CustomerServiceNew.wsdl");
        com.example.customerservice.CustomerServiceService service = 
            new com.example.customerservice.CustomerServiceService(wsdlURL);
        
        com.example.customerservice.CustomerService customerService = 
            service.getCustomerServicePort();

        // The outgoing new Customer data needs to be transformed for 
        // the old service to understand it and the response from the old service
        // needs to be transformed for this new client to understand it.
        Client client = ClientProxy.getClient(customerService);
        addTransformInterceptors(client.getInInterceptors(),
                                 client.getOutInterceptors(),
                                 false);
        
        System.out.println("Using new SOAP CustomerService with old client");
        
        customer.v1.Customer customer = createOldCustomer("Barry Old to New SOAP");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry Old to New SOAP");
        printOldCustomerDetails(customer);
    }
    
    public void useOldSOAPServiceWithNewClient() throws Exception {
        
        URL wsdlURL = getClass().getResource("/CustomerService.wsdl");
        org.customer.service.CustomerServiceService service = 
            new org.customer.service.CustomerServiceService(wsdlURL);
        
        org.customer.service.CustomerService customerService = 
            service.getCustomerServicePort();

        // The outgoing old Customer data needs to be transformed for 
        // the new service to understand it and the response from the new service
        // needs to be transformed for this old client to understand it.
        Client client = ClientProxy.getClient(customerService);
        addTransformInterceptors(client.getInInterceptors(),
                                 client.getOutInterceptors(),
                                 true);
        
        System.out.println("Using old SOAP CustomerService with new client");
        
        customer.v2.Customer customer = createNewCustomer("Barry New to Old SOAP");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry New to Old SOAP");
        printNewCustomerDetails(customer);
    }
    
    /**
     * Old SOAP client uses new SOAP service with the
     * redirection to the new endpoint and transformation 
     * on the server side  
     */
    public void useNewSOAPServiceWithOldClientAndRedirection() throws Exception {
        URL wsdlURL = getClass().getResource("/CustomerService.wsdl");
        com.example.customerservice.CustomerServiceService service = 
            new com.example.customerservice.CustomerServiceService(wsdlURL);
        
        com.example.customerservice.CustomerService customerService = 
            service.getCustomerServiceRedirectPort();

        System.out.println("Using new SOAP CustomerService with old client and the redirection");
        
        customer.v1.Customer customer = createOldCustomer("Barry Old to New SOAP With Redirection");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry Old to New SOAP With Redirection");
        printOldCustomerDetails(customer);
    }
    
    
    /**
     * Prepares transformation interceptors for a client.
     * 
     * @param clientConfig the client configuration 
     * @param newClient indicates if it is a new/updated client
     */
    private void addTransformInterceptors(List<Interceptor<?>> inInterceptors,
                                          List<Interceptor<?>> outInterceptors,
                                          boolean newClient) {
        
        // The old service expects the Customer data be qualified with
        // the 'http://customer/v1' namespace.
        
        // The new service expects the Customer data be qualified with
        // the 'http://customer/v2' namespace.
        
        // If it is an old client talking to the new service then:
        // - the out transformation interceptor is configured for 
        //   'http://customer/v1' qualified data be transformed into
        //   'http://customer/v2' qualified data.
        // - the in transformation interceptor is configured for 
        //   'http://customer/v2' qualified response data be transformed into
        //   'http://customer/v1' qualified data.
       
        // If it is a new client talking to the old service then:
        // - the out transformation interceptor is configured for 
        //   'http://customer/v2' qualified data be transformed into
        //   'http://customer/v1' qualified data.
        // - the in transformation interceptor is configured for 
        //   'http://customer/v1' qualified response data be transformed into
        //   'http://customer/v2' qualified data.
        // - new Customer type also introduces a briefDescription property
        //   which needs to be dropped for the old service validation to succeed
        
        
        // this configuration can be provided externally
        
        Map<String, String> newToOldTransformMap = new HashMap<String, String>();
        newToOldTransformMap.put("{http://customer/v2}*", "{http://customer/v1}*");
        Map<String, String> oldToNewTransformMap = 
            Collections.singletonMap("{http://customer/v1}*", "{http://customer/v2}*");
        
        TransformOutInterceptor outTransform =  new TransformOutInterceptor();
        outTransform.setOutTransformElements(newClient ? newToOldTransformMap 
                                              : oldToNewTransformMap);
        
        if (newClient) {
            newToOldTransformMap.put("{http://customer/v2}briefDescription", "");
            //outTransform.setOutDropElements(
            //    Collections.singletonList("{http://customer/v2}briefDescription"));    
        }
        
        TransformInInterceptor inTransform =  new TransformInInterceptor();
        inTransform.setInTransformElements(newClient ? oldToNewTransformMap 
                                            : newToOldTransformMap);

        inInterceptors.add(inTransform);
        outInterceptors.add(outTransform);
        
        
    }
    
    /**
     * namespace: {http://customer}v1
     */
    private customer.v1.Customer createOldCustomer(String name) {
        customer.v1.Customer cust = new customer.v1.Customer();
        cust.setName(name);
        cust.getAddress().add("Pine Street 200");
        Date bDate = new GregorianCalendar(2009, 01, 01).getTime();
        cust.setBirthDate(bDate);
        cust.setNumOrders(1);
        cust.setRevenue(10000);
        cust.setShares(new BigDecimal(1.5));
        cust.setType(customer.v1.CustomerType.BUSINESS);
        return cust;
    }
    
    /**
     * namespace: {http://customer}v2
     * new simple element: briefDescription
     */
    private customer.v2.Customer createNewCustomer(String name) {
        customer.v2.Customer cust = new customer.v2.Customer();
        cust.setName(name);
        cust.getAddress().add("Pine Street 200");
        Date bDate = new GregorianCalendar(2009, 01, 01).getTime();
        cust.setBirthDate(bDate);
        cust.setNumOrders(1);
        cust.setRevenue(10000);
        cust.setShares(new BigDecimal(1.5));
        cust.setType(customer.v2.CustomerType.BUSINESS);
        cust.setBriefDescription("Business Customer");
        return cust;
    }
    
    private void printOldCustomerDetails(customer.v1.Customer customer) {
        System.out.print("Name : " + customer.getName());
        System.out.print(", orders : " + customer.getNumOrders());
        System.out.print(", shares : " + customer.getShares());
        System.out.println();
    }
    
    private void printNewCustomerDetails(customer.v2.Customer customer) {
        System.out.print("Name : " + customer.getName());
        System.out.print(", orders : " + customer.getNumOrders());
        System.out.print(", shares : " + customer.getShares());
        System.out.println();
    }
    
    private static int getPort() {
        try {
            return Integer.valueOf(HTTP_PORT);
        } catch (NumberFormatException ex) {
            // ignore
        }
        return DEFAULT_PORT_VALUE;
    }

    public static void main(String args[]) throws Exception {
        SOAPClient client = new SOAPClient();
        
        // Scenario 1: 
        // - Old clients have become aware that the new endpoints have been deployed.
        // They continue talking to the old endpoints which have not been stopped yet
        // otherwise they are configured such that they can talk to new endpoints
        
        // - New clients talk to the new endpoints but in some cases where new endpoints
        // have not been introduced yet, they are configured such that they can talk to 
        // the old endpoints
        
        System.out.println("Scenario 1: Old and new clients are configured"
                           + " to invoke on the new and old endpoints respectively");
        
        // JAX-WS: Scenario 1
        System.out.println();
        System.out.println("JAX-WS:");
        System.out.println();
        
        // Old Client uses Old Service
        client.useOldSOAPService();
        System.out.println();
        // New Client uses Old Service
        client.useOldSOAPServiceWithNewClient();
        System.out.println();
        // New Client uses New Service
        client.useNewSOAPService(true);
        System.out.println();
        // Old Client uses New Service
        client.useNewSOAPServiceWithOldClient();
        System.out.println();
        
        // Scenario 2:
        // Old clients are unaware of the fact that the old endpoint has been removed
        // and the new endpoint has been introduced.
        // Old Client thinks Old Service is still being used but
        // on the server the request will be redirected to the New Service endpoint
        
        // Similarly, the new clients can be redirected to the old endpoints on the server
        // if no new endpoints have been introduced yet in a given destination
        
        System.out.println("Scenario 2: Old clients invoke on the new endpoints"
                           + " without being aware of it");
        
        // JAX-WS: Scenario 2
        System.out.println();
        System.out.println("JAX-WS:");
        System.out.println();
        
        // Old Client uses New Service without being aware of it
        client.useNewSOAPServiceWithOldClientAndRedirection();
        System.out.println();
        // New Client uses New Service, the same new endpoint handles
        // new and redirected old client requests
        client.useNewSOAPService(true);
        System.out.println();
        
        System.exit(0);
    }
}
