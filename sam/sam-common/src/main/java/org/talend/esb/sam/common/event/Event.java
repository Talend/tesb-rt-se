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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Table(name = "EVENTS")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Event implements Serializable {
    // TODO Filename, line number for logging events

    @Transient
    private static final long serialVersionUID = 1697021887985284206L;

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "EVENT_SEQ")
    @TableGenerator(name = "EVENT_SEQ", table = "SEQUENCE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "EVENT_SEQ", allocationSize = 1000)
    @Column(name = "ID")
    private Long persistedId;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EI_TIMESTAMP")
    private Date timestamp;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "EI_EVENT_TYPE")
    private EventTypeEnum eventType;

    @Embedded
    private Originator originator;

    @Embedded
    private MessageInfo messageInfo;
    
    private boolean isContentCut;

    @Lob
    @Column(name = "MESSAGE_CONTENT")
    private String content;

    private List<CustomInfo> customInfoList;

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
    
    public void setContentCut(boolean isContentCut) {
        this.isContentCut = isContentCut;
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

    public List<CustomInfo> getCustomInfoList() {
        if (customInfoList == null) {
            customInfoList = new ArrayList<CustomInfo>();
        }
        return customInfoList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((customInfoList == null) ? 0 : customInfoList.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + (isContentCut ? 1231 : 1237);
        result = prime * result + ((messageInfo == null) ? 0 : messageInfo.hashCode());
        result = prime * result + ((originator == null) ? 0 : originator.hashCode());
        result = prime * result + ((persistedId == null) ? 0 : persistedId.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event)obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (customInfoList == null) {
            if (other.customInfoList != null)
                return false;
        } else if (!customInfoList.equals(other.customInfoList))
            return false;
        if (eventType != other.eventType)
            return false;
        if (isContentCut != other.isContentCut)
            return false;
        if (messageInfo == null) {
            if (other.messageInfo != null)
                return false;
        } else if (!messageInfo.equals(other.messageInfo))
            return false;
        if (originator == null) {
            if (other.originator != null)
                return false;
        } else if (!originator.equals(other.originator))
            return false;
        if (persistedId == null) {
            if (other.persistedId != null)
                return false;
        } else if (!persistedId.equals(other.persistedId))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

}
