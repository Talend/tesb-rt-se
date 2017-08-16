package org.talend.esb.sam.server.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.common.event.EventTypeEnum;

public class FlowMapper implements RowMapper<Flow> {

    @Override
    public Flow mapRow(ResultSet rs, int rowNum) throws SQLException {

        Flow flow = new Flow();

        flow.setflowID(rs.getString("MI_FLOW_ID"));

        flow.setTimestamp(rs.getTimestamp("EI_TIMESTAMP").getTime());
        flow.setEventType(EventTypeEnum.valueOf(rs.getString("EI_EVENT_TYPE")));

        flow.setPort(rs.getString("MI_PORT_TYPE"));
        flow.setOperation(rs.getString("MI_OPERATION_NAME"));
        flow.setTransport(rs.getString("MI_TRANSPORT_TYPE"));
        flow.setHost(rs.getString("ORIG_HOSTNAME"));
        flow.setIp(rs.getString("ORIG_IP"));

        return flow;
    }

}
