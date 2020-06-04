package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.repository.BeautyContestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
public class BeautyContestService {

	private BeautyContestRepository beautyContestRepository;
	
	// AUXILIAR SERVICES 
	
	private AuthoritiesService authService;
	
	private BeautySolutionVisitService beautySolutionVisitService;
	
	private DiscountVoucherService discountVoucherService;

	@Autowired
	public BeautyContestService(BeautyContestRepository beautyContestRepository, AuthoritiesService authService, BeautySolutionVisitService beautyServiceVisitService, DiscountVoucherService discountVoucherService) {
		this.beautyContestRepository = beautyContestRepository;
		this.authService = authService;
		this.beautySolutionVisitService = beautyServiceVisitService;
		this.discountVoucherService = discountVoucherService;
	}
	
	// MAIN METHODS

	public BeautyContest create(LocalDateTime date) {
		LocalDateTime now = roundToMonth(LocalDateTime.now());
		Assert.isTrue(!date.isBefore(now), "beautycontest.error.nopastcontest");
		BeautyContest res = new BeautyContest();
		res.setDate(roundToMonth(date));
		return res;
	}
	
	@Transactional
	public BeautyContest save(BeautyContest beautyContest) {
		return this.beautyContestRepository.save(beautyContest);
	}

	public BeautyContest find(int beautyContestId) {
		Assert.isTrue(exists(beautyContestId), "beautycontest.error.notfound");
		return this.beautyContestRepository.findById(beautyContestId).orElse(null);
	}
	
	// ENDPOINT METHODS
	
	public Collection<BeautyContest> showBeautyContestList(LocalDateTime date){
		this.checkCurrentContest(date);
		Boolean showFuture = this.authService.checkAdminAuth();
		if(showFuture) {
			return this.beautyContestRepository.findContests();
		} else {
			return this.beautyContestRepository.findContests(roundToMonth(LocalDateTime.now()));
		}
	}
	
	public BeautyContest viewBeautyContest(Integer beautyContestId) {
		BeautyContest beautyContest = find(beautyContestId);
		Boolean isFuture = LocalDateTime.now().isBefore(beautyContest.getDate()) && !sameMonth(LocalDateTime.now(), beautyContest.getDate());
		Assert.isTrue(!isFuture || this.authService.checkAdminAuth(), "beautycontest.error.notfound");
		return beautyContest;
	}
	
	public Collection<BeautySolutionVisit> listParticipations(Integer beautyContestId){
		BeautyContest contest = this.find(beautyContestId);
		
		LocalDateTime startDate = contest.getDate();
		LocalDateTime endDate = contest.getDate().plus(1, ChronoUnit.MONTHS);
		
		return this.beautyContestRepository.listParticipations(startDate, endDate);
	}
	
	public Collection<BeautySolutionVisit> listPossibleParticipations(Integer beautyContestId){
		BeautyContest contest = this.find(beautyContestId);
		
		LocalDateTime startDate = contest.getDate();
		LocalDateTime endDate = contest.getDate().plus(1, ChronoUnit.MONTHS);
		
		return this.beautyContestRepository.listPossibleParticipations(startDate, endDate);
	}
	
	public void selectWinner(Integer visitId, LocalDateTime now) {
		BeautySolutionVisit visit = this.beautySolutionVisitService.find(visitId);
		BeautyContest contest = this.findByDate(roundToMonth(visit.getDate()));
		this.assertValidSelectWinner(contest, now);
		contest.setWinner(visit);
		contest = this.save(contest);
		this.discountVoucherService.awardContestVoucher(contest);
	}
	
	// MAINTENANCE METHODS
	
	public void checkCurrentContest(LocalDateTime date) {
		date = roundToMonth(date);
		BeautyContest contest = this.findByDate(date);
		if(contest == null) {
			contest = this.create(date);
			this.save(contest);
		}
	}
	

	// AUXILIAR METHODS
	
	public boolean exists(int beautyContestId) {
		return this.beautyContestRepository.existsById(beautyContestId);
	}
	
	public void assertCanParticipate(Integer beautyContestId, LocalDateTime now){
		BeautyContest contest = this.find(beautyContestId);
		Assert.isTrue(sameMonth(now, contest.getDate()), "beautycontest.error.notfound");
	}
	
	public void assertValidSelectWinner(BeautyContest contest, LocalDateTime now) {
		Assert.notNull(contest, "beautycontest.error.notfound");
		Assert.isNull(contest.getWinner(), "beautycontest.error.winnerselected");
		Assert.isTrue(now.isAfter(contest.getDate()) && !sameMonth(now, contest.getDate()), "beautycontest.error.notelapsed");
	}
	
	public BeautyContest findByDate(LocalDateTime date) {
		date = roundToMonth(date);
		return this.beautyContestRepository.findByDate(date);
	}
	
	public BeautyContest findCurrent(LocalDateTime date) {
		checkCurrentContest(date);
		return this.findByDate(date);
	}
	
	private boolean sameMonth(LocalDateTime date1, LocalDateTime date2) {
		return date1.getYear() == date2.getYear() && 
				date1.getMonthValue() == date2.getMonthValue();
	}
	
	private LocalDateTime roundToMonth(LocalDateTime date) {
		return date.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

}
