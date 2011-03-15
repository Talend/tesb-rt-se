package org.talend.esb.sam.agent.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.talend.esb.sam.agent.mapper.MessageToEventMapper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventManipulator;

/**
 * Acts as a callback for the out case and as an PhaseInterceptor in the in case
 */
public class EventProducerInterceptor extends AbstractPhaseInterceptor<Message> {
    MessageToEventMapper mapper;
    EventManipulator eventSender;

    public EventProducerInterceptor(MessageToEventMapper mapper, EventManipulator eventSender) {
        super(Phase.PRE_INVOKE);
        this.mapper = mapper;
        this.eventSender = eventSender;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Event event = mapper.mapToEvent(message);
        eventSender.handleEvent(event);
    }
    


}
