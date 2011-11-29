/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.event;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

//@Table(name = "EVENTS")
//@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Event implements Serializable {
    // TODO Filename, line number for logging events

    //@Transient
    private static final long serialVersionUID = 1697021887985284206L;

    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "EVENT_SEQ")
    //@TableGenerator(name = "EVENT_SEQ", table = "SEQUENCE", pkColumnName = "SEQ_NAME",
    //    valueColumnName = "SEQ_COUNT", pkColumnValue = "EVENT_SEQ", allocationSize = 1000)
    //@Column(name = "ID")
    private Long persistedId;

    //@Basic(optional = false)
    //@Temporal(TemporalType.TIMESTAMP)
    //@Column(name = "EI_TIMESTAMP")
    private Date timestamp;

    //@Basic(optional = false)
    //@Enumerated(EnumType.STRING)
    //@Column(name = "EI_EVENT_TYPE")
    private EventTypeEnum eventType;

    //@Embedded
    private Originator originator;

    //@Embedded
    private MessageInfo messageInfo;
    
    private boolean isContentCut;

    //@Lob
    //@Column(name = "MESSAGE_CONTENT")
    private String content;

    private Map<String, String> customInfo = new HashMap<String, String>();

    public Event() {
        super();
    }

    public Long getPersistedId() {
        return persistedId;
    }

    public void setPersistedId(Long persistedId) {
        this.persistedId = persistedId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public EventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public Originator getOriginator() {
        return originator;
    }

    public void setOriginator(Originator originator) {
        this.originator = originator;
    }

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }

    public void setContentCut(boolean contentCut) {
        isContentCut = contentCut;
    }

    public boolean isContentCut() {
        return isContentCut;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getCustomInfo() {
        return customInfo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

}
