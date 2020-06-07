package org.springframework.samples.petclinic.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PromotionIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc;

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testInitCreatePromotion() throws Exception {
		mockMvc.perform(get("/promotion/admin/create?beautySolutionId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("promotions/edit"))
				.andExpect(model().attributeExists("promotion"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
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


    @WithMockUser(username = "admin1", authorities = {"admin"})
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
