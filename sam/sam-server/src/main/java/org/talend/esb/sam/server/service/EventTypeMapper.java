/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.sam.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

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
        event.getCustomInfoList().clear();
        event.getCustomInfoList().addAll(mapCustomInfo(eventType.getCustomInfo()));
        return event;
    }

    private static List<CustomInfo> mapCustomInfo(CustomInfoType ciType){
    	List<CustomInfo> ciList = new ArrayList<CustomInfo>();
    	
    	if (ciType != null){
	    	for (CustomInfoType.Item item : ciType.getItem()){
	    		CustomInfo ci = new CustomInfo();
	    		ci.setCustKey(item.getKey());
	    		ci.setCustValue(item.getValue());
	    		ciList.add(ci);
	    	}
    	}
    	return ciList;
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
            originator.setPrincipal(originatorType.getPrincipal());
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
