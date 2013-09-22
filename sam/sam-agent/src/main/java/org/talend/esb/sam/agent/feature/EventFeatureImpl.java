package org.talend.esb.sam.agent.feature;

import java.util.Iterator;
import java.util.Queue;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.addressing.MAPAggregator;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.soap.MAPCodec;
import org.talend.esb.sam.agent.eventproducer.EventProducerInterceptor;
import org.talend.esb.sam.agent.eventproducer.MessageToEventMapper;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerIn;
import org.talend.esb.sam.agent.flowidprocessor.FlowIdProducerOut;
import org.talend.esb.sam.agent.wiretap.WireTapIn;
import org.talend.esb.sam.agent.wiretap.WireTapOut;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

public class EventFeatureImpl extends AbstractFeature implements EventFeature{

    /*
     * Log the message content to Event as Default
     */
    private boolean logMessageContent = true;
    /*
     * No max message content limitation as Default
     */
    private int maxContentLength = -1;

    /*
     * No WS-Addressing MessageID transfer as Default
     */
    private boolean enforceMessageIDTransfer;

    private EventProducerInterceptor epi;

    /**
     * Instantiates a new event feature.
     */
    public EventFeatureImpl() {
        super();
    }

    @Override
    public void initialize(Server server, Bus bus) {
        //if enforceMessageIDTransfer and WS Addressing feature/interceptors not enabled, 
        //then add its interceptors to InterceptorProvider
        if (server.getEndpoint().getBinding() instanceof SoapBinding &&
                enforceMessageIDTransfer && !detectWSAddressingFeature(server.getEndpoint(), bus)) {
            addWSAddressingInterceptors(server.getEndpoint());
        }

        initializeProvider(server.getEndpoint(), bus);
    }

    @Override
    public void initialize(Client client, Bus bus) {
        //if enforceMessageIDTransfer and WS Addressing feature/interceptors not enabled, 
        //then add its interceptors to InterceptorProvider
        if (client.getEndpoint().getBinding() instanceof SoapBinding &&
                enforceMessageIDTransfer && !detectWSAddressingFeature(client, bus)) {
            addWSAddressingInterceptors(client);
        }

        initializeProvider(client, bus);
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.feature.AbstractFeature#initializeProvider(org.apache.cxf.interceptor.InterceptorProvider, org.apache.cxf.Bus)
     */
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);

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

    @Override
    public void setLogMessageContent(boolean logMessageContent) {
        this.logMessageContent = logMessageContent;
    }

    @Override
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    public void setEnforceMessageIDTransfer(boolean enforceMessageIDTransfer) {
		this.enforceMessageIDTransfer = enforceMessageIDTransfer;
	}

	/**
     * Sets the queue.
     *
     * @param queue the new queue
     */
    public void setQueue(Queue<Event> queue) {
        if (epi == null) {
            MessageToEventMapper mapper = new MessageToEventMapper();
            mapper.setMaxContentLength(maxContentLength);
            
            epi = new EventProducerInterceptor(mapper, queue);
        }
    }

    @Override
    public void setHandler(EventHandler handler) {
        if (this.epi != null) {
            this.epi.setHandler(handler);
        }
    }

    /**
     * detect if WS Addressing feature already enabled.
     *
     * @param provider the interceptor provider
     * @param bus the bus
     * @return true, if successful
     */
    private boolean detectWSAddressingFeature(InterceptorProvider provider, Bus bus) {
        //detect on the bus level
        if (bus.getFeatures() != null) {
            Iterator<Feature> busFeatures = bus.getFeatures().iterator();
            while (busFeatures.hasNext()) {
                Feature busFeature = busFeatures.next();
                if (busFeature instanceof WSAddressingFeature) {
                    return true;
                }
            }
        }

        //detect on the endpoint/client level
        Iterator<Interceptor<? extends Message>> interceptors = provider.getInInterceptors().iterator();
        while (interceptors.hasNext()) {
            Interceptor<? extends Message> ic = interceptors.next();
            if (ic instanceof MAPAggregator) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add WSAddressing Interceptors to InterceptorProvider, in order to using
     * AddressingProperties to get MessageID.
     *
     * @param provider the interceptor provider
     */
    private void addWSAddressingInterceptors(InterceptorProvider provider) {
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
