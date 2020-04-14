package org.springframework.samples.petclinic.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "discountVouchers")
public class DiscountVoucher extends NamedEntity {
     
	@NotNull
	@Min(0)
	@Max(100)
	private Integer discount;

	@NotNull
	@ManyToOne
	private Owner owner;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime created;

	@OneToOne(optional = true)
	private BeautyServiceVisit redeemedBeautyServiceVisit;

	@NotBlank
	private String description;

}
