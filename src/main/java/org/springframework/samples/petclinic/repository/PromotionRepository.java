package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.Promotion;



public interface PromotionRepository extends  CrudRepository<Promotion, String>{
	
	@Query("select a from Promotion a where (a.beautySolution.id = ?1 or a.beautySolution.id is null) and a.startDate < ?2 and a.endDate > ?2")
	public Collection<Promotion> findSolutionCurrentPromotion(Integer beautySolutionId, LocalDateTime bookDate);
	
	@Query("select a from Promotion a where a.beautySolution.id is null and a.startDate < ?1 and a.endDate > ?1")
	public Promotion findContestCurrentPromotion(LocalDateTime bookDate);

	@Query("select a from Promotion a where (a.beautySolution.id = ?1 or a.beautySolution.id is null) and a.endDate > ?2")
	public Collection<Promotion> findAllSolutionPromotions(Integer beautySolutionId, LocalDateTime now);
	
	@Query("select a from Promotion a where a.beautySolution.id = ?1 and ((a.startDate < ?2 and a.endDate > ?2) or (a.startDate < ?3 and a.endDate > ?3) or (a.startDate >= ?2 and a.endDate <= ?3))")
	public Collection<Promotion> findAllSolutionPromotionsByDate(Integer beautySolutionId, LocalDateTime start, LocalDateTime end);
}
