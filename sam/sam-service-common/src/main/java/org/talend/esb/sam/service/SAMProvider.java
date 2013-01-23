package org.talend.esb.sam.service;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.server.ui.CriteriaAdapter;


public interface SAMProvider {
    
    Event getEventDetails(String eventID);
    
    EventCollection getEvents(CriteriaAdapter criteria);

    Flow getFlowDetails(String flowID);

    FlowCollection getFlows(CriteriaAdapter criteria);
}
