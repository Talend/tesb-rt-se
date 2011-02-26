package org.sopera.monitoring.interceptor;

import org.apache.cxf.message.Message;
import org.sopera.monitoring.producer.EventProducer;
import org.sopera.monitoring.producer.EventProducer.InterceptorType;

/**
 * Event producer for outgoing mesaasges
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class EventProducerOut<T extends Message> extends
		AbstractEventProducerOut<T> {

	public EventProducerOut(EventProducer eventProducer) {
		super(InterceptorType.OUT, eventProducer);
	}

}
