package com.ido.qna.repo;

import com.ido.qna.entity.QuestionLikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionLikeRecordRepo extends JpaRepository<QuestionLikeRecord,Integer> {
    QuestionLikeRecord findByUserId(Integer userId);
    int countByQuestionId(int questionId);

}
