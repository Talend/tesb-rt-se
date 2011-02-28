package org.sopera.monitoring.feature;

import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.sopera.monitoring.interceptor.EventProducerIn;
import org.sopera.monitoring.interceptor.EventProducerOut;
import org.sopera.monitoring.interceptor.FlowIdProducerIn;
import org.sopera.monitoring.interceptor.FlowIdProducerOut;
import org.sopera.monitoring.interceptor.InterceptorType;
import org.sopera.monitoring.interceptor.soap.FlowIdSoapCodec;
import org.sopera.monitoring.interceptor.transport.FlowIdTransportCodec;
import org.sopera.monitoring.producer.EventProducer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Feature adds FlowIdProducer Interceptor and EventProducer Interceptor.
 * 
 * @author cschmuelling
 * 
 */
public class EventFeature extends AbstractFeature implements ApplicationContextAware  {

	private static Logger logger = Logger.getLogger(EventFeature.class
			.getName());
	
	private EventProducer eventProducer;
	private ApplicationContext context;


	
	/**
	 * Use this constructor
	 */
	public EventFeature() {
		super();
	}
	
	public void setApplicationContext(ApplicationContext c) throws BeansException {
		context = c;  
	}


	public EventProducer getEventProducer() {
		return eventProducer;
	}

	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {
		super.initializeProvider(provider, bus);
		
		if (this.context == null) {
			logger.severe("Application context not found. Feature registration aborted");
			return;
		}
		this.eventProducer = (EventProducer) context.getBean("eventProducer");
		
		if (this.eventProducer == null) {
			logger.severe("Bean 'eventProducer' not found. Feature registration aborted");
			return;
		}
		
		FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
		FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();
		
		FlowIdSoapCodec flowIdSoapCodec = new FlowIdSoapCodec();
		FlowIdTransportCodec<Message> flowIdHttpCodecIn = new FlowIdTransportCodec<Message>(Phase.READ);
		FlowIdTransportCodec<Message> flowIdHttpCodecOut = new FlowIdTransportCodec<Message>(Phase.USER_PROTOCOL);
		 

		EventProducerIn eventProducerIn = new EventProducerIn(InterceptorType.IN,
				eventProducer);
		EventProducerOut eventProducerOut = new EventProducerOut(InterceptorType.OUT,
				eventProducer);
		EventProducerIn eventProducerInFault = new EventProducerIn(InterceptorType.IN_FAULT,
				eventProducer);
		EventProducerOut eventProducerOutFault = new EventProducerOut(InterceptorType.OUT_FAULT,
				eventProducer);

		// Add FlowIdProducer
		provider.getInInterceptors().add(flowIdProducerIn);
		provider.getInInterceptors().add(flowIdSoapCodec);
		provider.getInInterceptors().add(flowIdHttpCodecIn);
		provider.getInFaultInterceptors().add(flowIdProducerIn);
		provider.getInFaultInterceptors().add(flowIdSoapCodec);
		provider.getInFaultInterceptors().add(flowIdHttpCodecIn);
		provider.getOutInterceptors().add(flowIdProducerOut);
		provider.getOutInterceptors().add(flowIdSoapCodec);
		provider.getOutInterceptors().add(flowIdHttpCodecOut);
		provider.getOutFaultInterceptors().add(flowIdProducerOut);
		provider.getOutFaultInterceptors().add(flowIdSoapCodec);
		provider.getOutFaultInterceptors().add(flowIdHttpCodecOut);

		// Add EventProducer
		provider.getInInterceptors().add(eventProducerIn);
		provider.getInFaultInterceptors().add(eventProducerInFault);
		provider.getOutInterceptors().add(eventProducerOut);
		provider.getOutFaultInterceptors().add(eventProducerOutFault);

		// add dependencies for incoming messages
		flowIdProducerIn.addAfter(EventProducerIn.class.getName());
		eventProducerIn.addBefore(FlowIdProducerIn.class.getName());

		// add dependencies for outgoing messages
		flowIdProducerOut.addAfter(EventProducerOut.class.getName());
		eventProducerOut.addBefore(FlowIdProducerOut.class.getName());

		// Add a lifecyclelistener for stopping sendeing events on bus shutdown.
		BusLifeCycleManager lm = bus.getExtension(BusLifeCycleManager.class);
		if (null != lm) {
			lm.registerLifeCycleListener(new MonitoringBusListener(
					this.eventProducer));
		}
	}
}
