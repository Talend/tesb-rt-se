package org.talend.esb.sam.service;

import java.net.URI;
import java.util.Map;

public class FlowCollection {
    
    private Map<String, URI> flows;

    public Map<String, URI> getFlowss() {
        return flows;
    }

    public void setFlows(Map<String, URI> flows) {
        this.flows = flows;
    }
}
