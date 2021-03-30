package com.kakaopay.support;

/**
 * ErrorCode Enum 정의
 * @author smilek
 *
 */
public enum ErrorCodeEnum {
	
	E_NONE("",""),
	E_SERVER("E0001","서버 내부 오류가 발생하였습니다."),
	E_CREATE_COUPON("EC001","쿠폰 생성 중 오류가 발생하였습니다."),
	E_CREATE_COUPON_CSV_NOT_FILE("EC002","쿠폰 생성 중 오류가 발생하였습니다.(NOT CSV FILE )"),
	E_CREATE_COUPON_CSV_IO("EC002","쿠폰 생성 중 오류가 발생하였습니다.(CSV IO Exception)"),
	E_USE_COUPON_NOT_CREATE("E0001","생성되지 않은 쿠폰"),
	E_USE_COUPON_NOT_ISSUED("E0002","발급되지 않은 쿠폰"),
	E_USE_COUPON_EXPIRATION("E0003","기간이 만료된 쿠폰"),
	E_USE_COUPON_USED("E0004","이미 사용된 쿠폰"),
	E_USE_COUPON_NOT_USED("E0004","사용되지 않은 쿠폰");
	
	
	private String code;
	
	private String message;
	
	ErrorCodeEnum(String code, String message){
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
}
