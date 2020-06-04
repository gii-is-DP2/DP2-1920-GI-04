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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.BeautySolutionVisitRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautySolutionVisitServiceTests {
	
	protected BeautySolutionVisitService beautySolutionVisitService;
	
	
	// Auxiliar services
	@Mock
	protected BeautySolutionService beautySolutionService;
	
	@Mock
	protected PetService petService;

	// Main service mock parameters
	@Autowired
	protected BeautySolutionVisitRepository beautySolutionVisitRepository;
	
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
		
		this.beautySolutionVisitService = new BeautySolutionVisitService(beautySolutionVisitRepository, beautySolutionService, ownerService, promotionService, discountVoucherService);

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
		
		// Mock BeautySolution 1
		BeautySolution solution = new BeautySolution();
		solution.setId(1);
		solution.setEnabled(true);
		solution.setPrice(20.0);
		PetType type1 = new PetType();
		type1.setId(1);
		type1.setName("cat");
		solution.setType(type1);
		Vet vet = new Vet();
		vet.setId(1);
		vet.setFirstName("James");
		vet.setLastName("Carter");
		solution.setVet(vet);
		solution.setTitle("Unit test solution");
		when(this.beautySolutionService.find(1)).thenReturn(solution);
		
		// Mock BeautySolution 4
		BeautySolution solution4 = new BeautySolution();
		solution4.setId(1);
		solution4.setEnabled(true);
		solution4.setPrice(20.0);
		PetType type6 = new PetType();
		type6.setId(6);
		type6.setName("hamster");
		solution4.setType(type6);
		solution4.setVet(vet);
		solution4.setTitle("Unit test solution");
		when(this.beautySolutionService.find(4)).thenReturn(solution4);
		
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
		when(this.promotionService.findSolutionCurrentPromotion(any(Integer.class), any(LocalDateTime.class))).thenReturn(null);
		when(this.discountVoucherService.find(any(Integer.class))).thenReturn(null);
		
		
	}
	

	@Test
	@DisplayName("Book Beauty Solution Visit")
	void testBookBeautySolutionVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		BeautySolution solution = this.beautySolutionService.find(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		assertThat(
			visit.getFinalPrice() == solution.getPrice()
			&& !visit.isCancelled()
		).isTrue();
		
	}
	
	@Test
	@DisplayName("Forbid booking Beauty Solution Visit with a way too early date")
	void testForbidEarlyBookBeautySolutionVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		/* Set an early date (today at 23:59:59) */ 
		visit.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.earlybookdate");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Solution Visit for a pet that isn't yours")
	void testForbidBookBeautySolutionVisitForAnotherOwnersPet() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notauthorized");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Solution Visit for a pet of a wrong type")
	void testForbidBookBeautySolutionVisitForWrongPetType() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit (solution with petType 2)
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(4);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.wrongpettype");
	}
	

	@Test
	@DisplayName("Forbid booking visit on similar time 2")
	void testForbidVisitOnSimilarTime() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		Pet pet = pets.iterator().next();
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		visit.setPet(pet);
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		
		// Create second visit 4 minutes later
		BeautySolutionVisit visit2 = this.beautySolutionVisitService.create(1);
		visit2.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(4, ChronoUnit.MINUTES));
		visit2.setPet(pet);
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit2, null));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.collidingvisits");
	}

	@Test
	@DisplayName("Forbid booking visit on similar time 2")
	void testForbidVisitOnSimilarTime2() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		Pet pet = pets.iterator().next();
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		visit.setPet(pet);
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		
		// Create second visit 4 minutes earlier
		BeautySolutionVisit visit2 = this.beautySolutionVisitService.create(1);
		visit2.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS).minus(4, ChronoUnit.MINUTES));
		visit2.setPet(pet);
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit2, null));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.collidingvisits");
	}
	

	@Test
	@DisplayName("Book Beauty Solution Visit on not similar time")
	void testAllowVisitOnNotSimilarTime() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		Pet pet = pets.iterator().next();
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		visit.setPet(pet);
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		
		// Create second visit 5 minutes later
		BeautySolutionVisit visit2 = this.beautySolutionVisitService.create(1);
		visit2.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(5, ChronoUnit.MINUTES).plus(1, ChronoUnit.SECONDS));
		visit2.setPet(pet);
		this.beautySolutionVisitService.bookBeautySolutionVisit(visit2, null);
	}	
	

	@Test
	@DisplayName("Cancel Beauty Solution booking")
	void testCancelBeautySolutionVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();
		
		// Cancel it
		
		this.beautySolutionVisitService.cancelVisit(visit.getId());
		visit = this.beautySolutionVisitService.find(visit.getId());
		assertThat(visit.isCancelled()).isTrue();
		
	}
	

	@Test
	@DisplayName("Forbid cancelling a Beauty Solution booking when it's not more than 24h earlier")
	void testForbidLateCancelBeautySolutionVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		// Set date to 23h 59m 59s from now
		visit.setDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));
		
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.latecancel");
	}

	@Test
	@DisplayName("Forbid cancelling a Beauty Solution booking of another owner's pet")
	void testForbidCancelOtherOwnersPetBeautySolutionVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create beauty solution visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		assertThat(!visit.isCancelled()).isTrue();

		// Log as user 2
		Owner principal2 = new Owner();
		principal2.setId(2);
		when(this.ownerService.findPrincipal()).thenReturn(principal2);
		
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notfound");
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

		// Create Beauty Solution Visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, voucher);
		
		assertThat(visit.getBeautySolution().getPrice()/visit.getFinalPrice()).isCloseTo(100.0/(100.0 - voucher.getDiscount()), Offset.offset(0.01));
		assertThat(voucher.getRedeemedBeautySolutionVisit()).isEqualTo(visit);
	}

	@Test
	@DisplayName("Forbid using another owner's Discount Voucher")
	void testUseAnotherOwnersDiscountVoucher() {
		// Log as owner 2 and mock voucher (user1)
		when(this.ownerService.findPrincipal()).thenReturn(owner2);
		when(this.discountVoucherService.find(1)).thenReturn(testVoucher);
		
		// Select discount voucher
		DiscountVoucher voucher = this.discountVoucherService.find(1);
		
		// Create Beauty Solution Visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(4);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit, voucher));
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

		// Create First Beauty Solution Visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		this.beautySolutionVisitService.bookBeautySolutionVisit(visit, voucher);

		// Change again and save to create a second visit
		BeautySolutionVisit visit2 = this.beautySolutionVisitService.create(1);
		visit2.setPet(pets.iterator().next());
		visit2.setDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit2, voucher));
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

		// Create Beauty Solution Visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		// Mock promotion
		Promotion promo = new Promotion();
		promo.setId(1);
		promo.setBeautySolution(this.beautySolutionService.find(1));
		promo.setDiscount(23);
		promo.setStartDate(LocalDateTime.now().plus(2, ChronoUnit.DAYS));
		promo.setStartDate(LocalDateTime.now().plus(4, ChronoUnit.DAYS));
		when(this.promotionService.findSolutionCurrentPromotion(1, visit.getDate())).thenReturn(promo);

		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.bookBeautySolutionVisit(visit, voucher));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.voucheronpromotion");
	}

	
	// Use visits as beauty contest participations

	@Test
	@DisplayName("Participate on beauty contest")
	void testParticipateOnBeautyContest() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);

		// Participate with that visit
		BeautySolutionVisit participation = this.beautySolutionVisitService.saveParticipation(visit.getId(), "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));

		assertThat(participation.getId()).isEqualTo(visit.getId());
		assertThat(participation.getParticipationPhoto()).isEqualTo("https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg");
		
	}

	@Test
	@DisplayName("Forbid participating on a contest twice with the same visit")
	void testForbidParticipateWithAlreadyUsedVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Participate with that visit
		this.beautySolutionVisitService.saveParticipation(visitId, "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));

		// Participate again
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.saveParticipation(visitId, "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notvalidparticipation");
		
	}

	@Test
	@DisplayName("Forbid participating on a contest before the visit takes place")
	void testForbidParticipateWithNotElapsedVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Participate with that visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.saveParticipation(visitId, "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.earlyparticipation");
		
	}

	@Test
	@DisplayName("Forbid participating on a contest with another owner's visit")
	void testForbidParticipateWithAnotherOwnersVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();
		
		// Log as user 2
		when(this.ownerService.findPrincipal()).thenReturn(owner2);

		// Participate with that visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.saveParticipation(visitId, "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notvalidparticipation");
		
	}

	@Test
	@DisplayName("Forbid participating on a contest when it's not that month")
	void testForbidParticipateOnAnotherMonth() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Participate with that visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.saveParticipation(visitId, "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.MONTHS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.elapseddate");
		
	}
	
	
	@Test
	@DisplayName("Withdraw participation on beauty contest")
	void testWithdrawParticipationOnBeautyContest() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);

		// Participate with that visit
		this.beautySolutionVisitService.saveParticipation(visit.getId(), "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));
		// Withdraw that participation
		BeautySolutionVisit participation = this.beautySolutionVisitService.withdrawParticipation(visit.getId(), LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(2, ChronoUnit.SECONDS));

		assertThat(participation.getId()).isEqualTo(visit.getId());
		assertThat(participation.getParticipationPhoto()).isEqualTo(null);
		assertThat(participation.getParticipationDate() == null).isTrue();
		
	}

	@Test
	@DisplayName("Forbid withdrawing a visit that has not been used in a participation")
	void testForbidWithdrawingNonParticipatedVisit() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Withdraw visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.withdrawParticipation(visitId, LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notvalidparticipation");
		
	}

	@Test
	@DisplayName("Forbid withdrawing another owner's participation")
	void testForbidWithdrawingAnotherOwnersParticipation() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Participate with that visit
		this.beautySolutionVisitService.saveParticipation(visit.getId(), "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));
		
		// Log as user 2
		when(this.ownerService.findPrincipal()).thenReturn(owner2);

		// Participate with that visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.withdrawParticipation(visitId, LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(2, ChronoUnit.SECONDS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.notvalidparticipation");
		
	}

	@Test
	@DisplayName("Forbid withdrawing a participation when it's not that month")
	void testForbidWithdrawingAParticipationMonth() {
		// Log as user 1
		when(this.ownerService.findPrincipal()).thenReturn(owner1);
		
		// Create visit
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(1);
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautySolutionVisitService.bookBeautySolutionVisit(visit, null);
		Integer visitId = visit.getId();

		// Participate with that visit
		this.beautySolutionVisitService.saveParticipation(visit.getId(), "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg", LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.SECONDS));

		// Participate with that visit
		Throwable e = assertThrows(Throwable.class, () -> this.beautySolutionVisitService.withdrawParticipation(visitId, LocalDateTime.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.MONTHS)));
		assertThat(e.getMessage()).isEqualTo("beautysolutionvisit.error.elapseddate");
		
	}
	
}
