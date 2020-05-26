package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautySolution;



public interface BeautySolutionRepository extends CrudRepository<BeautySolution, Integer>{
	
	@Query("select a from BeautySolution a where (enabled = true) or (enabled = ?1)")
	Collection<BeautySolution> findAllSolutions(Boolean enabled);
	
	@Query("select a from BeautySolution a where ((a.enabled = true) or (a.enabled = ?1)) and a.type.id = ?2")
	Collection<BeautySolution> findSolutionsByPetType(Boolean enabled, Integer petType);
}
