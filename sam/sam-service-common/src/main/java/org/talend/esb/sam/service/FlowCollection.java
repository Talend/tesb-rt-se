package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowCollection {

    @XmlElement
    private List<FlowEvent> flows;

    public List<FlowEvent> getFlows() {
        return flows;
    }

    public void setFlows(List<FlowEvent> flows) {
        this.flows = flows;
    }
}
