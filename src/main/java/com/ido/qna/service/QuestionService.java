package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    void ask(QuestionController.QuestionReq req, MultipartFile f);

    Page<Map<String,Object>> getLatest(Pageable pageable);

    Map detail(QuestionController.DetailReq req);

    void vote(QuestionController.VoteReq req);

    /**
     *
     * @return
     */
    List<Map<String, Object>> checkQuestionsNeedToGenerateReputation();


}
