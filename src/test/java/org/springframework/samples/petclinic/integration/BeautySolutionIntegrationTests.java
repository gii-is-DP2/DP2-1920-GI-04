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
class BeautySolutionIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
    @Test
	void testShowBeautySolutionList() throws Exception {
		mockMvc.perform(get("/beauty-solution/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/list"))
				.andExpect(model().attributeExists("beautySolutions"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeDoesNotExist("selectedType"));
	}
	
    @Test
	void testFilterBeautySolutionList() throws Exception {
		mockMvc.perform(get("/beauty-solution/list").param("petType", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/list"))
				.andExpect(model().attributeExists("beautySolutions"))
				.andExpect(model().attributeExists("petTypes"))
				.andExpect(model().attributeExists("selectedType"));
	}
	
    @Test
	void testViewBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/{beautySolutionId}", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/view"))
				.andExpect(model().attributeExists("beautySolution"));
	}
	
    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testInitCreateBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/admin/create"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/edit"))
				.andExpect(model().attributeExists("beautySolution"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testEditBeautySolution() throws Exception {
		mockMvc.perform(get("/beauty-solution/admin/{beautySolutionId}/edit", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutions/edit"))
				.andExpect(model().attributeExists("beautySolution"))
				.andExpect(model().attributeExists("vets"))
				.andExpect(model().attributeExists("types"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
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


    @WithMockUser(username = "admin1", authorities = {"admin"})
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
