package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class DiscountVoucherTests {
	
	@Autowired
	protected DiscountVoucherService discountVoucherService;
	
	@Autowired
	protected BeautyServiceVisitService beautyServiceVisitService;
	
	@Autowired
	protected BeautyServiceService beautyServiceService;
	
	@Autowired
	protected PetService petService;
	
	@Autowired
	protected PromotionService promotionService;
	

	@Test
	@DisplayName("Create Discount Voucher")
	void testCreateDiscountVoucher() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
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
	@DisplayName("Redeem Discount Voucher")
	void testUseDiscountVoucher() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		voucher = this.discountVoucherService.save(voucher, false);

		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher);
		
		assertThat(visit.getBeautyService().getPrice()/visit.getFinalPrice()).isEqualTo(100.0/(100.0 - voucher.getDiscount()));
		assertThat(this.discountVoucherService.find(voucher.getId()).getRedeemedBeautyServiceVisit()).isEqualTo(visit);
	}

	@Test
	@DisplayName("Forbid using another owner's Discount Voucher")
	void testUseAnotherOwnersDiscountVoucher() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		DiscountVoucher toUse = this.discountVoucherService.save(voucher, false);

		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner2
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse));
		assertThat(e.getMessage()).isEqualTo("discountvoucher.error.notfound");
	}

	@Test
	@DisplayName("Forbid using an already used Discount Voucher")
	void testUseUsedDiscountVoucher() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		DiscountVoucher toUse = this.discountVoucherService.save(voucher, false);

		// Create First Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse);

		// Change again and save to create a second visit
		visit.setDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse));
		assertThat(e.getMessage()).isEqualTo("discountvoucher.error.notfound");
	}

	@Test
	@DisplayName("Forbid using a Discount Voucher during a promotion period")
	void testUseDiscountVoucherDuringPromotion() {
		// Create discount voucher
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		DiscountVoucher toUse = this.discountVoucherService.save(voucher, false);

		// Create Promotion
		Promotion promo = this.promotionService.create(1);
		promo.setDiscount(23);
		promo.setStartDate(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
		promo.setStartDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));
		this.promotionService.save(promo);

		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.voucheronpromotion");
	}

	
	@Test
	@DisplayName("Create voucher and list logged owners vouchers")
	void testCreateAndListDiscountVouchers() {
		// Create discount vouchers
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		
		DiscountVoucher toUse = this.discountVoucherService.save(voucher, false);
		DiscountVoucher notToUse = this.discountVoucherService.save(voucher, false);
		
		// Use one of them
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse);
		
		// Check if they appear
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listPrincipalAvailableVouchers();
		assertThat(discountVouchers).contains(notToUse);
		assertThat(discountVouchers).doesNotContain(toUse);
	}

	
	@Test
	@DisplayName("Create voucher and list owner's vouchers as administrator")
	void testCreateAndListDiscountVouchersAsAdministrator() {
		// Create discount vouchers
		DiscountVoucher voucher = this.discountVoucherService.create(1);
		LocalDateTime now = LocalDateTime.now();
		voucher.setCreated(now);
		voucher.setDescription("Test voucher");
		voucher.setDiscount(15);
		
		DiscountVoucher toUse = this.discountVoucherService.save(voucher, false);
		DiscountVoucher notToUse = this.discountVoucherService.save(voucher, false);
		
		// Use one of them
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, toUse);
		
		// Check if they appear
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listOwnerDiscountVouchers(1);
		assertThat(discountVouchers).contains(notToUse);
		assertThat(discountVouchers).contains(toUse);
	}
	
}
