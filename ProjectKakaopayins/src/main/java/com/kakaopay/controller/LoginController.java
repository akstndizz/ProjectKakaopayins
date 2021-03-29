package com.kakaopay.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakaopay.jwt.TokenProvider;
import com.kakaopay.repository.Entity.User;
import com.kakaopay.repository.dto.TokenDto;
import com.kakaopay.service.LoginService;

/**
 * JWT(Json Web Token)을 이용하여 
 * @author smilek
 *
 */
@Controller
@RequestMapping(value="login")
public class LoginController {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * 제약사항(선택)_signup 계정생성(ID, PW를 받아 저장, 패스워드는 안전한 방법으로 저장)
	 * 가정1. view가 있다고 가정, ID/PW가 null or 생성조건에 맞지 않게 되어있는 것은 화면에서 유효성 검사가 이루어진다고 가정한다.
	 * 가정2. 일반적인 회원가입처럼 view에서 onchange를 통해 ID의 유효성을 미리 확인하였다고 가정한다.
	 * 가정3. Role(권한) 은 view는 유저들을 위한(USER) 가입화면을 제공하며, 관리자는 추후 쿠폰발급회사에서 수작업으로 지정한다.
	 * @return
	 */
	@ResponseBody
	@PostMapping(value="signup")
	public ResponseEntity<User> signup(@RequestParam String userId, @RequestParam String userPw) {
		
		return ResponseEntity.ok( loginService.signup(userId, passwordEncoder.encode(userPw) ) );
	}
	
	/**
	 * 제약사항(선택)_signin 로그인(성공 시 jwt Token 발급)
	 * 
	 * @return
	 */
	@ResponseBody
	@PostMapping(value="signin")
	public ResponseEntity<TokenDto> signin(HttpServletResponse response, @RequestParam String userId, @RequestParam String userPw) {
		
		TokenDto token = loginService.signin( userId, userPw );

		response.addHeader(TokenProvider.AUTHORIZATION_HEADER, "Bearer "+token.getToken() );
        return ResponseEntity.ok( token );
	}
}
