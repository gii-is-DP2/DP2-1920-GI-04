package org.springframework.samples.petclinic.form;

import javax.persistence.OneToOne;
import javax.validation.Valid;

import org.springframework.data.annotation.Transient;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;

import lombok.Data;

@Data
public class BeautyServiceVisitForm {
	
	@Valid
	@Transient
	@OneToOne(optional = false)
	private BeautyServiceVisit beautyServiceVisit;
	
	@Valid
	@Transient
	@OneToOne(optional = true)
	private DiscountVoucher discountVoucher;


}
