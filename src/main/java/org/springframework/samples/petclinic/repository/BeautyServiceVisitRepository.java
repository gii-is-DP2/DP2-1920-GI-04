package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;



public interface BeautyServiceVisitRepository extends  CrudRepository<BeautyServiceVisit, Integer>{

	@Query("select a from BeautyServiceVisit a where a.pet.owner.id = ?1")
	Collection<BeautyServiceVisit> findByOwner(Integer ownerId);

	@Query("select a from BeautyServiceVisit a where a.pet.owner.id = ?1 and a.cancelled = false")
	Collection<BeautyServiceVisit> findActiveByOwner(Integer ownerId);
}
