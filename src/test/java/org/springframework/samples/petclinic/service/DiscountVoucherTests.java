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

	
}
