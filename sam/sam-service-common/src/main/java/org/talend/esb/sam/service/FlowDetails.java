package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowDetails {

    public List<AggregatedFlowEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AggregatedFlowEvent> events) {
        this.events = events;
    }

    private List<AggregatedFlowEvent> events;
    
}
