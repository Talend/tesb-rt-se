/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.example.customers.Customer;
import org.example.customers.CustomerService;
import org.example.customers.CustomerServiceService;
import org.example.customers.CustomerType;
import org.example.customers.NoSuchCustomerException;

public class CustomerServiceClient {

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(CustomerServiceClient.class.getResourceAsStream("/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    int port;

    public CustomerServiceClient() {
        port = getPort();
    }

    public void useCustomerServiceSoap(String args[]) throws Exception {
        final String address = "http://localhost:" + port + "/services/jaxws";
        System.out.println("Using SOAP CustomerService"); 
        
        Service service = Service.create(CustomerServiceService.SERVICE);
        service.addPort(CustomerServiceService.CustomerServicePort, SOAPBinding.SOAP11HTTP_BINDING, address);

        CustomerService customerService = service.getPort(CustomerService.class);
                
        Customer customer = createCustomer("Barry");
        customerService.updateCustomer(customer);
        customer = customerService.getCustomerByName("Barry");
        printCustomerDetails(customer);
        try {
            customerService.getCustomerByName("Smith");
            throw new RuntimeException("Exception is expected");
        } catch (NoSuchCustomerException ex) {
            System.out.println("NoSuchCustomerException : Smith");
        }
    }

    public void useCustomerServiceRest(String args[]) throws Exception {
        JAXBElementProvider provider = new JAXBElementProvider();
        provider.setUnmarshallAsJaxbElement(true);
        provider.setMarshallAsJaxbElement(true);

        List<Object> providers = new ArrayList<Object>();
        providers.add(provider);
        providers.add(new ResponseExceptionMapper<NoSuchCustomerException>() {

            @Override
            public NoSuchCustomerException fromResponse(Response r) {
                return new NoSuchCustomerException();
            }

        });

        CustomerService customerService = JAXRSClientFactory
            .createFromModel("http://localhost:" + port + "/services/jaxrs", CustomerService.class,
                             "classpath:/data/model/CustomerService-jaxrs.xml", providers, null);

        System.out.println("Using RESTful CustomerService");

        Customer customer = createCustomer("Smith");
        customerService.updateCustomer(customer);

        customer = customerService.getCustomerByName("Smith");
        printCustomerDetails(customer);

        customer = customerService.getCustomerByName("Barry");
        if (customer != null) {
            throw new RuntimeException("Barry should not be found");
        }
        System.out.println("Status : " + WebClient.client(customerService).getResponse().getStatus());

        try {
            customerService.getCustomerByName("John");
            throw new RuntimeException("Exception is expected");
        } catch (NoSuchCustomerException ex) {
            System.out.println("NoSuchCustomerException : John");
        }
    }

    private void printCustomerDetails(Customer customer) {
        System.out.print("Name : " + customer.getName());
        System.out.print(", orders : " + customer.getNumOrders());
        System.out.print(", shares : " + customer.getShares());
        System.out.println();
    }

    private Customer createCustomer(String name) {
        Customer cust = new Customer();
        cust.setName(name);
        cust.getAddress().add("Pine Street 200");
        Date bDate = new GregorianCalendar(2009, 01, 01).getTime();
        cust.setBirthDate(bDate);
        cust.setNumOrders(1);
        cust.setRevenue(10000);
        cust.setShares(new BigDecimal(1.5));
        cust.setType(CustomerType.BUSINESS);
        return cust;
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
        CustomerServiceClient client = new CustomerServiceClient();
        client.useCustomerServiceSoap(args);
        client.useCustomerServiceRest(args);
        System.exit(0);
    }
}
