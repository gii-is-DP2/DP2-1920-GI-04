package org.springframework.samples.petclinic.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Promotion;
import org.springframework.samples.petclinic.model.Vet;
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

@WebMvcTest(controllers = BeautySolutionController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class BeautySolutionControllerTests {

	@Autowired
	private BeautySolutionController beautySolutionController;
	
	@MockBean
	private BeautySolutionService beautySolutionService;
	@MockBean
	private PromotionService promotionService;
	
	@MockBean
	private BeautySolutionVisitService notused1;
	@MockBean
	private DiscountVoucherService notused2;
	@MockBean
	private OwnerService notused3;
	
	@MockBean
	private VetService vetService;
	
	@MockBean
	private PetService petService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		BeautySolution solution = new BeautySolution();
		given(this.beautySolutionService.create()).willReturn(solution);
		solution.setId(7);
		given(this.beautySolutionService.edit(any(BeautySolution.class))).willReturn(solution);
		
		Vet vet = new Vet();
		vet.setFirstName("Controller");
		vet.setLastName("Test");
		given(this.vetService.find(1)).willReturn(vet);
		
		PetType cat = new PetType();
		cat.setId(1);
		cat.setName("cat");
		given(this.petService.findPetTypes()).willReturn(Lists.newArrayList(cat));
		given(this.petService.findPetById(1)).willReturn(new Pet());

		given(this.beautySolutionService.showBeautySolutionList(any(Integer.class))).willReturn(new HashSet<BeautySolution>());
		given(this.beautySolutionService.viewBeautySolution(1)).willReturn(new BeautySolution());
		given(this.beautySolutionService.find(1)).willReturn(new BeautySolution());
		
		given(this.promotionService.findAllSolutionPromotions(1)).willReturn(new HashSet<Promotion>());
		
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testShowBeautySolutionList() throws Exception {
		mockMvc.perform(get("/beauty-solution/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/list"))
				.andExpect(model().attributeExists("beautySolutions"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeDoesNotExist("selectedType"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testFilterBeautySolutionList() throws Exception {
		mockMvc.perform(get("/beauty-solution/list").param("petType", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/list"))
				.andExpect(model().attributeExists("beautySolutions"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeExists("selectedType"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testViewBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/{beautySolutionId}", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/view"))
				.andExpect(model().attributeExists("beautySolution"));
	}
	
    @WithMockUser(value = "admin1")
    @Test
	void testInitCreateBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/admin/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/edit"))
				.andExpect(model().attributeExists("beautySolution"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}
	
	@WithMockUser(value = "admin1")
    @Test
	void testEditBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/admin/{beautySolutionId}/edit", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/edit"))
				.andExpect(model().attributeExists("beautySolution"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

	@WithMockUser(value = "admin1")
    @Test
	void testSaveBeautySolution() throws Exception {
		mockMvc.perform(post("/beauty-solution/admin/save")
				.param("title", "Controller test")    
				.param("type", "cat")
				.param("vet", "1")
				.param("price", "55")
				.param("enabled", "true")    
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/7"));
	}


	@WithMockUser(value = "admin1")
    @Test
	void testSaveBeautySolutionError() throws Exception {
		mockMvc.perform(post("/beauty-solution/admin/save")
				.param("title", "")    
				.param("type", "cat")
				.param("vet", "1")
				.param("price", "55")
				.param("enabled", "true")    
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/edit"))
				.andExpect(model().attributeExists("beautySolution"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"))
				.andExpect(model().attributeHasErrors("beautySolution"));
	}


}
