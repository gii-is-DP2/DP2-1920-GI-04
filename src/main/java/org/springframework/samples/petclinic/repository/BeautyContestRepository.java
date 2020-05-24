package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;



public interface BeautyContestRepository extends CrudRepository<BeautyContest, Integer>{
	@Query("select a from BeautyContest a")
	Collection<BeautyContest> findContests();
	
	@Query("select a from BeautyContest a where (year <= ?1) and (month <= ?2)")
	Collection<BeautyContest> findContests(Integer year, Integer month);
	
	@Query("select a from BeautyServiceVisit a where a.participationPhoto is not null and a.date >= ?1 and a.date < ?2")
	Collection<BeautyServiceVisit> listParticipations(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("select a from BeautyServiceVisit a where a.participationPhoto is null and a.date >= ?1 and a.date < ?2")
	Collection<BeautyServiceVisit> listPossibleParticipations(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("select a from BeautyContest a where year = ?1 and month = ?2")
	BeautyContest findByDate(Integer year, Integer month);
}
