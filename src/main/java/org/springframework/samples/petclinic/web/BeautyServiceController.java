package org.springframework.samples.petclinic.web;


import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.BeautyServiceService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/beauty-service")
public class BeautyServiceController {

	@Autowired
	private BeautyServiceService beautyServiceService;

	@Autowired
	private VetService vetService;

	@Autowired
	private PetService petService;

	@GetMapping("/list")
	public String showBeautyServiceList(ModelMap model, @RequestParam(value = "petType", required = false) Integer petType) {
		Iterable<BeautyService> beautyServices = this.beautyServiceService.showBeautyServiceList(petType);
		Collection<PetType> petTypes = this.petService.findPetTypes();
		model.addAttribute("beautyServices", beautyServices);
		model.addAttribute("petTypes", petTypes);
		model.addAttribute("selectedType", petType);
		return "beautyServices/list";
	}

	@GetMapping("/{beautyServiceId}")
	public String viewBeautyService(@PathVariable("beautyServiceId") int beautyServiceId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			BeautyService beautyService = this.beautyServiceService.viewBeautyService(beautyServiceId);
			model.addAttribute("beautyService", beautyService);
			return "beautyServices/view";
		} catch(Throwable e){
			redirectAttributes.addFlashAttribute("errorMessage", "master.error");
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-service/list";
		}
	}

	@GetMapping("/admin/create")
	public String createBeautyService(ModelMap model) {
		model.addAttribute("beautyService", this.beautyServiceService.create());
		model = this.prepareEditModel(model);
		return "beautyServices/edit";
	}

	@GetMapping("/admin/{beautyServiceId}/edit")
	public String editBeautyService(@PathVariable("beautyServiceId") int beautyServiceId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			model.addAttribute("beautyService", this.beautyServiceService.find(beautyServiceId));
			model = this.prepareEditModel(model);
			return "beautyServices/edit";
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-service/list";
			
		}
	}

	@PostMapping("/admin/save")
	public String saveBeautyService(@Valid BeautyService beautyService, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("beautyService", beautyService);
			model = this.prepareEditModel(model);
			return "beautyServices/edit";
		} else {
			try {
				beautyService = this.beautyServiceService.edit(beautyService);
				return "redirect:/beauty-service/" + beautyService.getId();
			} catch (Throwable e) {
				model.addAttribute("beautyService", beautyService);
				model = this.prepareEditModel(model);
				if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && e.getCause().getCause().getMessage().contains("Unique index")) {
					model.addAttribute("errorMessage", "beautyservice.error.unique");
				}
				return "beautyServices/edit";
			}
		}
	}
	
	
	// Auxiliary Methods
	
	private ModelMap prepareEditModel(ModelMap model) {
		model.addAttribute("vets", this.vetService.findVets());
		model.addAttribute("types", this.petService.findPetTypes());
		return model;
	}

}
