package com.ido.qna.repo;

import com.ido.qna.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTitleRepo extends JpaRepository<UserTitle,Integer> {
    List<UserTitle> findByUserId(Integer userId);
    UserTitle findByUserIdAndActiveIsTrue(Integer userId);
}
