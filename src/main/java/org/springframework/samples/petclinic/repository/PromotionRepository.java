package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.Promotion;



public interface PromotionRepository extends  CrudRepository<Promotion, String>{
	
	@Query("select a from Promotion a where a.beautyService.id = ?1 and a.startDate < ?2 and a.endDate > ?2")
	public Promotion findServiceCurrentPromotion(Integer beautyServiceId, LocalDateTime bookDate);

	@Query("select a from Promotion a where a.beautyService.id = ?1 and a.endDate > ?2")
	public Collection<Promotion> findAllServicePromotions(Integer beautyServiceId, LocalDateTime now);
	
	@Query("select a from Promotion a where a.beautyService.id = ?1 and ((a.startDate < ?2 and a.endDate > ?2) or (a.startDate < ?3 and a.endDate > ?3) or (a.startDate >= ?2 and a.endDate <= ?3))")
	public Collection<Promotion> findAllServicePromotionsByDate(Integer beautyServiceId, LocalDateTime start, LocalDateTime end);
}
