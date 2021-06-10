package org.talend.esb.client.app.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateNotInPastValidator implements
		ConstraintValidator<DateNotInPast, String> {

	public static final String DEFAULT_FORMAT = DateFormatValidator.DEFAULT_FORMAT;

	private String format;

	@Override
	public void initialize(DateNotInPast constraintAnnotation) {
		format = constraintAnnotation.format();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty()) {
			return true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			Date c = new Date();
			Date d = sdf.parse(value);

			return (d.after(c) || value.equals(sdf.format(c)));
		} catch (ParseException e) {
			// validation only in case of proper date format
			return true;
		}
	}
}
