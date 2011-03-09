package org.talend.esb.sam.agent.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.talend.esb.sam.agent.interceptor.EventProducerIn;
import org.talend.esb.sam.agent.interceptor.EventProducerOut;
import org.talend.esb.sam.agent.interceptor.FlowIdProducerIn;
import org.talend.esb.sam.agent.interceptor.FlowIdProducerOut;
import org.talend.esb.sam.agent.interceptor.InterceptorType;
import org.talend.esb.sam.agent.interceptor.soap.FlowIdSoapCodec;
import org.talend.esb.sam.agent.interceptor.transport.FlowIdTransportCodec;
import org.talend.esb.sam.agent.producer.EventProducer;

/**
 * Feature adds FlowIdProducer Interceptor and EventProducer Interceptor.
 * 
 * @author cschmuelling
 */
public class EventFeature extends AbstractFeature {

    private EventProducer eventProducer;

    public EventFeature() {
        super();
    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
        if (eventProducer == null) {
            throw new RuntimeException("Eventproducer must be set in feature ");
        }

        FlowIdProducerIn<Message> flowIdProducerIn = new FlowIdProducerIn<Message>();
        FlowIdProducerOut<Message> flowIdProducerOut = new FlowIdProducerOut<Message>();

        FlowIdSoapCodec flowIdSoapCodec = new FlowIdSoapCodec();
        FlowIdTransportCodec flowIdHttpCodecIn = new FlowIdTransportCodec(Phase.READ);
        FlowIdTransportCodec flowIdHttpCodecOut = new FlowIdTransportCodec(Phase.USER_PROTOCOL);

        EventProducerIn eventProducerIn = new EventProducerIn(InterceptorType.IN, eventProducer);
        EventProducerOut eventProducerOut = new EventProducerOut(InterceptorType.OUT, eventProducer);
        EventProducerIn eventProducerInFault = new EventProducerIn(InterceptorType.IN_FAULT, eventProducer);
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
            lm.registerLifeCycleListener(new MonitoringBusListener(this.eventProducer));
        }
    }

    public void setEventProducer(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }
    
}
