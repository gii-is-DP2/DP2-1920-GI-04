package org.springframework.samples.petclinic.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Data
@Entity
@Table(name = "beautyContests", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"year", "month"})
	})
public class BeautyContest extends NamedEntity {
    
	@NotNull
	@Min(2020)
	private Integer year;
    
	@NotNull
	@Range(min = 1, max = 12)
	private Integer month;

	@Valid
	@OneToOne(optional = true)
	private BeautyServiceVisit winner;
}
