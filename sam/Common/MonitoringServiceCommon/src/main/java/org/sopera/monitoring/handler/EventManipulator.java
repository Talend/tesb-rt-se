package org.sopera.monitoring.handler;

import org.sopera.monitoring.event.Event;

/**
 * EventManipulator interface is used for the basic handler. For example content
 * length cutter oder password filter.
 * 
 * @author cschmuelling
 * 
 */
public interface EventManipulator<E extends Event> {

	void handleEvent(E event);

}
