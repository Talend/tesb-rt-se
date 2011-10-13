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

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.ws.addressing.soap.MAPCodec;

import org.springframework.beans.factory.InitializingBean;

import org.talend.esb.sam.agent.eventproducer.EventProducerInterceptor;
import org.talend.esb.sam.agent.eventproducer.MessageToEventMapper;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerIn;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerOut;
import org.talend.esb.sam.agent.wiretap.WireTapIn;
import org.talend.esb.sam.agent.wiretap.WireTapOut;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Feature for adding FlowId Interceptor and EventProducer Interceptor.
 * 
 */
public class EventFeature extends AbstractFeature implements InitializingBean {

	/*
	 * Log the message content to Event as Default
	 */
    private boolean logMessageContent = true;
    /*
     * No max message content limitation as Default
     */
	private int maxContentLength = -1;
    private Queue<Event> queue;
    private EventProducerInterceptor epi;

    protected static Logger logger = Logger.getLogger(EventFeature.class.getName());

    public EventFeature() {
        super();
    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);

        //if WS Addressing feature/interceptors not enabled, then adding
        //its interceptors to InterceptorProvider
        if (!detectWSAddressingFeature(provider,bus)){
            addWSAddressingInterceptors(provider);
        }

        FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
        provider.getInInterceptors().add(flowIdProducerIn);
        provider.getInFaultInterceptors().add(flowIdProducerIn);

        FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
        provider.getOutInterceptors().add(flowIdProducerOut);
        provider.getOutFaultInterceptors().add(flowIdProducerOut);

        WireTapIn wireTapIn = new WireTapIn(logMessageContent);
        provider.getInInterceptors().add(wireTapIn);
        provider.getInInterceptors().add(epi);
        provider.getInFaultInterceptors().add(epi);

        WireTapOut wireTapOut = new WireTapOut(epi, logMessageContent);
        provider.getOutInterceptors().add(wireTapOut);
        provider.getOutFaultInterceptors().add(wireTapOut);
    }

    public void setLogMessageContent(boolean logMessageContent) {
        this.logMessageContent = logMessageContent;
    }
    
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
    
    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
        if (epi == null){
	        MessageToEventMapper mapper = new MessageToEventMapper();
	        mapper.setMaxContentLength(maxContentLength);
	        
	        epi = new EventProducerInterceptor(mapper, queue);
        }
    }

    public void setHandler(EventHandler handler) {
    	if (this.epi != null){
    		this.epi.setHandler(handler);
    	}
	}
    
    @Override
    public void afterPropertiesSet() throws Exception {

    }
    
    /**
     * detect if WS Addressing feature already enabled
     * @param provider
     * @param bus
     * @return
     */
    private boolean detectWSAddressingFeature(InterceptorProvider provider, Bus bus){
        //detect on the bus level
        if (bus.getFeatures() != null){
	        Iterator<AbstractFeature> busFeatures = bus.getFeatures().iterator();
	        while (busFeatures.hasNext()){
	            AbstractFeature busFeature = busFeatures.next();
	            if (busFeature instanceof WSAddressingFeature){
	                return true;
	            }
	        }
        }

        //detect on the endpoint/client level
        Iterator<Interceptor<? extends Message>> interceptors = provider.getInInterceptors().iterator();
        while (interceptors.hasNext()){
            Interceptor<? extends Message> ic = interceptors.next();
            if (ic instanceof MAPAggregator){
                return true;
            }
        }

        return false;
    }

    /**
     * Add WSAddressing Interceptors to InterceptorProvider, in order to using
     * AddressingProperties to get MessageID.
     * @param provider
     */
    private void addWSAddressingInterceptors(InterceptorProvider provider){
        MAPAggregator mapAggregator = new MAPAggregator();
        MAPCodec mapCodec = new MAPCodec();

        provider.getInInterceptors().add(mapAggregator);
        provider.getInInterceptors().add(mapCodec);

        provider.getOutInterceptors().add(mapAggregator);
        provider.getOutInterceptors().add(mapCodec);

        provider.getInFaultInterceptors().add(mapAggregator);
        provider.getInFaultInterceptors().add(mapCodec);

        provider.getOutFaultInterceptors().add(mapAggregator);
        provider.getOutFaultInterceptors().add(mapCodec);
    }

}
