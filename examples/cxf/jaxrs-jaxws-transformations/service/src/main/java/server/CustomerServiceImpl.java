/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomer;
import com.example.customerservice.NoSuchCustomerException;

import customer.v1.Customer;

import org.apache.cxf.annotations.SchemaValidation;

@SchemaValidation
public class CustomerServiceImpl implements CustomerService {

    private ConcurrentHashMap<String, Customer> customers = 
        new ConcurrentHashMap<String, Customer>();

    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        Customer customer = getCustomerByName(name);

        List<Customer> customers = new ArrayList<Customer>();
        customers.add(customer);

        return customers;
    }

    public Customer updateCustomer(Customer newCustomer) {
        System.out.println("Old CustomeService#updateCustomer: "
                           + newCustomer.getName());
        customers.put(newCustomer.getName(), newCustomer);
        return newCustomer;
    }

    public Customer getCustomerByName(String name) throws NoSuchCustomerException {
        Customer customer = customers.get(name);
        if (customer == null) {
            NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
            noSuchCustomer.setCustomerName(name);
            throw new NoSuchCustomerException("Did not find any matching customer for name=" + name,
                                              noSuchCustomer);
        }

        return customer;
    }
}
