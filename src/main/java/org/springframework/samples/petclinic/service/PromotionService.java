package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class PromotionService {

	private PromotionRepository promotionRepository;
	
	// AUXILIAR SERVICES 
	private BeautySolutionService beautySolutionService;

	@Autowired
	public PromotionService(PromotionRepository promotionRepository, BeautySolutionService beautySolutionService) {
		this.promotionRepository = promotionRepository;
		this.beautySolutionService = beautySolutionService;
	}
	
	// MAIN METHODS

	public Promotion create(Integer beautySolutionId) {
		Promotion res = new Promotion();
		BeautySolution beautySolution = this.beautySolutionService.find(beautySolutionId);
		res.setBeautySolution(beautySolution);
		return res;
	}
	
	@Transactional
	public Promotion save(Promotion promotion) {
		if(promotion.getBeautySolution() != null && (promotion.getId() == null || promotion.getId() == 0)) {
			Collection<Promotion> sameDatePromotions = this.findAllSolutionPromotionsByDate(promotion.getBeautySolution().getId(), promotion.getStartDate(), promotion.getEndDate());
			Assert.isTrue(sameDatePromotions.size() == 0, "promotion.error.overlappeddate");
		}
		Assert.isTrue(!promotion.getEndDate().isBefore(promotion.getStartDate()), "promotion.error.endDateStartDate");
		return this.promotionRepository.save(promotion);
	}
	
	// ENDPOINT METHODS
	/*None*/
	
	// AUXILIAR METHODS
	
	public Promotion findSolutionCurrentPromotion(Integer beautySolutionId, LocalDateTime bookDate) {
		this.checkMonthlyContestPromotion();
		Collection<Promotion> promotions = this.promotionRepository.findSolutionCurrentPromotion(beautySolutionId, bookDate);
		return promotions.stream().filter(x -> x.getDiscount().equals(promotions.stream().mapToInt(y -> y.getDiscount()).max().orElse(0))).findAny().orElse(null);
	}
	
	public Collection<Promotion> findAllSolutionPromotions(Integer solutionId){
		this.checkMonthlyContestPromotion();
		return this.promotionRepository.findAllSolutionPromotions(solutionId, LocalDateTime.now());
	}
	
	public Collection<Promotion> findAllSolutionPromotionsByDate(Integer solutionId, LocalDateTime start, LocalDateTime end){
		return this.promotionRepository.findAllSolutionPromotionsByDate(solutionId, start, end);
	}
	
	public void checkMonthlyContestPromotion() {
		LocalDateTime now = LocalDateTime.now();
		Promotion promotion = this.promotionRepository.findContestCurrentPromotion(now);
		LocalDateTime monthLastWeekStart = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0).plus(1, ChronoUnit.MONTHS).minus(7, ChronoUnit.DAYS);
		LocalDateTime monthLastWeekEnd = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0).plus(1, ChronoUnit.MONTHS);
		if(promotion == null && monthLastWeekStart.isBefore(now) && monthLastWeekEnd.isAfter(now)) {
			promotion = this.createMonthlyContestPromotion(monthLastWeekStart, monthLastWeekEnd);
		}		
	}
	
	public Promotion createMonthlyContestPromotion(LocalDateTime startDate, LocalDateTime endDate) {
		Promotion promotion = new Promotion();
		promotion.setDiscount(10);
		promotion.setStartDate(startDate);
		promotion.setEndDate(endDate);
		return this.save(promotion);
	}

}
