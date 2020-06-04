package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.DiscountVoucher;



public interface DiscountVoucherRepository extends CrudRepository<DiscountVoucher, Integer>{

	@Query("select a from DiscountVoucher a where a.owner.id = ?1")
	Collection<DiscountVoucher> listByOwnerId(Integer ownerId);

	@Query("select a from DiscountVoucher a where a.owner.id = ?1 and a.redeemedBeautySolutionVisit IS NULL")
	Collection<DiscountVoucher> listAvailableByOwnerId(Integer ownerId);
}
