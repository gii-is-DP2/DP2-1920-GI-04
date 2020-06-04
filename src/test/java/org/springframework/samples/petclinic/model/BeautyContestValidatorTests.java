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
class BeautyContestValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void validateBeautyContest() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyContest contest = new BeautyContest();
		contest.setYear(2020);
		contest.setMonth(1);
		
		// Too small year and month
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyContest>> constraintViolations = validator.validate(contest);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void validateBeautyContest2() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyContest contest = new BeautyContest();
		contest.setYear(2034);
		contest.setMonth(12);
		
		// Too small year and month
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyContest>> constraintViolations = validator.validate(contest);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}
	@Test
	void notValidateNullBeautyContest() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyContest contest = new BeautyContest();
		
		// Null month and year
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyContest>> constraintViolations = validator.validate(contest);
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("year")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("month")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
	}

	@Test
	void notValidateInvalidBeautyContest() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyContest contest = new BeautyContest();
		contest.setYear(1999);
		contest.setMonth(0);
		
		// Too small year and month
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyContest>> constraintViolations = validator.validate(contest);

		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("year")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 2000");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("month")).findFirst().orElse(null).getMessage()).isEqualTo("must be between 1 and 12");
	}

	@Test
	void notValidateInvalidBeautyContest2() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyContest contest = new BeautyContest();
		contest.setYear(2020);
		contest.setMonth(13);
		
		// Too small year and month
		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyContest>> constraintViolations = validator.validate(contest);

		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("month")).findFirst().orElse(null).getMessage()).isEqualTo("must be between 1 and 12");
	}

}
