package org.sopera.monitoring.server.persistence;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventInfo;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.sopera.monitoring.handler.EventRepository;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class EventRepositoryImpl extends SimpleJdbcDaoSupport implements EventRepository {
        private static long lastId = 1;

	@Override
	@Transactional
	public void writeEvent(Event event) {
	    EventInfo info = event.getEventInfo();
	    Originator originator = info.getOriginator();	    
	    MessageInfo messageInfo = event.getMessageInfo();
	    
	    // FIXME implement with database sequence
	    event.setPersistedId(lastId ++);
	    getSimpleJdbcTemplate().update("insert into EVENTS (ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_PROCESS_ID, ORIG_IP, ORIG_HOSTNAME, "
	                                   + " ORIG_CUSTOM_ID, MI_MESSAGE_ID, MI_FLOW_ID, MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, MESSAGE_CONTENT, EVENT_EXTENSION) " 
	                                   + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
	           event.getPersistedId(),
	           info.getTimestamp(),
	           info.getEventType().toString(),
	           originator.getProcessId(),
	           originator.getIp(),
	           originator.getHostname(),
	           originator.getCustomId(),
                   messageInfo.getMessageId(),
	           messageInfo.getFlowId(),
	           messageInfo.getPortType(),
	           messageInfo.getOperationName(),
	           messageInfo.getTransportType(),
	           event.getContent(),
	           event.getExtension());
	}


}
