# ProjectKakaopayins
[카카오페이-보험] '보험 신규사업 개발자' 과제 전형 (Rest API 기반 쿠폰시스템 개발)

## 1. 요구사항
- 사용자에게 할인, 선물 등 쿠폰을 제공하는 서비스를 개발
- 각 문항별 요구하는 API 개발 및 Test 코드 작성

## 2. 개발 환경

- Spring Boot - STS4 4.9.0
- OS : Windows 10
- Java : 1.8

## 3. 개발 프레임워크 구성

- 개발 프레임워크 구성은 아래와 같으며, Gradle 에 의해서 자동으로 로드 및 사용하도록 되어있음
- localhost:8080 으로 H2 연결이 되어야 함

###  3.1. Json
Json 스트링을 객체화할 때 사용 

- jackson-core : 2.11.4
- jackson-databind : 2.11.4

###  3.2. Spring Boot & Spring
서버 기본 개발 환경 구성에 사용

- spring-boot-starter-web : 2.4.2.RELEASE
- spring-boot-starter-security : 2.4.2.RELEASE
- spring-boot-starter-test : 2.4.2.RELEASE

###  3.3. DB 구성 및 조작
빠른 개발환경 구축을 위한 H2 RDBMS 사용
ORM 조작환경을 위한 JPA(Hibernate)

- h2 : 1.4.200
- hibernate-core : 5.4.27.Final
- hibernate-common-annotations : 5.1.2.Final
- spring-boot-starter-data-jpa : 2.4.2.RELEASE

※ InMemory DB인 H2특성상 실행시 auto create가 될 수 있도록 환경설정 
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create

### 3.4. 그 외 
N- jjwt 0.9.1 : spring security와 연동하여 Filter 인증 개발



## 문제 해결 전략

### 공통 구성
#### 1. DB TABLE 구성 (Create 문)

거래내역 Table USER
```
CREATE TABLE USER
(
	USER_ID VARCHAR(15) NOT NULL,
	USER_PW CHAR(72) NOT NULL,
	ROLE VARCHAR(20) NOT NULL,
  CONSTRAINT PK_ACCOUNT PRIMARY KEY (USER_ID)
);
```



고객(계좌) Table ACCOUNT
```
CREATE TABLE ACCOUNT
(
	NO CHAR(8) INT AUTO_INCREMENT,
	COUPON_CODE CHAR(16) NOT NULL,
	CREATE_TIME TIMESTAMP DEFAULT SYSTIMESTAMP,
	OWN_USER_ID CHAR(15),
	EXPIRATION_TIME DATE,
	USE_TIME TIMESTAMP,
 );
```


## 서비스 구성

### jwt 인증 Filter 구현
Restful API 호출시 SecurityConfing 설정에 맞게 접속 권한 부여
권한 받은 jsonwebtoken는 HEADER에 저장되며 Coupon API 접근을 가능하게 함
권한을 부여 받지 못했을시 401, 403 오류로 응답

### 회원가입 및 로그인
#### 1. signup 계정생성(ID, PW를 받아 저장, 패스워드는 안전한 방법으로 저장) (LoginController > function signup)
* PW는 BCryptPasswordEncoder를 통해 안전하게 저장
Request :
```
POST /login/singup

userId=userId&userPw=userPw
```

Response (Success) :
```
{
    "userId": "smilek92",
    "userPw": "$2a$10$LzV2lXqRwWayxqZk8LVoSe8/I9vixT.yboB7aWvW80tLcb4Eb8CeG",
    "role": "ROLE_ADMIN"
}
```




#### 2. signin 로그인(성공 시 jwt Token 발급) (LoginController > function signin)
* () PW는 BCryptPasswordEncoder을 통해 DB에 저장한 값에 대한 BCrypt.checkpw 를 통해 검증
Request :
```
POST /login/singin

userId=userId&userPw=userPw
```

Response (Success) :
```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzbWlsZWs5MiIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNjE3MDMwNDU4LCJleHAiOjE2MTcwMzQwNTh9.HzdZxN8YfQWvFvTBG8J5UiP8pzbMlin-3NhDHUYp3upuaxgvktq2wr25oWurVuLYWhih5QjJPqgd8_AET5FOVw"
}
```


### 쿠폰 서비스
#### 1. 랜덤한 코드의 쿠폰 N개를 생성하여 DB 보관 (CouponController > function couponCreate)
* (제약사항) 100억개 이상의 쿠폰관리 가능
* 쿠폰은 숫자 0~9, 영어 A-Z로 이루어진 16자리의 쿠폰으로 36^16개가 가능하며 기간만료 쿠폰이 있을 경우는 중복이 가능
Request :

```
POST /coupon/create

Authorization : Bearer $soij320r9j230...

n=3
```

