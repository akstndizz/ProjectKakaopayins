package com.kakaopay.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakaopay.repository.CouponRepository;
import com.kakaopay.repository.Entity.Coupon;
import com.kakaopay.support.ErrorCodeEnum;

@Service("couponService")
public class CouponServiceImpl implements CouponService {

	Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	final int DEFAULT_COUPON_LENGTH = 16;
	
	@Autowired
	private CouponRepository couponRepository;
	
	@Transactional(rollbackOn={Exception.class})
	@Override
	public List<Coupon> createCoupon(int n) throws Exception {

		List<Coupon> couponList = new ArrayList<>();
		
		try {
			
			for(int i = 0; i < n; i++) {
				Coupon coupon = null;
				
				while(true) {
					
					coupon = new Coupon( this.makeCouponCode(DEFAULT_COUPON_LENGTH) );
					
					//Coupon 유효성검사
					//1. 중복되는 쿠폰이 있는지
					//2. 중복되는 쿠폰이 있을때 만료기간이 모두 지났는지
					Coupon validateCoupon = couponRepository.findByCouponCodeIsValid( coupon.getCouponCode() );

					if( validateCoupon == null ) {
						break;
					}
				}
				
				couponList.add( couponRepository.save( coupon ) );
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception( ErrorCodeEnum.E_CREATE_COUPON.getCode() );
		}
		
		return couponList;
	}

	/**
	 * 쿠폰 코드 생성
	 * @param couponLength 쿠폰자리수
	 * @return
	 */
	private String makeCouponCode(int couponLength) {

		if(couponLength <= 0) {
			couponLength = DEFAULT_COUPON_LENGTH;
		}
		
		Random random = new Random();

		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < couponLength; i++){
			
			switch( random.nextInt(2) ) {
			case 0 :
				sb.append((char)((int)(random.nextInt(10))+48));
				break;
			case 1 :
				sb.append((char)((int)(random.nextInt(26))+65));
				break;
			}

		}

		return sb.toString();	
	}


	@Transactional(rollbackOn={Exception.class})
	@Override
	public List<Coupon> createCouponToCSV(String csvFilePath) throws Exception {

		List<Coupon> couponList = new ArrayList<>();

		BufferedReader bufferedReader = null;
		
		try {
			File file = new File(csvFilePath);
			
			bufferedReader = new BufferedReader( new FileReader(file) );
			//파일 내 제목으로 되어 있는 첫줄 열 이름 제외(파일형식에 따라 수정)
			bufferedReader.readLine();

			String line;
		    while ((line = bufferedReader.readLine()) != null) {
		    	couponList.add( new Coupon(line) );
		    }
			
			if( ! couponList.isEmpty() ) {
				couponRepository.saveAll(couponList);
			}
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException(ErrorCodeEnum.E_CREATE_COUPON_CSV_NOT_FILE.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(ErrorCodeEnum.E_CREATE_COUPON_CSV_IO.getMessage());
		} finally {
			if( bufferedReader != null ){
				this.closeStream(bufferedReader);
			}
		}
		
		return couponList;
	}
	
	/**
	 * stream, reader 종료
	 * @param inputStream
	 * @param bufferedReader
	 */
	private void closeStream (BufferedReader bufferedReader) {
		try {
			if( bufferedReader != null ){
				bufferedReader.close();
			}	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Transactional(rollbackOn={Exception.class})
	@Override
	public Coupon couponIssued(String userId) {
		Coupon notIssuedCoupon = couponRepository.selectNotIssuedCoupon();
		
		notIssuedCoupon.setOwnUserId(userId);
		
        Calendar c = Calendar.getInstance();
        c.setTime( new Date() );
        c.add(Calendar.DATE, 30);
		
        notIssuedCoupon.setExpirationTime( c.getTime() );
		
		return couponRepository.save( notIssuedCoupon );
	}
	
	@Override
	public List<Coupon> selectIssuedCoupon(String userId) {
		return couponRepository.findIssuedCouponByUserId(userId);
	}
	

	@Override
	public Coupon useCoupon(String couponCode) {
		Coupon coupon = couponRepository.findByCouponCode(couponCode);

		if(coupon == null) {
			throw new IllegalStateException("생성되지 않은 쿠폰");
		}
		
		if(coupon.getOwnUserId() == null) {
			throw new IllegalStateException("발급되지 않은 쿠폰");
		}
		
		if(coupon.getExpirationTime().getTime() < new Date().getTime() ) {
			throw new IllegalStateException("기간이 만료된 쿠폰");
		}

		if(coupon.getUseTime() != null) {
			throw new IllegalStateException("이미 사용된 쿠폰");
		}
		
		coupon.setUseTime(new Date());
		
		return couponRepository.save( coupon );
	}

	@Override
	public Coupon cancelCoupon(String couponCode) {
		Coupon coupon = couponRepository.findByCouponCode(couponCode);
		
		if(coupon == null) {
			throw new IllegalStateException("생성되지 않은 쿠폰");
		}
		
		if(coupon.getOwnUserId() == null) {
			throw new IllegalStateException("발급되지 않은 쿠폰");
		}
		
		if(coupon.getExpirationTime().getTime() < new Date().getTime() ) {
			throw new IllegalStateException("기간이 만료된 쿠폰");
		}

		if(coupon.getUseTime() == null) {
			throw new IllegalStateException("사용되지 않은 쿠폰");
		}
		
		coupon.setUseTime(null);
		
		return couponRepository.save( coupon );
	}
	
	@Override
	public List<Coupon> selectTodayExpireCoupon() {

		List<Coupon> todayExpireCoupon = couponRepository.findTodayExpireCoupon();
		
		return todayExpireCoupon;
	}

}
