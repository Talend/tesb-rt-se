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
/**
 * The Class Event.
 */
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

    /**
     * Instantiates a new event.
     */
    public Event() {
        super();
    }

    /**
     * Gets the persisted id.
     *
     * @return the persisted id
     */
    public Long getPersistedId() {
        return persistedId;
    }

    /**
     * Sets the persisted id.
     *
     * @param persistedId the new persisted id
     */
    public void setPersistedId(Long persistedId) {
        this.persistedId = persistedId;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public EventTypeEnum getEventType() {
        return eventType;
    }

    /**
     * Sets the event type.
     *
     * @param eventType the new event type
     */
    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the originator.
     *
     * @return the originator
     */
    public Originator getOriginator() {
        return originator;
    }

    /**
     * Sets the originator.
     *
     * @param originator the new originator
     */
    public void setOriginator(Originator originator) {
        this.originator = originator;
    }

    /**
     * Gets the message info.
     *
     * @return the message info
     */
    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    /**
     * Sets the message info.
     *
     * @param messageInfo the new message info
     */
    public void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }

    /**
     * Sets the content cut.
     *
     * @param contentCut the new content cut
     */
    public void setContentCut(boolean contentCut) {
        isContentCut = contentCut;
    }

    /**
     * Checks if is content cut.
     *
     * @return true, if is content cut
     */
    public boolean isContentCut() {
        return isContentCut;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the new content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the custom info.
     *
     * @return the custom info
     */
    public Map<String, String> getCustomInfo() {
        return customInfo;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

}
