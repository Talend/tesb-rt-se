package org.talend.esb.sam.agent.eventproducer;

import java.util.List;
import java.util.Queue;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;

import com.example.customerservice.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractEventProducerTest {

    @Resource
    protected CustomerService customerService;

    @Resource
    protected Queue<Event> queue;

	protected void checkFlowIdPresentAndSame(List<Event> eventList) {
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
	protected void checkMessageIdPresentAndSame(List<Event> eventList, boolean isOneway) {
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

	protected void checkReq_Out(Event event) {
        Assert.assertEquals(EventTypeEnum.REQ_OUT, event.getEventType());
    }

	protected void checkReq_In(Event event) {
        Assert.assertEquals(EventTypeEnum.REQ_IN, event.getEventType());
    }

	protected void checkResp_Out(Event event) {
        Assert.assertEquals(EventTypeEnum.RESP_OUT, event.getEventType());
    }

	protected void checkResp_In(Event event) {
        Assert.assertEquals(EventTypeEnum.RESP_IN, event.getEventType());
    }

	protected void checkFault_Out(Event event) {
        Assert.assertEquals(EventTypeEnum.FAULT_OUT, event.getEventType());
        Assert.assertTrue("Content should not be empty", (event.getContent() != null) && (event.getContent().length() >0));
    }

	protected void checkFault_In(Event event) {
        Assert.assertEquals(EventTypeEnum.FAULT_IN, event.getEventType());
        Assert.assertTrue("Content should not be empty", (event.getContent() != null) && (event.getContent().length() >0));
    }
}
