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
class BeautyServiceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
    @Test
	void testShowBeautyServiceList() throws Exception {
		mockMvc.perform(get("/beauty-service/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/list"))
				.andExpect(model().attributeExists("beautyServices"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeDoesNotExist("selectedType"));
	}
	
    @Test
	void testFilterBeautyServiceList() throws Exception {
		mockMvc.perform(get("/beauty-service/list").param("petType", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/list"))
				.andExpect(model().attributeExists("beautyServices"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeExists("selectedType"));
	}
	
    @Test
	void testViewBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/{beautyServiceId}", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/view"))
				.andExpect(model().attributeExists("beautyService"));
	}
	
    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testInitCreateBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/admin/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/edit"))
				.andExpect(model().attributeExists("beautyService"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testEditBeautyService() throws Exception {
		mockMvc.perform(get("/beauty-service/admin/{beautyServiceId}/edit", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyServices/edit"))
				.andExpect(model().attributeExists("beautyService"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
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


    @WithMockUser(username = "admin1", authorities = {"admin"})
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
