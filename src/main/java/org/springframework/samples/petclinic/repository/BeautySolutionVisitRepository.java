package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;



public interface BeautySolutionVisitRepository extends  CrudRepository<BeautySolutionVisit, Integer>{

	@Query("select a from BeautySolutionVisit a where a.pet.owner.id = ?1")
	Collection<BeautySolutionVisit> findByOwner(Integer ownerId);

	@Query("select a from BeautySolutionVisit a where a.pet.owner.id = ?1 and a.cancelled = false")
	Collection<BeautySolutionVisit> findActiveByOwner(Integer ownerId);

	@Query("select a from BeautySolutionVisit a where a.cancelled = false and a.beautySolution.vet.id = ?3 and a.date >= ?1 and a.date <= ?2")
	Collection<BeautySolutionVisit> findCollidingVisitsByVet(LocalDateTime start, LocalDateTime end, Integer vetId);

	@Query("SELECT a FROM BeautySolutionVisit a WHERE a.date < ?1 AND a.cancelled = false AND a.awardedDiscountVoucher IS NULL AND a.beautySolution.price > 10")
	Collection<BeautySolutionVisit> pendingVisitAwardDiscountVoucher(LocalDateTime now);
}
