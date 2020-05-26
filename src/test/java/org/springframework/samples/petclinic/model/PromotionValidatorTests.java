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
class PromotionValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void validatePromotion() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().plus(2, ChronoUnit.SECONDS));
		promotion.setDiscount(100);		

		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void validatePromotion2() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().plus(2, ChronoUnit.SECONDS));
		promotion.setDiscount(0);		

		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void notValidateNullPromotion() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();
		
		// Null discount, start date and end date
		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);
		
		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("startDate")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("endDate")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
	}

	@Test
	void notValidateInvalidPromotion() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();
		promotion.setDiscount(-1);
		promotion.setStartDate(LocalDateTime.now().minus(2, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		
		// Negative discount and past end date (start date can be past)
		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("endDate")).findFirst().orElse(null).getMessage()).isEqualTo("must be a future date");
	}

	@Test
	void notValidateInvalidPromotion2() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();
		promotion.setDiscount(101);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().plus(2, ChronoUnit.SECONDS));

		// More than 100% discount
		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be less than or equal to 100");
	}

}
