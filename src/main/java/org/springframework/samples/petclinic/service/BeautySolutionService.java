package org.springframework.samples.petclinic.service;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.repository.BeautySolutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class BeautySolutionService {

	private BeautySolutionRepository beautySolutionRepository;
	
	// AUXILIAR SERVICES 
	
	private AuthoritiesService authService;
	
	@Autowired
	public BeautySolutionService(AuthoritiesService authoritiesService, BeautySolutionRepository beautySolutionRepository) {
		this.authService = authoritiesService;
		this.beautySolutionRepository = beautySolutionRepository;
	}
	
	// MAIN METHODS

	public BeautySolution create() {
		BeautySolution res = new BeautySolution();
		res.setEnabled(true);
		return res;
	}
	
	@Transactional
	public BeautySolution save(BeautySolution beautySolution) {
		return this.beautySolutionRepository.save(beautySolution);
	}

	public BeautySolution find(int beautySolutionId) {
		Assert.isTrue(exists(beautySolutionId), "beautysolution.error.notfound");
		return this.beautySolutionRepository.findById(beautySolutionId).orElse(null);
	}
	
	// ENDPOINT METHODS
	
	public Collection<BeautySolution> showBeautySolutionList(Integer petType){
		Boolean showDisabled = this.authService.checkAdminAuth();
		if(petType != null) {
			return findSolutionsByPetType(showDisabled, petType);
		} else {
			return findAll(showDisabled);
		}
	}
	
	public BeautySolution viewBeautySolution(Integer beautySolutionId) {
		BeautySolution beautySolution = find(beautySolutionId);
		Assert.isTrue(beautySolution.isEnabled() || this.authService.checkAdminAuth(), "beautysolution.error.notfound");
		return beautySolution;
	}
	
	@Transactional
	public BeautySolution edit(BeautySolution beautySolution) {
		if(beautySolution.getId() != null) {
			BeautySolution original = this.find(beautySolution.getId());
			Assert.notNull(original, "beautysolution.error.notfound");
			Assert.isTrue(original.getType().equals(beautySolution.getType()), "beautysolution.error.edittype");			
		}
		return this.save(beautySolution);
	}

	// AUXILIAR METHODS

	public Collection<BeautySolution> findAll(Boolean showDisabled) {
		return this.beautySolutionRepository.findAllSolutions(!showDisabled);
	}

	public Collection<BeautySolution> findSolutionsByPetType(Boolean showDisabled, Integer petType) {
		return this.beautySolutionRepository.findSolutionsByPetType(!showDisabled, petType);
	}
	
	public boolean exists(int beautySolutionId) {
		return this.beautySolutionRepository.existsById(beautySolutionId);
	}

}
