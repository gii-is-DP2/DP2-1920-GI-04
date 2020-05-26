package org.springframework.samples.petclinic.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import org.springframework.samples.petclinic.model.DiscountVoucher;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.BeautySolutionService;
import org.springframework.samples.petclinic.service.BeautySolutionVisitService;
import org.springframework.samples.petclinic.service.DiscountVoucherService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DiscountVoucherController.class,
			includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class DiscountVoucherControllerTests {

	@Autowired
	private DiscountVoucherController discountVoucherController;
	
	@MockBean
	private DiscountVoucherService discountVoucherService;
	@MockBean
	private OwnerService ownerService;
	
	// Converter fixes mock
	@MockBean
	private BeautySolutionService notused1;
	@MockBean
	private BeautySolutionVisitService notused2;
	@MockBean
	private VetService notused3;
	@MockBean
	private PetService notused4;
	
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
		given(this.ownerService.findOwnerById(1)).willReturn(owner);
		
		Collection<DiscountVoucher> vouchers = new HashSet<DiscountVoucher>();
		given(this.discountVoucherService.listPrincipalAvailableVouchers()).willReturn(vouchers);
		given(this.discountVoucherService.listOwnerDiscountVouchers(1)).willReturn(vouchers);
		
		DiscountVoucher discountVoucher = new DiscountVoucher();
		discountVoucher.setOwner(owner);
		discountVoucher.setCreated(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
		given(this.discountVoucherService.create(1)).willReturn(discountVoucher);
		
		DiscountVoucher saved = new DiscountVoucher();
		saved.setId(7);
		saved.setOwner(owner);
		given(this.discountVoucherService.save(any(DiscountVoucher.class), eq(true))).willReturn(saved);
	}
	
    @WithMockUser(value = "owner1")
    @Test
	void testListPrincipalDiscountVouchers() throws Exception {
		
		mockMvc.perform(get("/discount-voucher/owner/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/list"))
				.andExpect(model().attributeExists("discountVouchers"))
				.andExpect(model().attributeExists("ownerUserName"))
				.andExpect(model().attributeExists("now"));
	}
	
    @WithMockUser(value = "admin1")
    @Test
	void testOwnerDiscountVouchersAsAdmin() throws Exception {
		
		mockMvc.perform(get("/discount-voucher/admin/list?ownerId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/list"))
				.andExpect(model().attributeExists("discountVouchers"))
				.andExpect(model().attributeExists("ownerUserName"))
				.andExpect(model().attributeExists("now"));
	}
	
    @WithMockUser(value = "admin1")
    @Test
	void testInitCreateDiscountVoucher() throws Exception {
		mockMvc.perform(get("/discount-voucher/admin/create?ownerId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/edit"))
				.andExpect(model().attributeExists("discountVoucher"))
				.andExpect(model().attributeExists("createdDate"));
	}

	@WithMockUser(value = "admin1")
    @Test
	void testSaveDiscountVoucher() throws Exception {
		mockMvc.perform(post("/discount-voucher/admin/save")
				.param("discount", "24")    
				.param("created", "2020/05/01 10:00:00")
				.param("owner", "1")
				.param("description", "test voucher")    
				.with(csrf()))                    
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/discount-voucher/admin/list?ownerId=1"));
	}


	@WithMockUser(value = "admin1")
    @Test
	void testSaveDiscountVoucherError() throws Exception {
		mockMvc.perform(post("/discount-voucher/admin/save")
				.param("discount", "24")    
				.param("created", "2021/05/01 10:00:00")
				.param("owner", "1")
				.param("description", "test voucher")
				.with(csrf()))             
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/edit"))
				.andExpect(model().attributeExists("discountVoucher"))
				.andExpect(model().attributeExists("createdDate"))
				.andExpect(model().attributeHasErrors("discountVoucher"));
	}


}
