package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "flowCollection")
public class AggregatedFlowCollection {

    private int count;
    
    private List<AggregatedFlow> flows;
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<AggregatedFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<AggregatedFlow> flows) {
        this.flows = flows;
    }
}
