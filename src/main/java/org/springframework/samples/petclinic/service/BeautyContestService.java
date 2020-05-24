package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.BeautyContestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


@Service
public class BeautyContestService {

	private BeautyContestRepository beautyContestRepository;
	
	// AUXILIAR SERVICES 
	
	private AuthoritiesService authService;
	
	private OwnerService ownerService;
	
	private BeautyServiceVisitService beautyServiceVisitService;

	@Autowired
	public BeautyContestService(BeautyContestRepository beautyContestRepository, AuthoritiesService authService, OwnerService ownerService, BeautyServiceVisitService beautyServiceVisitService) {
		this.beautyContestRepository = beautyContestRepository;
		this.authService = authService;
		this.ownerService = ownerService;
		this.beautyServiceVisitService = beautyServiceVisitService;
	}
	
	// MAIN METHODS

	public BeautyContest create(LocalDateTime date) {
		Assert.isTrue(!date.isBefore(LocalDateTime.now().minus(30, ChronoUnit.SECONDS)), "beautycontest.error.nopastcontest");
		BeautyContest res = new BeautyContest();
		res.setMonth(date.getMonthValue());
		res.setYear(date.plus(1, ChronoUnit.MONTHS).getYear());
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
			return this.beautyContestRepository.findContests(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
		}
	}
	
	public BeautyContest viewBeautyContest(Integer beautyContestId) {
		BeautyContest beautyContest = find(beautyContestId);
		Boolean isFuture = LocalDateTime.now().getYear() < beautyContest.getYear() || LocalDateTime.now().getMonthValue() < beautyContest.getMonth();
		Assert.isTrue(!isFuture || this.authService.checkAdminAuth(), "beautycontest.error.notfound");
		return beautyContest;
	}
	
	public Collection<BeautyServiceVisit> listParticipations(Integer beautyContestId){
		BeautyContest contest = this.find(beautyContestId);
		
		LocalDateTime startDate = LocalDateTime.of(contest.getYear(), contest.getMonth(), 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(contest.getYear(), contest.getMonth(), 1, 0, 0).plus(1, ChronoUnit.MONTHS);
		
		return this.beautyContestRepository.listParticipations(startDate, endDate);
	}
	
	public Collection<BeautyServiceVisit> listPossibleParticipations(Integer beautyContestId){
		BeautyContest contest = this.find(beautyContestId);
		
		LocalDateTime startDate = LocalDateTime.of(contest.getYear(), contest.getMonth(), 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(contest.getYear(), contest.getMonth(), 1, 0, 0).plus(1, ChronoUnit.MONTHS);
		
		return this.beautyContestRepository.listPossibleParticipations(startDate, endDate);
	}
	
	public void saveParticipation(Integer contestId, Integer visitId, String photo, LocalDateTime date) {
		this.assertCanParticipate(contestId);
		this.assertValidVisit(visitId);
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(visitId);
		visit.setParticipationPhoto(photo);
		visit.setPaticipationDate(date);
		this.beautyServiceVisitService.save(visit);
	}

	
	public void withdrawParticipation(Integer visitId) {
		this.assertValidWithdrawVisit(visitId);
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(visitId);
		visit.setParticipationPhoto(null);
		visit.setPaticipationDate(null);
		this.beautyServiceVisitService.save(visit);
	}

	
	public void selectWinner(Integer visitId) {
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(visitId);
		BeautyContest contest = this.findByDate(visit.getDate().getYear(), visit.getDate().getMonthValue());
		this.assertValidSelectWinner(contest);
		contest.setWinner(visit);
		this.save(contest);
	}
	
	// MAINTENANCE METHODS
	
	public void checkCurrentContest(LocalDateTime date) {
		BeautyContest contest = this.findByDate(date.getYear(), date.getMonthValue());
		if(contest == null) {
			contest = this.create(date);
			this.save(contest);
		}
	}
	

	// AUXILIAR METHODS
	
	public boolean exists(int beautyContestId) {
		return this.beautyContestRepository.existsById(beautyContestId);
	}
	
	public void assertCanParticipate(Integer beautyContestId){
		BeautyContest contest = this.find(beautyContestId);
		Assert.isTrue(LocalDateTime.now().getYear() == contest.getYear() || LocalDateTime.now().getMonthValue() == contest.getMonth(), "beautycontest.error.notfound");
	}
	
	public void assertValidVisit(Integer visitId){
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(visitId);
		Assert.isTrue(visit.getPaticipationDate() == null, "beautycontest.error.notvalidparticipation");
		Assert.isTrue(LocalDateTime.now().getYear() == visit.getDate().getYear() && LocalDateTime.now().getMonthValue() == visit.getDate().getMonthValue(), "beautycontest.error.elapseddate");
		Owner principal = this.ownerService.findPrincipal();
		Assert.isTrue(principal.equals(visit.getPet().getOwner()), "beautycontest.error.notvalidparticipation");
	}
	
	public void assertValidWithdrawVisit(Integer visitId){
		BeautyServiceVisit visit = this.beautyServiceVisitService.find(visitId);
		Assert.isTrue(visit.getPaticipationDate() != null, "beautycontest.error.notvalidparticipation");
		Assert.isTrue(LocalDateTime.now().getYear() == visit.getDate().getYear() && LocalDateTime.now().getMonthValue() == visit.getDate().getMonthValue(), "beautycontest.error.elapseddate");
		Owner principal = this.ownerService.findPrincipal();
		Assert.isTrue(principal.equals(visit.getPet().getOwner()), "beautycontest.error.notvalidparticipation");
	}
	
	public void assertValidSelectWinner(BeautyContest contest) {
		Assert.notNull(contest, "beautycontest.error.notfound");
		Assert.isNull(contest.getWinner(), "beautycontest.error.winnerselected");
		Assert.isTrue(LocalDateTime.now().getYear() > contest.getYear() || LocalDateTime.now().getMonthValue() > contest.getMonth(), "beautycontest.error.notelapsed");
	}
	
	public BeautyContest findByDate(Integer year, Integer month) {
		return this.beautyContestRepository.findByDate(year, month);
	}
	
	public BeautyContest findCurrent(LocalDateTime date) {
		checkCurrentContest(date);
		return this.findByDate(date.getYear(), date.getMonthValue());
	}

}
