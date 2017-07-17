package org.talend.esb.sam.server.persistence;

import java.util.List;

public class FlowCollection {

    private int count;
    
    private List<Flow> flows;
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }
}
