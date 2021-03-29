package com.kakaopay.service;

import com.kakaopay.repository.Entity.User;
import com.kakaopay.repository.dto.TokenDto;

public interface LoginService {

	public User signup(String userId, String userEncPw);

	public TokenDto signin(String userId, String userEncPw);

}
