package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.repository.BeautySolutionVisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class BeautySolutionVisitService {

	private BeautySolutionVisitRepository beautySolutionVisitRepository;
	
	// AUXILIAR SERVICES 
	
	private BeautySolutionService beautySolutionService;
	
	private OwnerService ownerService;
	
	private PromotionService promotionService;
	
	private DiscountVoucherService discountVoucherService;

	@Autowired
	public BeautySolutionVisitService(BeautySolutionVisitRepository beautySolutionVisitRepository, BeautySolutionService beautySolutionService, OwnerService ownerService, PromotionService promotionService, DiscountVoucherService discountVoucherService) {
		this.beautySolutionVisitRepository = beautySolutionVisitRepository;
		this.beautySolutionService = beautySolutionService;
		this.ownerService = ownerService;
		this.promotionService = promotionService;
		this.discountVoucherService = discountVoucherService;
	}
	
	// MAIN METHODS
	
	public BeautySolutionVisit create(Integer beautySolutionId) {
		BeautySolutionVisit beautySolutionVisit = new BeautySolutionVisit();
		BeautySolution assigned = this.beautySolutionService.find(beautySolutionId);
		beautySolutionVisit.setBeautySolution(assigned);
		beautySolutionVisit.setCancelled(false);
		Double price = assigned.getPrice();
		beautySolutionVisit.setFinalPrice(price);
		return beautySolutionVisit;
	}
	

	@Transactional
	public BeautySolutionVisit save(BeautySolutionVisit beautySolutionVisit) {
		return beautySolutionVisitRepository.save(beautySolutionVisit);
	}
	
	public Collection<BeautySolutionVisit> findVisitsByPrincipal(){
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return this.beautySolutionVisitRepository.findByOwner(owner.getId());
	}
	
	public BeautySolutionVisit find(int beautySolutionVisitId) {
		Assert.isTrue(exists(beautySolutionVisitId), "beautysolutionvisit.error.notfound");
		return this.beautySolutionVisitRepository.findById(beautySolutionVisitId).orElse(null);
	}
	
	// ENDPOINT METHODS
	public Collection<BeautySolutionVisit> findActiveVisitsByPrincipal(){
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return this.beautySolutionVisitRepository.findActiveByOwner(owner.getId());
	}
	
	@Transactional
	public BeautySolutionVisit bookBeautySolutionVisit(BeautySolutionVisit visit, DiscountVoucher voucher) {
		//TODO check vet availability
		Assert.isTrue(!visit.getDate().isBefore(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS)), "beautysolutionvisit.error.earlybookdate");
		
		Owner principal = this.ownerService.findPrincipal();
		Assert.isTrue(principal.getId() == visit.getPet().getOwner().getId(), "beautysolutionvisit.error.notauthorized");
		Assert.isTrue(visit.getBeautySolution().getType().equals(visit.getPet().getType()), "beautysolutionvisit.error.wrongpettype");
		
		Promotion promotion = this.promotionService.findSolutionCurrentPromotion(visit.getBeautySolution().getId(), visit.getDate());
		Assert.isTrue(promotion == null || voucher == null, "beautysolutionvisit.error.voucheronpromotion");
		
		if(promotion != null) {
			visit.setFinalPrice(visit.getBeautySolution().getPrice() * effectivePercentage(promotion.getDiscount()));
		}
		
		if(voucher != null) {
			voucher = this.discountVoucherService.find(voucher.getId());
			Assert.isTrue(voucher != null && voucher.getOwner().getId() == principal.getId(), "discountvoucher.error.notfound");
			Assert.isNull(voucher.getRedeemedBeautySolutionVisit(), "discountvoucher.error.alreadyused");
			visit.setFinalPrice(visit.getBeautySolution().getPrice() * effectivePercentage(voucher.getDiscount()));
			visit = save(visit);
			voucher.setRedeemedBeautySolutionVisit(visit);
			this.discountVoucherService.save(voucher, false);
			return visit;
		} else {
			return save(visit);
		}
		
	}
	
	public void cancelVisit(Integer beautySolutionVisitId) {
		Assert.isTrue(exists(beautySolutionVisitId), "beautysolutionvisit.error.notfound");
		BeautySolutionVisit visit = this.beautySolutionVisitRepository.findById(beautySolutionVisitId).orElse(null);
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		Assert.isTrue(visit.getPet().getOwner().getId() == owner.getId(), "beautysolutionvisit.error.notfound");
		Assert.isTrue(LocalDateTime.now().plus(1, ChronoUnit.DAYS).isBefore(visit.getDate()), "beautysolutionvisit.error.latecancel");
		visit.setCancelled(true);
		save(visit);
	}
	
	// AUXILIAR METHODS
	
	public boolean exists(int beautySolutionVisitId) {
		return this.beautySolutionVisitRepository.existsById(beautySolutionVisitId);
	}
	
	public Double effectivePercentage(Integer discount) {
		return (100.0-discount)/100.0;
	}


}
