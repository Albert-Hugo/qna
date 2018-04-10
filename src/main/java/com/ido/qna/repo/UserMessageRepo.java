package com.ido.qna.repo;

import com.ido.qna.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepo extends JpaRepository<UserMessage,Integer> {
}
