package com.kakaopay.repository.Entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JWT Token에서 활용하기 위한 유저 정보 Entity
 * @author smilek
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="USER")
public class User implements Serializable {

	/** user ID */
    @Id
    @Column(name="USER_ID", columnDefinition = "VARCHAR(15)", nullable = false, unique = true)
    private String userId;

    /** user PW */
    @Column(name="USER_PW", columnDefinition = "CHAR(72)", nullable = false)
    private String userPw;

    /** user 권한 */
    @Column(name="ROLE", columnDefinition = "VARCHAR(20)", nullable = false)
    private String role;
    
    public User() {
    	
    }

	public User(String userId, String userPw, String role) {
		super();
		this.userId = userId;
		this.userPw = userPw;
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
