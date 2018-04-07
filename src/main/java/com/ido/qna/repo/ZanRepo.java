package com.ido.qna.repo;

import com.ido.qna.entity.ZanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZanRepo extends JpaRepository<ZanRecord,Integer> {
    /**
     *
     * @param userId 用户ID
     * @param replyId 评论ID
     * @return
     */
    long countByUserIdAndReplyId(int userId, int replyId);
}
