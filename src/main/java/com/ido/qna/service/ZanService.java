package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ZanService {
    void zan(QuestionController.ZanReq req);

    /**
     * 加载赞
     * @param questionId 问题ID
     * @param userId 当前查看页面的userId
     * @return
     */
    List loadZan(int questionId, int userId, Pageable pageable);
}
