package org.sopera.monitoring.customconverter;

import javax.xml.namespace.QName;

import org.dozer.CustomConverter;

public class QNameToString implements CustomConverter {

	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {

		if (sourceFieldValue instanceof QName) {
			return sourceFieldValue.toString();
		} else
			return null;

	}

}
