package com.kakaopay.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakaopay.repository.CouponRepository;
import com.kakaopay.repository.Entity.Coupon;
import com.kakaopay.support.ErrorCodeEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponServiceTest {

	@Autowired
	private CouponServiceImpl couponService;

	@MockBean
	private CouponRepository couponRepository;

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
    	BDDMockito.given( couponRepository.save(notUseCoupon) ).willReturn(useCoupon);

		//when
		Coupon coupon = couponService.useCoupon(couponCode);
		
		//then
		Assert.assertNotNull( coupon.getUseTime() );
	}
	
	@Test
	void 쿠폰_사용_useCoupon_Exception_생성되지_않은_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.useCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_NOT_CREATE.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_사용_useCoupon_Exception_발급되지_않은_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, null, null, null);
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.useCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_NOT_ISSUED.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_사용_useCoupon_Exception_기간이_만료된_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//쿠폰사용기간(2020년 1월 1일)
		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20000101000000"), null);
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.useCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_EXPIRATION.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_사용_useCoupon_Exception_이미_사용된_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//쿠폰사용기간(2022년 1월 1일), 즉시사용
		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220101000000"), new Date() );
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.useCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_USED.getMessage(), exception.getMessage());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	void 쿠폰_취소_cancelCoupon_정상취소() throws Exception {
		//given
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String couponCode = "AAAA0000BBBB0001";

    	Coupon useCoupon =  new Coupon(102, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220415000000"), new Date() );
    	BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(useCoupon);
		
		//생성 된 후 발급까지 된 쿠폰_(만기 : 2022년 4월 15일, 미사용)
    	Coupon notUseCoupon =  new Coupon(102, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220415000000"), null);
    	BDDMockito.given( couponRepository.save(useCoupon) ).willReturn(notUseCoupon);

		//when
		Coupon coupon = couponService.cancelCoupon(couponCode);
		
		//then
		Assert.assertNull( coupon.getUseTime() );
	}
	
	@Test
	void 쿠폰_취소_cancelCoupon_Exception_생성되지_않은_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.cancelCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_NOT_CREATE.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_취소_cancelCoupon_Exception_발급되지_않은_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, null, null, null);
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.cancelCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_NOT_ISSUED.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_취소_cancelCoupon_Exception_기간이_만료된_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//쿠폰사용기간(2020년 1월 1일)
		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20000101000000"), null);
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.cancelCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_EXPIRATION.getMessage(), exception.getMessage());
	}
	
	@Test
	void 쿠폰_취소_cancelCoupon_Exception_사용하지_않은_쿠폰() throws Exception {
		//given
		String couponCode = "AAAA0000BBBB0001";

		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//쿠폰사용기간(2022년 1월 1일), 즉시사용
		Coupon coupon = new Coupon(100, "AAAA0000BBBB0001", null, "smilek", dtFormat.parse("20220101000000"), null );
		BDDMockito.given( couponRepository.findByCouponCode(couponCode) ).willReturn(coupon);
		//when
		Exception exception = Assert.assertThrows(IllegalStateException.class, () -> {
			couponService.cancelCoupon(couponCode);
		});

		//then
		Assert.assertEquals(ErrorCodeEnum.E_USE_COUPON_NOT_USED.getMessage(), exception.getMessage());
	}
}
