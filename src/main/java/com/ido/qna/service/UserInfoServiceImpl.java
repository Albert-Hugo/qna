package com.ido.qna.service;

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
}
