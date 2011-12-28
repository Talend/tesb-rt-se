package org.talend.esb.sam.agent.eventproducer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.talend.esb.sam.common.event.Event;

import com.example.customerservice.Customer;
import com.example.customerservice.NoSuchCustomerException;

@ContextConfiguration("/ServerSAMEnabledTest-context.xml")
public class ServerSAMEnabledTest extends AbstractEventProducerTest{

    @Test
    public void testServiceCallSuccess() throws NoSuchCustomerException, InterruptedException {
        queue.clear();
        List<Customer> customers = customerService.getCustomersByName("test");
        Assert.assertEquals(2, customers.size());
        List<Event> eventsList = new ArrayList<Event>();
        while(!queue.isEmpty()){
            eventsList.add(queue.remove());
        }
        checkEventsNum(eventsList, 2);
        checkFlowIdPresentAndSame(eventsList);
        checkReq_In(eventsList.get(0));
        checkResp_Out(eventsList.get(1));
    }

    @Test
    public void testServiceCallFault() throws NoSuchCustomerException, InterruptedException {
        queue.clear();
        try {
            customerService.getCustomersByName("None");
            Assert.fail("We should get an exception for this request");
        } catch (NoSuchCustomerException e) {
            // That is what we expect to happen
        }
        List<Event> eventsList = new ArrayList<Event>();
        while(!queue.isEmpty()){
            eventsList.add(queue.remove());
        }
        checkEventsNum(eventsList, 2);
        checkFlowIdPresentAndSame(eventsList);
        checkReq_In(eventsList.get(0));
        checkFault_Out(eventsList.get(1));
    }

    @Test
    public void testServiceCallOneway() {
        queue.clear();

        Customer cust = new Customer();
        cust.setName("test");
        customerService.updateCustomer(cust);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Event> eventsList = new ArrayList<Event>();
        while(!queue.isEmpty()){
            eventsList.add(queue.remove());
        }

        checkEventsNum(eventsList, 1);
        checkFlowIdPresentAndSame(eventsList);
        checkReq_In(eventsList.get(0));
    }
}
