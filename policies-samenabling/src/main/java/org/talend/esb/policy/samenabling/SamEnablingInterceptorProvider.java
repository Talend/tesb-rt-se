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
import org.talend.esb.sam.agent.feature.EventFeature;

import org.talend.esb.policy.samenabling.SamEnablingPolicy.ApplyToType;

public class SamEnablingInterceptorProvider extends
        AbstractPolicyInterceptorProvider {

    /**
     * 
     */
    private static final long serialVersionUID = 4595900233265934333L;

    public SamEnablingInterceptorProvider() {

        super(Arrays.asList(SamEnablingPolicyBuilder.SAM_ENABLE));

        this.getOutInterceptors().add(new SAMEnableOutInterceptor());
        this.getOutFaultInterceptors().add(new SAMEnableOutInterceptor());
        this.getInInterceptors().add(new SAMEnableInInterceptor());
        this.getInFaultInterceptors().add(new SAMEnableInInterceptor());

    }

    static class SAMEnableOutInterceptor extends
            AbstractPhaseInterceptor<Message> {

        public SAMEnableOutInterceptor() {
            super(Phase.POST_LOGICAL_ENDING);
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

            if (ais == null) {
                return;
            }

            for (AssertionInfo ai : ais) {
                if (ai.getAssertion() instanceof SamEnablingPolicy) {
                    SamEnablingPolicy vPolicy = (SamEnablingPolicy) ai
                            .getAssertion();

                    ApplyToType applyToType = vPolicy.getApplyToType();

                    // Service service = ServiceModelUtil.getService(message
                    // .getExchange());
                    Exchange ex = message.getExchange();
                    Bus b = ex.getBus();

                    if (b.getFeatures().contains(EventFeature.class))
                        return;
                    
                    Endpoint ep = ex.getEndpoint();

                    BundleContext context = b.getExtension(BundleContext.class);
                    ServiceReference sref = context
                            .getServiceReference(EventFeature.class.getName());

                    EventFeature eventFeature = (EventFeature) context
                            .getService(sref);

                    if (MessageUtils.isRequestor(message)) {
                        if (MessageUtils.isOutbound(message)) { // REQ_OUT
                            if ((applyToType == ApplyToType.consumer || applyToType == ApplyToType.always)) {
                                Client cli = ex.get(Client.class);
                                eventFeature.initialize(cli, b);
                            }
                        } else { // RESP_IN
                            if ((applyToType == ApplyToType.consumer || applyToType == ApplyToType.always)) {
                                Client cli = ex.get(Client.class);
                                eventFeature.initialize(cli, b);
                            }
                        }
                    } else {
                        ServerRegistry registry = b
                                .getExtension(ServerRegistry.class);
                        List<Server> servers = registry.getServers();
                        if (MessageUtils.isOutbound(message)) { // RESP_OUT
                            if ((applyToType == ApplyToType.provider || applyToType == ApplyToType.always)) {
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
                            if ((applyToType == ApplyToType.provider || applyToType == ApplyToType.always)) {
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
                        }
                    }

                }
                ai.setAsserted(true);
                return;
            }
        }
    }

}
