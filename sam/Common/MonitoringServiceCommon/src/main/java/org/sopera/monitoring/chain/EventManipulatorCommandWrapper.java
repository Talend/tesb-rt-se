package org.sopera.monitoring.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.EventManipulator;

/**
 * Wrapper for hiding Cammand interface and dependency to commons-chain 
 * @author cschmuelling
 *
 */
public class EventManipulatorCommandWrapper<E extends Event> implements Command{

	private EventManipulator<E> eventManipulator;
	private E event;
	
	public EventManipulatorCommandWrapper(EventManipulator<E> eventManipulator, E event) {
		this.eventManipulator = eventManipulator;
		this.event = event;
	}

	public boolean execute(Context context) throws Exception {
		if (eventManipulator.filter(event) == false)
			eventManipulator.handleEvent(event);
		return false;
	}

	

}
