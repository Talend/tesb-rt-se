/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.serviceclient;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.GregorianCalendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.cxf.attachment.ByteDataSource;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

public class EventMapper {

    /**
     * convert Event bean to EventType manually
     * 
     * @param event
     * @return
     */
    public static EventType map(Event event) {
        EventType eventType = new EventType();
        eventType.setTimestamp(convertDate(event.getTimestamp()));
        eventType.setEventType(convertEventType(event.getEventType()));
        OriginatorType origType = mapOriginator(event.getOriginator());
        eventType.setOriginator(origType);
        MessageInfoType miType = mapMessageInfo(event.getMessageInfo());
        eventType.setMessageInfo(miType);
        eventType.setCustomInfo(convertCustomInfo(event.getCustomInfo()));
        eventType.setContentCut(event.isContentCut());
        DataHandler datHandler = getDataHandlerForString(event);
        eventType.setContent(datHandler);
        return eventType;
    }

    private static DataHandler getDataHandlerForString(Event event) {
        DataSource ds;
        try {
            ds = new ByteDataSource(event.getContent().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        DataHandler datHandler = new DataHandler(ds);
        return datHandler;
    }

    private static MessageInfoType mapMessageInfo(MessageInfo messageInfo) {
    	if (messageInfo == null) {
    		return null;
    	}
        MessageInfoType miType = new MessageInfoType();
        miType.setMessageId(messageInfo.getMessageId());
        miType.setFlowId(messageInfo.getFlowId());
        miType.setPorttype(convertString(messageInfo.getPortType()));
        miType.setOperationName(messageInfo.getOperationName());
        miType.setTransport(messageInfo.getTransportType());
        return miType;
    }

    private static OriginatorType mapOriginator(Originator originator) {
    	if (originator == null) {
    		return null;
    	}
        OriginatorType origType = new OriginatorType();
        origType.setProcessId(originator.getProcessId());
        origType.setIp(originator.getIp());
        origType.setHostname(originator.getHostname());
        origType.setCustomId(originator.getCustomId());
        origType.setPrincipal(originator.getPrincipal());
        return origType;
    }

    private static CustomInfoType convertCustomInfo(Map<String, String> customInfo) {
        if (customInfo == null) {
            return null;
        }

        CustomInfoType ciType = new CustomInfoType();
        for (Entry<String, String> entry : customInfo.entrySet()) {
            CustomInfoType.Item cItem = new CustomInfoType.Item();
            cItem.setKey(entry.getKey());
            cItem.setValue(entry.getValue());
            ciType.getItem().add(cItem);
        }

        return ciType;
    }

    private static XMLGregorianCalendar convertDate(Date date) {
    	if (date == null) {
    		return null;
    	}
        XMLGregorianCalendar gCal = null;

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        
        try {
            gCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return gCal;
    }

    private static EventEnumType convertEventType(org.talend.esb.sam.common.event.EventTypeEnum eventType) {
    	if (eventType == null) {
    		return null;
    	}
        return EventEnumType.valueOf(eventType.name());
    }

    private static QName convertString(String str) {
        if (str != null) {
            return QName.valueOf(str);
        } else {
            return null;
        }
    }
}
