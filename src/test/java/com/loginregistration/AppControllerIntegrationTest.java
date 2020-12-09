package com.loginregistration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.loginregistration.controller.AppController;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AppControllerIntegrationTest {
	
	private MockMvc mockMvc;

	@InjectMocks
	private AppController appController;
	
	@Autowired
	private FilterChainProxy springSecurityFilterChain;
	
	private HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;
	
	private CsrfToken csrfToken;
	
	private String TOKEN_ATTR_NAME = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
	
	@BeforeAll
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(appController)
				.apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain)).build();
		
		httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
		csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());
	}
	
	 @Test 
	 public void shouldReturnErrorFieldsEmptyWhileLogin() throws Exception {
	   this.mockMvc.perform(post("/login").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
		.param(csrfToken.getParameterName(), csrfToken.getToken())
		.param("username", "")
		.param("password", ""))
	  .andExpect(redirectedUrl("/login?error"));
	 }
	  
	  
	  @Test 
	  public void shouldReturnErrorUsernameFieldEmptyWhileLogin() throws Exception {
		  this.mockMvc.perform(post("/login").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
			.param(csrfToken.getParameterName(), csrfToken.getToken())
			.param("username", "")
			.param("password", "password"))
		  .andExpect(redirectedUrl("/login?error"));
	  }
	 
	  @Test 
	  public void shouldReturnErrorPasswordFieldEmptyWhileLogin() throws Exception {
		  this.mockMvc.perform(post("/login").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
			.param(csrfToken.getParameterName(), csrfToken.getToken())
			.param("username", "username")
			.param("password", ""))
		  .andExpect(redirectedUrl("/login?error"));
	  }
	  
	  @Test
	  @WithMockUser(roles = "ADMIN") 
	  public void shouldReturnDashboardAdminMessageUserLoggedAsAdmin() throws Exception {
		  this.mockMvc.perform(get("/admin"))
		  			.andExpect(status().isFound())
	  				.andExpect(redirectedUrl("/dashboard"))
	  				.andExpect(MockMvcResultMatchers.flash().attribute("success","Witaj adminie!"));
	  }
	  
	  @Test
	  @WithMockUser(roles = "USER") 
	  public void shouldForwardToDeniedPageUserLoggedAsUser() throws Exception {
		  this.mockMvc.perform(get("/admin"))
		  			.andExpect(status().isForbidden())
	  				.andExpect(forwardedUrl("/denied")); 
	  }
}
