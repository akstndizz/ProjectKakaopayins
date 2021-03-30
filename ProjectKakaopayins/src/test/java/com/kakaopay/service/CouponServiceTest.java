package com.kakaopay.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakaopay.repository.CouponRepository;
import com.kakaopay.repository.Entity.Coupon;

@RunWith(SpringRunner.class)
@SpringBootTest
//@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@Ignore
public class CouponServiceTest {

	@Autowired
//	@InjectMocks
	private CouponServiceImpl couponService;
//	private CouponService couponService;

	@MockBean
//	@Mock
	private CouponRepository couponRepository;


//	@InjectMocks
//	private CouponServiceImpl couponService;

	@Test
	void 쿠폰_생성_createCoupon() throws Exception {
		//given
		int n = 1;
		BDDMockito.given( couponRepository.selectNotIssuedCoupon() ).willReturn(null);

		Coupon coupon = new Coupon(100, "AAAA0000BBBB0000", null, null, null, null);
		BDDMockito.given( couponRepository.save( coupon ) ).willReturn(coupon);
		
		//when
		List<Coupon> couponList = couponService.createCoupon(n);
		
		
		//then
		Assert.assertEquals(n, couponList.size() );
	}
	
	@Test
	void 쿠폰_발급_couponIssued() throws Exception {
		//given
		Coupon notIssuedCoupon = new Coupon(100, "AAAA0000BBBB0000", null, null, null, null);
		BDDMockito.given( couponRepository.selectNotIssuedCoupon() ).willReturn(notIssuedCoupon);
		
		String userId = "smilek";
		
        Calendar c = Calendar.getInstance();
        c.setTime( new Date() );
        c.add(Calendar.DATE, 30);
        
        notIssuedCoupon.setExpirationTime( c.getTime() );
		
        BDDMockito.given( couponRepository.save( notIssuedCoupon ) ).willReturn(notIssuedCoupon);
		
		//when
		Coupon coupon = couponService.couponIssued(userId);
		
		//then
		//쿠폰 1개가 정상적으로 나왔는지
		Assert.assertNotNull( coupon );
		//쿠폰이 정상적으로 발급되었는지
		Assert.assertEquals("smilek", coupon.getOwnUserId() );
	}
	
	@Test
	void 쿠폰_사용_useCoupon_정상사용() throws Exception {
		//given
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String couponCode = "AAAA0000BBBB0001";
		//생성 된 후 발급까지 된 쿠폰_(만기 : 2022년 4월 15일, 미사용)
    	Coupon notUseCoupon =  new Coupon(102, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220415000000"), null);
    	BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(notUseCoupon);

    	Coupon useCoupon =  new Coupon(102, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220415000000"), new Date() );
    	BDDMockito.given( couponRepository.save(useCoupon) ).willReturn(useCoupon);

//    	BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(notUseCoupon);
    	
		//when
		Coupon coupon = couponService.useCoupon(couponCode);
		
		//then
		Assert.assertNotNull( coupon.getUseTime() );
	}
	
	@Test
	void 쿠폰_사용_usedCoupon_생성되지_않은_쿠폰() throws Exception {
		//given
//		String couponCode = "AAAA0000BBBB0001";
//		
//		//when
//		Coupon coupon = couponService.useCoupon(couponCode);
//		
//		//then
//		Assert.assertThrows(null, null)   ( coupon.getUseTime() );
	}
	
	@Test
	void 쿠폰_취소_cencelCoupon_정상취소() throws Exception {
		//given
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	//생성 된 후 발급까지 된 쿠폰_(만기 : 2021년 4월 15일, 사용 : 2021년 4월 10일)
		couponRepository.save( new Coupon(103, "AAAA0000BBBB0002", null, "smilek", dtFormat.parse("20210415000000"), dtFormat.parse("20210410000000")) );
		String couponCode = "AAAA0000BBBB0002";
		
		//when
		Coupon coupon = couponService.useCoupon(couponCode);
		
		//then
		Assert.assertNull( coupon.getUseTime() );
	}
}
