package org.talend.esb.sam.service;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EventCollection {
    
    private List<URI> events;

    public List<URI> getEvents() {
        return events;
    }

    public void setEvents(List<URI> eventLinks) {
        this.events = eventLinks;
    }
    
}
