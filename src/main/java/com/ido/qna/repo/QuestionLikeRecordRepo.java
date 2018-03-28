package com.ido.qna.repo;

import com.ido.qna.entity.QuestionLikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionLikeRecordRepo extends JpaRepository<QuestionLikeRecord,Integer> {
    QuestionLikeRecord findByUserIdAndQuestionId(Integer userId,Integer questionId);

    List<QuestionLikeRecord> findByQuestionId(Integer questionId);

    int countByQuestionId(int questionId);
    int countByQuestionIdAndLiked(int questionId,boolean like);

}
