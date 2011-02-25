package org.sopera.monitoring.interceptor;

import org.apache.cxf.message.Message;
import org.sopera.monitoring.producer.EventProducer;
import org.sopera.monitoring.producer.EventProducer.InterceptorType;

/**
 * Event producer for incoming messages
 * 
 * @author cschmuelling
 * 
 * @param <T>
 */
public class EventProducerIn<T extends Message> extends
		AbstractEventProducerIn<T> {

	public EventProducerIn(EventProducer eventProducer) {
		super(InterceptorType.IN,eventProducer);
	}

}
