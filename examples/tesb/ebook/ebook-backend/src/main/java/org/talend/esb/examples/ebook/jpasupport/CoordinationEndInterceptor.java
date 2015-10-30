package org.talend.esb.examples.ebook.jpasupport;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.osgi.service.coordinator.Coordination;

public class CoordinationEndInterceptor extends AbstractPhaseInterceptor<org.apache.cxf.message.Message> {

    public CoordinationEndInterceptor() {
        super(null, Phase.POST_MARSHAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Coordination coordination = message.getExchange().get(Coordination.class);
        coordination.end();
    }

}
