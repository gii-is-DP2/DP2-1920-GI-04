package org.springframework.samples.petclinic.web;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.service.PromotionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/promotion")
public class PromotionController {

	@Autowired
	private PromotionService promotionService;
	
	@GetMapping("/admin/create")
	public String createPromotion(@RequestParam("beautySolutionId") int beautySolutionId, ModelMap model) {
		model.addAttribute("promotion", this.promotionService.create(beautySolutionId));
		model = this.prepareEditModel(model);
		return "promotions/edit";
	}

	@PostMapping("/admin/save")
	public String savePromotion(@Valid Promotion promotion, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("promotion", promotion);
			model = this.prepareEditModel(model);
			return "promotions/edit";
		} else {
			try {
				promotion = this.promotionService.save(promotion);
				return "redirect:/beauty-solution/" + promotion.getBeautySolution().getId();
			} catch (Throwable e) {
				model.addAttribute("promotion", promotion);
				model = this.prepareEditModel(model);
				if(e.getMessage() != null && e.getMessage().contains(".error.")) {
					model.addAttribute("errorMessage", e.getMessage());
				}
				return "promotions/edit";
			}
		}
	}
	
	// Auxiliary Methods
	
	private ModelMap prepareEditModel(ModelMap model) {
		return model;
	}

}
