package org.talend.esb.policy.samenabling;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
//import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.samenabling.SamEnablingPolicy.AppliesToType;
import org.talend.esb.sam.agent.feature.EventFeature;
import org.talend.esb.sam.agent.wiretap.WireTapIn;
import org.talend.esb.sam.agent.wiretap.WireTapOut;

public class SamEnablingInterceptorProvider extends
        AbstractPolicyInterceptorProvider {

	/**
     * 
     */
    private static final long serialVersionUID = 4595900233265934333L;
    
    private static final String AGENT_CONTEXT_PATH = "META-INF/tesb/agent-context.xml";
    private static ClassPathXmlApplicationContext springContext;
    private static RuntimeException springContextException;

    public SamEnablingInterceptorProvider() {

        super(Arrays.asList(SamEnablingPolicyBuilder.SAM_ENABLE));

        this.getOutInterceptors().add(new SAMEnableOutInterceptor());
        this.getOutFaultInterceptors().add(new SAMEnableOutInterceptor());
        this.getInInterceptors().add(new SAMEnableInInterceptor());
        this.getInFaultInterceptors().add(new SAMEnableInInterceptor());
        
        // Try to initialize SAM Spring context for non-OSGi environments
        try {
    		springContext = new ClassPathXmlApplicationContext(new String[] { AGENT_CONTEXT_PATH });
        } catch (RuntimeException e) {
        	// Ignore exception for OSGi and save it for non-OSGi
        	springContextException = e;
        }
    }

    static class SAMEnableOutInterceptor extends
            AbstractPhaseInterceptor<Message> {

        public SAMEnableOutInterceptor() {
            super(Phase.SETUP);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            process(message);
        }

    }

    static class SAMEnableInInterceptor extends
            AbstractPhaseInterceptor<Message> {

        public SAMEnableInInterceptor() {
            super(Phase.RECEIVE);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            process(message);
        }

    }

    static void process(Message message) {
        AssertionInfoMap aim = message.get(AssertionInfoMap.class);

        if (aim != null) {
            Collection<AssertionInfo> ais = aim
                    .get(SamEnablingPolicyBuilder.SAM_ENABLE);
            if (ais != null) {
                for (AssertionInfo ai : ais) {
                    if (ai.getAssertion() instanceof SamEnablingPolicy) {
                        SamEnablingPolicy vPolicy = (SamEnablingPolicy) ai
                                .getAssertion();

                        AppliesToType appliesToType = vPolicy
                                .getAppliesToType();

                        // Service service = ServiceModelUtil.getService(message
                        // .getExchange());
                        Exchange ex = message.getExchange();
                        Bus b = ex.getBus();

                        if (b.getFeatures().contains(EventFeature.class)) {
                            ai.setAsserted(true);
                            return;
                        }
                        Endpoint ep = ex.getEndpoint();

                        BundleContext context = b
                                .getExtension(BundleContext.class);
                        
                        EventFeature eventFeature = null;
                        if (context != null) {
                        	// OSGi
                            ServiceReference sref = context
                                    .getServiceReference(EventFeature.class
                                            .getName());

                            eventFeature = (EventFeature) context
                                    .getService(sref);
                        } else {
                        	// non-OSGi
                        	if (springContext == null) {
                        		throw springContextException;
                        	}
							eventFeature = (EventFeature) springContext.getBean("eventFeature");
                        }

                        if (MessageUtils.isRequestor(message)) {
                            if (MessageUtils.isOutbound(message)) { // REQ_OUT
                                if ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)) {
                                    Client cli = ex.get(Client.class);
                                    if (!cli.getOutInterceptors().contains(
                                            WireTapOut.class)) {
                                        eventFeature.initialize(cli, b);
                                        List<Interceptor<? extends Message>> outInterceptors = cli
                                                .getOutInterceptors();
                                        message.getInterceptorChain().add(
                                                outInterceptors);
                                        outInterceptors.getClass();
                                    }
                                }
                            } else { // RESP_IN
                                if ((appliesToType == AppliesToType.consumer || appliesToType == AppliesToType.always)) {
                                    Client cli = ex.get(Client.class);
                                    eventFeature.initialize(cli, b);
                                }
                            }
                        } else {
                            ServerRegistry registry = b
                                    .getExtension(ServerRegistry.class);
                            List<Server> servers = registry.getServers();
                            if (MessageUtils.isOutbound(message)) { // RESP_OUT
                                if ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)) {
                                    for (Server sr : servers) {
                                        EndpointInfo ei = sr.getEndpoint()
                                                .getEndpointInfo();
                                        if (null != ei
                                                && ei.getAddress().equals(
                                                        ep.getEndpointInfo()
                                                                .getAddress())) {
                                            eventFeature.initialize(sr, b);
                                        }
                                    }
                                }
                            } else { // REQ_IN
                                if ((appliesToType == AppliesToType.provider || appliesToType == AppliesToType.always)) {
                                    for (Server sr : servers) {
                                        EndpointInfo ei = sr.getEndpoint()
                                                .getEndpointInfo();
                                        if (null != ei
                                                && ei.getAddress().equals(
                                                        ep.getEndpointInfo()
                                                                .getAddress())
                                                && (!sr.getEndpoint()
                                                        .getInInterceptors()
                                                        .contains(
                                                                WireTapIn.class))) {
                                            eventFeature.initialize(sr, b);
                                            List<Interceptor<? extends Message>> inInterceptors = sr
                                                    .getEndpoint()
                                                    .getInInterceptors();
                                            message.getInterceptorChain().add(
                                                    inInterceptors);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (AssertionInfo ai : ais) {
                ai.setAsserted(true);
            }
        }
    }

    public static void shutdown() {
    	if (springContext != null) {
    		springContext.close();
    		springContext = null;
    		springContextException = new IllegalStateException("Sam enabling shut down. ");
    	}
    }
}
