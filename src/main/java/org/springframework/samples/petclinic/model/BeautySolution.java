package org.springframework.samples.petclinic.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "beautySolutions", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"title", "type_id"})
	})
public class BeautySolution extends NamedEntity {
     
	@NotBlank
	private String title;

	@Valid
	@ManyToOne(optional = false)
	private PetType type;

	@Valid
	@ManyToOne(optional = false)
	@JoinColumn(name = "vet_id")
	private Vet vet;

	@NotNull
	private boolean enabled;

	@NotNull
	@Min(0)
	private Double price;
}
