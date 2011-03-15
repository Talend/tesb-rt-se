package org.talend.esb.sam.agent.interceptor;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ServiceRoundTripTest-context.xml")
public class EventProducerTest {
    @Resource
    CustomerService customerService;
    
    @Resource
    MockEventSender eventSender;

    @Test
    public void testServiceCallSuccess() throws NoSuchCustomerException, InterruptedException {
        eventSender.getEventList().clear();
        List<Customer> customers = customerService.getCustomersByName("test");
        Assert.assertEquals(2, customers.size());
        List<Event> eventsList = eventSender.getEventList();
        Assert.assertEquals(4, eventsList.size());
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
        checkServerOut(eventsList.get(2));
        checkClientIn(eventsList.get(3));
    }
    
    @Test
    public void testServiceCallFault() throws NoSuchCustomerException, InterruptedException {
        eventSender.getEventList().clear();
        try {
            customerService.getCustomersByName("None");
            Assert.fail("We should get an exception for this request");
        } catch (NoSuchCustomerException e) {
            // That is what we expect to happen
        }
        List<Event> eventsList = eventSender.getEventList();
        Assert.assertEquals(4, eventsList.size());
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
        checkServerFaultOut(eventsList.get(2));
        checkClientFaultIn(eventsList.get(3));
    }

    private void checkClientIn(Event clientIn) {
        Assert.assertEquals(EventTypeEnum.RESP_IN, clientIn.getEventType());
    }

    private void checkServerOut(Event serverOut) {
        Assert.assertEquals(EventTypeEnum.RESP_OUT, serverOut.getEventType());
    }

    private void checkServerIn(Event clientOut) {
        Assert.assertEquals(EventTypeEnum.REQ_IN, clientOut.getEventType());
    }

    private void checkClientOut(Event clientOut) {
        Assert.assertEquals(EventTypeEnum.REQ_OUT, clientOut.getEventType());
    }
    
    private void checkServerFaultOut(Event serverOut) {
        Assert.assertEquals(EventTypeEnum.FAULT_OUT, serverOut.getEventType());
    }
    
    private void checkClientFaultIn(Event clientIn) {
        Assert.assertEquals(EventTypeEnum.FAULT_IN, clientIn.getEventType());
    }
}
