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
class DiscountVoucherIntegrationTests {
	
	@Autowired
	private MockMvc mockMvc;

    @WithMockUser(username = "owner1", authorities = {"owner"})
    @Test
	void testListPrincipalDiscountVouchers() throws Exception {
		
		mockMvc.perform(get("/discount-voucher/owner/list"))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/list"))
				.andExpect(model().attributeExists("discountVouchers"))
				.andExpect(model().attributeExists("ownerUserName"))
				.andExpect(model().attributeExists("now"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testOwnerDiscountVouchersAsAdmin() throws Exception {
		
		mockMvc.perform(get("/discount-voucher/admin/list?ownerId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/list"))
				.andExpect(model().attributeExists("discountVouchers"))
				.andExpect(model().attributeExists("ownerUserName"))
				.andExpect(model().attributeExists("now"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
    @Test
	void testInitCreateDiscountVoucher() throws Exception {
		mockMvc.perform(get("/discount-voucher/admin/create?ownerId=" + 1))
				.andExpect(status().isOk())
				.andExpect(view().name("discountVouchers/edit"))
				.andExpect(model().attributeExists("discountVoucher"))
				.andExpect(model().attributeExists("createdDate"));
	}

    @WithMockUser(username = "admin1", authorities = {"admin"})
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


    @WithMockUser(username = "admin1", authorities = {"admin"})
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
