package com.kakaopay.repository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakaopay.repository.Entity.Coupon;

/**
 * Coupon 관련 Repository Select Query TEST CODE 작성
 * @author smilek
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class CouponRepositoryTest {

    @Autowired
	private CouponRepository couponRepository;
    
    @BeforeEach
    void setUp() throws Exception {

    	SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	
    	//생성만 된 쿠폰_발급되지 않음
    	couponRepository.save( new Coupon(100, "AAAA0000BBBB0000", null, null, null, null) );

    	//생성만 된 쿠폰_발급되지 않음
    	couponRepository.save( new Coupon(101, "AAAA0000CCCC0000", null, null, null, null) );
    	
    	//생성 된 후 발급까지 된 쿠폰_(만기 : 2021년 4월 15일, 미사용)
    	couponRepository.save( new Coupon(102, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220415000000"), null) );
    	//생성 된 후 발급까지 된 쿠폰_(만기 : 2021년 4월 15일, 사용 : 2021년 4월 10일)
    	couponRepository.save( new Coupon(103, "AAAA0000BBBB0002", null, "smilek", dtFormat.parse("20210415000000"), dtFormat.parse("20210410000000")) );

    	//당일 만들어 지고 당일 바로 만료기간이 되는 쿠폰
    	couponRepository.save( new Coupon(104, "AAAA0000BBBB0003", null, "kakao", new Date(), null) );
    	couponRepository.save( new Coupon(105, "AAAA0000BBBB0004", null, "kakao", new Date(), null) );
    	
    	//만료일이 3일 남은 쿠폰
        Calendar c = Calendar.getInstance();
        c.setTime( new Date() );
        c.add(Calendar.DATE, 3);
        
        couponRepository.save( new Coupon(105, "AAAA0000BBBB0005", null, "kakaopay", c.getTime(), null) );
    }
    
    @Test
    void 쿠폰_유효성_검사_findByCouponCodeIsValid() throws Exception {
    	
    	//Coupon Table 에 존재하지 않는 쿠폰 _ 예상값 :null
    	Coupon coupon1 = couponRepository.findByCouponCodeIsValid("ZZZZ9999YYYY0000");
    	Assert.assertNull(coupon1);
    	
    	//Coupon Table 에 존재하고 만료기간이 지나지 않은 경우 _ 예상값 :not null 
    	Coupon coupon2 = couponRepository.findByCouponCodeIsValid("AAAA0000BBBB0000");
    	Assert.assertNotNull(coupon2);

    }
    
    @Test
    void 발급되지_않은_쿠폰_selectNotIssuedCoupon() throws Exception {
    	
    	//Coupon Table 에 존재하지 않는 쿠폰(현재 두개) _ 예상값 : ROWRUM을 이용하므로 무조건 1개
    	Coupon coupon = couponRepository.selectNotIssuedCoupon();
    	Assert.assertNotNull(coupon);
    	
    }
    
    @Test
    void 지급된_쿠폰_조회_findIssuedCouponByUserId() throws Exception {
    	
    	//userId : smilek 에 발급된 쿠폰_현재 두개(사용1, 미사용1) _ 예상값 : 2개
    	List<Coupon> coupon = couponRepository.findIssuedCouponByUserId("smilek");
    	Assert.assertEquals(2, coupon.size() );
    	
    }
    
    @Test
    void 쿠폰_1대1_검색_findByCouponCode() throws Exception {
    	
    	//Coupon Table에 존재하는 쿠폰 _ 예상값 : not null
    	Coupon coupon1 = couponRepository.findByCouponCode("AAAA0000BBBB0000");
    	Assert.assertNotNull( coupon1 );

    	//Coupon Table에 존재하지 않는 쿠폰 _ 예상값 : null
    	Coupon coupon2 = couponRepository.findByCouponCode("ZZZZ9999YYYY0000");
    	Assert.assertNull( coupon2 );
    }
    
    @Test
    void 당일_만료되는_쿠폰_조회_findTodayExpireCoupon() throws Exception {
    	
    	//userId : 당일만료되는 쿠폰 104, 105_ 예상값 : 2개
    	List<Coupon> coupon = couponRepository.findTodayExpireCoupon();
    	Assert.assertEquals(2, coupon.size() );
    	
    }
    
    @Test
    void 만료기간이_3일남은_쿠폰조회_findAfter3DaysExpireCoupon() throws Exception {
    	
    	//userId : 당일만료되는 쿠폰 104, 105_ 예상값 : 2개
    	List<Coupon> coupon = couponRepository.findAfter3DaysExpireCoupon();
    	Assert.assertEquals(1, coupon.size() );
    	
    }
}
