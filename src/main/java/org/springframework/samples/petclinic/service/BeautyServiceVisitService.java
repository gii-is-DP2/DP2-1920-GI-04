package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.BeautyServiceVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class BeautyServiceVisitService {

	private BeautyServiceVisitRepository beautyServiceVisitRepository;
	
	// AUXILIAR SERVICES 
	
	private BeautyServiceService beautyServiceService;
	
	private OwnerService ownerService;
	
	private PromotionService promotionService;
	
	private DiscountVoucherService discountVoucherService;

	@Autowired
	public BeautyServiceVisitService(BeautyServiceVisitRepository beautyServiceVisitRepository, BeautyServiceService beautyServiceService, OwnerService ownerService, PromotionService promotionService, DiscountVoucherService discountVoucherService) {
		this.beautyServiceVisitRepository = beautyServiceVisitRepository;
		this.beautyServiceService = beautyServiceService;
		this.ownerService = ownerService;
		this.promotionService = promotionService;
		this.discountVoucherService = discountVoucherService;
	}
	
	// MAIN METHODS
	
	public BeautyServiceVisit create(Integer beautyServiceId) {
		BeautyServiceVisit beautyServiceVisit = new BeautyServiceVisit();
		BeautyService assigned = this.beautyServiceService.find(beautyServiceId);
		beautyServiceVisit.setBeautyService(assigned);
		beautyServiceVisit.setCancelled(false);
		Double price = assigned.getPrice();
		beautyServiceVisit.setFinalPrice(price);
		return beautyServiceVisit;
	}
	

	@Transactional
	public BeautyServiceVisit save(BeautyServiceVisit beautyServiceVisit) {
		return beautyServiceVisitRepository.save(beautyServiceVisit);
	}
	
	public Collection<BeautyServiceVisit> findVisitsByPrincipal(){
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return this.beautyServiceVisitRepository.findByOwner(owner.getId());
	}
	
	public BeautyServiceVisit find(int beautyServiceVisitId) {
		Assert.isTrue(exists(beautyServiceVisitId), "beautyservicevisit.error.notfound");
		return this.beautyServiceVisitRepository.findById(beautyServiceVisitId).orElse(null);
	}
	
	// ENDPOINT METHODS
	public Collection<BeautyServiceVisit> findActiveVisitsByPrincipal(){
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return this.beautyServiceVisitRepository.findActiveByOwner(owner.getId());
	}
	
	@Transactional
	public BeautyServiceVisit bookBeautyServiceVisit(BeautyServiceVisit visit, DiscountVoucher voucher) {
		//TODO check vet availability
		Assert.isTrue(!visit.getDate().isBefore(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS)), "beautyservicevisit.error.earlybookdate");
		
		Owner principal = this.ownerService.findPrincipal();
		Assert.isTrue(principal.getId() == visit.getPet().getOwner().getId(), "beautyservicevisit.error.notauthorized");
		Assert.isTrue(visit.getBeautyService().getType().equals(visit.getPet().getType()), "beautyservicevisit.error.wrongpettype");
		
		Promotion promotion = this.promotionService.findServiceCurrentPromotion(visit.getBeautyService().getId(), visit.getDate());
		Assert.isTrue(promotion == null || voucher == null, "beautyservicevisit.error.voucheronpromotion");
		
		if(promotion != null) {
			visit.setFinalPrice(visit.getBeautyService().getPrice() * effectivePercentage(promotion.getDiscount()));
		}
		
		if(voucher != null) {
			voucher = this.discountVoucherService.find(voucher.getId());
			Assert.isTrue(voucher != null && voucher.getOwner().getId() == principal.getId(), "discountvoucher.error.notfound");
			Assert.isNull(voucher.getRedeemedBeautyServiceVisit(), "discountvoucher.error.alreadyused");
			visit.setFinalPrice(visit.getBeautyService().getPrice() * effectivePercentage(voucher.getDiscount()));
			visit = save(visit);
			voucher.setRedeemedBeautyServiceVisit(visit);
			this.discountVoucherService.save(voucher, false);
			return visit;
		} else {
			return save(visit);
		}
		
	}
	
	public void cancelVisit(Integer beautyServiceVisitId) {
		Assert.isTrue(exists(beautyServiceVisitId), "beautyservicevisit.error.notfound");
		BeautyServiceVisit visit = this.beautyServiceVisitRepository.findById(beautyServiceVisitId).orElse(null);
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		Assert.isTrue(visit.getPet().getOwner().getId() == owner.getId(), "beautyservicevisit.error.notfound");
		Assert.isTrue(LocalDateTime.now().plus(1, ChronoUnit.DAYS).isBefore(visit.getDate()), "beautyservicevisit.error.latecancel");
		visit.setCancelled(true);
		save(visit);
	}
	
	// AUXILIAR METHODS
	
	public boolean exists(int beautyServiceVisitId) {
		return this.beautyServiceVisitRepository.existsById(beautyServiceVisitId);
	}
	
	public Double effectivePercentage(Integer discount) {
		return (100.0-discount)/100.0;
	}


}
