package org.talend.esb.sam.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;


public class SAMProviderImpl extends SimpleJdbcDaoSupport implements SAMProvider {

//    private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS "
//            + DatabaseDialect.SUBSTITUTION_STRING;

    private static final String SELECT_FLOW_QUERY = "select "
            + "EVENTS.ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
            + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
            + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, " + "CUST_KEY, CUST_VALUE " + "from EVENTS "
            + "left join EVENTS_CUSTOMINFO on EVENTS_CUSTOMINFO.EVENT_ID = EVENTS.ID " + "where MI_FLOW_ID = :flowID";

    private static final String SELECT_EVENT_QUERY = "select "
            + "ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_CUSTOM_ID, ORIG_PROCESS_ID, "
            + "ORIG_HOSTNAME, ORIG_IP, ORIG_PRINCIPAL, MI_PORT_TYPE, MI_OPERATION_NAME, "
            + "MI_MESSAGE_ID, MI_FLOW_ID, MI_TRANSPORT_TYPE, CONTENT_CUT, MESSAGE_CONTENT "
            + "from EVENTS where ID = :eventID";

    private DatabaseDialect dialect;

    public DatabaseDialect getDialect() {
        return dialect;
    }

    public void setDialect(DatabaseDialect dialect) {
        this.dialect = dialect;
    }

    private final RowMapper<Event> eventMapper = new EventMapper();
    
    private final RowMapper<FlowEvent> flowMapper = new FlowMapper();

    @Override
    public Event getEventDetails(String eventID) {
        List<Event> list = getSimpleJdbcTemplate().query(SELECT_EVENT_QUERY, eventMapper, Collections.singletonMap("eventID", eventID));
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public EventCollection getEvents(long offset, Map<String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Flow getFlowDetails(String flowID) {
        Flow flow = new Flow();
        flow.setId(flowID);
        List<FlowEvent> list = getSimpleJdbcTemplate().query(SELECT_FLOW_QUERY, flowMapper, Collections.singletonMap("flowID", flowID));
        flow.setEvents(list);
        return flow;
    }

    @Override
    public FlowCollection getFlows(long offset, Map<String, String> params) {
        FlowCollection flowCollection = new FlowCollection();
        // TODO Add implementation
        return flowCollection;
    }

}
