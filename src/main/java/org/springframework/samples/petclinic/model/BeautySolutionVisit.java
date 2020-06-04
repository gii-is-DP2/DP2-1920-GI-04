package org.springframework.samples.petclinic.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "beautySolutionVisits")
public class BeautySolutionVisit extends NamedEntity {

	@Valid
	@ManyToOne(optional = false)
	private BeautySolution beautySolution;

	@Valid
	@ManyToOne(optional = false)
	private Pet pet;

	@Valid
	@OneToOne(optional = true)
	private DiscountVoucher awardedDiscountVoucher;

	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime date;

	@Min(0)
	private double finalPrice;

	private boolean cancelled;

	@URL
	private String participationPhoto;

	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime participationDate;

	
	@Transient
	public String getVisitLabel() {
		return this.getPet().getName() + " - " + this.getBeautySolution().getTitle() + 
				" (" + this.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")";
	}

}
