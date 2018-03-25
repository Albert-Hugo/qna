package com.ido.qna.service;

import com.ido.qna.controller.QuestionController;
import com.ido.qna.entity.Question;
import com.ido.qna.repo.QuestionRepo;
import com.ido.qna.repo.UserInfoRepo;
import com.ido.qna.util.CacheMap;
import com.ido.qna.util.FunctionInterface;
import com.rainful.dao.SqlAppender;
import com.rainful.util.BeanUtil;
import com.rainful.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ido.qna.QnaApplication.toUpdateUserInfo;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService,FunctionInterface.BeforeCleanUp<Integer> {
    @Autowired
    QuestionRepo repo;
    @Autowired
    UserInfoRepo useRepo;
    @Autowired
    EntityManager em;
    private CacheMap<Integer> detailReadCountTable = new CacheMap<>(new ConcurrentHashMap<>(10), this);

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void beforeCleanUp(Map<Integer, Object> toRemove) {
        if(toRemove== null || toRemove.size() == 0){
            return;
        }
        List<Question> toUpdatas = new ArrayList<>(toRemove.size());
        for (Map.Entry<Integer, Object> entry : toRemove.entrySet()) {
            Integer id = entry.getKey();
            Integer readCount = (Integer) entry.getValue();
            Question q = new Question();
            q.setId(id);
            q.setReadCount(readCount);
            Question toUpdate = repo.findOne(id);
            BeanUtil.copyNonNullProperties(q, toUpdate);
            toUpdatas.add(toUpdate);

        }
        log.info("flushing read count cache to db");
        repo.save(toUpdatas);

    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void ask(QuestionController.QuestionReq req) {
        Date now = new Date();
        if (toUpdateUserInfo) {
            new SqlAppender(em)
                    .update("user_info")
                    .set("nick_name", "nickName", req.getUserBasicInfo().getNickName())
                    .set("avatar_url", "avatar", req.getUserBasicInfo().getAvatarUrl())
                    .set("gender", "gender", req.getUserBasicInfo().getGender())
                    .set("phone", "phone", req.getUserBasicInfo().getPhone())
                    .update_where_1e1()
                    .update_where_and("id", "id", req.getUserId())
                    .execute_update();
        }
        repo.save(Question.builder()
                .content(req.getContent())
                .title(req.getTitle())
                .topicId(req.getTopicId())
                .userId(req.getUserId())
                .createTime(now)
                .readCount(0)
                .updateTime(now)
                .build());
    }

    @Override
    public Page<Map<String, Object>> getLatest(Pageable pageable) {
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time,q.read_count," +
                "u.nick_name as userName , u.id as userId,  t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 order by create_time desc");
        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .limit(pageable.getOffset(), pageable.getPageSize())
                .getResultList();

        //get the latest read count from memory
        result.stream().forEach(m -> {
            Integer count = (Integer) detailReadCountTable.get((Integer) m.get("id"));
            if (count != null) {
                m.put("readCount", count);

            } else {
                return;
            }

        });

        StringBuilder countSql = new StringBuilder("select count(*) from question q " +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1");
        long total = new SqlAppender(em, countSql)
                .count();
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Map detail(int id) {
        StringBuilder sql = new StringBuilder("select q.id, q.title, q.content, q.create_time,q.read_count " +
                " ,u.nick_name as userName , u.id as userId, u.avatar_url , t.name as topicName from question q" +
                " left join user_info u on q.user_id = u.id" +
                " left join topic t on t.id = q.topic_id " +
                " where 1 = 1 ");
        List<Map<String, Object>> result = new SqlAppender(em, sql)
                .and("q.id", "id", Integer.valueOf(id))
                .getResultList();

        result.stream().forEach(r -> r.put("createTime", DateUtil.toYyyyMMdd_HHmmss((Date) r.get("createTime"))));

        if (!result.isEmpty()) {
            Map m = result.get(0);
            Integer idg = Integer.valueOf(id);
            if (detailReadCountTable.get(idg) == null) {
                detailReadCountTable.put(idg, (Integer) m.get("readCount") + 1);
            } else {
                detailReadCountTable.put(id, (Integer) detailReadCountTable.get((Integer) m.get("id")) + 1);
            }
            return m;
        }
        return null;
    }


}
