package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;



public interface DiscountVoucherRepository extends  CrudRepository<DiscountVoucher, Integer>{

	@Query("select a from DiscountVoucher a where a.owner.id = ?1")
	Collection<DiscountVoucher> listByOwnerId(Integer ownerId);

	@Query("select a from DiscountVoucher a where a.owner.id = ?1 and a.redeemedBeautyServiceVisit IS NULL")
	Collection<DiscountVoucher> listAvailableByOwnerId(Integer ownerId);

	@Query("SELECT a FROM BeautyServiceVisit a WHERE a.date < ?1 AND a.cancelled = false AND a.awardedDiscountVoucher IS NULL AND a.beautyService.price > 10")
	Collection<BeautyServiceVisit> awardPendingVisitVouchers(LocalDateTime now);
}
