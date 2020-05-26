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
class BeautySolutionValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void validateBeautySolution() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolution solution = new BeautySolution();
		solution.setPrice(0.0);
		solution.setTitle("0");

		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolution>> constraintViolations = validator.validate(solution);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void notValidateNullBeautySolution() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolution solution = new BeautySolution();

		// Title and price null
		
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolution>> constraintViolations = validator.validate(solution);
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("title")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("price")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
	}

	@Test
	void notValidateInvalidBeautySolution() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautySolution solution = new BeautySolution();
		solution.setPrice(-0.1);
		solution.setTitle("");
		
		// Title blank and price negative

		Validator validator = createValidator();
		Set<ConstraintViolation<BeautySolution>> constraintViolations = validator.validate(solution);
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("title")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("price")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		
	}

}
