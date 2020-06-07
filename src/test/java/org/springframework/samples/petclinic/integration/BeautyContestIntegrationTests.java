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
class BeautyContestIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc;

    @Test
	void testShowBeautyContestList() throws Exception {
		
		mockMvc.perform(get("/beauty-contest/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/list"))
				.andExpect(model().attributeExists("beautyContests"));
	}

    @Test
	void testViewBeautyContest() throws Exception {
		
		mockMvc.perform(get("/beauty-contest/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/view"))
				.andExpect(model().attributeExists("beautyContest"))
				.andExpect(model().attributeExists("participations"))
				.andExpect(model().attributeExists("ended"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testInitCreateParticipation() throws Exception {
		mockMvc.perform(get("/beauty-contest/list"));
		mockMvc.perform(get("/beauty-contest/owner/125/participate"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/participate"))
				.andExpect(model().attributeExists("participationForm"))
				.andExpect(model().attributeExists("visits"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testSaveParticipation() throws Exception {
		mockMvc.perform(get("/beauty-contest/list"));
		mockMvc.perform(post("/beauty-contest/owner/participate")
				.param("beautyContestId", "125")  
				.param("participationPhoto", "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg")  
				.param("visitId", "302")
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/125"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testSaveParticipationError() throws Exception {
		mockMvc.perform(post("/beauty-contest/owner/participate")
				.param("beautyContestId", "1")  
				.param("participationPhoto", "960x0jpg")  
				.param("visitId", "1")
				.with(csrf()))                    
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/participate"))
				.andExpect(model().attributeExists("participationForm"))
				.andExpect(model().attributeExists("visits"));
	}

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testWithdrawParticipation() throws Exception {
		mockMvc.perform(get("/beauty-contest/list"));
		mockMvc.perform(get("/beauty-contest/owner/withdraw?beautySolutionVisitId=301"))
        		.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/125"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testSelectWinner() throws Exception {
		mockMvc.perform(get("/beauty-contest/admin/49/1/award"))
        		.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/49"));
	}


}
