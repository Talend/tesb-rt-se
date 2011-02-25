package org.sopera.monitoring.filter;

import org.sopera.monitoring.event.Event;

public interface EventFilter<T extends Event> {

	public boolean filter(T event);
}
