package com.kakaopay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kakaopay.repository.Entity.Coupon;
import com.kakaopay.service.CouponService;

@Controller
@RequestMapping(value="coupon")
public class CouponController {

	@Autowired
	private CouponService couponService;
	
	/**
	 * Question 1. 랜덤한 코드의 쿠폰 N개를 생성하여 DB 보관
	 * @param n
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="create")
	public ResponseEntity<List<Coupon>> couponCreate(int n) throws Exception {
		
		List<Coupon> couponCreateList = couponService.createCoupon(n);
		
		return ResponseEntity.ok( couponCreateList );
	}
	
	/**
	 * Question 2. 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현
	 * 인증된 사용자 && 생성된 쿠폰 중 여분이 있을 시 사용자에게 지급
	 * 가정1. 기본적으로 만료일은 30일 이후로 계산한다.
	 * @param authentication
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="issued")
	public ResponseEntity<Coupon> couponIssued(Authentication authentication) throws Exception {
		
		String userId = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
		Coupon couponIssued = couponService.couponIssued( userId );
		
		return ResponseEntity.ok( couponIssued );
	}

	/**
	 * Question 3. 사용자에게 지급된 쿠폰을 조회하는 API 구현
	 * 가정1. 인증된 사용자가 자신에게 지급된 쿠폰만을 확인 할 수 있다.
	 * 가정2. 만료된 쿠폰이더라도 조회는 모두 가능하다.
	 * @param authentication
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping(value="issued")
	public ResponseEntity<List<Coupon>> selectIssuedCoupon(Authentication authentication) throws Exception {
		
		String userId = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
		List<Coupon> couponIssued = couponService.selectIssuedCoupon( userId );
		
		return ResponseEntity.ok( couponIssued );
	}
	
	/**
	 * Question 4. 인증된 쿠폰 중 하나를 사용하는 API 구현(쿠폰 재사용 불가)
	 * 가정1. 쿠폰은 받은사람에 관계없이 누구나 사용가능 (인증이 필요없음)
	 * @param couponCode
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="use")
	public ResponseEntity<Coupon> useCoupon(String couponCode) throws Exception {
		
		Coupon useCoupon = couponService.useCoupon( couponCode );
		
		return ResponseEntity.ok( useCoupon );
	}
	
	/**
	 * Question 5. 인증된 쿠폰 중 하나를 취소하는 API 구현(취소된 쿠폰 재사용 가능)
	 * 가정1. 쿠폰을 사용한 사용처에 직접가서 환불을 받는 상황이라 가정, 누구나 취소가능 (인증이 필요없음)
	 * @param couponCode
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping(value="cancel")
	public ResponseEntity<Coupon> cancelCoupon(String couponCode) throws Exception {
		
		Coupon cancelCoupon = couponService.cancelCoupon( couponCode );
		
		return ResponseEntity.ok( cancelCoupon );
	}
	
	/**
	 * Question 6. 발급된 쿠폰 중 당일 만료된 전체 쿠폰 목록을 조회
	 * 가정1. 사용유무에 관계없이 당일 만료되는 쿠폰 모두 검색한다
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@GetMapping(value="todayExpire")
	public ResponseEntity<List<Coupon>> selectTodayExprieCoupon() {
		
		List<Coupon> couponList = couponService.selectTodayExpireCoupon();
		
		return ResponseEntity.ok( couponList ); 
	}
}
