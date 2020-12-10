package com.loginregistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import com.loginregistration.model.Token;
import com.loginregistration.model.Users;
import com.loginregistration.repository.TokenRepo;
import com.loginregistration.repository.UserRepo;
import com.loginregistration.service.UserService;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AppControllerIntegrationTest {
	
	private MockMvc mockMvc;

	@InjectMocks
	private AppController appController;
	
	@Mock
	private UserRepo userRepo;
	
	@Mock
	private TokenRepo tokenRepo;
	
	@Mock
	private UserService userService;
	
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
	  
		@Test
		public void shouldReturnErrorFieldUsernameEmpty() throws Exception {
			this.mockMvc
					.perform(post("/signup").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
							.param(csrfToken.getParameterName(), csrfToken.getToken())
							.param("username", "")
							.param("password", "password")
							.param("email", "user@mail.com"))
					.andExpect(model().attributeHasFieldErrors("user", "username"))
					.andExpect(forwardedUrl("register"));
		}
		
		@Test
		public void shouldReturnErrorFieldPasswordEmpty() throws Exception {
			this.mockMvc
					.perform(post("/signup").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
							.param(csrfToken.getParameterName(), csrfToken.getToken())
							.param("username", "username")
							.param("password", "")
							.param("email", "user@mail.com"))
					.andExpect(model().attributeHasFieldErrors("user", "password"))
					.andExpect(forwardedUrl("register"));
		}
		
		@Test
		public void shouldReturnErrorFieldEmailEmpty() throws Exception {
			this.mockMvc
					.perform(post("/signup").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
							.param(csrfToken.getParameterName(), csrfToken.getToken())
							.param("username", "username")
							.param("password", "password")
							.param("email", ""))
					.andExpect(model().attributeHasFieldErrors("user", "email"))
					.andExpect(forwardedUrl("register"));
		}
		
		@Test
		public void shouldReturnErrorUsernameIsTaken() throws Exception {
			Users user = new  Users();
			user.setUsername("username");
			Optional<Users> optionalUser = Optional.ofNullable(user);
			
			when(userRepo.findUserByUsernameIgnoreCase("username")).thenReturn(optionalUser);
			
			this.mockMvc
					.perform(post("/signup").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
							.param(csrfToken.getParameterName(), csrfToken.getToken())
							.param("username", "username")
							.param("password", "password")
							.param("email", "user@mail.com"))
					.andExpect(MockMvcResultMatchers.flash().attribute("danger", "Login zajęty!"))
					.andExpect(redirectedUrl("/register"));
		}
		
		@Test
		public void shouldReturnCorrectlyCreatedAccountInformation() throws Exception {
			Users user = new  Users();
			user.setUsername("username");
			user.setPassword("password");
			user.setEmail("user@mail.com");
			Optional<Users> optionalUser = Optional.empty();
					
			when(userRepo.findUserByUsernameIgnoreCase("username")).thenReturn(optionalUser);
			
			when(userRepo.save(any(Users.class)))
	        .thenAnswer(i -> i.getArguments()[0]);
			
			
			this.mockMvc
					.perform(post("/signup").sessionAttr(TOKEN_ATTR_NAME, csrfToken)
							.param(csrfToken.getParameterName(), csrfToken.getToken())
							.param("username", "username")
							.param("password", "password")
							.param("email", "user@mail.com"))
					.andExpect(MockMvcResultMatchers.flash().attribute("success", "Konto utworzono pomyślnie! Sprawdź skrzynkę pocztową w celu aktywacji konta"))
					.andExpect(redirectedUrl("/register"));
		}
		
		@Test
		public void shouldReturnErrorTokenDoesNotExist() throws Exception {
			Optional<Token> optionalToken = Optional.empty();
			
			when(tokenRepo.findByValue("ec2e8c0c-e93c-4aec-8cfe-2c5cbd376bb8")).thenReturn(optionalToken);

			this.mockMvc
					.perform(get("/token")
							.param("value", "ec2e8c0c-e93c-4aec-8cfe-2c5cbd376bb8"))
					.andExpect(MockMvcResultMatchers.flash().attribute("danger", "Wystąpił problem! Upewnij się, że podajesz prawidłowy token"))
					.andExpect(redirectedUrl("/login"));
		}
		
		@Test
		public void shouldReturnCorrectlyActivatedAccountInformation() throws Exception {
			Users user = new Users();
			user.setUsername("username");
			user.setPassword("password");
			user.setEmail("user@mail.com");
			Token token = new Token();
			token.setValue("ec2e8c0c-e93c-4aec-8cfe-2c5cbd376bb8");
			token.setUser(user);
			Optional<Token> optionalToken = Optional.ofNullable(token);
			
			when(tokenRepo.findByValue("ec2e8c0c-e93c-4aec-8cfe-2c5cbd376bb8")).thenReturn(optionalToken);

			this.mockMvc
					.perform(get("/token")
						 .param("value", "ec2e8c0c-e93c-4aec-8cfe-2c5cbd376bb8"))
						 .andExpect(MockMvcResultMatchers.flash().attribute("success", "Konto zweryfikowano pomyślnie! Możesz się teraz zalogować"))
					     .andExpect(redirectedUrl("/login"));
		}
		
		  @Test 
		  public void shouldForwardToLoginPageUserNotLogged() throws Exception {
		  this.mockMvc.perform(get("/admin"))
		  				.andExpect(status().isFound())
		  				.andExpect(redirectedUrl("http://localhost/login"));
		  
		  this.mockMvc.perform(get("/dashboard"))
		  				.andExpect(status().isFound())
		  				.andExpect(redirectedUrl("http://localhost/login")); 
		  }
}
