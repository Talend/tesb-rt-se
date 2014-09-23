package org.talend.esb.policy.schemavalidate.testservice.client;

import java.util.List;

import junit.framework.Assert;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

@SuppressWarnings("deprecation")
public final class CustomerServiceTester {
    
    CustomerService customerService;
    
    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void testCustomerService(String customerName) throws NoSuchCustomerException {
        List<Customer> customers = null;

        System.out.println("Sending request for customers named " + customerName);
        customers = customerService.getCustomersByName(customerName);
        System.out.println("Response received");
        Assert.assertEquals(2, customers.size());
        Assert.assertEquals(customerName, customers.get(0).getName());
    }
}
