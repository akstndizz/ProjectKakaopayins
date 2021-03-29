package com.kakaopay.repository.dto;

public class TokenDto {

	private String token;

	public TokenDto(String jwt) {
		this.token = jwt;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
