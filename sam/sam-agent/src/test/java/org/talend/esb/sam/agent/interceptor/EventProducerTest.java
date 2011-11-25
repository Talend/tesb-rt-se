/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.sam.agent.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
    Queue<Event> queue;

    @Test
    public void testServiceCallSuccess() throws NoSuchCustomerException, InterruptedException {
        queue.clear();
        List<Customer> customers = customerService.getCustomersByName("test");
        Assert.assertEquals(2, customers.size());
        List<Event> eventsList = new ArrayList<Event>();
        while(!queue.isEmpty()){
        	eventsList.add(queue.remove());
        }
        Assert.assertEquals(4, eventsList.size());
        checkFlowIdPresentAndSame(eventsList);
        checkMessageIdPresentAndSame(eventsList, false);
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
        checkServerOut(eventsList.get(2));
        checkClientIn(eventsList.get(3));
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
        Assert.assertEquals(4, eventsList.size());
        checkFlowIdPresentAndSame(eventsList);
        checkMessageIdPresentAndSame(eventsList, false);
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
        checkServerFaultOut(eventsList.get(2));
        checkClientFaultIn(eventsList.get(3));
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

        Assert.assertEquals(2, eventsList.size());
        checkFlowIdPresentAndSame(eventsList);
        checkMessageIdPresentAndSame(eventsList, true);
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
    }

	private void checkFlowIdPresentAndSame(List<Event> eventList) {
		String flowId = eventList.get(0).getMessageInfo().getFlowId();
        for (Event event : eventList) {
        	String newFlowId = event.getMessageInfo().getFlowId();
        	Assert.assertNotNull(newFlowId);
        	Assert.assertEquals("All flowIds should be the same", flowId, newFlowId);
		}
	}

	/**
	 * check if the MessageId is present and same
	 * @param eventList
	 */
	private void checkMessageIdPresentAndSame(List<Event> eventList, boolean isOneway) {
        for (Event event : eventList) {
            String messageId = event.getMessageInfo().getMessageId();
            Assert.assertNotNull(messageId);
        }

        String messageId0 = eventList.get(0).getMessageInfo().getMessageId();
        String messageId1 = eventList.get(1).getMessageInfo().getMessageId();
        Assert.assertEquals("MessageId from REQ_OUT/REQ_IN should be the same", messageId0, messageId1);

        if (!isOneway){
            String messageId2 = eventList.get(2).getMessageInfo().getMessageId();
            String messageId3 = eventList.get(3).getMessageInfo().getMessageId();
            Assert.assertEquals("MessageId from RESP_OUT/RESP_IN(FAULT_OUT/FAULT_IN)  should be the same", messageId2, messageId3);
        }
    }

    private void checkClientIn(Event event) {
        Assert.assertEquals(EventTypeEnum.RESP_IN, event.getEventType());
    }

    private void checkServerOut(Event event) {
        Assert.assertEquals(EventTypeEnum.RESP_OUT, event.getEventType());
    }

    private void checkServerIn(Event event) {
        Assert.assertEquals(EventTypeEnum.REQ_IN, event.getEventType());
    }

    private void checkClientOut(Event event) {
        Assert.assertEquals(EventTypeEnum.REQ_OUT, event.getEventType());
        Assert.assertNotNull(event.getMessageInfo().getFlowId());
    }
    
    private void checkServerFaultOut(Event event) {
        Assert.assertEquals(EventTypeEnum.FAULT_OUT, event.getEventType());
        Assert.assertTrue("Content should not be empty", (event.getContent() != null) && (event.getContent().length() >0));
    }
    
    private void checkClientFaultIn(Event event) {
        Assert.assertEquals(EventTypeEnum.FAULT_IN, event.getEventType());
        Assert.assertTrue("Content should not be empty", (event.getContent() != null) && (event.getContent().length() >0));
    }
}
