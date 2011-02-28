package org.sopera.monitoring.interceptor;

import org.apache.cxf.message.Message;
import org.sopera.monitoring.producer.EventProducer;

/**
 * Event producer for incoming fault messages
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class EventProducerInFault<T extends Message> extends
		AbstractEventProducerIn<T> {
	public EventProducerInFault(EventProducer eventProducer) {
		super(InterceptorType.IN_FAULT, eventProducer);
	}
}
