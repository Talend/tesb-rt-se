/*
 * #%L
 * Service Activity Monitoring :: Example Service
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sam.example.service;

import java.util.List;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

/**
 * Delegates all calls to customerService2 to show how the flowId works with delegating services
 */
public class CustomerServiceImpl implements CustomerService {

    private CustomerService customerService2;

    private long dummyDelay;

    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        try {
            Thread.sleep(dummyDelay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return customerService2.getCustomersByName(name);
    }

    public void updateCustomer(Customer customer) {
        customerService2.updateCustomer(customer);
    }

    public void setCustomerService2(CustomerService customerService2) {
        this.customerService2 = customerService2;
    }

    public void setDummyDelay(long dummyDelay) {
        this.dummyDelay = dummyDelay;
    }

}
