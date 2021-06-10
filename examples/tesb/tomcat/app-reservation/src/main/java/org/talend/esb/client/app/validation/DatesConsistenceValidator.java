package org.talend.esb.client.app.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.talend.esb.client.app.model.SearchRequestParameters;

public class DatesConsistenceValidator implements
		ConstraintValidator<DatesConsistent, SearchRequestParameters> {

	public static final String DEFAULT_FORMAT = DateFormatValidator.DEFAULT_FORMAT;

	private String format;

	@Override
	public void initialize(DatesConsistent constraintAnnotation) {
		format = constraintAnnotation.format();
	}

	@Override
	public boolean isValid(SearchRequestParameters value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		
		if (isBlank(value.getPickupDate()) || isBlank(value.getReturnDate())) {
			return true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			Date c = sdf.parse(value.getPickupDate());
			Date d = sdf.parse(value.getReturnDate());

			return !d.before(c);
		} catch (ParseException e) {
			// validation only in case of proper date format
			return true;
		}
	}
	
	private boolean isBlank(String s) {
		return (s == null || s.trim().length() == 0);
	}
}
