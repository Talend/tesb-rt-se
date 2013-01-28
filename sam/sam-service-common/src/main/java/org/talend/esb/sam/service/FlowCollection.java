package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowCollection {

    private int count;
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private List<Flow> flows;

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows(List<Flow> flows) {
        this.flows = flows;
    }
}
