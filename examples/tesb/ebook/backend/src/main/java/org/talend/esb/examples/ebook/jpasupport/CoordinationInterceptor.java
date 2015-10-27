package org.talend.esb.examples.ebook.jpasupport;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

@Singleton
public class CoordinationInterceptor extends AbstractPhaseInterceptor<org.apache.cxf.message.Message> {
    @Inject @OsgiService
    Coordinator coordinator;
    
    public CoordinationInterceptor() {
        super(null, Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(org.apache.cxf.message.Message message) throws Fault {
        Coordination coordination = coordinator.begin("cxf", 10000);
        message.getExchange().put(Coordination.class, coordination);
    }

}
