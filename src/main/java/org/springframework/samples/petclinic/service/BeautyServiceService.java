package org.springframework.samples.petclinic.service;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.repository.BeautyServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class BeautyServiceService {

	@Autowired
	private BeautyServiceRepository beautyServiceRepository;
	
	// AUXILIAR SERVICES 
	
	@Autowired
	private AuthoritiesService authService;
	
	// MAIN METHODS

	public BeautyService create() {
		BeautyService res = new BeautyService();
		res.setEnabled(true);
		return res;
	}
	
	@Transactional
	public BeautyService save(BeautyService beautyService) {
		return this.beautyServiceRepository.save(beautyService);
	}

	public BeautyService find(int beautyServiceId) {
		Assert.isTrue(exists(beautyServiceId), "beautyservice.error.notfound");
		return this.beautyServiceRepository.findById(beautyServiceId).orElse(null);
	}
	
	// ENDPOINT METHODS
	
	public Collection<BeautyService> showBeautyServiceList(Integer petType){
		Boolean showDisabled = this.authService.checkAdminAuth();
		if(petType != null) {
			return findServicesByPetType(showDisabled, petType);
		} else {
			return findAll(showDisabled);
		}
	}
	
	public BeautyService viewBeautyService(Integer beautyServiceId) {
		BeautyService beautyService = find(beautyServiceId);
		Assert.isTrue(beautyService.isEnabled() || this.authService.checkAdminAuth(), "beautyservice.error.notfound");
		return beautyService;
	}
	
	@Transactional
	public BeautyService edit(BeautyService beautyService) {
		if(beautyService.getId() != null) {
			BeautyService original = this.find(beautyService.getId());
			Assert.notNull(original, "beautyservice.error.notfound");
			Assert.isTrue(original.getType().equals(beautyService.getType()), "beautyservice.error.edittype");			
		}
		return this.save(beautyService);
	}

	// AUXILIAR METHODS

	public Collection<BeautyService> findAll(Boolean showDisabled) {
		return this.beautyServiceRepository.findAllServices(!showDisabled);
	}

	public Collection<BeautyService> findServicesByPetType(Boolean showDisabled, Integer petType) {
		return this.beautyServiceRepository.findServicesByPetType(!showDisabled, petType);
	}
	
	public boolean exists(int beautyServiceId) {
		return this.beautyServiceRepository.existsById(beautyServiceId);
	}

}
