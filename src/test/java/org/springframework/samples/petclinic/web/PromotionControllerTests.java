package org.springframework.samples.petclinic.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.BeautySolution;
import org.springframework.samples.petclinic.model.Promotion;
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

@WebMvcTest(controllers = PromotionController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class PromotionControllerTests {

	@Autowired
	private PromotionController promotionController;
	
	@MockBean
	private PromotionService promotionService;
	
	// Converter fixes mock
	@MockBean
	private BeautySolutionService notused1;
	@MockBean
	private BeautySolutionVisitService notused2;
	@MockBean
	private DiscountVoucherService notused3;
	@MockBean
	private OwnerService notused4;
	@MockBean
	private VetService notused5;
	@MockBean
	private PetService notused6;
	
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		BeautySolution solution = new BeautySolution();
		solution.setId(1);
		Promotion promotion = new Promotion();
		promotion.setBeautySolution(solution);
		given(this.promotionService.create(1)).willReturn(promotion);
		
		Promotion saved = new Promotion();
		saved.setId(7);
		saved.setBeautySolution(solution);
		given(this.promotionService.save(any(Promotion.class))).willReturn(saved);
	}
	
    @WithMockUser(value = "admin1")
    @Test
	void testInitCreatePromotion() throws Exception {
		mockMvc.perform(get("/promotion/admin/create?beautySolutionId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("promotions/edit"))
				.andExpect(model().attributeExists("promotion"));
	}

	@WithMockUser(value = "admin1")
    @Test
	void testSavePromotion() throws Exception {
		mockMvc.perform(post("/promotion/admin/save")
				.param("discount", "24")    
				.param("startDate", "2020/07/10 10:00:00")
				.param("endDate", "2020/07/10 21:00:00")
				.param("beautySolution", "1")
				.param("enabled", "true")    
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/1"));
	}


	@WithMockUser(value = "admin1")
    @Test
	void testSavePromotionError() throws Exception {
		mockMvc.perform(post("/promotion/admin/save")
				.param("discount", "-1")    
				.param("startDate", "2020/07/10 10:00:00")
				.param("endDate", "2020/07/10 21:00:00")
				.param("beautySolution", "1")
				.param("enabled", "true")    
				.with(csrf()))             
				.andExpect(status().isOk())
				.andExpect(view().name("promotions/edit"))
				.andExpect(model().attributeExists("promotion"))
				.andExpect(model().attributeHasErrors("promotion"));
	}


}
