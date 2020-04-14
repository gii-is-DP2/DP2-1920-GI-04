package org.springframework.samples.petclinic.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.Authorities;



public interface AuthoritiesRepository extends CrudRepository<Authorities, String>{

	@Query("select a from Authorities a where username = ?1 ")
	Collection<Authorities> findAuthoritiesByUser(String name);
}
