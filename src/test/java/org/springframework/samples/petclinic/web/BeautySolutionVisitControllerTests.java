package org.springframework.samples.petclinic.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.BeautySolutionService;
import org.springframework.samples.petclinic.service.BeautySolutionVisitService;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PromotionService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BeautySolutionVisitController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class BeautySolutionVisitControllerTests {

	@Autowired
	private BeautySolutionVisitController beautySolutionVisitController;
	
	@MockBean
	private BeautySolutionVisitService beautySolutionVisitService;
	@MockBean
	private OwnerService ownerService;
	@MockBean
	private DiscountVoucherService discountVoucherService;
	@MockBean
	private PetService petService;
	@MockBean
	private PromotionService promotionService;
	@MockBean
	private BeautySolutionService beautySolutionService;
	
	// Converter fixes mock
	@MockBean
	private VetService notused2;
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
    	User user = new User();
    	user.setUsername("test user");
		Owner owner = new Owner();
		owner.setId(1);
    	owner.setUser(user);
		given(this.ownerService.findPrincipal()).willReturn(owner);
		
		BeautySolution solution = new BeautySolution();
		solution.setId(1);
		solution.setPrice(20.0);
		solution.setTitle("Test solution");
		given(this.beautySolutionService.find(1)).willReturn(solution);


		BeautySolutionVisit saved = new BeautySolutionVisit();
		saved.setId(7);
		given(this.beautySolutionVisitService.bookBeautySolutionVisit(any(BeautySolutionVisit.class), any(DiscountVoucher.class))).willReturn(saved);
		
		Collection<Pet> pets = new HashSet<Pet>();
		given(this.petService.findPetsByOwner(1)).willReturn(pets);
		
		Collection<DiscountVoucher> discountVouchers = new HashSet<DiscountVoucher>();
		given(this.discountVoucherService.listPrincipalAvailableVouchers()).willReturn(discountVouchers);
		
		Collection<Promotion> promotions = new HashSet<Promotion>();
		given(this.promotionService.findAllSolutionPromotions(1)).willReturn(promotions);
	}
	
    @WithMockUser(value = "owner1")
    @Test
	void testListPrincipalBeautySolutionVisits() throws Exception {
		Collection<BeautySolutionVisit> visits = new HashSet<BeautySolutionVisit>();
		given(this.beautySolutionVisitService.findActiveVisitsByPrincipal()).willReturn(visits);
		
		mockMvc.perform(get("/beauty-solution/visit/owner/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutionVisits/list"))
				.andExpect(model().attributeExists("beautySolutionVisits"))
				.andExpect(model().attributeExists("now"));
	}
	
    @WithMockUser(value = "owner1")
    @Test
	void testInitCreateBeautySolutionVisit() throws Exception {
		
		BeautySolutionVisit visit = new BeautySolutionVisit();
		given(this.beautySolutionVisitService.create(1)).willReturn(visit);
		
		mockMvc.perform(get("/beauty-solution/visit/owner/create?beautySolutionId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutionVisits/edit"))
				.andExpect(model().attributeExists("beautySolutionVisitForm"))
				.andExpect(model().attributeExists("pets"))
				.andExpect(model().attributeExists("availableVouchers"))
				.andExpect(model().attributeExists("promotions"));
	}
	
    @WithMockUser(value = "owner1")
    @Test
	void testCancelBeautySolutionVisit() throws Exception {
		
		BeautySolutionVisit visit = new BeautySolutionVisit();
		given(this.beautySolutionVisitService.create(1)).willReturn(visit);
		
		mockMvc.perform(get("/beauty-solution/visit/owner/1/cancel"))
		        .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/visit/owner/list"))
				.andExpect(model().attributeDoesNotExist("errorMessage"));
	}

	@WithMockUser(value = "owner1")
    @Test
	void testSaveBeautySolutionVisit() throws Exception {
		mockMvc.perform(post("/beauty-solution/visit/owner/save")
				.param("beautySolutionVisit.beautySolution", "1")  
				.param("beautySolutionVisit.pet", "1")  
				.param("beautySolutionVisit.date", "2020/08/01 10:00:00")
				.param("beautySolutionVisit.finalPrice", "20")
				.param("beautySolutionVisit.cancelled", "false")
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/visit/owner/list"));
	}

	@WithMockUser(value = "owner1")
    @Test
	void testSaveBeautySolutionVisitError() throws Exception {
		mockMvc.perform(post("/beauty-solution/visit/owner/save")
				.param("beautySolutionVisit.beautySolution", "1")  
				.param("beautySolutionVisit.pet", "1")  
				.param("beautySolutionVisit.date", "2020/08/01 10:00:00")
				.param("beautySolutionVisit.finalPrice", "-20")
				.param("beautySolutionVisit.cancelled", "false")
				.with(csrf()))    
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutionVisits/edit"))
				.andExpect(model().attributeExists("beautySolutionVisitForm"))
				.andExpect(model().attributeExists("pets"))
				.andExpect(model().attributeExists("availableVouchers"))
				.andExpect(model().attributeExists("promotions"));
	}


}
