package org.springframework.samples.petclinic.web;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.form.BeautyServiceVisitForm;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.service.BeautyServiceVisitService;
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
@RequestMapping("/beauty-service/visit")
public class BeautyServiceVisitController {

	@Autowired
	private BeautyServiceVisitService beautyServiceVisitService;

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
		Iterable<BeautyServiceVisit> principalVisits = this.beautyServiceVisitService.findActiveVisitsByPrincipal();
		model.addAttribute("beautyServiceVisits", principalVisits);
		return "beautyServiceVisits/list";
	}

	@GetMapping("/owner/create")
	public String createBeautyServiceVisit(@RequestParam("beautyServiceId") int beautyServiceId, ModelMap model) {
		BeautyServiceVisit visit = this.beautyServiceVisitService.create(beautyServiceId);
		BeautyServiceVisitForm beautyServiceVisitForm = new BeautyServiceVisitForm();
		beautyServiceVisitForm.setBeautyServiceVisit(visit);
		model.addAttribute("beautyServiceVisitForm", beautyServiceVisitForm);
		model = this.prepareEditModel(model, beautyServiceId);
		return "beautyServiceVisits/edit";
	}

	@GetMapping("/owner/{beautyServiceVisitId}/cancel")
	public String cancelBeautyService(@PathVariable("beautyServiceVisitId") int beautyServiceVisitId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			this.beautyServiceVisitService.cancelVisit(beautyServiceVisitId);
			return "redirect:/beauty-service/visit/owner/list";
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-service/visit/owner/list";
			
		}
	}

	@PostMapping("/owner/save")
	public String saveBeautyServiceVisit(@Valid BeautyServiceVisitForm beautyServiceVisitForm, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("beautyServiceVisitForm", beautyServiceVisitForm);
			model = this.prepareEditModel(model, beautyServiceVisitForm.getBeautyServiceVisit().getBeautyService().getId());
			return "beautyServiceVisits/edit";
		} else {
			try {
				this.beautyServiceVisitService.bookBeautyServiceVisit(beautyServiceVisitForm.getBeautyServiceVisit(), beautyServiceVisitForm.getDiscountVoucher());
				return "redirect:/beauty-service/visit/list";
			} catch (Throwable e) {
				model.addAttribute("beautyServiceVisitForm", beautyServiceVisitForm);
				model = this.prepareEditModel(model, beautyServiceVisitForm.getBeautyServiceVisit().getBeautyService().getId());
				if(e.getMessage() != null && e.getMessage().contains(".error.")) {
					model.addAttribute("errorMessage", e.getMessage());
				}
				return "beautyServiceVisits/edit";
			}
		}
	}
	
	// Auxiliary Methods
	
	private ModelMap prepareEditModel(ModelMap model, Integer serviceId) {
		model.addAttribute("pets", this.petService.findPetsByOwner(this.ownerService.findPrincipal().getId()));
		model.addAttribute("availableVouchers", this.discountVoucherService.listPrincipalAvailableVouchers());
		model.addAttribute("promotions", this.promotionService.findAllServicePromotions(serviceId));
		return model;
	}

}
