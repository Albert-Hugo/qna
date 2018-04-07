package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ZanService {
    /**
     * 点赞
     * @param req
     */
    void zan(QuestionController.ZanReq req);

    /**
     * 加载赞
     * @param questionId 问题ID
     * @param userId 当前查看页面的userId
     * @return
     */
    List loadZan(int questionId, int userId, Pageable pageable);

    /**
     * 查看用户当前用户是否对某个评论已经点赞
     * @param userId
     * @param replyId
     * @return true 为已经点赞
     */
    boolean checkIfUserZanReply(int userId,int replyId);
}
