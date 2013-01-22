package org.talend.esb.sam.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.talend.esb.sam.common.event.Event;
//import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;

public class SAMProviderImpl extends SimpleJdbcDaoSupport implements SAMProvider {

//    private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS "
//            + DatabaseDialect.SUBSTITUTION_STRING;

//    private static final String SELECT_FLOW_QUERY = "select "
//            + "EVENTS.ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
//            + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
//            + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, " + "CUST_KEY, CUST_VALUE " + "from EVENTS "
//            + "left join EVENTS_CUSTOMINFO on EVENTS_CUSTOMINFO.EVENT_ID = EVENTS.ID " + "where MI_FLOW_ID = :flowID";

    private static final String SELECT_EVENT_QUERY = "select "
            + "ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
            + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
            + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, MESSAGE_CONTENT "
            + "from EVENTS where ID = :eventID";

//    private DatabaseDialect dialect;

    private final RowMapper<Event> eventMapper = new EventMapper();

    @Override
    public Event getEventDetails(String eventID) {
        return getSimpleJdbcTemplate().queryForObject(SELECT_EVENT_QUERY, eventMapper,
                Collections.singletonMap("eventID", eventID));
    }

    @Override
    public EventCollection getEvents(long offset, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Flow getFlowDetails(String flowID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FlowCollection getFlows(long offset, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

}
