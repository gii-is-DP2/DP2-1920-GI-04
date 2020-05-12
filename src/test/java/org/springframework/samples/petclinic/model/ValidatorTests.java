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
class ValidatorTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void shouldNotValidateWhenFirstNameEmpty() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Person person = new Person();
		person.setFirstName("");
		person.setLastName("smith");

		Validator validator = createValidator();
		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);

		assertThat(constraintViolations.size()).isEqualTo(1);
		ConstraintViolation<Person> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void validateBeautyService() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyService service = new BeautyService();

		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyService>> constraintViolations = validator.validate(service);
		
		// Title and price null
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("title")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("price")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		
		// Title blank and price negative
		
		service.setPrice(-0.1);
		service.setTitle("");
		validator = createValidator();
		constraintViolations = validator.validate(service);
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("title")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("price")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		
		// Valid
		
		service.setPrice(0.0);
		service.setTitle("0");
		validator = createValidator();
		constraintViolations = validator.validate(service);

		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void validateBeautyServiceVisit() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		BeautyServiceVisit visit = new BeautyServiceVisit();
		visit.setFinalPrice(-0.1);

		Validator validator = createValidator();
		Set<ConstraintViolation<BeautyServiceVisit>> constraintViolations = validator.validate(visit);

		// Negative price and null date
		
		assertThat(constraintViolations.size()).isEqualTo(2);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("date")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("finalPrice")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");

		// Past date
		
		visit.setFinalPrice(0.0);
		visit.setDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		constraintViolations = validator.validate(visit);

		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("date")).findFirst().orElse(null).getMessage()).isEqualTo("must be a future date");
		
		// Valid
		
		visit.setDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		constraintViolations = validator.validate(visit);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void validateDiscountVoucher() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		DiscountVoucher voucher = new DiscountVoucher();

		Validator validator = createValidator();
		Set<ConstraintViolation<DiscountVoucher>> constraintViolations = validator.validate(voucher);

		// Null discount, created and description
		
		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("created")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("description")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");

		// Negative discount, future created and blank description
		voucher.setDiscount(-1);
		voucher.setCreated(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		voucher.setDescription("");
		constraintViolations = validator.validate(voucher);

		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("created")).findFirst().orElse(null).getMessage()).isEqualTo("must be a past date");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("description")).findFirst().orElse(null).getMessage()).isEqualTo("must not be blank");
		
		// More than 100% discount
		voucher.setDiscount(101);
		voucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		voucher.setDescription("1");
		constraintViolations = validator.validate(voucher);

		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be less than or equal to 100");

		// Valid 1
		voucher.setDiscount(100);
		constraintViolations = validator.validate(voucher);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
		
		// Valid 2
		voucher.setDiscount(0);
		constraintViolations = validator.validate(voucher);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
	}

	@Test
	void validatePromotion() {

		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Promotion promotion = new Promotion();

		Validator validator = createValidator();
		Set<ConstraintViolation<Promotion>> constraintViolations = validator.validate(promotion);

		// Null discount, start date and end date
		
		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("startDate")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("endDate")).findFirst().orElse(null).getMessage()).isEqualTo("must not be null");

		// Negative discount, past start date and past end date
		promotion.setDiscount(-1);
		promotion.setStartDate(LocalDateTime.now().minus(2, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		
		constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(3);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be greater than or equal to 0");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("startDate")).findFirst().orElse(null).getMessage()).isEqualTo("must be a future date");
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("endDate")).findFirst().orElse(null).getMessage()).isEqualTo("must be a future date");

		// More than 100% discount
		promotion.setDiscount(101);
		promotion.setStartDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
		promotion.setEndDate(LocalDateTime.now().plus(2, ChronoUnit.SECONDS));
		constraintViolations = validator.validate(promotion);

		assertThat(constraintViolations.size()).isEqualTo(1);
		assertThat(constraintViolations.stream().filter(x -> x.getPropertyPath().toString().equals("discount")).findFirst().orElse(null).getMessage()).isEqualTo("must be less than or equal to 100");
		

		// Valid 1
		promotion.setDiscount(100);
		constraintViolations = validator.validate(promotion);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
		
		// Valid 2
		promotion.setDiscount(0);
		constraintViolations = validator.validate(promotion);
		
		assertThat(constraintViolations.size()).isEqualTo(0);
	}

}