Response (Success) :
```
[
    {
        "no": 1,
        "couponCode": "4L4N186K89VB1H98",
        "createTime": "2021-03-29T15:07:58.154+00:00",
        "ownUserId": null,
        "expirationTime": null,
        "useTime": null
    },
    {
        "no": 2,
        "couponCode": "Q999A10F70U71UA7",
        "createTime": "2021-03-29T15:07:58.163+00:00",
        "ownUserId": null,
        "expirationTime": null,
        "useTime": null
    },
    {
        "no": 3,
        "couponCode": "M277NFVRJ17KW430",
        "createTime": "2021-03-29T15:07:58.163+00:00",
        "ownUserId": null,
        "expirationTime": null,
        "useTime": null
    }
]
```

#### 1-1.(제약사항) 10만개 이상의 쿠폰 Create, CSV Import 구현 (CouponService > function couponCreateToCSV)

#### 2. 생성된 쿠폰중 하나를 사용자에게 지급하는 API를 구현 (CouponController > function couponIssued)
* 인증된 사용자 && 생성된 쿠폰 중 여분이 있을 시 사용자에게 지급
* 가정1. 기본적으로 만료일은 30일 이후로 계산한다.

Request :
```
POST /coupon/issued

Authorization : Bearer $soij320r9j230...
```

Response (Success) :
```
{
    "no": 1,
    "couponCode": "4L4N186K89VB1H98",
    "createTime": "2021-03-29T15:07:58.154+00:00",
    "ownUserId": "smilek92",
    "expirationTime": 2021-04-29T16:01:22.154+00:00,
    "useTime": null
}
```


#### 3. 사용자에게 지급된 쿠폰을 조회하는 API 구현 (CouponController > function selectIssuedCoupon)
* 가정1. 인증된 사용자가 자신에게 지급된 쿠폰만을 확인 할 수 있다.
* 가정2. 만료된 쿠폰이더라도 조회는 모두 가능하다.

Request :
```
GET /coupon/issued

Authorization : Bearer $soij320r9j230...
```

Response (Success) :
```
[
    {
        "no": 1,
        "couponCode": "4L4N186K89VB1H98",
        "createTime": "2021-03-29T15:07:58.154+00:00",
        "ownUserId": "smilek92",
        "expirationTime": 2021-04-29T16:01:22.154+00:00,
        "useTime": null
    }
]
```


#### 4. 인증된 쿠폰 중 하나를 사용하는 API 구현(쿠폰 재사용 불가) (CouponController > function useCoupon)
* 가정1. 쿠폰은 받은사람에 관계없이 누구나 사용가능 (인증이 필요없음)

Request :
```
POST /coupon/use

Authorization : Bearer $soij320r9j230...

couponCode=4L4N186K89VB1H98
```

Response (Success) :
```
[
    {
        "no": 1,
        "couponCode": "4L4N186K89VB1H98",
        "createTime": "2021-03-29T15:07:58.154+00:00",
        "ownUserId": "smilek92",
        "expirationTime": 2021-04-29T16:01:22.154+00:00,
        "useTime": 2021-03-29T16:02:44.154+00:00
    }
]
```

#### 5. 인증된 쿠폰 중 하나를 취소하는 API 구현(취소된 쿠폰 재사용 가능) (CouponController > function cancelCoupon)
* 가정1. 쿠폰을 사용한 사용처에 직접가서 환불을 받는 상황이라 가정, 누구나 취소가능 (인증이 필요없음)

Request :
```
POST /coupon/cancel

Authorization : Bearer $soij320r9j230...

couponCode=4L4N186K89VB1H98
```

Response (Success) :
```
[
    {
        "no": 1,
        "couponCode": "4L4N186K89VB1H98",
        "createTime": "2021-03-29T15:07:58.154+00:00",
        "ownUserId": "smilek92",
        "expirationTime": 2021-04-29T16:01:22.154+00:00,
        "useTime": null
    }
]
```

#### 6. 발급된 쿠폰 중 당일 만료된 전체 쿠폰 목록을 조회 (CouponController > function selectTodayExprieCoupon)
* 가정1. 사용유무에 관계없이 당일 만료되는 쿠폰 모두 검색한다

Request :
```
GET /coupon/todayExpire
```

Response (Success) :
```
[
    {
        "no": 1,
        "couponCode": "4L4N186K89VB1H98",
        "createTime": "2021-03-29T15:07:58.154+00:00",
        "ownUserId": "smilek92",
        "expirationTime": 2021-04-29T16:01:22.154+00:00,
        "useTime": null
    }
]
```

#### 7. (선택) 발급된 쿠폰 중 만료 3일전 사용자에게 메세지를 발송하는 기능 (CouponScheduler > function selectTodayExprieCoupon)
* 조건1. 발급은 되었으나 아직 사용하지 않은 쿠폰에 발송
* 가정1. 사용된 쿠폰 중 만료 3일 이내에 취소하는 경우도 발생할 수 있으나 해당 사항은 고려하지 않으며 딱 3일 전 기준으로만 발송
* 가정2. 실시간으로 보내기엔 트래픽이 많이 발생할 것으로 예상되어 점심 12시 기준으로 발송 




## 테스트
- "src/test/java/com/kakaopay/repository/CouponRepositoryTest.java"
- - "src/test/java/com/kakaopay/service/CouponServiceTest.java"


