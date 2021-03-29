package com.kakaopay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kakaopay.repository.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query(value =
			"SELECT "+
				"USER_ID "+
				", USER_PW "+
				", ROLE "+
			"FROM USER "+
			"WHERE 1=1 "+
				"AND USER_ID = :userId ", nativeQuery= true)
	User findByUserId(@Param("userId") String userId);
}