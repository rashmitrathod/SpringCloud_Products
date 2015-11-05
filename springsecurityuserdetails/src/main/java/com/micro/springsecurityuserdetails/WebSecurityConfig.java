package com.micro.springsecurityuserdetails;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	 protected void configure(HttpSecurity http) throws Exception {
	   http.authorizeRequests()
	  .antMatchers("/hello").access("hasRole('ROLE_ADMIN')")  
	  .anyRequest().permitAll()
	  .and()
	  		.formLogin().loginPage("/login")
	  		.usernameParameter("username").passwordParameter("password")
	  .and()
	  		.logout().logoutSuccessUrl("/login?logout") 
	  .and()
	  		.exceptionHandling().accessDeniedPage("/403")
	  .and()
	    .csrf();
	 }

	 @Autowired
	 DataSource dataSource;
	
	 /*@Autowired
	 public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		 System.out.println("inside config authentication");
	   auth.jdbcAuthentication().dataSource(dataSource)
	  .usersByUsernameQuery(
	   "select username,password, enabled from users where username=?")
	  .authoritiesByUsernameQuery(
	   "select username, role from user_roles where username=?");
	 } */
	
	
	/*@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		//auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		System.out.println("before auth");
		auth.inMemoryAuthentication().withUser("rashmit").password("cybage123").roles("admin");
		System.out.println("after auth");
	}*/
	

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/*");
	}
}