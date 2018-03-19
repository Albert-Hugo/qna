package com.ido.qna.repo;

import com.ido.qna.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepo extends JpaRepository<Reply,Integer> {
}
