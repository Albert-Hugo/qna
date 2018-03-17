package com.ido.qna.repo;

import com.ido.qna.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepo extends JpaRepository<UserInfo,Integer> {
    UserInfo findByOpenID(String oid);
}
