package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.Question;
import com.ido.qna.repo.QuestionRepo;
import com.ido.qna.repo.UserInfoRepo;
import com.rainful.dao.SqlAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ido.qna.QnaApplication.toUpdateUserInfo;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionRepo repo;
    @Autowired
    UserInfoRepo useRepo;
    @Autowired
    EntityManager em;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void ask(QuestionController.QuestionReq req) {
        Date now  = new Date();
        if(toUpdateUserInfo){
            new SqlAppender(em)
                    .update("user_info")
                    .set("nick_name","nickName",req.getUserBasicInfo().getNickName())
                    .set("avatar_url","avatar",req.getUserBasicInfo().getAvatarUrl())
                    .set("gender","gender",req.getUserBasicInfo().getGender())
                    .set("phone","phone",req.getUserBasicInfo().getPhone())
                    .update_where_1e1()
                    .update_where_and("id","id",req.getUserId())
                    .execute_update();
        }
        repo.save(Question.builder()
                .content(req.getContent())
                .title(req.getTitle())
                .topicId(req.getTopicId())
                .userId(req.getUserId())
                .createTime(now)
                .updateTime(now)
        .build());
    }

    @Override
    public Page<Map<String,Object>> getLatest(Pageable pageable) {
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time," +
                "u.nick_name as userName , u.id as userId,  t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 order by create_time ");
        List<Map<String,Object>> result = new SqlAppender(em,sql)
                .limit(pageable.getOffset(),pageable.getPageSize())
        .getResultList();

        StringBuilder countSql = new StringBuilder("select count(*) from question q " +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1");
       long total = new SqlAppender(em,countSql)
                .count();
        return new PageImpl<>(result,pageable,total);
    }

    @Override
    public Map detail(int id) {
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time," +
                "u.nick_name as userName , u.id as userId, u.avatar_url , t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 ");
        List<Map<String,Object>> result = new SqlAppender(em,sql)
                .and("q.id","id",Integer.valueOf(id))
                .getResultList();
        return result.get(0);
    }
}
