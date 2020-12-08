package com.loginregistration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${usersByUsernameStatement}")
	private String usersByUsernameStatement;
	
	@Value("${authoritiesByUsernameStatement}")
	private String authoritiesByUsernameStatement;
	
	private DataSource dataSource;
   
	public WebSecurityConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource((javax.sql.DataSource) dataSource).passwordEncoder(getPasswordEncoder())
		.usersByUsernameQuery(usersByUsernameStatement)
            .authoritiesByUsernameQuery(authoritiesByUsernameStatement);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/css/**").permitAll()
		.anyRequest().authenticated()
        .and()
        .formLogin()
        .defaultSuccessUrl("/dashboard", true)
        .loginPage("/login").permitAll()
        .and()
        .logout().permitAll();
	}

}

