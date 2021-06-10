package org.talend.esb.client.app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy=DateNotInPastValidator.class)
@Documented
public @interface DateNotInPast {
	String format() default DateNotInPastValidator.DEFAULT_FORMAT;
	
	String message() default "{org.talend.esb.client.app.validation.DateNotInPast.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
