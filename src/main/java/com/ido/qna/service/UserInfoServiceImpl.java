package com.ido.qna.service;

import com.ido.qna.QnaApplication;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.repo.UserInfoRepo;
import com.rainful.dao.SqlAppender;
import com.rainful.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoRepo repo;
    @Autowired
    EntityManager em;

    @Override
    public UserInfo getByUserOpenID(String id) {
        return repo.findByOpenID(id);
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
        return HashMap.<String, Object>builder()
                .put("questions", questions)
                .put("replies", replies)
                .build();
    }
}
