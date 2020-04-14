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
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class BeautyServiceVisitTests {
	
	@Autowired
	protected BeautyServiceVisitService beautyServiceVisitService;
	
	@Autowired
	protected BeautyServiceService beautyServiceService;
	
	@Autowired
	protected PetService petService;
	

	@Test
	@DisplayName("Book Beauty Service Visit")
	void testBookBeautyServiceVisit() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		BeautyService service = this.beautyServiceService.find(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(
			visit.getFinalPrice() == service.getPrice()
			&& !visit.getCancelled()
		).isTrue();
		
	}
	
	@Test
	@DisplayName("Forbid booking Beauty Service Visit with a way too early date")
	void testForbidEarlyBookBeautyServiceVisit() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		// Set an early date (today at 23:59:59)
		visit.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.earlybookdate");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Service Visit for a pet that isn't yours")
	void testForbidBookBeautyServiceVisitForAnotherOwnersPet() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		
		// Set a pet from owner2
		Collection<Pet> pets = this.petService.findPetsByOwner(2);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.notauthorized");
	}
	
	@Test
	@DisplayName("Fordib booking a Beauty Service Visit for a pet of a wrong type")
	void testForbidBookBeautyServiceVisitForWrongPetType() {
		// Create beauty service visit (service with petType 2)
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(4);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.wrongpettype");
	}
	

	@Test
	@DisplayName("Cancel Beauty Service booking")
	void testCancelBeautyServiceVisit() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.getCancelled()).isTrue();
		
		// Cancel it
		
		this.beautyServiceVisitService.cancelVisit(visit.getId());
		visit = this.beautyServiceVisitService.find(visit.getId());
		assertThat(visit.getCancelled()).isTrue();
		
	}
	

	@Test
	@DisplayName("Forbid cancelling a Beauty Service booking when it's not more than 24h earlier")
	void testForbidLateCancelBeautyServiceVisit() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());

		// Set date to 23h 59m 59s from now
		visit.setDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS));
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.getCancelled()).isTrue();
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.latecancel");
	}
	

	@Test
	@DisplayName("Forbid cancelling a Beauty Service booking of another owner's pet")
	void testForbidCancelOtherOwnersPetBeautyServiceVisit() {
		// Create beauty service visit
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(1);
		// TODO auth as owner1
		visit.setDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS));
		Collection<Pet> pets = this.petService.findPetsByOwner(1);
		visit.setPet(pets.iterator().next());
		
		visit = this.beautyServiceVisitService.bookBeautyServiceVisit(visit, null);
		assertThat(!visit.getCancelled()).isTrue();
		
		// TODO auth as owner2
		
		// Cancel it

		final Integer visitId = visit.getId();
		Throwable e = assertThrows(Throwable.class, () -> this.beautyServiceVisitService.cancelVisit(visitId));
		assertThat(e.getMessage()).isEqualTo("beautyservicevisit.error.notfound");
	}
	
}
