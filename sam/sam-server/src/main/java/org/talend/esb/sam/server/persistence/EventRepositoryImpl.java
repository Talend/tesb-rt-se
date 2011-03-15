package org.talend.esb.sam.server.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.transaction.annotation.Transactional;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.common.event.persistence.EventRepository;

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
        
        writeCustomInfo(event);
        
    }

	@Override
	public Event readEvent(long id) {
		RowMapper<Event> rowMapper = new EventRowMapper();
		Event event = getSimpleJdbcTemplate().queryForObject("select * from EVENTS where ID=" + id, rowMapper);
		event.getCustomInfoList().clear();
		event.getCustomInfoList().addAll(readCustomInfo(id));
		return event;
	}
	
	/**
	 * write CustomInfo list into table
	 * @param event
	 */
	private void writeCustomInfo(Event event){
        //insert customInfo (key/value) into DB
		List<CustomInfo> ciList = event.getCustomInfoList();
		if (ciList != null && ciList.size() > 0){
			for (int i=0; i<ciList.size();i++){
				CustomInfo cInfo = ciList.get(i);
				long cust_id = incrementer.nextLongValue();
	            getSimpleJdbcTemplate().update("insert into EVENTS_CUSTOMINFO (ID, EVENT_ID, CUST_KEY, CUST_VALUE) values (?,?,?,?)",
	            		cust_id,
	            		event.getPersistedId(),
	            		cInfo.getCustKey(),
	            		cInfo.getCustValue().toString());
			}
		}
		
	}

	/**
	 * read CustomInfo list from table
	 * @param eventId
	 * @return
	 */
	private List<CustomInfo> readCustomInfo(long eventId){
		List<CustomInfo> customInfoList = new ArrayList<CustomInfo>();
		List<Map<String,Object>> rows = getSimpleJdbcTemplate().queryForList("select * from EVENTS_CUSTOMINFO where EVENT_ID=" + eventId);
		for (Map<String,Object> row : rows) {
			CustomInfo ci = new CustomInfo();
			//ci.setPersistedId((Long)row.get("ID"));
			ci.setCustKey((String)row.get("CUST_KEY"));
			ci.setCustValue(row.get("CUST_VALUE"));
			customInfoList.add(ci);
		}

		return customInfoList;
	}
}
