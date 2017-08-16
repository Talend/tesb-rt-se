package org.talend.esb.sam.server.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.common.event.EventTypeEnum;

public class FlowEventMapper implements RowMapper<FlowEvent> {

    @Override
    public FlowEvent mapRow(ResultSet rs, int rowNum) throws SQLException {

        FlowEvent event = new FlowEvent();

        event.setId(rs.getLong("ID"));
        event.setTimestamp(rs.getTimestamp("EI_TIMESTAMP").getTime());
        event.setType(EventTypeEnum.valueOf(rs.getString("EI_EVENT_TYPE")));

        event.setCustomId(rs.getString("ORIG_CUSTOM_ID"));
        event.setProcess(rs.getString("ORIG_PROCESS_ID"));
        event.setHost(rs.getString("ORIG_HOSTNAME"));
        event.setIp(rs.getString("ORIG_IP"));
        event.setPrincipal(rs.getString("ORIG_PRINCIPAL"));

        event.setPort(rs.getString("MI_PORT_TYPE"));
        event.setOperation(rs.getString("MI_OPERATION_NAME"));
        event.setFlowID(rs.getString("MI_FLOW_ID"));
        event.setMessageID(rs.getString("MI_MESSAGE_ID"));
        event.setTransport(rs.getString("MI_TRANSPORT_TYPE"));

        event.setContentCut(rs.getBoolean("CONTENT_CUT"));

        event.setCustomKey(rs.getString("CUST_KEY"));
        event.setCustomValue(rs.getString("CUST_VALUE"));
        return event;
    }

}
