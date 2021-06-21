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
package org.talend.esb.sam.service.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.cxf.helpers.IOUtils;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;

/**
 * The Class EventTypeMapper used for mapping EventTypes.
 */
public final class EventTypeMapper {

    /**
     * Instantiates a new event type mapper.
     */
    private EventTypeMapper() {
    }

    /**
     * Map the EventType.
     *
     * @param eventType the event type
     * @return the event
     */
    public static Event map(EventType eventType) {
        Event event = new Event();
        event.setEventType(mapEventTypeEnum(eventType.getEventType()));
        Date date = (eventType.getTimestamp() == null)
                ? new Date() : eventType.getTimestamp().toGregorianCalendar().getTime();
        event.setTimestamp(date);
        event.setOriginator(mapOriginatorType(eventType.getOriginator()));
        MessageInfo messageInfo = mapMessageInfo(eventType.getMessageInfo());
        event.setMessageInfo(messageInfo);
        String content = mapContent(eventType.getContent());
        event.setContent(content);
        event.getCustomInfo().clear();
        event.getCustomInfo().putAll(mapCustomInfo(eventType.getCustomInfo()));
        return event;
    }

    /**
     * Map custom info.
     *
     * @param ciType the custom info type
     * @return the map
     */
    private static Map<String, String> mapCustomInfo(CustomInfoType ciType){
        Map<String, String> customInfo = new HashMap<String, String>();
        if (ciType != null){
            for (CustomInfoType.Item item : ciType.getItem()) {
                customInfo.put(item.getKey(), item.getValue());
            }
        }
        return customInfo;
    }

    /**
     * Map content.
     *
     * @param dh the data handler
     * @return the string
     */
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

    /**
     * Map message info.
     *
     * @param messageInfoType the message info type
     * @return the message info
     */
    private static MessageInfo mapMessageInfo(MessageInfoType messageInfoType) {
        MessageInfo messageInfo = new MessageInfo();
        if (messageInfoType != null) {
            messageInfo.setFlowId(messageInfoType.getFlowId());
            messageInfo.setMessageId(messageInfoType.getMessageId());
            messageInfo.setOperationName(messageInfoType.getOperationName());
            messageInfo.setPortType(messageInfoType.getPorttype() == null
                ? "" : messageInfoType.getPorttype().toString());
            messageInfo.setTransportType(messageInfoType.getTransport());
        }
        return messageInfo;
    }

    /**
     * Map originator type.
     *
     * @param originatorType the originator type
     * @return the originator
     */
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

    /**
     * Map event type enum.
     *
     * @param eventType the event type
     * @return the event type enum
     */
    private static EventTypeEnum mapEventTypeEnum(EventEnumType eventType) {
        if (eventType != null) {
            return EventTypeEnum.valueOf(eventType.name());
        }
        return EventTypeEnum.UNKNOWN;
    }

}
