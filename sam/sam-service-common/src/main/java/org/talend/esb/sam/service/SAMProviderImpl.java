package org.talend.esb.sam.service;

import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;
import org.talend.esb.sam.server.ui.CriteriaAdapter;

public class SAMProviderImpl extends SimpleJdbcDaoSupport implements SAMProvider {

    private static final String COUNT_QUERY = "select count(distinct MI_FLOW_ID) from EVENTS "
            + DatabaseDialect.SUBSTITUTION_STRING;

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

    private final RowMapper<Flow> flowMapper = new FlowMapper();

    private final RowMapper<FlowEvent> flowEventMapper = new FlowEventMapper();

    @Override
    public Event getEventDetails(String eventID) {
        List<Event> list = getSimpleJdbcTemplate().query(SELECT_EVENT_QUERY, eventMapper,
                Collections.singletonMap("eventID", eventID));
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<FlowEvent> getFlowDetails(String flowID) {
        return getSimpleJdbcTemplate().query(SELECT_FLOW_QUERY, flowEventMapper,
                Collections.singletonMap("flowID", flowID));
    }

    @Override
    public FlowCollection getFlows(CriteriaAdapter criteria) {
        final String whereClause = criteria.getWhereClause();
        final String countQuery = COUNT_QUERY.replaceAll(DatabaseDialect.SUBSTITUTION_STRING,
                (whereClause != null && whereClause.length() > 0) ? " WHERE " + whereClause : "");
        int rowCount = getSimpleJdbcTemplate().queryForInt(countQuery, criteria);
        int offset = Integer.parseInt(criteria.getValue("offset").toString());
        List<Flow> flows = null;
        FlowCollection flowCollection = new FlowCollection();
        if (offset < rowCount) {
            String dataQuery = dialect.getDataQuery(criteria);
            flows = getSimpleJdbcTemplate().query(dataQuery, flowMapper, criteria);
        }
        flowCollection.setCount(rowCount);
        flowCollection.setFlows(flows);
        return flowCollection;
    }

}
