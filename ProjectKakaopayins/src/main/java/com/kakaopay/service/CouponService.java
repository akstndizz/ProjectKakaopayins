package com.kakaopay.service;

import java.util.List;

import com.kakaopay.repository.Entity.Coupon;

public interface CouponService {

	/**
	 * Question 1. 랜덤한 코드의 쿠폰 N개를 생성하여 DB 보관
	 * @param n, 생성할 갯수
	 * @return
	 * @throws Exception
	 */
	public List<Coupon> createCoupon(int n) throws Exception;

	/**
	 * 제약사항(선택)_쿠폰 10만개 이상 벌크 CSV Import 기능
	 * @param csvFilePath
	 * @return
	 */
	public List<Coupon> createCouponToCSV(String csvFilePath) throws Exception;

	/**
	 * 
	 * @param principal
	 * @return
	 */
	public Coupon couponIssued(String userId);

	/**
	 * 
	 * @param principal
	 * @return
	 */
	public List<Coupon> selectIssuedCoupon(String userId);

	/**
	 * 
	 * @param couponCode
	 * @return
	 */
	public Coupon useCoupon(String couponCode);

	/**
	 * 
	 * @param couponCode
	 * @return
	 */
	public Coupon cancelCoupon(String couponCode);

	/**
	 * Question 6. 발급된 쿠폰 중 당일 만료된 전체 쿠폰 목록을 조회
	 * @return
	 */
	public List<Coupon> selectTodayExpireCoupon();

}
