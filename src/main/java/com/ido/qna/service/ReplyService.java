package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.ReplyController;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ReplyService {

    void reply(QuestionController.ReplyReq req);

    Page<Map<String,Object>> getReply(ReplyController.ReplyListReq replyReq);
}
