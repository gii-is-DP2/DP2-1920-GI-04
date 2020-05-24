package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.BeautyServiceVisitRepository;
import org.springframework.samples.petclinic.repository.DiscountVoucherRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DiscountVoucherTests {
	
	protected DiscountVoucherService discountVoucherService;
	
	// Auxiliar services
	@Mock
	protected BeautyServiceVisitService beautyServiceVisitService;

	// Main service mock parameters
	@Autowired
	protected DiscountVoucherRepository discountVoucherRepository;

	@Mock
	protected OwnerService ownerService;
	
	@Mock
	protected BeautyServiceVisitRepository beautyServiceVisitRepository;
	
	// Auxiliar variables
	
	Owner owner1;
	
	// Mock setup
	@BeforeEach
	void setup() {
		
		this.discountVoucherService = new DiscountVoucherService(discountVoucherRepository, beautyServiceVisitRepository, ownerService);
		
		BeautyServiceVisit visit = new BeautyServiceVisit();
		visit.setId(1);
		when(this.beautyServiceVisitService.find(1)).thenReturn(visit);
		
		owner1 = new Owner();
		owner1.setId(1);
		when(this.ownerService.findOwnerById(1)).thenReturn(owner1);
		
		
	}

	@Test
	@DisplayName("Create Discount Voucher")
	void testCreateDiscountVoucher() {
		
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		voucher = this.discountVoucherService.save(voucher, false);
		
		assertThat(
			voucher.getId() > 0
			&& voucher.getDescription().equals("Test voucher")
			&& voucher.getDiscount().equals(15)
			&& voucher.getCreated().equals(now)
		).isTrue();
		
	}
	
	@Test
	@DisplayName("Create voucher and list logged owners vouchers")
	void testCreateAndListVoucher() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		voucher = this.discountVoucherService.save(voucher, false);

		// Log in as owner1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Check if it appears
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listPrincipalAvailableVouchers();
		assertThat(discountVouchers).contains(voucher);
	}
	
	@Test
	@DisplayName("Create voucher, use it, and list logged owners vouchers")
	void testCreateAndListUsedVouchers() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(1);
		voucher.setRedeemedBeautyServiceVisit(visit);
		voucher = this.discountVoucherService.save(voucher, false);

		// Log in as owner1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Check if they appear
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listPrincipalAvailableVouchers();
		assertThat(discountVouchers).doesNotContain(voucher);
	}

	
	@Test
	@DisplayName("Create voucher and list owner's vouchers as administrator")
	void testCreateAndListVoucherAsAdministrator() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		voucher = this.discountVoucherService.save(voucher, false);
		
		// Check if they appear
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listOwnerDiscountVouchers(1);
		assertThat(discountVouchers).contains(voucher);
	}

	
	@Test
	@DisplayName("Create voucher, use it, and list owner's vouchers as administrator")
	void testCreateAndListUsedVoucherAsAdministrator() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(1);
		voucher.setRedeemedBeautyServiceVisit(visit);
		voucher = this.discountVoucherService.save(voucher, false);
		
		// Check if they appear
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listOwnerDiscountVouchers(1);
		assertThat(discountVouchers).contains(voucher);
	}
	
}
