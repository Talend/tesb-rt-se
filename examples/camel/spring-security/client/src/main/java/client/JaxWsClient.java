/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package client;

import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

/**
 * <p>
 * Calls several methods of the customerservice with different
 * users and credentials. Depending on the users roles the operation
 * should work or be denied.
 * </p>
 * <p>
 * Also see the user and roles config in common-security.
 * </p>
 */
public class JaxWsClient {

    private final Logger logger = Logger.getLogger(JaxWsClient.class);
    private String address;
    public JaxWsClient(int port) {
        address = "http://localhost:" + port + "/spring-security/CustomerServicePort";
    }

    private void run() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setAddress(address);
        factoryBean.setServiceClass(CustomerService.class);
        CustomerService customerService = factoryBean.create(CustomerService.class);

        // Anonymous should not be able to read customers
        try {
            List<Customer> customersByName = customerService.getCustomersByName("Fred");
            customersByName.get(0);
            Assert.fail("Anonymous should not be allowed to read customers");
        } catch (Exception e) {
            logger.info("Anonymous request was correctly denied. " + getMessage(e));
        }

        // Alex should not be able to read customers
        CredentialsInjector.inject(customerService, "alex", "alexspassword");
        try {
            customerService.getCustomersByName("Test");
            Assert.fail("Alex should not be allowed to read customers");
        } catch (Exception e) {
            logger.info("Alex's request was correctly denied. " + getMessage(e));
        }

        // Bob should be able to read customers but not to update
        CredentialsInjector.inject(customerService, "bob", "bobspassword");
        try {
            List<Customer> customersByName = customerService.getCustomersByName("Fred");
            Customer customer = customersByName.get(0);
            logger.info("Bob was able to load the customer " + customer.getName());
        } catch (Exception e) {
            Assert.fail("Bob should be allowed to read customers");
        }
        CredentialsInjector.inject(customerService, "bob", "bobspassword");
        try {
            Customer customer = new Customer();
            customer.setName("Fred");
            customerService.updateCustomer(customer);
            Assert.fail("Bob should not be allowed to update a customer");
        } catch (Exception e) {
            logger.info("Bob's request was correctly denied. " + getMessage(e));
        }

        // Jim should be able to read and update customers
        CredentialsInjector.inject(customerService, "jim", "jimspassword");
        try {
            List<Customer> customersByName = customerService.getCustomersByName("Fred");
            Customer customer = customersByName.get(0);
            logger.info("Jim was able to load the customer " + customer.getName());
        } catch (Exception e) {
            Assert.fail("Jim should be allowed to read customers");
        }
        CredentialsInjector.inject(customerService, "jim", "jimspassword");
        try {
            Customer customer = new Customer();
            customer.setName("Fred");
            customerService.updateCustomer(customer);
            logger.info("Jim was able to update the customer");
        } catch (Exception e) {
            Assert.fail("Jim should be allowed to update a customer");
        }
        logger.info("All request were processed as expected");
    }

    public String getMessage(Exception e) {
        String message = "Error Message: " + e.getMessage();
        Throwable cause = e.getCause();
        if (cause != null) {
            return message + " cause: " + cause.getMessage();
        } else {
            return message;
        }
    }

    public static void main(String[] args) throws NoSuchCustomerException {
        int port = args.length == 2 && "http.port".equals(args[0]) 
                   ? Integer.valueOf(args[1]) : 8080;

        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");
        new JaxWsClient(port).run();
    }
}
