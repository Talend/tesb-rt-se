package org.talend.esb.sam.server.persistence;

import java.util.List;

import org.talend.esb.sam.server.persistence.criterias.CriteriaAdapter;

public interface SAMProvider {
	
	String SUBSTITUTION_STRING_LIMIT = "%%LIMIT%%";
	String SUBSTITUTION_STRING_OFFSET = "%%OFFSET%%";

    FlowEvent getEventDetails(Integer eventID);

    List<FlowEvent> getFlowDetails(String flowID);

    FlowCollection getFlows(CriteriaAdapter criteria);

}
