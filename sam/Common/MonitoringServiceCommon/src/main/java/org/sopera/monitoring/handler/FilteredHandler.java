package org.sopera.monitoring.handler;

import java.util.Collection;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.filter.EventFilter;

public interface FilteredHandler<E extends Event> {
	
	void setEventFilter(Collection<EventFilter<E>> eventFilter);
	
	Collection<EventFilter<E>> getEventFilter();
	
	public boolean filter(E event);
}
