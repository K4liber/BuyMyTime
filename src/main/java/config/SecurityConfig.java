package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity 

public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	DataSource dataSource;
	
	public void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password").and()
			.authorizeRequests()
			.antMatchers(HttpMethod.POST, "/newcard").authenticated()
			.antMatchers(HttpMethod.GET, "/newcard").authenticated()
			.anyRequest().permitAll().and().formLogin();
		http.csrf().disable();
		http.sessionManagement()
			.maximumSessions(100)
			.maxSessionsPreventsLogin(false)   
			.sessionRegistry(sessionRegistry());
		
	}
	
	@Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery("select username, password, enabled from user where username=?")
			.authoritiesByUsernameQuery("select username, role from user_roles where username=?");
	}
}
