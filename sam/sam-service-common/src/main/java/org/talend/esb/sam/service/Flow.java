package org.talend.esb.sam.service;

import java.net.URI;
import java.util.Map;

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

    public Map<String, URI> getEvents() {
        return events;
    }

    public void setEvents(Map<String, URI> events) {
        this.events = events;
    }

    private Map<String, URI> events;
    
}
