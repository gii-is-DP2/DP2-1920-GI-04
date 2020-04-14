package org.springframework.samples.petclinic.web;


import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/discount-voucher")
public class DiscountVoucherController {

	@Autowired
	private DiscountVoucherService discountVoucherService;

	@Autowired
	private OwnerService ownerService;

	@GetMapping("/owner/list")
	public String listPrincipalDiscountVouchers(ModelMap model) {
		Owner principal = this.ownerService.findPrincipal();
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listPrincipalAvailableVouchers();
		model.addAttribute("discountVouchers", discountVouchers);
		model.addAttribute("ownerUserName", principal.getUser().getUsername());
		return "discountVouchers/list";
	}

	@GetMapping("/admin/list")
	public String listOwnerDiscountVouchers(@RequestParam(value = "ownerId", required = false) Integer ownerId, ModelMap model) {
		Owner owner = this.ownerService.findOwnerById(ownerId);
		Collection<DiscountVoucher> discountVouchers = this.discountVoucherService.listOwnerDiscountVouchers(ownerId);
		model.addAttribute("discountVouchers", discountVouchers);
		model.addAttribute("ownerUserName", owner.getUser().getUsername());
		return "discountVouchers/list";
	}

	@GetMapping("/admin/create")
	public String createDiscountVoucher(@RequestParam(value = "ownerId", required = false) Integer ownerId, ModelMap model) {
		model.addAttribute("discountVoucher", this.discountVoucherService.create(ownerId));
		model = this.prepareEditModel(model);
		return "discountVouchers/edit";
	}
	
	@PostMapping("/admin/save")
	public String saveBeautyService(@Valid DiscountVoucher discountVoucher, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("discountVoucher", discountVoucher);
			model = this.prepareEditModel(model);
			return "discountVouchers/edit";
		} else {
			try {
				discountVoucher = this.discountVoucherService.save(discountVoucher, true);
				return "redirect:/discount-voucher/admin/list?ownerId=" + discountVoucher.getOwner().getId();
			} catch (Throwable e) {
				model.addAttribute("discountVoucher", discountVoucher);
				model = this.prepareEditModel(model);
				if(e.getMessage() != null && e.getMessage().contains(".error.")) {
					model.addAttribute("errorMessage", e.getMessage());
				}
				return "discountVouchers/edit";
			}
		}
	}
	
	
	// Auxiliary Methods
	
	private ModelMap prepareEditModel(ModelMap model) {
		return model;
	}

}
