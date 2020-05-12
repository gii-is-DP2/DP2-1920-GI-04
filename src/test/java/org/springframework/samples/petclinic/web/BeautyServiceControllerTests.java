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
import org.springframework.samples.petclinic.model.BeautyService;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.BeautyServiceService;
import org.springframework.samples.petclinic.service.BeautyServiceVisitService;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BeautyServiceController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class BeautyServiceControllerTests {

	@Autowired
	private BeautyServiceController beautyServiceController;
	
	@MockBean
	private BeautyServiceService beautyServiceService;
	
	@MockBean
	private BeautyServiceVisitService notused1;
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
		BeautyService service = new BeautyService();
		given(this.beautyServiceService.create()).willReturn(service);
		service.setId(7);
		given(this.beautyServiceService.edit(any(BeautyService.class))).willReturn(service);
		
		Vet vet = new Vet();
		vet.setFirstName("Controller");
		vet.setLastName("Test");
		given(this.vetService.find(1)).willReturn(vet);
		
		PetType cat = new PetType();
		cat.setId(1);
		cat.setName("cat");
		given(this.petService.findPetTypes()).willReturn(Lists.newArrayList(cat));
		given(this.petService.findPetById(1)).willReturn(new Pet());

		given(this.beautyServiceService.showBeautyServiceList(any(Integer.class))).willReturn(new HashSet<BeautyService>());
		given(this.beautyServiceService.viewBeautyService(1)).willReturn(new BeautyService());
		given(this.beautyServiceService.find(1)).willReturn(new BeautyService());
		
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testShowBeautyServiceList() throws Exception {
		mockMvc.perform(get("/beauty-service/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/list"))
				.andExpect(model().attributeExists("beautyServices"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeDoesNotExist("selectedType"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testFilterBeautyServiceList() throws Exception {
		mockMvc.perform(get("/beauty-service/list").param("petType", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/list"))
				.andExpect(model().attributeExists("beautyServices"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeExists("selectedType"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testViewBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/{beautyServiceId}", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/view"))
				.andExpect(model().attributeExists("beautyService"));
	}
	
    @WithMockUser(value = "admin1")
    @Test
	void testInitCreateBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/admin/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/edit"))
				.andExpect(model().attributeExists("beautyService"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}
	
	@WithMockUser(value = "admin1")
    @Test
	void testEditBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/admin/{beautyServiceId}/edit", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/edit"))
				.andExpect(model().attributeExists("beautyService"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

	@WithMockUser(value = "admin1")
    @Test
	void testSaveBeautyService() throws Exception {
		mockMvc.perform(post("/beauty-service/admin/save")
				.param("title", "Controller test")    
				.param("type", "cat")
				.param("vet", "1")
				.param("price", "55")
				.param("enabled", "true")    
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-service/7"));
	}


	@WithMockUser(value = "admin1")
    @Test
	void testSaveBeautyServiceError() throws Exception {
		mockMvc.perform(post("/beauty-service/admin/save")
				.param("title", "")    
				.param("type", "cat")
				.param("vet", "1")
				.param("price", "55")
				.param("enabled", "true")    
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/edit"))
				.andExpect(model().attributeExists("beautyService"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"))
				.andExpect(model().attributeHasErrors("beautyService"));
	}


}
