package org.springframework.samples.petclinic.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;



public interface BeautyContestRepository extends CrudRepository<BeautyContest, Integer>{
	@Query("select a from BeautyContest a order by a.year, a.month desc")
	Collection<BeautyContest> findContests();
	
	@Query("select a from BeautyContest a where (year <= ?1) and (month <= ?2) order by a.year, a.month desc")
	Collection<BeautyContest> findContests(Integer year, Integer month);
	
	@Query("select a from BeautySolutionVisit a where a.participationPhoto is not null and a.date >= ?1 and a.date < ?2")
	Collection<BeautySolutionVisit> listParticipations(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("select a from BeautySolutionVisit a where a.participationPhoto is null and a.date >= ?1 and a.date < ?2")
	Collection<BeautySolutionVisit> listPossibleParticipations(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query("select a from BeautyContest a where year = ?1 and month = ?2")
	BeautyContest findByDate(Integer year, Integer month);
}
