/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;

public class CustomerServiceServer {

    protected CustomerServiceServer() throws Exception {

    }

    public static void main(String args[]) throws Exception {

        System.out.println("Starting Server");
        
        // old SOAP service
        com.example.customerservice.CustomerService oldImplementor = 
            new CustomerServiceImpl();
        Endpoint.publish("http://localhost:8080/services/direct/soap", oldImplementor);
        
        // new SOAP service
        org.customer.service.CustomerService newImplementor = 
            new NewCustomerServiceImpl();
        Endpoint.publish("http://localhost:8080/services/direct/new-soap", newImplementor);

        
        // old REST endpoint
        publishRestEndpoint(oldImplementor,
                            "http://localhost:8080/services/direct/rest");
        
        // new REST endpoint
        publishRestEndpoint(newImplementor,
                            "http://localhost:8080/services/direct/new-rest");

        System.out.println("Server ready...");
        Thread.sleep(50 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
    
    private static void publishRestEndpoint(Object implementor,
                                            String address) {
        JAXRSServerFactoryBean jaxrsFactory = new JAXRSServerFactoryBean();
        jaxrsFactory.setAddress(address);
        jaxrsFactory.setModelRef("classpath:/model/CustomerService-jaxrs.xml");
        jaxrsFactory.setServiceBean(implementor);

        List<Object> providers = createProviders();
        providers.add(new NoCustomerExceptionMapper());

        jaxrsFactory.setProviders(providers);

        jaxrsFactory.create();
    }
    
    private static List<Object> createProviders() {
        JAXBElementProvider provider = new JAXBElementProvider();
        provider.setUnmarshallAsJaxbElement(true);
        provider.setMarshallAsJaxbElement(true);
        List<Object> providers = new ArrayList<Object>();
        providers.add(provider);
        return providers;
    }
}
