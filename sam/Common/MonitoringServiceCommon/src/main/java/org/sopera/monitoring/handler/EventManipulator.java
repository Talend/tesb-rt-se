package org.sopera.monitoring.handler;

import org.sopera.monitoring.event.Event;

/**
 * EventManipulator interface is used for the basic handler. For example content
 * length cutter or password filter.
 * 
 * @author cschmuelling
 * 
 */
public interface EventManipulator<E extends Event> extends FilteredHandler<E> {

	void handleEvent(E event);

}
