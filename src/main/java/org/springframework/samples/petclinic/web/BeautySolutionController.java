package org.springframework.samples.petclinic.web;


import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.service.BeautySolutionService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PromotionService;
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
@RequestMapping("/beauty-solution")
public class BeautySolutionController {

	@Autowired
	private BeautySolutionService beautySolutionService;

	@Autowired
	private PromotionService promotionService;

	@Autowired
	private VetService vetService;

	@Autowired
	private PetService petService;

	@GetMapping("/list")
	public String showBeautySolutionList(ModelMap model, @RequestParam(value = "petType", required = false) Integer petType) {
		Iterable<BeautySolution> beautySolutions = this.beautySolutionService.showBeautySolutionList(petType);
		Collection<PetType> petTypes = this.petService.findPetTypes();
		model.addAttribute("beautySolutions", beautySolutions);
		model.addAttribute("petTypes", petTypes);
		model.addAttribute("selectedType", petType);
		return "beautySolutions/list";
	}

	@GetMapping("/{beautySolutionId}")
	public String viewBeautySolution(@PathVariable("beautySolutionId") int beautySolutionId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			BeautySolution beautySolution = this.beautySolutionService.viewBeautySolution(beautySolutionId);
			Collection<Promotion> promotions = this.promotionService.findAllSolutionPromotions(beautySolutionId);
			model.addAttribute("beautySolution", beautySolution);
			model.addAttribute("promotions", promotions);
			return "beautySolutions/view";
		} catch(Throwable e){
			redirectAttributes.addFlashAttribute("errorMessage", "master.error");
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-solution/list";
		}
	}

	@GetMapping("/admin/create")
	public String createBeautySolution(ModelMap model) {
		model.addAttribute("beautySolution", this.beautySolutionService.create());
		model = this.prepareEditModel(model);
		return "beautySolutions/edit";
	}

	@GetMapping("/admin/{beautySolutionId}/edit")
	public String editBeautySolution(@PathVariable("beautySolutionId") int beautySolutionId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			model.addAttribute("beautySolution", this.beautySolutionService.find(beautySolutionId));
			model = this.prepareEditModel(model);
			return "beautySolutions/edit";
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-solution/list";
			
		}
	}

	@PostMapping("/admin/save")
	public String saveBeautySolution(@Valid BeautySolution beautySolution, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("beautySolution", beautySolution);
			model = this.prepareEditModel(model);
			return "beautySolutions/edit";
		} else {
			try {
				beautySolution = this.beautySolutionService.edit(beautySolution);
				return "redirect:/beauty-solution/" + beautySolution.getId();
			} catch (Throwable e) {
				model.addAttribute("beautySolution", beautySolution);
				model = this.prepareEditModel(model);
				if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null && e.getCause().getCause().getMessage().contains("Unique index")) {
					model.addAttribute("errorMessage", "beautysolution.error.unique");
				}
				return "beautySolutions/edit";
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
