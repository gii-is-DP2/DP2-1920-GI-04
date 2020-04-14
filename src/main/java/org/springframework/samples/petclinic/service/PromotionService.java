package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionService {

	private PromotionRepository promotionRepository;

	@Autowired
	public PromotionService(PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
	
	// AUXILIAR SERVICES 
	
	@Autowired
	private BeautyServiceService beautyServiceService;
	
	// MAIN METHODS

	public Promotion create(Integer beautyServiceId) {
		Promotion res = new Promotion();
		BeautyService beautyService = this.beautyServiceService.find(beautyServiceId);
		res.setBeautyService(beautyService);
		return res;
	}
	
	@Transactional
	public Promotion save(Promotion promotion) {
		return this.promotionRepository.save(promotion);
	}
	
	// AUXILIAR METHODS
	
	public Promotion findServiceCurrentPromotion(Integer beautyServiceId, LocalDateTime bookDate) {
		return this.promotionRepository.findServiceCurrentPromotion(beautyServiceId, bookDate);
	}
	
	public Collection<Promotion> findAllServicePromotions(Integer serviceId){
		return this.promotionRepository.findAllServicePromotions(serviceId, LocalDateTime.now());
	}

}
