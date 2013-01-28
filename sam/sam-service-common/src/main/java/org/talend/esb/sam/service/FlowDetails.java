package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowDetails {

    public List<FlowEvent> getEvents() {
        return events;
    }

    public void setEvents(List<FlowEvent> events) {
        this.events = events;
    }

    private List<FlowEvent> events;
    
}
