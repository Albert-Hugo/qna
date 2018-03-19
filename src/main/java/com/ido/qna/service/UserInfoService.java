package com.ido.qna.service;

import com.ido.qna.entity.UserInfo;

public interface UserInfoService {
    UserInfo getByUserOpenID(String id);

    UserInfo signUp(String openid);

    UserInfo findUser(Integer id);
}
