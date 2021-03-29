package com.kakaopay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kakaopay.jwt.JwtAuthenticationEntryPoint;
import com.kakaopay.jwt.JwtFilter;
import com.kakaopay.jwt.TokenProvider;
import com.kakaopay.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.httpBasic().disable()
			.formLogin().disable()
			
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			
		http
			.authorizeRequests()
			//해당 URL 요청은 인증없이 접근을 허용
				.antMatchers(HttpMethod.POST,"/login/signup").permitAll()
				.antMatchers(HttpMethod.POST,"/login/signin").permitAll()
				.antMatchers(HttpMethod.POST,"/coupon/todayExpire").permitAll()
				.antMatchers(HttpMethod.POST,"/coupon/create").hasRole("ADMIN")
				.antMatchers(HttpMethod.POST,"/coupon/createCSV").hasRole("ADMIN")
				//그외 나머지 요청에 대해서는 모두 인증이 되어야 한다
				.anyRequest().authenticated();

		//인증오류 발생시 처리를 위한 HandlerO
		http
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint);
		http.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
