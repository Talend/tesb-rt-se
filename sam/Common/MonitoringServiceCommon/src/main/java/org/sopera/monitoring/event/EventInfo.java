package org.sopera.monitoring.event;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Embeddable
public class EventInfo implements Serializable{

	@Transient
	private static final long serialVersionUID = 7599623365844743424L;

	@Basic(optional=false)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EI_TIMESTAMP")
	private Calendar timestamp;
	
	@Basic(optional=false)
	@Enumerated(EnumType.STRING)
	@Column(name="EI_EVENT_TYPE")
	private EventType eventType;
	
	@Embedded
	private Originator originator;
	
	public EventInfo(Calendar timestamp, EventType eventType, Originator originator) {
		super();
		this.timestamp = timestamp;
		this.eventType = eventType;
		this.originator = originator;
	}
	
	public EventInfo() {
		super();
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	public Originator getOriginator() {
		return originator;
	}
	public void setOriginator(Originator originator) {
		this.originator = originator;
	}
}
