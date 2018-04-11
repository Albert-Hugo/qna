package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.controller.ReplyController;
import com.ido.qna.entity.Reply;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.repo.ReplyRepo;
import com.rainful.dao.Sorter;
import com.rainful.dao.SqlAppender;
import com.rainful.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.util.Arrays;
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
    @Qualifier("mysqlManager")
    EntityManager em;
    @Autowired
    ZanService zanService;

    @Override
    public Page<Map<String,Object>> reply(QuestionController.ReplyReq req) {
        UserInfo u = userSer.findUser(req.getUserId());
        if(u == null){
            log.error("user not found by id {}", req.getUserId());
            return null;
        }

        replyRepo.save(Reply.builder()
                .userId(req.getUserId())
                .content(req.getContent())
                .questionId(req.getQuestionId())
                .createTime(new Date())
        .build());

        //返回最新的回复列表，这里排序根据最新事件，用用户看到自己最新的评论
        return getReply(ReplyController.ReplyListReq.builder()
                .questionId(req.getQuestionId())
                .userId(req.getUserId())
                .sorter(new Sorter(){

                    @Override
                    public boolean isDesc() {
                        return true;
                    }

                    @Override
                    public String sortField() {
                        return "createTime";
                    }
                })
                .pageable(new PageRequest(0,5))
        .build());

    }

    @Override
    public Page<Map<String,Object>> getReply(ReplyController.ReplyListReq replyReq) {
        StringBuilder sql = new StringBuilder("select r.id, r.user_id, u.nick_name as userName, u.gender " +
                " , u.avatar_url , ut.title as userTitle ,ut.title_color as titleColor" +
                ", r.content ,r.create_time " +
                "from reply r " +
                " join user_info u on u.id = r.user_id " +
                " join user_title ut on ut.id = u.title_id " +
                " where 1 = 1 ");
        List<Sorter> sorters = null;
        if(replyReq.getSorter() != null){
            sorters = Arrays.asList(replyReq.getSorter());
        }
        List<Map<String,Object>> result = new SqlAppender(em,sql)
                .and("r.question_id","question_id",replyReq.getQuestionId())
                .orderBy(sorters)
                .limit(replyReq.getPageable().getOffset(),replyReq.getPageable().getPageSize())
                .getResultList();

        //convert time format
        result.stream().forEach(r-> {
            r.put("createTime",DateUtil.toYyyyMMdd_HHmmss((Date) r.get("createTime")));
            int userId = (int) r.get("userId");
            int replyId = (int) r.get("id");
            r.put("isZaned",zanService.checkIfUserZanReply(userId,replyId));
            r.put("zanCount",zanService.countByReplyId(replyId));
        });

        int size  = getReplyCount(replyReq.getQuestionId());
        return new PageImpl<>(result,replyReq.getPageable(),size);
    }

    @Override
    public int getReplyCount(int questionId) {
        StringBuilder countSql = new StringBuilder("select count(*) from reply r where 1 = 1 ");
        int size  = new SqlAppender(em,countSql)
                .and("r.question_id","question_id",questionId)
                .count();
        return size;
    }


}
