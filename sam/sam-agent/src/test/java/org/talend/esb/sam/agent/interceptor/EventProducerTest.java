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
        checkFlowIdPresentAndSame(eventsList);
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
        checkFlowIdPresentAndSame(eventsList);
        checkClientOut(eventsList.get(0));
        checkServerIn(eventsList.get(1));
        checkServerFaultOut(eventsList.get(2));
        checkClientFaultIn(eventsList.get(3));
    }
    
	private void checkFlowIdPresentAndSame(List<Event> eventsList) {
		String flowId = eventsList.get(0).getMessageInfo().getFlowId();
        for (Event event : eventsList) {
        	String newFlowId = event.getMessageInfo().getFlowId();
        	Assert.assertNotNull(newFlowId);
        	Assert.assertEquals("All flowIds should be the same", flowId, newFlowId);
		}
	}

    private void checkClientIn(Event clientIn) {
        Assert.assertEquals(EventTypeEnum.RESP_IN, clientIn.getEventType());
    }

    private void checkServerOut(Event serverOut) {
        Assert.assertEquals(EventTypeEnum.RESP_OUT, serverOut.getEventType());
    }

    private void checkServerIn(Event serverIn) {
        Assert.assertEquals(EventTypeEnum.REQ_IN, serverIn.getEventType());
    }

    private void checkClientOut(Event clientOut) {
        Assert.assertEquals(EventTypeEnum.REQ_OUT, clientOut.getEventType());
        Assert.assertNotNull(clientOut.getMessageInfo().getFlowId());
    }
    
    private void checkServerFaultOut(Event serverOut) {
        Assert.assertEquals(EventTypeEnum.FAULT_OUT, serverOut.getEventType());
        Assert.assertTrue("Content should not be empty", (serverOut.getContent() != null) && (serverOut.getContent().length() >0));
    }
    
    private void checkClientFaultIn(Event clientIn) {
        Assert.assertEquals(EventTypeEnum.FAULT_IN, clientIn.getEventType());
        Assert.assertTrue("Content should not be empty", (clientIn.getContent() != null) && (clientIn.getContent().length() >0));
    }
}
