package com.kakaopay.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class JwtFilter extends GenericFilterBean {

	Logger logger = LoggerFactory.getLogger( this.getClass() ); 
	
	public final static String AUTHORIZATION_HEADER = "Authorization";
	
	private TokenProvider tokenProvider;
	
	public JwtFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

		String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

		String token = null;
		
		if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
			token = authorizationHeader.substring(7);
		}

		if( token != null ) {
			Authentication authentication = tokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			System.out.println(authentication.getPrincipal());
			System.out.println(authentication.getAuthorities());
		}
		
		filterChain.doFilter(servletRequest, servletResponse);
	}


}
