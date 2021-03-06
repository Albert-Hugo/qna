package com.ido.qna.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ido.qna.QnaApplication;
import com.ido.qna.entity.SignInRecord;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.entity.UserTitle;
import com.ido.qna.repo.SignInRecordRepo;
import com.ido.qna.repo.UserInfoRepo;
import com.ido.qna.repo.UserTitleRepo;
import com.ido.qna.service.domain.AddScoreParam;
import com.rainful.dao.SqlAppender;
import com.rainful.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
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
    UserTitleRepo userTitleRepo;

    @Autowired
    SignInRecordRepo signInRecordRepo;

    @Autowired
    @Qualifier("mysqlManager")
    EntityManager em;
    LoadingCache<String, Integer> openIdToUserId = CacheBuilder.newBuilder()
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
    public Integer getIdByOpenId(String id) {
        //here to cache the login result , for frequency login will be a bottle neck for this system
        try {
            return openIdToUserId.get(id);
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        } catch (CacheLoader.InvalidCacheLoadException e) {
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
                .titleId(1)
                .build());
    }

    @Override
    public Map personalInfo(Integer userId) {
        //获取最近用户自己发的帖子
        StringBuilder sql = new StringBuilder("select  q.id , q.title,q.content,q.read_count from question q  where 1 = 1 ");
        List<Map<String, Object>> questions = new SqlAppender(em, sql)
                .and("q.user_id", "userId", userId)
                .orderBy("updateTime", true)
                .limit(0, 3)
                .getResultList();

        //获取最近评论的信息,限制5条
        String s = "select q.title,q.id\n" +
                "from question q " +
                "join ( select r.question_id as qid  , max(r.create_time) as create_time  from reply r where r.user_id = " + userId + " group by r.question_id  ) as t1 on t1.qid = q.id " +
                " where 1 = 1 order by t1.create_time desc limit 0, 5 \n";
        StringBuilder sql2 = new StringBuilder(s);
        List<Map<String, Object>> replies = new SqlAppender(em, sql2)
                .getResultList();
        UserInfo userInfo = repo.findOne(userId);
        Map<String, Object> user =
                new ObjectMapper().convertValue(userInfo, Map.class);
        UserTitle title = userTitleRepo.findOne(userInfo.getTitleId());
        if (title != null) {
            user.put("titleColor", title.getTitleColor());
            user.put("userTitle", title.getTitle());
        }
        user.put("alreadySign",alreadySignToday(userId));
        return HashMap.<String, Object>builder()
                .put("questions", questions)
                .put("replies", replies)
                .put("userInfo", user)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addScore(List<AddScoreParam> params) {
        // add reputation to user
        params.forEach(param -> {
            new SqlAppender(em)
                    .update("user_info")
                    .final_set("score", "score", param.getScore())
                    .update_where_1e1()
                    .update_where_and("id", "id", param.getUserId())
                    .execute_update();

        });


    }


    @Override
    public void changeTitle(Integer userId, Integer titleId) {
        UserInfo userInfo = repo.findOne(userId);
        userInfo.setTitleId(titleId);
        repo.save(userInfo);

    }

    @Override
    public void createTitle(Integer userId, String title) {
        //TODO decide if user can create user defined title
        UserInfo userInfo = repo.findOne(userId);
        if (userInfo.getScore() < 1000) {
            return;
        }
        userTitleRepo.save(UserTitle.builder()
                .createTime(new Date())
                .title(title)
                .userId(userId)
                .build());


    }

    @Override
    public List<UserTitle> listAllUserTitle(Integer userId) {
        return userTitleRepo.findByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void signIn(int userId) {
        UserInfo userInfo = repo.findOne(userId);
        if(userInfo == null){
            return ;
        }
        //查看用户今天是否已经签到过
        if(alreadySignToday(userId)){
            log.info("user:{} already sign in today ",userId);
            return ;
        }
        //增加 5 声望
        userInfo.setScore(userInfo.getScore() + 5);

        repo.save(userInfo);

        signInRecordRepo.save(SignInRecord.builder()
                .userId(userId)
                .signInDate(new Date())
        .build());
    }

    @Override
    public boolean alreadySignToday(int userId) {
        Date today = new Date();
        int c = signInRecordRepo.countByUserIdAndSignInDate(userId,today);
        return c > 0;
    }
}
