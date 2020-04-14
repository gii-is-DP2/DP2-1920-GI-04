package org.springframework.samples.petclinic.service;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.DiscountVoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class DiscountVoucherService {

	private DiscountVoucherRepository discountVoucherRepository;

	@Autowired
	public DiscountVoucherService(DiscountVoucherRepository discountVoucherRepository) {
		this.discountVoucherRepository = discountVoucherRepository;
	}
	
	@Autowired
	private OwnerService ownerService;
	
	@Autowired
	private BeautyServiceVisitService beautyServiceVisitService;
	
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
			discountVoucher.setCreated(LocalDateTime.now());			
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
		this.awardPendingVouchers();
		return this.discountVoucherRepository.listByOwnerId(ownerId);
	}
	
	public DiscountVoucher find(Integer voucherId) {
		this.awardPendingVouchers();
		return this.discountVoucherRepository.findById(voucherId).orElse(null);
	}

	
	// AUXILIARY METHODS
	
	@Transactional
	public void awardPendingVouchers() {
		Collection<BeautyServiceVisit> visits = this.discountVoucherRepository.awardPendingVisitVouchers(LocalDateTime.now());
		Iterator<BeautyServiceVisit> iterator = visits.iterator();
		while(iterator.hasNext()) {
			BeautyServiceVisit visit = iterator.next();
			DiscountVoucher voucher = this.initializeVisitVoucher(this.create(visit.getPet().getOwner().getId()), visit);
			voucher = this.save(voucher, false);
			visit.setAwardedDiscountVoucher(voucher);
			this.beautyServiceVisitService.save(visit);
		}
		// TODO this.discountVoucherRepository.awardPendingContestVouchers();
	}
	
	public DiscountVoucher initializeVisitVoucher(DiscountVoucher voucher, BeautyServiceVisit visit) {
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
		this.awardPendingVouchers();
		return this.discountVoucherRepository.listAvailableByOwnerId(ownerId);
	}

}
