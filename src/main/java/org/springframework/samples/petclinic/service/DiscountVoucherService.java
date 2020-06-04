package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.BeautySolutionVisitRepository;
import org.springframework.samples.petclinic.repository.DiscountVoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class DiscountVoucherService {

	private DiscountVoucherRepository discountVoucherRepository;
	
	// Auxiliar services
	private OwnerService ownerService;
	
	private BeautySolutionVisitRepository beautySolutionVisitRepository;

	@Autowired
	public DiscountVoucherService(DiscountVoucherRepository discountVoucherRepository, BeautySolutionVisitRepository beautySolutionVisitRepository, OwnerService ownerService) {
		this.discountVoucherRepository = discountVoucherRepository;
		this.beautySolutionVisitRepository = beautySolutionVisitRepository;
		this.ownerService = ownerService;
	}
	
	// MAIN METHODS

	public DiscountVoucher create(Integer ownerId) {
		DiscountVoucher res = new DiscountVoucher();
		Owner owner = this.ownerService.findOwnerById(ownerId);
		res.setCreated(LocalDateTime.now());
		res.setOwner(owner);
		return res;
	}

	@Transactional
	public DiscountVoucher save(DiscountVoucher discountVoucher, Boolean resetHour) {
		if(resetHour) {
			discountVoucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));			
		}
		return discountVoucherRepository.save(discountVoucher);
	}
	
	// ENDPOINT METHODS
	
	public Collection<DiscountVoucher> listPrincipalDiscountVouchers() {
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return listOwnerDiscountVouchers(owner.getId());
		
	}
	
	public Collection<DiscountVoucher> listOwnerDiscountVouchers(Integer ownerId) {
		this.awardPendingVouchers(LocalDateTime.now());
		return this.discountVoucherRepository.listByOwnerId(ownerId);
	}
	
	public DiscountVoucher find(Integer voucherId) {
		this.awardPendingVouchers(LocalDateTime.now());
		return this.discountVoucherRepository.findById(voucherId).orElse(null);
	}

	
	// AUXILIARY METHODS
	
	public void awardPendingVouchers(LocalDateTime now) {
		Collection<BeautySolutionVisit> visits = this.discountVoucherRepository.awardPendingVisitVouchers(now);
		Iterator<BeautySolutionVisit> iterator = visits.iterator();
		while(iterator.hasNext()) {
			BeautySolutionVisit visit = iterator.next();
			if(visit.getFinalPrice() >= 10.0) {
				this.awardPendingVoucher(visit);
			}
		}
	}
	
	@Transactional
	public void awardPendingVoucher(BeautySolutionVisit visit) {
		DiscountVoucher voucher = this.initializeVisitVoucher(this.create(visit.getPet().getOwner().getId()), visit);
		voucher = this.save(voucher, false);
		visit.setAwardedDiscountVoucher(voucher);
		this.beautySolutionVisitRepository.save(visit);	
	}
	
	@Transactional
	public DiscountVoucher awardContestVoucher(BeautyContest contest) {
		DiscountVoucher voucher = this.create(contest.getWinner().getPet().getOwner().getId());
		voucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		voucher.setDescription("Contest winner Voucher (" + contest.getLabel() + ")");
		voucher.setDiscount(50);
		return this.save(voucher, false);		
	}
	
	public DiscountVoucher initializeVisitVoucher(DiscountVoucher voucher, BeautySolutionVisit visit) {
		voucher.setCreated(visit.getDate());
		voucher.setDescription("Visit Voucher");
		voucher.setDiscount(5);
		return voucher;
	}
	
	public Collection<DiscountVoucher> listPrincipalAvailableVouchers() {
		Owner owner = this.ownerService.findPrincipal();
		Assert.isTrue(owner != null, "owner.error.notlogged");
		return listOwnerAvailableVouchers(owner.getId());
	}
	
	public Collection<DiscountVoucher> listOwnerAvailableVouchers(Integer ownerId) {
		this.awardPendingVouchers(LocalDateTime.now());
		return this.discountVoucherRepository.listAvailableByOwnerId(ownerId);
	}

}
