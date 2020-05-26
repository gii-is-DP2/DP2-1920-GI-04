package org.springframework.samples.petclinic.form;

import javax.persistence.OneToOne;
import javax.validation.Valid;

import org.springframework.data.annotation.Transient;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;

import lombok.Data;

@Data
public class BeautySolutionVisitForm {
	
	@Valid
	@Transient
	@OneToOne(optional = false)
	private BeautySolutionVisit beautySolutionVisit;
	
	@Valid
	@Transient
	@OneToOne(optional = true)
	private DiscountVoucher discountVoucher;


}
