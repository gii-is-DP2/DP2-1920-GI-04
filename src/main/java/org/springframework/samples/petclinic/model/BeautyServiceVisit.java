package org.springframework.samples.petclinic.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "beautyServiceVisits")
public class BeautyServiceVisit extends NamedEntity {

	@Valid
	@ManyToOne(optional = false)
	private BeautyService beautyService;

	@Valid
	@ManyToOne(optional = false)
	private Pet pet;

	@Valid
	@OneToOne(optional = true)
	private DiscountVoucher awardedDiscountVoucher;

	@NotNull
	@Future
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime date;

	@Min(0)
	private double finalPrice;

	private boolean cancelled;

}
