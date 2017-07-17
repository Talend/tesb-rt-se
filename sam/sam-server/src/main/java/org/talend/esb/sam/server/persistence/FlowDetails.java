package org.talend.esb.sam.server.persistence;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowDetails {

    private List<AggregatedFlowEvent> events;

    public List<AggregatedFlowEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AggregatedFlowEvent> events) {
        this.events = events;
    }

}
