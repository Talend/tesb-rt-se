package org.sopera.monitoring.customconverter;

import org.dozer.CustomConverter;
import org.sopera.monitoring._2010._09.base.EventEnumType;
import org.sopera.monitoring.event.EventType;

public class EventTypeToEventEnumType implements CustomConverter {

	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		if (sourceFieldValue != null && sourceFieldValue instanceof EventType) {
			EventType type = (EventType) sourceFieldValue;
			if (EventType.REQ_IN.equals(type)) {
				return EventEnumType.REQ_IN;
			} else if (EventType.REQ_OUT.equals(type)) {
				return EventEnumType.REQ_OUT;
			} else if (EventType.RESP_IN.equals(type)) {
				return EventEnumType.RESP_IN;
			} else if (EventType.RESP_OUT.equals(type)) {
				return EventEnumType.RESP_OUT;
			} else if (EventType.FAULT_IN.equals(type)) {
				return EventEnumType.FAULT_IN;
			} else if (EventType.FAULT_OUT.equals(type)) {
				return EventEnumType.FAULT_OUT;
			}
			return null;
		} else
			return null;
	}

}
