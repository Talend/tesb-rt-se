package org.talend.esb.examples.ebook.jpasupport;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

public class CoordinationInterceptor extends AbstractPhaseInterceptor<org.apache.cxf.message.Message> {
    Coordinator coordinator;
    
    public CoordinationInterceptor(Coordinator coordinator) {
        super(null, Phase.PRE_INVOKE);
        this.coordinator = coordinator;
    }

    @Override
    public void handleMessage(org.apache.cxf.message.Message message) throws Fault {
        Coordination coordination = coordinator.begin("cxf", 10000);
        message.getExchange().put(Coordination.class, coordination);
    }

}
