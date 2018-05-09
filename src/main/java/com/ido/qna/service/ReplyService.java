package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.ReplyController;
import com.ido.qna.controller.request.CommentReplyReq;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ReplyService {

    Page<Map<String,Object>> reply(QuestionController.ReplyReq req);

    Page<Map<String,Object>> getReply(ReplyController.ReplyListReq replyReq);

    int getReplyCount(int questionId);

    void deleteByQuestionId(int questionId);

    void commentReply(CommentReplyReq req);

}
