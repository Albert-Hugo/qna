package com.ido.qna.repo;

import com.ido.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question,Integer> {
    @Override
    Page<Question> findAll(Pageable pageable);
}
