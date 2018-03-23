package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.ReplyController;
import com.ido.qna.entity.Reply;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.repo.ReplyRepo;
import com.rainful.dao.SqlAppender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReplyServiceImpl implements ReplyService {
    @Autowired
    ReplyRepo replyRepo;
    @Autowired
    UserInfoService userSer;

    @Autowired
    EntityManager em;

    @Override
    public void reply(QuestionController.ReplyReq req) {
        UserInfo u = userSer.findUser(req.getUserId());
        if(u == null){
            log.error("user not found by id {}", req.getUserId());
            return ;
        }

        replyRepo.save(Reply.builder()
                .userId(req.getUserId())
                .content(req.getContent())
                .questionId(req.getQuestionId())
                .createTime(new Date())
        .build());

    }

    @Override
    public Page<Map<String,Object>> getReply(ReplyController.ReplyListReq replyReq) {
        StringBuilder sql = new StringBuilder("select r.id, r.user_name, r.content  from reply r where 1 = 1 ");
        List<Map<String,Object>> result = new SqlAppender(em,sql)
                .and("r,question_id","question_id",replyReq.getQuestionId())
                .limit(replyReq.getPageable().getOffset(),replyReq.getPageable().getPageSize())
                .getResultList();

        int size  = getReplyCount(replyReq.getQuestionId());
        return new PageImpl<>(result,replyReq.getPageable(),size);
    }

    @Override
    public int getReplyCount(int questionId) {
        StringBuilder countSql = new StringBuilder("select count(*) from reply r where 1 = 1 ");
        int size  = new SqlAppender(em,countSql)
                .and("r,question_id","question_id",questionId)
                .count();
        return size;
    }
}
