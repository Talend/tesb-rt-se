package org.sopera.monitoring.server.persistence;

import java.util.Map;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.sopera.monitoring.event.CustomInfo;
import org.sopera.monitoring.event.persistence.EventRepository;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.transaction.annotation.Transactional;

public class EventRepositoryImpl extends SimpleJdbcDaoSupport implements EventRepository {
    DataFieldMaxValueIncrementer incrementer;

    public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
        this.incrementer = incrementer;
    }

    @Override
    @Transactional
    public void writeEvent(Event event) {
        Originator originator = event.getOriginator();	    
        MessageInfo messageInfo = event.getMessageInfo();

        long id = incrementer.nextLongValue();
        event.setPersistedId(id);

        getSimpleJdbcTemplate().update("insert into EVENTS (ID, EI_TIMESTAMP, EI_EVENT_TYPE, ORIG_PROCESS_ID, ORIG_IP, ORIG_HOSTNAME, "
                                       + " ORIG_CUSTOM_ID, MI_MESSAGE_ID, MI_FLOW_ID, MI_PORT_TYPE, MI_OPERATION_NAME, MI_TRANSPORT_TYPE, MESSAGE_CONTENT) " 
                                       + " values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                                       event.getPersistedId(),
                                       event.getTimestamp(),
                                       event.getEventType().toString(),
                                       originator.getProcessId(),
                                       originator.getIp(),
                                       originator.getHostname(),
                                       originator.getCustomId(),
                                       messageInfo.getMessageId(),
                                       messageInfo.getFlowId(),
                                       messageInfo.getPortType(),
                                       messageInfo.getOperationName(),
                                       messageInfo.getTransportType(),
                                       event.getContent());
        
        //insert customInfo (key/value) into DB
        CustomInfo cInfo = event.getCustomInfo();
        if (cInfo != null){
	        Map<String, Object> prop = cInfo.getProperties();
	                
	        if (prop != null && prop.size() > 0){
		        for (Map.Entry<String, Object> entry : prop.entrySet()) {
		            long cust_id = incrementer.nextLongValue();
		            //System.out.println("cust_value: " + entry.getValue().toString());
		            getSimpleJdbcTemplate().update("insert into EVENTS_CUSTOMINFO (ID, EVENT_ID, CUST_KEY, CUST_VALUE) values (?,?,?,?)",
		            		cust_id,
		            		id,
		            		entry.getKey(),
		            		entry.getValue().toString());
		        }
	        }
        }
        
    }

}
