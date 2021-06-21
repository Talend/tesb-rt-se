/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sam.server.persistence;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.cxf.helpers.IOUtils;
import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

/**
 * The Class EventRowMapper is mapping event to row.
 */
public class EventRowMapper implements RowMapper<Event> {

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
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
        originator.setPrincipal(rs.getString("ORIG_PRINCIPAL"));
        event.setOriginator(originator);

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMessageId(rs.getString("MI_MESSAGE_ID"));
        messageInfo.setFlowId(rs.getString("MI_FLOW_ID"));
        messageInfo.setPortType(rs.getString("MI_PORT_TYPE"));
        messageInfo.setOperationName(rs.getString("MI_OPERATION_NAME"));
        messageInfo.setTransportType(rs.getString("MI_TRANSPORT_TYPE"));
        event.setMessageInfo(messageInfo);
        event.setContentCut(rs.getBoolean("CONTENT_CUT"));
        try {
            event.setContent(IOUtils.toString(rs.getClob("MESSAGE_CONTENT").getAsciiStream()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading content", e);
        }
        return event;
    }
}
