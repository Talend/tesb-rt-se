package org.talend.esb.sam.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setContent(rs.getString("MESSAGE_CONTENT"));
        event.setContentCut(rs.getBoolean("CONTENT_CUT"));
        event.setEventType(EventTypeEnum.valueOf(rs.getString("EI_EVENT_TYPE")));
        
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setFlowId(rs.getString("MI_FLOW_ID"));
        messageInfo.setMessageId(rs.getString("MI_MESSAGE_ID"));
        messageInfo.setOperationName(rs.getString("MI_OPERATION_NAME"));
        messageInfo.setPortType(rs.getString("MI_PORT_TYPE"));
        messageInfo.setTransportType(rs.getString("MI_TRANSPORT_TYPE"));
        event.setMessageInfo(messageInfo);
        
        Originator originator = new Originator();
        originator.setCustomId(rs.getString("ORIG_CUSTOM_ID"));
        originator.setHostname(rs.getString("ORIG_HOSTNAME"));
        originator.setIp(rs.getString("ORIG_IP"));
        originator.setPrincipal(rs.getString("ORIG_PRINCIPAL"));
        originator.setProcessId(rs.getString("ORIG_PROCESS_ID"));
        event.setOriginator(originator);
        
        event.setPersistedId(rs.getLong("ID"));
        event.setTimestamp(rs.getDate("EI_TIMESTAMP"));
        return event;
    }

}
