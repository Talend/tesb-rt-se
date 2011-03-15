package org.talend.esb.sam.agent.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventManipulator;

public class MockEventSender implements EventManipulator {
    private List<Event> eventList = new ArrayList<Event>();

    public List<Event> getEventList() {
        return eventList;
    }

    @Override
    public void handleEvent(Event event) {
        eventList.add(event);
    }

    
}
