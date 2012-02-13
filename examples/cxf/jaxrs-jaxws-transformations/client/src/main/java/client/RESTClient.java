/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.transform.TransformInInterceptor;
import org.apache.cxf.interceptor.transform.TransformOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.xml.XMLSource;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;

/**
 * Demonstrates how the forward and backward compatibility between
 * the old and new SOAP and REST clients is achieved with the help
 * of the CXF STAX Transform Feature
 */

public class RESTClient {

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
        port = getPort();
    }

        
    /**
     * Old REST client uses old REST service 
     */
    public void useOldRESTService() throws Exception {
        List<Object> providers = createJAXRSProviders();

        com.example.customerservice.CustomerService customerService = JAXRSClientFactory
            .createFromModel("http://localhost:" + port + "/services/direct/rest", 
                             com.example.customerservice.CustomerService.class,
                             "classpath:/model/CustomerService-jaxrs.xml", 
                             providers, 
                             null);

        System.out.println("Using old RESTful CustomerService with old client");

        customer.v1.Customer customer = createOldCustomer("Smith Old REST");
        customerService.updateCustomer(customer);

        customer = customerService.getCustomerByName("Smith Old REST");
        printOldCustomerDetails(customer);
    }

    /**
     * New REST client uses new REST service 
     */
    public void useNewRESTService(String address) throws Exception {
        List<Object> providers = createJAXRSProviders();

        org.customer.service.CustomerService customerService = JAXRSClientFactory
            .createFromModel(address, 
                             org.customer.service.CustomerService.class,
                             "classpath:/model/CustomerService-jaxrs.xml", 
                             providers, 
                             null);

        System.out.println("Using new RESTful CustomerService with new client");

        customer.v2.Customer customer = createNewCustomer("Smith New REST");
        customerService.updateCustomer(customer);

        customer = customerService.getCustomerByName("Smith New REST");
        printNewCustomerDetails(customer);
    }

    /**
     * New REST client uses old REST service 
     */
    public void useOldRESTServiceWithNewClient() throws Exception {
        List<Object> providers = createJAXRSProviders();

        org.customer.service.CustomerService customerService = JAXRSClientFactory
            .createFromModel("http://localhost:" + port + "/services/direct/rest", 
                             org.customer.service.CustomerService.class,
                             "classpath:/model/CustomerService-jaxrs.xml", 
                             providers, 
                             null);
        
        // The outgoing new Customer data needs to be transformed for 
        // the old service to understand it and the response from the old service
        // needs to be transformed for this new client to understand it.
        ClientConfiguration config = WebClient.getConfig(customerService);
        addTransformInterceptors(config.getInInterceptors(),
                                 config.getOutInterceptors(),
                                 true);
        
        
        System.out.println("Using old RESTful CustomerService with new client");

        customer.v2.Customer customer = createNewCustomer("Smith New to Old REST");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Smith New to Old REST");
        printNewCustomerDetails(customer);
    }
    
    /**
     * New REST client uses old REST service 
     */
    public void useOldRESTServiceWithNewClientAndXPath() throws Exception {
        List<Object> providers = createJAXRSProviders();

        String address = "http://localhost:" + port + "/services/direct/rest/customerservice";
        WebClient client = WebClient.create(address, providers);
        
        // The outgoing new Customer data needs to be transformed for 
        // the old service to understand it and the response from the old service
        // needs to be transformed for this new client to understand it.
        ClientConfiguration config = WebClient.getConfig(client);
        addTransformInterceptors(config.getInInterceptors(),
                                 config.getOutInterceptors(),
                                 true);
        
        
        System.out.println("Consuming old RESTful CustomerService with new client and XPath");

        customer.v2.Customer customer = createNewCustomer("Smith New to Old REST, XPath");
        client.path("customer");
        client.put(customer);
        
        XMLSource source = 
            client.query("name", "Smith New to Old REST, XPath").get(XMLSource.class);
        customer.v2.Customer xmlCustomer =
            source.getNode("/ns:customer", 
                           Collections.singletonMap("ns", "http://customer/v2"), 
                           customer.v2.Customer.class);
        printNewCustomerDetails(xmlCustomer);
    }
    
    /**
     * Old REST client uses new REST service 
     */
    public void useNewRESTServiceWithOldClient() throws Exception {
        List<Object> providers = createJAXRSProviders();

        com.example.customerservice.CustomerService customerService = JAXRSClientFactory
            .createFromModel("http://localhost:" + port + "/services/direct/new-rest", 
                             com.example.customerservice.CustomerService.class,
                             "classpath:/model/CustomerService-jaxrs.xml", 
                             providers, 
                             null);
        
        // The outgoing old Customer data needs to be transformed for 
        // the new service to understand it and the response from the new service
        // needs to be transformed for this old client to understand it.
        ClientConfiguration config = WebClient.getConfig(customerService);
        addTransformInterceptors(config.getInInterceptors(),
                                 config.getOutInterceptors(),
                                 false);
        
        
        System.out.println("Using new RESTful CustomerService with old Client");

        customer.v1.Customer customer = createOldCustomer("Smith Old to New REST");
        customerService.updateCustomer(customer);

        customer = customerService.getCustomerByName("Smith Old to New REST");
        printOldCustomerDetails(customer);
    }
    
    /**
     * Old REST client uses new REST service with the 
     * redirection to the new endpoint and transformation 
     * on the server side 
     */
    public void useNewRESTServiceWithOldClientAndRedirection() throws Exception {
        List<Object> providers = createJAXRSProviders();

        com.example.customerservice.CustomerService customerService = JAXRSClientFactory
            .createFromModel("http://localhost:" + port + "/services/old/rest-endpoint", 
                             com.example.customerservice.CustomerService.class,
                             "classpath:/model/CustomerService-jaxrs.xml", 
                             providers, 
                             null);

        
        System.out.println("Using new RESTful CustomerService with old client and the redirection");

        customer.v1.Customer customer = createOldCustomer("Smith Old to New REST With Redirection");
        customerService.updateCustomer(customer);

        customer = customerService.getCustomerByName("Smith Old to New REST With Redirection");
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
        
        Map<String, String> newToOldTransformMap = 
            Collections.singletonMap("{http://customer/v2}*", "{http://customer/v1}*");
        Map<String, String> oldToNewTransformMap = 
            Collections.singletonMap("{http://customer/v1}*", "{http://customer/v2}*");
        
        TransformOutInterceptor outTransform =  new TransformOutInterceptor();
        outTransform.setOutTransformElements(newClient ? newToOldTransformMap 
                                              : oldToNewTransformMap);
        
        if (newClient) {
            outTransform.setOutDropElements(
                Collections.singletonList("{http://customer/v2}briefDescription"));    
        }
        
        TransformInInterceptor inTransform =  new TransformInInterceptor();
        inTransform.setInTransformElements(newClient ? oldToNewTransformMap 
                                            : newToOldTransformMap);

        inInterceptors.add(inTransform);
        outInterceptors.add(outTransform);
        
        
    }
    
    /**
     * Creates a custom JAX-RS JAXBElementProvider which can handle
     * generated JAXB clasess with no XmlRootElement annotation
     * @return providers
     */
    private List<Object> createJAXRSProviders() {
        JAXBElementProvider provider = new JAXBElementProvider();
        provider.setUnmarshallAsJaxbElement(true);
        provider.setMarshallAsJaxbElement(true);
        List<Object> providers = new ArrayList<Object>();
        providers.add(provider);
        return providers;
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
        RESTClient client = new RESTClient();
        
        // Scenario 1: 
        // - Old clients have become aware that the new endpoints have been deployed.
        // They continue talking to the old endpoints which have not been stopped yet
        // otherwise they are configured such that they can talk to new endpoints
        
        // - New clients talk to the new endpoints but in some cases where new endpoints
        // have not been introduced yet, they are configured such that they can talk to 
        // the old endpoints
        
        System.out.println("Scenario 1: Old and new clients are configured"
                           + " to invoke on the new and old endpoints respectively");
        
        // JAX-RS: Scenario 1
        System.out.println("JAX-RS:");
        System.out.println();
        
        // Old Client uses Old Service
        client.useOldRESTService();
        System.out.println();
        // New Client uses Old Service
        client.useOldRESTServiceWithNewClient();
        System.out.println();
        
        // New Client uses Old Service, XPath is used to consume the response
        client.useOldRESTServiceWithNewClientAndXPath();
        System.out.println();
        
        // New Client uses New Service
        client.useNewRESTService("http://localhost:" + RESTClient.getPort()
                                         + "/services/direct/new-rest");
        System.out.println();
        // Old Client uses New Service
        client.useNewRESTServiceWithOldClient();
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
        
        // JAX-RS: Scenario 2
        System.out.println("JAX-RS:");
        System.out.println();
        
        // Old Client uses New Service without being aware of it
        client.useNewRESTServiceWithOldClientAndRedirection();
        System.out.println();
        // New Client uses New Service, the same new endpoint handles
        // new and redirected old client requests
        client.useNewRESTService("http://localhost:" + RESTClient.getPort()
                                         + "/services/new/rest-endpoint");
        
        System.exit(0);
    }
}
