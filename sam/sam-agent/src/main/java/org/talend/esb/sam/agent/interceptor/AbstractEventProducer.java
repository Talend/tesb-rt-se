package org.talend.esb.sam.agent.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.talend.esb.sam.agent.producer.EventProducer;

public abstract class AbstractEventProducer extends AbstractPhaseInterceptor<Message> {

	private InterceptorType type;
	protected EventProducer eventProducer;

	public InterceptorType getType() {
		return type;
	}

	public AbstractEventProducer(String phase, InterceptorType type,
			EventProducer eventProducer) {
		super(phase);
		this.type = type;
		this.eventProducer = eventProducer;

	}

	abstract public void handleMessage(Message message) throws Fault;

	public EventProducer getEventProducer() {
		return eventProducer;
	}
	
}
