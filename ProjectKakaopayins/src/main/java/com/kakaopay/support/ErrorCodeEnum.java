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
	
	
	E_PICKUP_NOT_ROOM("EP002","방에 속해있지 않습니다."),
	E_PICKUP_MINE("EP003","자신이 만든 뿌리기는 주울 수 없습니다."),
	E_PICKUP_TIMEOVER("EP004","줍기 허용시간이 만료되었습니다."),
	E_PICKUP_DUPLICATE("EP005","이미 주웠던 사용자 입니다."),
	E_SELECT_NULL("ES001","조회할 내용이 없습니다.");
	
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
