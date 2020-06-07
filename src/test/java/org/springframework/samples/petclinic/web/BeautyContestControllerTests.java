package org.springframework.samples.petclinic.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
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
import org.springframework.samples.petclinic.model.BeautyContest;
import org.springframework.samples.petclinic.model.BeautySolutionVisit;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.BeautyContestService;
import org.springframework.samples.petclinic.service.BeautySolutionService;
import org.springframework.samples.petclinic.service.BeautySolutionVisitService;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BeautyContestController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class BeautyContestControllerTests {

	@Autowired
	private BeautyContestController beautyContestController;
	
	@MockBean
	private BeautyContestService beautyContestService;
	@MockBean
	private BeautySolutionVisitService beautySolutionVisitService;
	@MockBean
	private OwnerService ownerService;
	
	// Converter fixes mock
	@MockBean
	private VetService notused1;
	@MockBean
	private DiscountVoucherService notused2;
	@MockBean
	private PetService notused3;
	@MockBean
	private BeautySolutionService notused4;
	
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

		Collection<BeautySolutionVisit> visits = new HashSet<BeautySolutionVisit>();
		given(this.beautyContestService.listPossibleParticipations(1)).willReturn(visits);
		
		BeautyContest contest = new BeautyContest();
		contest.setId(1);
		given(this.beautyContestService.findCurrent(any(LocalDateTime.class))).willReturn(contest);
	}

    @WithMockUser(value = "spring")
    @Test
	void testShowBeautyContestList() throws Exception {
		Collection<BeautyContest> contests = new HashSet<BeautyContest>();
		given(this.beautyContestService.showBeautyContestList(any(LocalDateTime.class))).willReturn(contests);
		
		mockMvc.perform(get("/beauty-contest/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/list"))
				.andExpect(model().attributeExists("beautyContests"));
	}

    @WithMockUser(value = "spring")
    @Test
	void testViewBeautyContest() throws Exception {
		BeautyContest contest = new BeautyContest();
		contest.setId(1);
		contest.setDate(LocalDateTime.of(2020, 6, 1, 0, 0));
		given(this.beautyContestService.viewBeautyContest(1)).willReturn(contest);
		Collection<BeautySolutionVisit> visits = new HashSet<BeautySolutionVisit>();
		given(this.beautyContestService.listParticipations(1)).willReturn(visits);
		
		mockMvc.perform(get("/beauty-contest/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/view"))
				.andExpect(model().attributeExists("beautyContest"))
				.andExpect(model().attributeExists("participations"))
				.andExpect(model().attributeExists("ended"));
	}

    @WithMockUser(value = "owner1")
    @Test
	void testInitCreateParticipation() throws Exception {
		
		mockMvc.perform(get("/beauty-contest/owner/1/participate"))
				.andExpect(status().isOk())
				.andExpect(view().name("beautyContests/participate"))
				.andExpect(model().attributeExists("participationForm"))
				.andExpect(model().attributeExists("visits"));
	}

	@WithMockUser(value = "owner1")
    @Test
	void testSaveParticipation() throws Exception {
		mockMvc.perform(post("/beauty-contest/owner/participate")
				.param("beautyContestId", "1")  
				.param("participationPhoto", "https://specials-images.forbesimg.com/imageserve/5db4c7b464b49a0007e9dfac/960x0.jpg")  
				.param("visitId", "1")
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/1"));
	}

	@WithMockUser(value = "owner1")
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

	@WithMockUser(value = "owner1")
    @Test
	void testWithdrawParticipation() throws Exception {
		mockMvc.perform(get("/beauty-contest/owner/withdraw?beautySolutionVisitId=1"))
        		.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/1"));
	}

	@WithMockUser(value = "admin1")
    @Test
	void testSelectWinner() throws Exception {
		mockMvc.perform(get("/beauty-contest/admin/1/1/award"))
        		.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/beauty-contest/1"));
	}


}
