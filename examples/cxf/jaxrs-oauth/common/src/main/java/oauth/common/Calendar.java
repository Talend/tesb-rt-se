/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.common;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Calendar {
    private List<CalendarEntry> entries = new LinkedList<CalendarEntry>();
    
    public Calendar() {
    	for (int i = 0; i < 24; i++) {
    		entries.add(new CalendarEntry(i, ""));
    	}
    }
    
    public List<CalendarEntry> getEntries() {
    	return entries;
    }
    
    public void setEntries(List<CalendarEntry> entries) {
    	this.entries = entries;
    }
    
    public void setEntry(CalendarEntry entry) {
        if (entry == null) {
            return;
        }   
    	validateHour(entry.getHour());
    	entries.set(entry.getHour(), entry);
    }
    
    public CalendarEntry getEntry(int hour) {
    	validateHour(hour);
    	return entries.get(hour);
    }
    
    private static void validateHour(int hour) {
    	if (hour < 0 || hour > 23) {
    		throw new IllegalArgumentException("Wrong hour: " + hour);
    	}
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < 24; i++) {
    		String event = entries.get(i).getEventDescription();
    		sb.append("Hour: ").append(i).append(", event: ")
    		    .append(event == null ? "Not Booked" : event)
    		    .append(System.getProperty("line.separator"));
    	}
    	return sb.toString();
    }
}
