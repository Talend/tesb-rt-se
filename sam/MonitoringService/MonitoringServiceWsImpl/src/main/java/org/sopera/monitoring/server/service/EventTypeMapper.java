package org.sopera.monitoring.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.DataHandler;

import org.apache.cxf.helpers.IOUtils;
import org.sopera.monitoring._2010._09.common.EventEnumType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring._2010._09.common.MessageInfoType;
import org.sopera.monitoring._2010._09.common.OriginatorType;
import org.sopera.monitoring._2010._09.common.CustomInfoType;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.EventTypeEnum;
import org.sopera.monitoring.event.MessageInfo;
import org.sopera.monitoring.event.Originator;
import org.sopera.monitoring.event.CustomInfo;

public class EventTypeMapper {
    public static Event map(EventType eventType) {
        Event event = new Event();
        event.setEventType(mapEventTypeEnum(eventType.getEventType()));
        Date date = (eventType.getTimestamp()==null) ? new Date() : eventType.getTimestamp().toGregorianCalendar().getTime();        
        event.setTimestamp(date);
        event.setOriginator(mapOriginatorType(eventType.getOriginator()));
        MessageInfo messageInfo = mapMessageInfo(eventType.getMessageInfo());
        event.setMessageInfo(messageInfo);
        String content = mapContent(eventType.getContent());
        event.setContent(content);
        event.setCustomInfo(mapCustomInfo(eventType.getCustomInfo()));
        return event;
    }

    private static CustomInfo mapCustomInfo(CustomInfoType ciType){
    	CustomInfo ci = new CustomInfo();

    	if (ciType != null){
	    	for (CustomInfoType.Item item : ciType.getItem()){
	    		ci.getProperties().put(item.getKey(),item.getValue());
	    	}
    	}
    	return ci;
    }
    private static String mapContent(DataHandler dh) {
        if (dh == null) {
            return "";
        }
        try {
            InputStream is = dh.getInputStream();
            String content = IOUtils.toString(is);
            is.close();
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static MessageInfo mapMessageInfo(MessageInfoType messageInfoType) {
        MessageInfo messageInfo = new MessageInfo();
        if (messageInfoType != null) {
            messageInfo.setFlowId(messageInfoType.getFlowId());
            messageInfo.setMessageId(messageInfoType.getMessageId());
            messageInfo.setOperationName(messageInfoType.getOperationName());
            messageInfo.setPortType(messageInfoType.getPorttype() == null ? "" : messageInfoType.getPorttype().toString());
            messageInfo.setTransportType(messageInfoType.getTransport());
        }
        return messageInfo;
    }

    private static Originator mapOriginatorType(OriginatorType originatorType) {
        Originator originator = new Originator();
        if (originatorType != null) {
            originator.setCustomId(originatorType.getCustomId());
            originator.setHostname(originatorType.getHostname());
            originator.setIp(originatorType.getIp());
            originator.setProcessId(originatorType.getProcessId());
        }
        return originator;
    }

    private static EventTypeEnum mapEventTypeEnum(EventEnumType eventType) {
        if (eventType == null) {
            return EventTypeEnum.UNKNOWN;
        } else {
            return EventTypeEnum.valueOf(eventType.name());
        }
    }
}
