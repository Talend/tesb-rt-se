package org.sopera.monitoring.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.sopera.monitoring.producer.EventProducer;
import org.sopera.monitoring.producer.EventProducer.InterceptorType;

public abstract class AbstractEventProducer<T extends Message> extends
		AbstractPhaseInterceptor<T> {

	private InterceptorType type;
	private EventProducer eventProducer;

	public InterceptorType getType() {
		return type;
	}

	public AbstractEventProducer(String phase, InterceptorType type,
			EventProducer eventProducer) {
		super(phase);
		this.type = type;
		this.eventProducer = eventProducer;

	}

	abstract public void handleMessage(T message) throws Fault;

	public EventProducer getEventProducer() {
		return eventProducer;
	}
	
	protected boolean isLogMessageContent() {
		if (EventProducer.LOG_CONTENT_MODE_ON.equals(eventProducer.getLogMessageContent())) {
			return true;
		}
		return false;
	}
}
