package org.sopera.monitoring.event;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Table(name = "EVENTS")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Event implements Serializable {

	@Transient
	private static final long serialVersionUID = 1697021887985284206L;

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "EVENT_SEQ")
	@TableGenerator(name = "EVENT_SEQ", table = "SEQUENCE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "EVENT_SEQ", allocationSize = 1000)
	@Column(name = "ID")
	private Long persistedId;

	@Embedded
	private EventInfo eventInfo;

	@Embedded
	private MessageInfo messageInfo;

	@Lob
	@Column(name = "MESSAGE_CONTENT")
	private String content;

	@Lob
	@Column(name = "EVENT_EXTENSION")
	private String extension;

	public Event() {
		super();
	}

	public Long getPersistedId() {
		return persistedId;
	}

	public void setPersistedId(Long persistedId) {
		this.persistedId = persistedId;
	}

	public EventInfo getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}

	public MessageInfo getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
}
