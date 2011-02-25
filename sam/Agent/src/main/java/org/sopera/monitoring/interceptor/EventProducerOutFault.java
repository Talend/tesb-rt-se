package org.sopera.monitoring.interceptor;

import org.apache.cxf.message.Message;
import org.sopera.monitoring.producer.EventProducer;
import org.sopera.monitoring.producer.EventProducer.InterceptorType;

/**
 * Eventproducer for outgoing faults
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class EventProducerOutFault<T extends Message> extends
		AbstractEventProducerOut<T> {

	public EventProducerOutFault(EventProducer eventProducer) {
		super(InterceptorType.OUT_FAULT, eventProducer);
	}

}
