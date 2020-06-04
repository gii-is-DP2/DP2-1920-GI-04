package org.springframework.samples.petclinic.web;


import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.form.BeautySolutionVisitForm;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.service.BeautySolutionVisitService;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PromotionService;
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
@RequestMapping("/beauty-solution/visit")
public class BeautySolutionVisitController {

	@Autowired
	private BeautySolutionVisitService beautySolutionVisitService;

	@Autowired
	private PetService petService;
	
	@Autowired
	private OwnerService ownerService;
	
	@Autowired
	private DiscountVoucherService discountVoucherService;

	@Autowired
	private PromotionService promotionService;

	@GetMapping("/owner/list")
	public String findVisitsByPrincipal(ModelMap model) {
		Iterable<BeautySolutionVisit> principalVisits = this.beautySolutionVisitService.findActiveVisitsByPrincipal();
		model.addAttribute("beautySolutionVisits", principalVisits);
		model.addAttribute("now", LocalDateTime.now());
		return "beautySolutionVisits/list";
	}

	@GetMapping("/owner/create")
	public String createBeautySolutionVisit(@RequestParam("beautySolutionId") int beautySolutionId, ModelMap model) {
		BeautySolutionVisit visit = this.beautySolutionVisitService.create(beautySolutionId);
		BeautySolutionVisitForm beautySolutionVisitForm = new BeautySolutionVisitForm();
		beautySolutionVisitForm.setBeautySolutionVisit(visit);
		model.addAttribute("beautySolutionVisitForm", beautySolutionVisitForm);
		model = this.prepareEditModel(model, beautySolutionId, visit.getBeautySolution().getType().getId());
		return "beautySolutionVisits/edit";
	}

	@GetMapping("/owner/{beautySolutionVisitId}/cancel")
	public String cancelBeautySolution(@PathVariable("beautySolutionVisitId") int beautySolutionVisitId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			this.beautySolutionVisitService.cancelVisit(beautySolutionVisitId);
			return "redirect:/beauty-solution/visit/owner/list";
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-solution/visit/owner/list";
			
		}
	}

	@PostMapping("/owner/save")
	public String saveBeautySolutionVisit(@Valid BeautySolutionVisitForm beautySolutionVisitForm, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("beautySolutionVisitForm", beautySolutionVisitForm);
			model = this.prepareEditModel(model, beautySolutionVisitForm.getBeautySolutionVisit().getBeautySolution().getId(), beautySolutionVisitForm.getBeautySolutionVisit().getBeautySolution().getType().getId());
			return "beautySolutionVisits/edit";
		} else {
			try {
				this.beautySolutionVisitService.bookBeautySolutionVisit(beautySolutionVisitForm.getBeautySolutionVisit(), beautySolutionVisitForm.getDiscountVoucher());
				return "redirect:/beauty-solution/visit/owner/list";
			} catch (Throwable e) {
				model.addAttribute("beautySolutionVisitForm", beautySolutionVisitForm);
				model = this.prepareEditModel(model, beautySolutionVisitForm.getBeautySolutionVisit().getBeautySolution().getId(), beautySolutionVisitForm.getBeautySolutionVisit().getBeautySolution().getType().getId());
				if(e.getMessage() != null && e.getMessage().contains(".error.")) {
					model.addAttribute("errorMessage", e.getMessage());
				}
				return "beautySolutionVisits/edit";
			}
		}
	}
	
	// Auxiliary Methods
	
	private ModelMap prepareEditModel(ModelMap model, Integer solutionId, Integer type) {
		this.beautySolutionVisitService.checkAwardPendingVouchers(LocalDateTime.now());
		model.addAttribute("availableVouchers", this.discountVoucherService.listPrincipalAvailableVouchers());
		model.addAttribute("pets", this.petService.findPetsByOwnerAndType(this.ownerService.findPrincipal().getId(), type));
		model.addAttribute("promotions", this.promotionService.findAllSolutionPromotions(solutionId));
		return model;
	}

}
