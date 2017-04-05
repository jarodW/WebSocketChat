package com.jarod.chat.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.jarod.chat.models.User;
import com.jarod.chat.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	UserRepository userRepository;
	
	@Autowired
	public void setWebSecurityConfig(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.formLogin()
				.loginPage("/index.html")
				.defaultSuccessUrl("/chat.html")
				.permitAll()
				.and()
			.logout()
				.logoutSuccessUrl("/index.html")
				.permitAll()
				.and()
			.authorizeRequests()
				.antMatchers("/js/**", "/lib/**", "/images/**", "/css/**", "/index.html","/signup.html", "/register", "/").permitAll()
				.antMatchers("/websocket").hasRole("ADMIN")
				.anyRequest().authenticated();
				
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
				
		auth.authenticationProvider(new AuthenticationProvider() {
			
			@Override
			public boolean supports(Class<?> authentication) {
				return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
			}
			
			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			    List list = userRepository.findByUsername(authentication.getName());
			    if(list.isEmpty()){
			    	 throw new BadCredentialsException("1000");
			    }
			    User user = (User) list.get(0);
				String username = authentication.getName();
			    String password = (String) authentication.getCredentials();
			    if(encoder.matches(password, user.getPassword()) == false){
			    	throw new BadCredentialsException("1000");
			    }
			   
				List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER") ;
														
				return new UsernamePasswordAuthenticationToken(username, password, authorities);
			}
		});
	}
}
