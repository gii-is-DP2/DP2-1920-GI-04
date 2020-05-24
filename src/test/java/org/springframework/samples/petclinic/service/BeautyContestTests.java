package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.BeautyContestRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautyContestTests {
	
	protected BeautyContestService beautyContestService;

	// Main service mock parameters
	@Autowired
	protected BeautyContestRepository beautyContestRepository;

	@Mock
	protected AuthoritiesService authoritiesService;

	@Mock
	protected OwnerService ownerService;
	
	@Mock
	protected BeautyServiceVisitService beautyServiceVisitService;
	
	// Auxiliar variables
	
	Owner owner1;
	
	// Mock setup
	@BeforeEach
	void setup() {
		
		this.beautyContestService = new BeautyContestService(beautyContestRepository, authoritiesService, ownerService, beautyServiceVisitService);
		
		BeautyServiceVisit visit = new BeautyServiceVisit();
		visit.setId(1);
		when(this.beautyServiceVisitService.find(1)).thenReturn(visit);
		
		owner1 = new Owner();
		owner1.setId(1);
		when(this.ownerService.findOwnerById(1)).thenReturn(owner1);

		when(this.authoritiesService.checkAdminAuth()).thenReturn(false);
	}

	@Test
	@DisplayName("Create Beauty Contest automatically")
	void testCreateContestAutomatically() {

		// Log as admin to see future contests also
		when(this.authoritiesService.checkAdminAuth()).thenReturn(true);
		
		// List now
		Collection<BeautyContest> contestsNow = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		// List of next month, thus creating a new one
		Collection<BeautyContest> contestsOneMonth = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
		// List of next 2 months, thus not creating a new one
		Collection<BeautyContest> contestsTwoMonths = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(2, ChronoUnit.MONTHS));
		

		assertThat(contestsOneMonth.size()).isEqualTo(contestsNow.size() + 1);
		assertThat(contestsTwoMonths.size()).isEqualTo(contestsOneMonth.size() + 1);
		
	}

	@Test
	@DisplayName("Forbid create Beauty Contest automatically on past month")
	void testForbidCreateContestPastMonth() {

		// Log as admin to see future contests also
		when(this.authoritiesService.checkAdminAuth()).thenReturn(true);
		
		// List now		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.showBeautyContestList(LocalDateTime.now().minus(1, ChronoUnit.MONTHS)));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.nopastcontest");
		
	}

	@Test
	@DisplayName("Forbid create Beauty Contest automatically on same month")
	void testForbidCreateContestSameMonth() {

		// Log as admin to see future contests also
		when(this.authoritiesService.checkAdminAuth()).thenReturn(true);
		
		// List now
		Collection<BeautyContest> contestsNow = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		// List of next month, thus creating a new one
		Collection<BeautyContest> contestsOneMonth = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
		// List of next month, thus creating no contest
		Collection<BeautyContest> contestsOneMonthAgain = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));

		assertThat(contestsOneMonth.size()).isEqualTo(contestsNow.size() + 1);
		assertThat(contestsOneMonth.size()).isEqualTo(contestsOneMonthAgain.size());
		
	}

	@Test
	@DisplayName("List Beauty Contests as owner")
	void testListBeautyContestsAsOwner() {
		
		// List now
		Collection<BeautyContest> contestsNow = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		
		assertThat(contestsNow.size()).isGreaterThan(0);
		
	}

	@Test
	@DisplayName("Forbid list future Beauty Contests as owner")
	void testForbidListFutureBeautyContestsAsOwner() {
		

		// List now
		Collection<BeautyContest> contestsNow = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		// List of next month, thus creating a new one
		Collection<BeautyContest> contestsOneMonth = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
		
		assertThat(contestsNow.size()).isEqualTo(contestsOneMonth.size());
		
	}

	@Test
	@DisplayName("Show Beauty Contest as owner")
	void testShowBeautyContestAsOwner() {
		
		// List now
		Collection<BeautyContest> contests = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		BeautyContest contest = this.beautyContestService.viewBeautyContest(contests.iterator().next().getId());
		
		assertThat(contest).isNotNull();
		
	}

	@Test
	@DisplayName("Forbid showing future Beauty Contest as owner")
	void testForbidShowFutureBeautyContestAsOwner() {
		
		// Log as admin to see future contests also
		when(this.authoritiesService.checkAdminAuth()).thenReturn(true);
		
		// List next month
		Collection<BeautyContest> contests = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));

		// Remove admin auth
		when(this.authoritiesService.checkAdminAuth()).thenReturn(false);
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.viewBeautyContest(contests.iterator().next().getId()));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.notfound");
		
	}

	@Test
	@DisplayName("Show future Beauty Contest as admin")
	void testShowFutureBeautyContestAsAdmin() {
		
		// Log as admin to see future contests also
		when(this.authoritiesService.checkAdminAuth()).thenReturn(true);
		
		// List next month
		Collection<BeautyContest> contests = this.beautyContestService.showBeautyContestList(LocalDateTime.now().plus(1, ChronoUnit.MONTHS));
		BeautyContest contest = this.beautyContestService.viewBeautyContest(contests.iterator().next().getId());
		
		assertThat(contest).isNotNull();
		
	}

	@Test
	@DisplayName("Forbid showing non existing Beauty Contest as owner")
	void testForbidShowNonExistingBeautyContest() {
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.viewBeautyContest(99999));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.notfound");
		
	}
	
	
}
