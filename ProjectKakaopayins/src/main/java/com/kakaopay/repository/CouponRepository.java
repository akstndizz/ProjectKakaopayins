package com.kakaopay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kakaopay.repository.Entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>{

	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON "+
			"WHERE 1=1 "+
				"AND COUPON_CODE = :couponCode "+
				"AND (EXPIRATION_TIME IS NULL OR EXPIRATION_TIME < CURRENT_DATE )", nativeQuery= true)
	public Coupon findByCouponCodeIsValid(@Param("couponCode") String couponCode);

	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON " +
			"WHERE 1=1 "+
				"AND EXPIRATION_TIME IS NULL "+
				"AND ROWNUM <= 1 "+
			"ORDER BY RAND()", nativeQuery= true)
	public Coupon selectNotIssuedCoupon();

	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON " +
			"WHERE 1=1 "+
				"AND EXPIRATION_TIME IS NOT NULL "+
				"AND OWN_USER_ID = :userId ", nativeQuery= true)
	public List<Coupon> findIssuedCouponByUserId(@Param("userId") String userId);

	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON "+
			"WHERE 1=1 "+
				"AND COUPON_CODE = :couponCode ", nativeQuery= true)
	public Coupon findByCouponCode(@Param("couponCode") String couponCode);

	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON "+
			"WHERE 1=1 "+
				"AND TO_CHAR(EXPIRATION_TIME,'YYYYMMDD') = TO_CHAR(CURRENT_DATE,'YYYYMMDD') ", nativeQuery= true)
	public List<Coupon> findTodayExpireCoupon();
	
	@Query(value =
			"SELECT NO, COUPON_CODE, CREATE_TIME, OWN_USER_ID, EXPIRATION_TIME, USE_TIME "+
			"FROM COUPON "+
			"WHERE 1=1 "+
				"AND USE_TIME IS NULL "+
				"AND TO_CHAR(EXPIRATION_TIME,'YYYYMMDD') = TO_CHAR(CURRENT_DATE+3,'YYYYMMDD') ", nativeQuery= true)	
	public List<Coupon> findAfter3DaysExpireCoupon();

}
