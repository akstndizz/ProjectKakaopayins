package com.kakaopay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.kakaopay.jwt.TokenProvider;
import com.kakaopay.repository.UserRepository;
import com.kakaopay.repository.Entity.User;
import com.kakaopay.repository.dto.TokenDto;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenProvider tokenProvider;

	@Override
	public User signup(String userId, String userEncPw) {

		User user = new User(userId, userEncPw, "ROLE_ADMIN");
		
		return userRepository.save(user);
	}

	@Override
	public TokenDto signin(String userId, String userPw) {

		User userDBInfo = userRepository.findByUserId(userId);
		
		if(userDBInfo == null) {
			throw new IllegalStateException("가입된 회원이 아님");
		}
		
		if( ! BCrypt.checkpw(userPw, userDBInfo.getUserPw()) ) {
			throw new IllegalStateException("PW 오류");
		}
		
		return tokenProvider.issueToken(userId, userDBInfo.getRole() );
		
	}

}
