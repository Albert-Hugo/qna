package com.ido.qna.service;

import com.ido.qna.QnaApplication;
import com.ido.qna.entity.UserInfo;
import com.ido.qna.entity.UserTitle;
import com.ido.qna.service.domain.AddScoreParam;

import java.util.List;
import java.util.Map;

public interface UserInfoService {
    /**
     * 根据用户openID查找
     * @param id
     * @return
     */
    UserInfo getByUserOpenID(String id);

    /**
     * 查找用户
     * @param id
     * @return
     */
    UserInfo findUser(Integer id);

    /**
     * 注册
     * @param req
     * @return
     */
    UserInfo signUp(QnaApplication.LoginRequest req);

    /**
     * 个人中心页面信息
     * @param id
     * @return
     */
    Map personalInfo(Integer id);


    Integer getIdByOpenId(String openId);

    void addScore(List<AddScoreParam> params);

    /**
     *
     * @param userId
     * @param titleId
     */
    void changeTitle(Integer userId, Integer titleId);

    /**
     *
     * @param userId
     * @param title
     */
    void createTitle(Integer userId, String title);

    /**
     *
     * @param userId
     * @return
     */
    List<UserTitle> listAllUserTitle(Integer userId);


}
