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
package org.talend.esb.sam.agent.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.talend.esb.sam.agent.eventproducer.EventProducerInterceptor;
import org.talend.esb.sam.agent.eventproducer.MessageToEventMapper;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerIn;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerOut;
import org.talend.esb.sam.agent.wiretap.WireTapIn;
import org.talend.esb.sam.agent.wiretap.WireTapOut;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Feature adds FlowIdProducer Interceptor and EventProducer Interceptor.
 * 
 * @author cschmuelling
 */
public class EventFeature extends AbstractFeature {

    private MessageToEventMapper mapper;
    private EventHandler eventSender;
    private boolean logMessageContent;

    public EventFeature() {
        super();
    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);

        FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
        provider.getInInterceptors().add(flowIdProducerIn);
        provider.getInFaultInterceptors().add(flowIdProducerIn);

        FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
        provider.getOutInterceptors().add(flowIdProducerOut);
        provider.getOutFaultInterceptors().add(flowIdProducerOut);

        EventProducerInterceptor epi = new EventProducerInterceptor(mapper, eventSender);
        
        WireTapIn wireTapIn = new WireTapIn(logMessageContent);
        provider.getInInterceptors().add(wireTapIn);
        provider.getInInterceptors().add(epi);
        provider.getInFaultInterceptors().add(wireTapIn);
        provider.getInFaultInterceptors().add(epi);

        WireTapOut wireTapOut = new WireTapOut(epi, logMessageContent);
        provider.getOutInterceptors().add(wireTapOut);
        WireTapOut wireTapOutFault = new WireTapOut(epi, logMessageContent);
        provider.getOutFaultInterceptors().add(wireTapOutFault);
    }

    public void setMapper(MessageToEventMapper mapper) {
        this.mapper = mapper;
    }

    public void setEventSender(EventHandler eventSender) {
        this.eventSender = eventSender;
    }

    public void setLogMessageContent(boolean logMessageContent) {
        this.logMessageContent = logMessageContent;
    }
    
}
