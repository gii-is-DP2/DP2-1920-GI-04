package org.springframework.samples.petclinic.web;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.form.BeautyContestParticipationForm;
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautyServiceVisit;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.BeautyContestService;
import org.springframework.samples.petclinic.service.OwnerService;
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
@RequestMapping("/beauty-contest")
public class BeautyContestController {

	@Autowired
	private BeautyContestService beautyContestService;

	@Autowired
	private OwnerService ownerService;

	@GetMapping("/list")
	public String showBeautyContestList(ModelMap model) {
		Collection<BeautyContest> beautyContests = this.beautyContestService.showBeautyContestList(LocalDateTime.now());
		model.addAttribute("beautyContests", beautyContests);
		return "beautyContests/list";
	}

	@GetMapping("/{beautyContestId}")
	public String viewBeautyContest(@PathVariable("beautyContestId") int beautyContestId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			BeautyContest beautyContest = this.beautyContestService.viewBeautyContest(beautyContestId);
			Collection<BeautyServiceVisit> participations = this.beautyContestService.listParticipations(beautyContestId);
			model.addAttribute("beautyContest", beautyContest);
			model.addAttribute("participations", participations);
			model.addAttribute("ended", LocalDateTime.now().isAfter(LocalDateTime.of(beautyContest.getYear(), beautyContest.getMonth(), 1, 0, 0).plus(1, ChronoUnit.MONTHS)));
			Owner principal = this.ownerService.findPrincipal();
			if(principal != null) {
				model.addAttribute("principalId", principal.getId());
			}
			return "beautyContests/view";
		} catch(Throwable e){
			redirectAttributes.addFlashAttribute("errorMessage", "master.error");
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-contest/list";
		}
	}

	@GetMapping("/owner/{beautyContestId}/participate")
	public String createParticipation(@PathVariable("beautyContestId") int beautyContestId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			this.beautyContestService.assertCanParticipate(beautyContestId);
			BeautyContestParticipationForm participationForm = new BeautyContestParticipationForm();
			participationForm.setBeautyContestId(beautyContestId);
			model.addAttribute("participationForm", participationForm);
			model = this.prepareEditModel(model, participationForm.getBeautyContestId());
			return "beautyContests/participate";
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-contest/list";
			
		}
	}

	@PostMapping("/owner/participate")
	public String saveBeautyService(@Valid BeautyContestParticipationForm form, BindingResult bindingResult, ModelMap model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("participationForm", form);
			model = this.prepareEditModel(model, form.getBeautyContestId());
			return "beautyContests/participate";
		} else {
			try {
				this.beautyContestService.saveParticipation(form.getBeautyContestId(), form.getVisitId(), form.getParticipationPhoto(), LocalDateTime.now());
				return "redirect:/beauty-contest/" + form.getBeautyContestId();
			} catch (Throwable e) {
				model.addAttribute("participationForm", form);
				model = this.prepareEditModel(model, form.getBeautyContestId());
				if(e.getMessage() != null && e.getMessage().contains(".error.")) {
					model.addAttribute("errorMessage", e.getMessage());
				}
				return "beautyContests/participate";
			}
		}
	}

	@GetMapping("/owner/withdraw")
	public String withdrawParticipation(@RequestParam("beautyServiceVisitId") int beautyServiceVisitId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			this.beautyContestService.withdrawParticipation(beautyServiceVisitId);
			return "redirect:/beauty-contest/" + this.beautyContestService.findCurrent(LocalDateTime.now()).getId();
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-contest/" +  this.beautyContestService.findCurrent(LocalDateTime.now()).getId();
			
		}
	}

	@GetMapping("/admin/participation/{beautyServiceVisitId}/award")
	public String selectWinner(@PathVariable("beautyServiceVisitId") int beautyServiceVisitId, ModelMap model, RedirectAttributes redirectAttributes) {
		try {
			this.beautyContestService.selectWinner(beautyServiceVisitId);
			return "redirect:/beauty-contest/" + beautyServiceVisitId;
		} catch(Throwable e) {
			if(e.getMessage() != null && e.getMessage().contains(".error.")) {
				redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			}
			return "redirect:/beauty-contest/" + beautyServiceVisitId;
			
		}
	}
	
	// AUXILIAR METHODS

	private ModelMap prepareEditModel(ModelMap model, Integer contestId) {
		model.addAttribute("visits", this.beautyContestService.listPossibleParticipations(contestId));
		return model;
	}

}
