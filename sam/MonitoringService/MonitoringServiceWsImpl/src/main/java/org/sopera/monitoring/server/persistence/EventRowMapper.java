package org.sopera.monitoring.server.persistence;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.cxf.helpers.IOUtils;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventTypeEnum;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.springframework.jdbc.core.RowMapper;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setPersistedId(rs.getLong("ID"));
        event.setTimestamp(rs.getTimestamp("EI_TIMESTAMP"));
        event.setEventType(EventTypeEnum.valueOf(rs.getString("EI_EVENT_TYPE")));
        
        Originator originator = new Originator();
        originator.setProcessId(rs.getString("ORIG_PROCESS_ID"));
        originator.setIp(rs.getString("ORIG_IP"));
        originator.setHostname(rs.getString("ORIG_HOSTNAME"));
        originator.setCustomId(rs.getString("ORIG_CUSTOM_ID"));
        event.setOriginator(originator );
        
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId(rs.getString("MI_MESSAGE_ID"));
        messageInfo.setFlowId(rs.getString("MI_FLOW_ID"));
        messageInfo.setPortType(rs.getString("MI_PORT_TYPE"));
        messageInfo.setOperationName(rs.getString("MI_OPERATION_NAME"));
        messageInfo.setTransportType(rs.getString("MI_TRANSPORT_TYPE"));
        event.setMessageInfo(messageInfo );
        String content;
        try {
            content = IOUtils.toString(rs.getClob("MESSAGE_CONTENT").getAsciiStream());
        } catch (IOException e) {
            throw new RuntimeException("Error reading content", e);
        }
        event.setContent(content);
        return event;
    }
}