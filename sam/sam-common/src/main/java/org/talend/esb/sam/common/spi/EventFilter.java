package org.talend.esb.sam.common.spi;

import org.talend.esb.sam.common.event.Event;

public interface EventFilter {

	public boolean filter(Event event);
}
