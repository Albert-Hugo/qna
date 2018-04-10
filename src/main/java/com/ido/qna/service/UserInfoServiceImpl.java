package com.ido.qna.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ido.qna.QnaApplication;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.entity.UserMessage;
import com.ido.qna.repo.UserInfoRepo;
import com.ido.qna.repo.UserMessageRepo;
import com.ido.qna.service.domain.AddScoreParam;
import com.rainful.dao.SqlAppender;
import com.rainful.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoRepo repo;
    @Autowired
    private UserMessageRepo userMessageRepo;
    @Autowired
    private QuestionService questionService;
    @Autowired
    @Qualifier("mysqlManager")
    EntityManager em;
    LoadingCache<String,Integer> openIdToUserId = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
        @Override
        public Integer load(String s) throws Exception {
            return repo.getIdByOpenId(s);
        }
    });


    @Override
    public UserInfo getByUserOpenID(String id) {
        return repo.findByOpenID(id);
    }

    @Override
    public Integer getIdByOpenId(String id)  {
        //here to cache the login result , for frequency login will be a bottle neck for this system
        try {
            return openIdToUserId.get(id);
        } catch (ExecutionException e) {
            log.error(e.getMessage(),e);
        }catch (CacheLoader.InvalidCacheLoadException e){
            log.warn(e.getMessage());
        }
        return null;

    }


    @Override
    public UserInfo findUser(Integer id) {
        return repo.findOne(id);
    }

    @Override
    public UserInfo signUp(QnaApplication.LoginRequest req) {
        UserInfo user = req.getUserInfo();
        return repo.save(UserInfo.builder()
                .openID(user.getOpenID())
                .nickName(user.getNickName())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .province(user.getProvince())
                .score(0)
                .title("江湖小虾")
                .build());
    }

    @Override
    public Map personalInfo(Integer userId) {
        StringBuilder sql = new StringBuilder("select  q.id , q.title,q.content,q.read_count from question q  where 1 = 1 ");
        List<Map<String, Object>> questions = new SqlAppender(em, sql)
                .and("q.user_id", "userId", userId)
                .orderBy("q.update_time", true,true)
                .limit(0, 3)
                .getResultList();
        String s = "select q.title,q.id\n" +
                "from question q " +
                "join ( select r.question_id as qid  , max(r.create_time) as create_time  from reply r where r.user_id = "+userId+" group by r.question_id  ) as t1 on t1.qid = q.id " +
                " where 1 = 1 order by t1.create_time desc  \n";
        StringBuilder sql2 = new StringBuilder(s);
        List<Map<String, Object>> replies = new SqlAppender(em, sql2)
                .getResultList();
        UserInfo userInfo = repo.findOne(userId);
        return HashMap.<String, Object>builder()
                .put("questions", questions)
                .put("replies", replies)
                .put("userInfo", userInfo)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addScore(List<AddScoreParam> params ) {
        // add reputation to user
        params.forEach(param->{
            new SqlAppender(em)
                    .update("user_info")
                    .final_set("score","score",param.getScore())
                    .update_where_1e1()
                    .update_where_and("id","id",param.getUserId())
                    .execute_update();

        });


    }
}
