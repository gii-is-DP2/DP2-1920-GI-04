package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.repository.BeautyContestRepository;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@AutoConfigureTestDatabase(replace=Replace.NONE)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeautyContestServiceTests {
	
	protected BeautyContestService beautyContestService;

	// Main service mock parameters
	@Autowired
	protected BeautyContestRepository beautyContestRepository;

	@Mock
	protected AuthoritiesService authoritiesService;
	
	@Mock
	protected BeautySolutionVisitService beautySolutionVisitService;
	
	@Mock
	protected DiscountVoucherService discountVoucherService;
	
	// Mock setup
	@BeforeEach
	void setup() {
		
		this.beautyContestService = new BeautyContestService(beautyContestRepository, authoritiesService, beautySolutionVisitService, discountVoucherService);
		
		BeautySolutionVisit visit = new BeautySolutionVisit();
		visit.setId(1);
		visit.setDate(LocalDateTime.of(2014, 1, 1, 22, 0));
		when(this.beautySolutionVisitService.find(1)).thenReturn(visit);

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

	@Test
	@DisplayName("Assert can participate on contest")
	void testAssertCanParticipateOnContest() {
		LocalDateTime now = LocalDateTime.now();
		this.beautyContestService.checkCurrentContest(now);
		BeautyContest contest = this.beautyContestService.findByDate(now.getYear(), now.getMonthValue());

		this.beautyContestService.assertCanParticipate(contest.getId(), now);
	
	}

	@Test
	@DisplayName("Throw assertion can participate on contest")
	void testThrowCanParticipateOnContest() {
		LocalDateTime now = LocalDateTime.now();
		this.beautyContestService.checkCurrentContest(now);
		BeautyContest contest = this.beautyContestService.findByDate(now.getYear(), now.getMonthValue());
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.assertCanParticipate(contest.getId(), now.plus(1, ChronoUnit.MONTHS)));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.notfound");
		
	}

	@Test
	@DisplayName("Select a winner of a contest")
	void testSelectWinner() {
		this.beautyContestService.selectWinner(1, LocalDateTime.now());
		BeautyContest contest = this.beautyContestService.findByDate(2014, 1);
		assertThat(contest.getWinner().getId()).isEqualTo(1);
	}

	@Test
	@DisplayName("Forbid selecting a winner of a non elapsed contest")
	void testForbidSelectWinnerNotElapsedContest() {
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.selectWinner(1, LocalDateTime.of(2014, 1, 5, 0, 0)));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.notelapsed");
	}

	@Test
	@DisplayName("Forbid selecting a winner of a contest for a second time")
	void testForbidSelectWinnerTwice() {
		this.beautyContestService.selectWinner(1, LocalDateTime.now());
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.selectWinner(1, LocalDateTime.now()));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.winnerselected");
	}

	@Test
	@DisplayName("Forbid selecting a winner of non existing contest")
	void testForbidSelectWinnerOfANonExistingContest() {
		// Mock visit to a previous, non prepared, month
		BeautySolutionVisit visit = new BeautySolutionVisit();
		visit.setId(1);
		visit.setDate(LocalDateTime.of(2009, 1, 1, 22, 0));
		when(this.beautySolutionVisitService.find(1)).thenReturn(visit);
		
		Throwable e = assertThrows(Throwable.class, () -> this.beautyContestService.selectWinner(1, LocalDateTime.now()));
		assertThat(e.getMessage()).isEqualTo("beautycontest.error.notfound");
	}

	@Test
	@DisplayName("Check giving voucher on winner selection is called")
	void testCheckWinnerVoucherCall() {
		this.beautyContestService.selectWinner(1, LocalDateTime.now());
	    verify(this.discountVoucherService).awardContestVoucher(any(BeautyContest.class));
	}
	
	
}
