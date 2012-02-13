/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.Collections;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.security.core.context.SecurityContextHolder;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerType;
import com.example.customerservice.NoSuchCustomerException;

public class CustomerServiceImpl implements CustomerService {

    @RolesAllowed("ROLE_ADMIN")
    @Override
    public void updateCustomer(Customer customer) {
    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Customer " + customer.getName() + " updated by user " + userName);
    }

    @RolesAllowed("ROLE_USER")
    @Override
    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        
        // The authenticated users can be retrieved from the spring security context at any time
    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Customers matching name " + name + " are read by user " + userName);
        Customer customer = createDummyCustomer(name);
        return Collections.singletonList(customer);
    }

	private Customer createDummyCustomer(String name) {
		Customer customer = new Customer();
        customer.setName(name);
        customer.setNumOrders(10);
        customer.setRevenue(10.500d);
        customer.setType(CustomerType.BUSINESS);
		return customer;
	}

}
