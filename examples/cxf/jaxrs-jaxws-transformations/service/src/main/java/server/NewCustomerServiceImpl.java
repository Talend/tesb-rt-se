/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import customer.v2.Customer;

import org.apache.cxf.annotations.SchemaValidation;
import org.customer.service.CustomerService;
import org.customer.service.NoSuchCustomer;
import org.customer.service.NoSuchCustomerException;

@SchemaValidation
public class NewCustomerServiceImpl implements CustomerService {

    private ConcurrentHashMap<String, Customer> customers = 
        new ConcurrentHashMap<String, Customer>();

    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        Customer customer = getCustomerByName(name);

        List<Customer> customers = new ArrayList<Customer>();
        customers.add(customer);

        return customers;
    }

    public Customer updateCustomer(Customer newCustomer) {
        System.out.println("New CustomeService#updateCustomer: "
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
