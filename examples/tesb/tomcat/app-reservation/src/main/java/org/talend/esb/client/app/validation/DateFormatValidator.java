package org.talend.esb.client.app.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateFormatValidator implements
		ConstraintValidator<ValidDateFormat, String> {

	public static final String DEFAULT_FORMAT = "dd.MM.yyyy";

	private String format;

	@Override
	public void initialize(ValidDateFormat constraintAnnotation) {
		format = constraintAnnotation.format();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty()) {
			return true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);

		try {
			sdf.parse(value);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}
