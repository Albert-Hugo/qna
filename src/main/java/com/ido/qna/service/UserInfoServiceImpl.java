package com.ido.qna.service;

import com.ido.qna.QnaApplication;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.repo.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired UserInfoRepo repo;
    @Override
    public UserInfo getByUserOpenID(String id){
        return repo.findByOpenID(id);
    }

    @Override
    public UserInfo signUp(String openid) {
        return repo.save(UserInfo.builder()
                .openID(openid)
        .build());

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
}
