package org.talend.esb.sam.service;

import java.net.URI;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EventCollection {
    
    private Map<String, URI> events;

    public Map<String, URI> getEvents() {
        return events;
    }

    public void setEvents(Map<String, URI> events) {
        this.events = events;
    }
    
}
