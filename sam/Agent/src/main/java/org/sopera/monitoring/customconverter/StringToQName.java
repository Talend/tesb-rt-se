package org.sopera.monitoring.customconverter;

import javax.xml.namespace.QName;

import org.dozer.CustomConverter;

public class StringToQName implements CustomConverter {

	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {

		if (sourceFieldValue != null && sourceFieldValue instanceof String) {
			return QName.valueOf((String) sourceFieldValue);
		} else
			return null;
	}
}
