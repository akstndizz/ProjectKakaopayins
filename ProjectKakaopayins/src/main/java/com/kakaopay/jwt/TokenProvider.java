package com.kakaopay.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.kakaopay.repository.dto.TokenDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenProvider {

	Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	public final static String AUTHORIZATION_HEADER = "Authorization";		

	private final String secret = "a2FrYW9wYXlpbnNrYWthb3BheWluc2tha2FvcGF5aW5za2FrYW9wYXlpbnNrYWthb3BheWluc2tha2FvcGF5aW5z";
	
	private final long tokenValidityInMilliseconds = 1000L * 60 * 60;

	/**
	 * HTTP요청에서 토큰 취득
	 * @param request
	 * @return 토큰
	 */
	public String parseTokenString(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		
		return null;
	}
	
    /***
     * 토큰 발급
     * @param userId
     * @return 토큰
     */
	public TokenDto issueToken(String userId, String role) {
		return new TokenDto(createToken(userId, role) );
	}
	
    /***
     * 토큰 생성
     * @param userId
     * @return 토큰
     */
	private String createToken(String userId, String role) {
		
		Date now = new Date();
		
        return Jwts.builder()
        		.setSubject(userId)
        		.claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidityInMilliseconds))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
	/**
	 * Token 에서 userId 정보 찾기
	 * @param token
	 * @return
	 */
    public String getTokenOwnerId(String token) {
    	
    	System.out.println("getTokenOwnerId");
    	
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);
        
        return (String) claims.getBody().get("userId");
    }
	
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
           Arrays.stream(claims.get("role").toString().split(","))
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
     }
    
	public boolean validateToken(String token) {
		
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
		    return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature", e);
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token", e);
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token", e);
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token", e);
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty.", e);
		}
	
		return false;
	}
}
