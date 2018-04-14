package com.ido.qna.repo;

import com.ido.qna.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface UserInfoRepo extends JpaRepository<UserInfo,Integer> {
    UserInfo findByOpenID(String oid);
    @Query(value = "select u.id from user_info u where u.openid = :oid",nativeQuery = true)
    Integer getIdByOpenId(@Param("oid") String oid);
    Integer countByIdAndUpdateTime(int userId, Date updatedTime);
}
