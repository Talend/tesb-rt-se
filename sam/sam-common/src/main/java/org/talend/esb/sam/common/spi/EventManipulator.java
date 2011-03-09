package org.talend.esb.sam.common.spi;

import org.talend.esb.sam.common.event.Event;

/**
 * EventManipulator interface is used for the basic handler. For example content
 * length cutter or password filter
 * 
 */
public interface EventManipulator {

	void handleEvent(Event event);

}
