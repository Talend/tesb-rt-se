/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CalendarEntry {
	private int hour;
	private String eventDescription;
	
	public CalendarEntry() {
		
	}
	
    public CalendarEntry(int hour, String eventDescription) {
		this.hour = hour;
		this.eventDescription = eventDescription;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getHour() {
		return hour;
	}
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	public String getEventDescription() {
		return eventDescription;
	}

}
