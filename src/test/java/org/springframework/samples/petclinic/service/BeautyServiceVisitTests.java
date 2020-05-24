package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

import org.assertj.core.data.Offset;
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
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.BeautyServiceVisitRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautyServiceVisitTests {
	
	protected BeautyServiceVisitService beautyServiceVisitService;
	
	
	// Auxiliar services
	@Mock
	protected BeautyServiceService beautyServiceService;
	
	@Mock
	protected PetService petService;

	// Main service mock parameters
	@Autowired
	protected BeautyServiceVisitRepository beautyServiceVisitRepository;
	
	@Mock
	protected OwnerService ownerService;
	
	@Mock
	protected PromotionService promotionService;
	
	@Mock
	protected DiscountVoucherService discountVoucherService;
	
	// Auxiliar variables
	
	Owner owner1, owner2;
	DiscountVoucher testVoucher;
	
	// Mock setup
	@BeforeEach
	void setup() {
		
		this.beautyServiceVisitService = new BeautyServiceVisitService(beautyServiceVisitRepository, beautyServiceService, ownerService, promotionService, discountVoucherService);

		owner1 = new Owner();
		owner1.setId(1);
		owner2 = new Owner();
		owner2.setId(2);
		testVoucher = new DiscountVoucher();
		testVoucher.setId(1);
		testVoucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		testVoucher.setDescription("Test voucher");
		testVoucher.setOwner(owner1);
		testVoucher.setDiscount(32);
		
		// Mock BeautyService 1
		BeautyService service = new BeautyService();
		service.setId(1);
		service.setEnabled(true);
		service.setPrice(20.0);
		PetType type1 = new PetType();
		type1.setId(1);
		type1.setName("cat");
		service.setType(type1);
		Vet vet = new Vet();
		vet.setId(1);
		vet.setFirstName("James");
		vet.setLastName("Carter");
		service.setVet(vet);
		service.setTitle("Unit test service");
		when(this.beautyServiceService.find(1)).thenReturn(service);
		
		// Mock BeautyService 4
		BeautyService service4 = new BeautyService();
		service4.setId(1);
		service4.setEnabled(true);
		service4.setPrice(20.0);
		PetType type6 = new PetType();
		type6.setId(6);
		type6.setName("hamster");
		service4.setType(type6);
		service4.setVet(vet);
		service4.setTitle("Unit test service");
		when(this.beautyServiceService.find(4)).thenReturn(service4);
		
		// Mock owner 1 pet list
		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setBirthDate(LocalDate.now());
		pet1.setName("Test pet 1");
		pet1.setType(type1);
		pet1.setOwner(owner1);
		Collection<Pet> owner1Pets = new ArrayList<Pet>();
		owner1Pets.add(pet1);
		when(this.petService.findPetsByOwner(1)).thenReturn(owner1Pets);
		
		// Mock owner 2 pet list
		Pet pet2 = new Pet();
		pet2.setId(1);
		pet2.setBirthDate(LocalDate.now());
		pet2.setName("Test pet 2");
		pet2.setType(type6);
		pet2.setOwner(owner2);
		Collection<Pet> owner2Pets = new ArrayList<Pet>();
		owner2Pets.add(pet2);
		when(this.petService.findPetsByOwner(2)).thenReturn(owner2Pets);
		
		// Other mocks
		when(this.promotionService.findServiceCurrentPromotion(any(Integer.class), any(LocalDateTime.class))).thenReturn(null);
		when(this.discountVoucherService.find(any(Integer.class))).thenReturn(null);
		
		
	}
	

	@Test
	@DisplayName("Book Beauty Service Visit")
	void testBookBeautyServiceVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		BeautyService service = this.beautyServiceService.find(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(
			visit.getFinalPrice() == service.getPrice()
			&& !visit.isCancelled()
		).isTrue();
		
	}
	
	@Test
	@DisplayName("Forbid booking Beauty Service Visit with a way too early date")
	void testForbidEarlyBookBeautyServiceVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		/* Set an early date (today at 23:59:59) */ 
		visit.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.earlybookdate");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Service Visit for a pet that isn't yours")
	void testForbidBookBeautyServiceVisitForAnotherOwnersPet() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.notauthorized");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Service Visit for a pet of a wrong type")
	void testForbidBookBeautyServiceVisitForWrongPetType() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit (service with petType 2)
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(4);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.wrongpettype");
	}
	

	@Test
	@DisplayName("Cancel Beauty Service booking")
	void testCancelBeautyServiceVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();
		
		// Cancel it
		
		this.beautyServiceVisitService.cancelVisit(visit.getId());
		visit = this.beautyServiceVisitService.find(visit.getId());
		assertThat(visit.isCancelled()).isTrue();
		
	}
	

	@Test
	@DisplayName("Forbid cancelling a Beauty Service booking when it's not more than 24h earlier")
	void testForbidLateCancelBeautyServiceVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		// Set date to 23h 59m 59s from now
		visit.setDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.latecancel");
	}
	

	@Test
	@DisplayName("Forbid cancelling a Beauty Service booking of another owner's pet")
	void testForbidCancelOtherOwnersPetBeautyServiceVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();

		// Log as user 2
		Owner principal2 = new Owner();
		principal2.setId(2);
		when(this.ownerService.findPrincipal()).thenReturn(principal2);
		
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.notfound");
	}
	
	
	// Booking visits with vouchers/promotions

	@Test
	@DisplayName("Redeem Discount Voucher")
	void testUseDiscountVoucher() {
		// Log as owner 1 and mock voucher
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		when(this.discountVoucherService.find(1)).thenReturn(testVoucher);
		
		// Select voucher
		DiscountVoucher voucher = this.discountVoucherService.find(1);

		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher);
		
		assertThat(visit.getBeautyService().getPrice()/visit.getFinalPrice()).isCloseTo(100.0/(100.0 - voucher.getDiscount()), Offset.offset(0.01));
		assertThat(voucher.getRedeemedBeautyServiceVisit()).isEqualTo(visit);
	}

	@Test
	@DisplayName("Forbid using another owner's Discount Voucher")
	void testUseAnotherOwnersDiscountVoucher() {
		// Log as owner 2 and mock voucher (user1)
		when(this.ownerService.findPrincipal()).thenReturn(owner2);
		when(this.discountVoucherService.find(1)).thenReturn(testVoucher);
		
		// Select discount voucher
		DiscountVoucher voucher = this.discountVoucherService.find(1);
		
		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(4);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher));
		assertThat(e.getMessage()).isEqualTo("discountvoucher.error.notfound");
	}

	@Test
	@DisplayName("Forbid using an already used Discount Voucher")
	void testUseUsedDiscountVoucher() {
		// Log as owner 1 and mock voucher
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		when(this.discountVoucherService.find(1)).thenReturn(testVoucher);
		
		// Select discount voucher
		DiscountVoucher voucher = this.discountVoucherService.find(1);

		// Create First Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher);

		// Change again and save to create a second visit
		visit.setDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher));
		assertThat(e.getMessage()).isEqualTo("discountvoucher.error.alreadyused");
	}

	@Test
	@DisplayName("Forbid using a Discount Voucher during a promotion period")
	void testUseDiscountVoucherDuringPromotion() {
		// Log as owner 1 and mock voucher
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		when(this.discountVoucherService.find(1)).thenReturn(testVoucher);
		
		// Select discount voucher
		DiscountVoucher voucher = this.discountVoucherService.find(1);

		// Create Beauty Service Visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		// Mock promotion
		Promotion promo = new Promotion();
		promo.setId(1);
		promo.setBeautyService(this.beautyServiceService.find(1));
		promo.setDiscount(23);
		promo.setStartDate(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
		promo.setStartDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));
		when(this.promotionService.findServiceCurrentPromotion(1, visit.getDate())).thenReturn(promo);

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, voucher));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.voucheronpromotion");
	}
	
}
