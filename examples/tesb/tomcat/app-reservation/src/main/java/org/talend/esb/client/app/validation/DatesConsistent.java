package org.talend.esb.client.app.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy=DatesConsistenceValidator.class)
@Documented
public @interface DatesConsistent {
	String format() default DatesConsistenceValidator.DEFAULT_FORMAT;
	
	String message() default "{org.talend.esb.client.app.validation.DatesConsistent.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
