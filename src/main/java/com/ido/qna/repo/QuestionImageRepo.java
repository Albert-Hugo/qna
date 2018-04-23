package com.ido.qna.repo;

import com.ido.qna.entity.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImageRepo extends JpaRepository<QuestionImage,Integer> {
    List<QuestionImage> findByQuestionId(Integer questionId);

}
