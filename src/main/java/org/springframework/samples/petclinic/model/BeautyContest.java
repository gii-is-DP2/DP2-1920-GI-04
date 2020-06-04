package org.springframework.samples.petclinic.model;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "beautyContests", uniqueConstraints={
	    @UniqueConstraint(columnNames = {"date"})
	})
public class BeautyContest extends NamedEntity {

	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	private LocalDateTime date;

	@Valid
	@OneToOne(optional = true)
	private BeautySolutionVisit winner;
	

	
	@Transient
	public String getLabel() {
		return new DateFormatSymbols().getMonths()[this.getDate().getMonthValue()-1] + " " + this.getDate().getYear();
	}
}
