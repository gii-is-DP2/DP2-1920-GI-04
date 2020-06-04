package org.springframework.samples.petclinic.model;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

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

	@ManyToOne(optional = false)
	private Owner owner;
	
	@NotNull
	@Past
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime created;

	@Valid
	@OneToOne(optional = true)
	private BeautySolutionVisit redeemedBeautySolutionVisit;

	@NotBlank
	private String description;

	
	@Transient
	public String getLabel() {
		return this.discount + "% discount (" + this.description + ")";
	}
}
