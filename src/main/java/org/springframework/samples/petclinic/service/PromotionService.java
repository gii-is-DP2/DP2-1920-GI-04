package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class PromotionService {

	private PromotionRepository promotionRepository;
	
	// AUXILIAR SERVICES 
	private BeautyServiceService beautyServiceService;

	@Autowired
	public PromotionService(PromotionRepository promotionRepository, BeautyServiceService beautyServiceService) {
		this.promotionRepository = promotionRepository;
		this.beautyServiceService = beautyServiceService;
	}
	
	// MAIN METHODS

	public Promotion create(Integer beautyServiceId) {
		Promotion res = new Promotion();
		BeautyService beautyService = this.beautyServiceService.find(beautyServiceId);
		res.setBeautyService(beautyService);
		return res;
	}
	
	@Transactional
	public Promotion save(Promotion promotion) {
		if(promotion.getId() == 0) {
			Collection<Promotion> sameDatePromotions = this.findAllServicePromotionsByDate(promotion.getBeautyService().getId(), promotion.getStartDate(), promotion.getEndDate());
			Assert.isTrue(sameDatePromotions.size() == 0, "promotion.error.overlappeddate");
		}
		return this.promotionRepository.save(promotion);
	}
	
	// ENDPOINT METHODS
	
	
	
	// AUXILIAR METHODS
	
	public Promotion findServiceCurrentPromotion(Integer beautyServiceId, LocalDateTime bookDate) {
		return this.promotionRepository.findServiceCurrentPromotion(beautyServiceId, bookDate);
	}
	
	public Collection<Promotion> findAllServicePromotions(Integer serviceId){
		return this.promotionRepository.findAllServicePromotions(serviceId, LocalDateTime.now());
	}
	
	public Collection<Promotion> findAllServicePromotionsByDate(Integer serviceId, LocalDateTime start, LocalDateTime end){
		return this.promotionRepository.findAllServicePromotionsByDate(serviceId, start, end);
	}

}
