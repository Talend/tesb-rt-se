package org.talend.esb.sam.service;

import java.util.Map;

import org.talend.esb.sam.common.event.Event;


public interface SAMProvider {
    
    Event getEventDetails(String eventID);
    
    EventCollection getEvents(long offset, Map<String, String> params);

    Flow getFlowDetails(String flowID);

    FlowCollection getFlows(long offset, Map<String, String> params);
}
