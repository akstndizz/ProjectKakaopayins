package com.kakaopay.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.kakaopay.repository.CouponRepository;
import com.kakaopay.repository.Entity.Coupon;

/**
 * 쿠폰관련 스케줄링 관리 
 * @author smilek
 *
 */
@EnableScheduling
public class CouponScheduler {

	@Autowired
	private CouponRepository couponRepository;
	
	/**
	 * 선택문제_발급된 쿠폰 중 만료 3일전 사용자에게 메세지를 발송하는 기능
	 * 조건1. 발급은 되었으나 아직 사용하지 않은 쿠폰에 발송
	 * 가정1. 사용된 쿠폰 중 만료 3일 이내에 취소하는 경우도 발생할 수 있으나 해당 사항은 고려하지 않으며 딱 3일 전 기준으로만 발송
	 * 가정2. 실시간으로 보내기엔 트래픽이 많이 발생할 것으로 예상되어 점심 12시 기준으로 발송 
	 */
	@Scheduled(cron = "0 12 0 * * *")
	public void printAfter3DaysExpireCoupon() {
		List<Coupon> expireCouponList = couponRepository.findAfter3DaysExpireCoupon();
		
		System.out.println("-------------------------------------------");
		System.out.println("만료 3일전 미사용된 Coupon 목록");
		for(Coupon coupon : expireCouponList) {
			System.out.println( coupon.getCouponCode()+", OWN_USER_ID : "+ coupon.getOwnUserId() );
		}
		System.out.println("-------------------------------------------");
	}
}
