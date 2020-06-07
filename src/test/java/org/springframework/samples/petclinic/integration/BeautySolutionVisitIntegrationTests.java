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
class BeautySolutionVisitIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc;

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testListPrincipalBeautySolutionVisits() throws Exception {
		
		mockMvc.perform(get("/beauty-solution/visit/owner/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutionVisits/list"))
				.andExpect(model().attributeExists("beautySolutionVisits"))
				.andExpect(model().attributeExists("now"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testInitCreateBeautySolutionVisit() throws Exception {
		
		mockMvc.perform(get("/beauty-solution/visit/owner/create?beautySolutionId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("beautySolutionVisits/edit"))
				.andExpect(model().attributeExists("beautySolutionVisitForm"))
				.andExpect(model().attributeExists("pets"))
				.andExpect(model().attributeExists("availableVouchers"))
				.andExpect(model().attributeExists("promotions"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testCancelBeautySolutionVisit() throws Exception {
		
		mockMvc.perform(get("/beauty-solution/visit/owner/1/cancel"))
		        .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/visit/owner/list"))
				.andExpect(model().attributeDoesNotExist("errorMessage"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testSaveBeautySolutionVisit() throws Exception {
		mockMvc.perform(post("/beauty-solution/visit/owner/save")
				.param("beautySolutionVisit.beautySolution", "23")  
				.param("beautySolutionVisit.pet", "1")  
				.param("beautySolutionVisit.date", "2020/08/01 10:00:00")
				.param("beautySolutionVisit.finalPrice", "20")
				.param("beautySolutionVisit.cancelled", "false")
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-solution/visit/owner/list"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
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
