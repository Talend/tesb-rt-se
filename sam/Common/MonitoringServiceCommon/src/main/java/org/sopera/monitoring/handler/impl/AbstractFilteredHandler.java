package org.sopera.monitoring.handler.impl;

import java.util.Collection;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.filter.EventFilter;
import org.sopera.monitoring.handler.FilteredHandler;

public abstract class AbstractFilteredHandler<E extends Event> implements
		FilteredHandler<E> {

	private Collection<EventFilter<E>> eventFilter;

	public void setEventFilter(Collection<EventFilter<E>> eventFilter) {
		this.eventFilter = eventFilter;
	}

	public Collection<EventFilter<E>> getEventFilter() {
		return this.eventFilter;
	}

	public boolean filter(E event) {
		if (eventFilter != null) {
			for (EventFilter<E> filter : eventFilter) {
				if (filter.filter(event) == true)
					return true;
			}
		}
		return false;
	}
}
