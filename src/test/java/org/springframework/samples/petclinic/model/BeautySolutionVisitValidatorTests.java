package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author Michael Isvy Simple test to make sure that Bean Validation is working (useful
 * when upgrading to a new version of Hibernate Validator/ Bean Validation)
 */
class BeautySolutionVisitValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void validateBeautySolutionVisit() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolutionVisit visit = new BeautySolutionVisit();

		visit.setFinalPrice(0.0);
		visit.setDate(LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
		
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolutionVisit>> constraintViolations = validator.validate(visit);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void notValidateNullBeautySolutionVisit() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolutionVisit visit = new BeautySolutionVisit();

		// Negative price and null date
		
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolutionVisit>> constraintViolations = validator.validate(visit);
		
		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("date")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
	}

	@Test
	void notValidateInvalidBeautySolutionVisit() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolutionVisit visit = new BeautySolutionVisit();
		visit.setFinalPrice(-0.1);
		visit.setDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));

		// Negative price and past date
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolutionVisit>> constraintViolations = validator.validate(visit);

		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("date")).findFirst().orElse(null).getMessage()).isEqualTo("must be a future date");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("finalPrice")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");

	}

}
