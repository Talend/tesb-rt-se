package org.sopera.monitoring.handler;

import org.sopera.monitoring.event.Event;

public interface CustomHandlerPreProcessing<E extends Event> extends EventManipulator<E>{

}
