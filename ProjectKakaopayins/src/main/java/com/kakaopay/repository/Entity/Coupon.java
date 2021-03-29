package com.kakaopay.repository.Entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

/**
 * 쿠폰 Entity
 * @author smilek
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="COUPON", indexes = {@Index(columnList="COUPON_CODE") })
public class Coupon implements Serializable {

	@Id
	@Column(name="NO")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long no;
	
	/** 쿠폰코드 */
	@Column(name="COUPON_CODE", columnDefinition = "CHAR(16)", nullable = false)
	private String couponCode;
	
	/** 쿠폰생성시간 */
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATE_TIME" , columnDefinition = "TIMESTAMP DEFAULT SYSTIMESTAMP")
	private Date createTime;

	/** 쿠폰소유ID */
	@Column(name="OWN_USER_ID", columnDefinition = "VARCHAR(15)")
	private String ownUserId;
	
	/**
	 * 쿠폰만료시간
	 * 쿠폰은 보통 0시 기준으로 만료되기 때문에 직관적인 DATE를 사용 
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="EXPIRATION_TIME", columnDefinition = "DATE")
	private Date expirationTime;

	/** 쿠폰사용한시간 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="USE_TIME", columnDefinition = "TIMESTAMP")
	private Date useTime;

	public Coupon() {
	}

	public Coupon(String couponCode){
		this.couponCode = couponCode;
	}
	
	public Coupon(long no, String couponCode, Date createTime, String ownUserId, Date expirationTime, Date useTime) {
		this.no = no;
		this.couponCode = couponCode;
		this.createTime = createTime;
		this.ownUserId = ownUserId;
		this.expirationTime = expirationTime;
		this.useTime = useTime;
	}

	public long getNo() {
		return no;
	}

	public void setNo(long no) {
		this.no = no;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOwnUserId() {
		return ownUserId;
	}

	public void setOwnUserId(String ownUserId) {
		this.ownUserId = ownUserId;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Date getUseTime() {
		return useTime;
	}

	public void setUseTime(Date useTime) {
		this.useTime = useTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Coupon [no=");
		builder.append(no);
		builder.append(", couponCode=");
		builder.append(couponCode);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", ownUserId=");
		builder.append(ownUserId);
		builder.append(", expirationTime=");
		builder.append(expirationTime);
		builder.append(", useTime=");
		builder.append(useTime);
		builder.append("]");
		return builder.toString();
	}
	
}
