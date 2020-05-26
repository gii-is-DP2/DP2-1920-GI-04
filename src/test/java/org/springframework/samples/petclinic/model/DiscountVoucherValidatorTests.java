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
class DiscountVoucherValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void validateDiscountVoucher() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();
		voucher.setDiscount(100);
		voucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		voucher.setDescription("1");

		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);
		assertThat(constraintViolations.size()).isEqualTo(0);

	}

	@Test
	void validateDiscountVoucher2() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();
		voucher.setDiscount(0);
		voucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		voucher.setDescription("1");

		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);
		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void notValidateNullDiscountVoucher() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();

		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);

		// Null discount, created and description
		
		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("created")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("description")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
	}

	@Test
	void notValidateInvalidDiscountVoucher() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();
		voucher.setDiscount(-1);
		voucher.setCreated(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		voucher.setDescription("");
		
		// Negative discount, future created and blank description
		
		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);

		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("created")).findFirst().orElse(null).getMessage()).isEqualTo("must be a past date");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("description")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
	}

	@Test
	void notValidateInvalidDiscountVoucher2() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();
		voucher.setDiscount(101);
		voucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		voucher.setDescription("1");

		// More than 100% discount
		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);
		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be less than or equal to 100");
		
	}

}
