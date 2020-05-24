package org.springframework.samples.petclinic.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

import lombok.Data;

@Data
public class BeautyContestParticipationForm {

	@NotNull
	private Integer beautyContestId;

	@NotNull
	private Integer visitId;
	
	@NotBlank
	@URL
	private String participationPhoto;

}
