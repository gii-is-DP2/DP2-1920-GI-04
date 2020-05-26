package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;



public interface BeautySolutionVisitRepository extends  CrudRepository<BeautySolutionVisit, Integer>{

	@Query("select a from BeautySolutionVisit a where a.pet.owner.id = ?1")
	Collection<BeautySolutionVisit> findByOwner(Integer ownerId);

	@Query("select a from BeautySolutionVisit a where a.pet.owner.id = ?1 and a.cancelled = false")
	Collection<BeautySolutionVisit> findActiveByOwner(Integer ownerId);
}
