package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautyService;



public interface BeautyServiceRepository extends CrudRepository<BeautyService, Integer>{
	
	@Query("select a from BeautyService a where (enabled = true) or (enabled = ?1)")
	Collection<BeautyService> findAllServices(Boolean enabled);
	
	@Query("select a from BeautyService a where ((a.enabled = true) or (a.enabled = ?1)) and a.type.id = ?2")
	Collection<BeautyService> findServicesByPetType(Boolean enabled, Integer petType);
}
