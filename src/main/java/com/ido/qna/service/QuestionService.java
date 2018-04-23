package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.request.HotQuestionReq;
import com.ido.qna.controller.request.ListQuestionReq;
import com.ido.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    Question ask(QuestionController.QuestionReq req, MultipartFile f);

//    Page<Map<String,Object>> getLatest(Pageable pageable);

    Map detail(QuestionController.DetailReq req);

    void vote(QuestionController.VoteReq req);

    /**
     *
     * @return
     */
    List<Map<String, Object>> checkQuestionsNeedToGenerateReputation();

    Page<Map<String,Object>> findQuestions(ListQuestionReq req);

    /**
     * 近期热门
     * @param req
     * @return
     */
    Page<Map<String,Object>> hotestQuestions(HotQuestionReq req);

    void delete(int userId, int questionId);


}
