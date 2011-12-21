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
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.cxf.attachment.ByteDataSource;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.agent.util.Converter;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

/**
 * The Class EventMapper using for mapping and converting events.
 */
public final class EventMapper {

    /**
     * Instantiates a new event mapper.
     */
    private EventMapper() {
    }

    /**
     * convert Event bean to EventType manually.
     *
     * @param event the event
     * @return the event type
     */
    public static EventType map(Event event) {
        EventType eventType = new EventType();
        eventType.setTimestamp(Converter.convertDate(event.getTimestamp()));
        eventType.setEventType(convertEventType(event.getEventType()));
        OriginatorType origType = mapOriginator(event.getOriginator());
        eventType.setOriginator(origType);
        MessageInfoType miType = mapMessageInfo(event.getMessageInfo());
        eventType.setMessageInfo(miType);
        eventType.setCustomInfo(convertCustomInfo(event.getCustomInfo()));
        eventType.setContentCut(event.isContentCut());
        if (event.getContent() != null) {
            DataHandler datHandler = getDataHandlerForString(event);
            eventType.setContent(datHandler);
        }
        return eventType;
    }

    /**
     * Gets the data handler from event.
     *
     * @param event the event
     * @return the data handler
     */
    private static DataHandler getDataHandlerForString(Event event) {
        try {
            return new DataHandler(new ByteDataSource(event.getContent().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Mapping message info.
     *
     * @param messageInfo the message info
     * @return the message info type
     */
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

    /**
     * Mapping originator.
     *
     * @param originator the originator
     * @return the originator type
     */
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

    /**
     * Convert custom info.
     *
     * @param customInfo the custom info map
     * @return the custom info type
     */
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

    /**
     * Convert event type.
     *
     * @param eventType the event type
     * @return the event enum type
     */
    private static EventEnumType convertEventType(org.talend.esb.sam.common.event.EventTypeEnum eventType) {
        if (eventType == null) {
            return null;
        }
        return EventEnumType.valueOf(eventType.name());
    }

    /**
     * Convert string to qname.
     *
     * @param str the string
     * @return the qname
     */
    private static QName convertString(String str) {
        if (str != null) {
            return QName.valueOf(str);
        } else {
            return null;
        }
    }
}
