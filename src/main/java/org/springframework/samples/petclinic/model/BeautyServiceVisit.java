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
	@NotNull
	@Valid
	@ManyToOne
	private BeautyService beautyService;

	@NotNull
	@Valid
	@ManyToOne
	private Pet pet;

	@Valid
	@OneToOne(optional = true)
	private DiscountVoucher awardedDiscountVoucher;

	@NotNull
	@Future
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime date;

	@NotNull
	@Min(0)
	private Double finalPrice;

	@NotNull
	private Boolean cancelled;

}
