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
