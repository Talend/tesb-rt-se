package org.talend.esb.sam.server.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.talend.esb.sam.server.persistence.criterias.CriteriaAdapter;
import org.talend.esb.sam.server.persistence.dialects.DatabaseDialect;
import org.talend.esb.sam.server.persistence.dialects.DialectFactory;

public class SAMProviderImpl extends JdbcDaoSupport implements SAMProvider {

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

    private String dialect;

    private DatabaseDialect dbDialect;

    private NamedParameterJdbcTemplate npJdbcTemplate = null;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    private final RowMapper<FlowEvent> eventMapper = new EventMapper();

    private final RowMapper<Flow> flowMapper = new FlowMapper();

    private final RowMapper<FlowEvent> flowEventMapper = new FlowEventMapper();

    public void init() {
        DialectFactory dialectFactory = new DialectFactory(getDataSource());
        this.dbDialect = dialectFactory.getDialect(dialect);
        this.npJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
    }

    @Override
    public FlowEvent getEventDetails(Integer eventID) {
        List<FlowEvent> list = npJdbcTemplate.query(
                SELECT_EVENT_QUERY, Collections.singletonMap("eventID", eventID), eventMapper);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<FlowEvent> getFlowDetails(String flowID) {
        return npJdbcTemplate.query(SELECT_FLOW_QUERY, Collections.singletonMap("flowID", flowID), flowEventMapper);
    }

    @Override
    public FlowCollection getFlows(CriteriaAdapter criteria) {
        FlowCollection flowCollection = new FlowCollection();
        final String whereClause = criteria.getWhereClause();
        String countQuery = dbDialect.getCountQuery().replaceAll(DatabaseDialect.SUBSTITUTION_STRING,
                (whereClause != null && whereClause.length() > 0) ? " AND " + whereClause : "");

        int rowCount = npJdbcTemplate.queryForObject(countQuery, criteria, Integer.class);
        int offset = Integer.parseInt(criteria.getValue("offset").toString());
        int limit = Integer.parseInt(criteria.getValue("limit").toString());

        List<Flow> flows = null;

        if (offset < rowCount) {
            String dataQuery = dbDialect.getDataQuery(criteria);

            if ((rowCount - offset) < limit) limit = rowCount - offset;
            String soffset = String.valueOf(offset);
            String slimit =  String.valueOf(limit);
            dataQuery = dataQuery.replaceAll(SUBSTITUTION_STRING_LIMIT, slimit).replaceAll(SUBSTITUTION_STRING_OFFSET, soffset);

            flows = npJdbcTemplate.query(dataQuery, criteria, flowMapper);
        }
        if(flows == null) flows = new ArrayList<Flow>();
        flowCollection.setFlows(flows);
        flowCollection.setCount(rowCount);
        return flowCollection;
    }

}
