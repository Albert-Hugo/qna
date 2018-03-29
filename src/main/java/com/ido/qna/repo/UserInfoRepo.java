package com.ido.qna.repo;

import com.ido.qna.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInfoRepo extends JpaRepository<UserInfo,Integer> {
    UserInfo findByOpenID(String oid);
    @Query(value = "select u.open_id from user_info u where u = :oid",nativeQuery = true)
    Integer getIdByOpenId(@Param("oid") String oid);
}
