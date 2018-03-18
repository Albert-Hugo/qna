package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    void ask(QuestionController.QuestionReq req);

    Page<Map<String,Object>> getLatest(Pageable pageable);

    Map detail(int id);
}
