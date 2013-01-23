package org.talend.esb.sam.service;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flow {

    private String id;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FlowEvent> getEvents() {
        return events;
    }

    public void setEvents(List<FlowEvent> events) {
        this.events = events;
    }

    private List<FlowEvent> events;
    
}
