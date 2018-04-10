package com.ido.qna.service;

import com.ido.qna.entity.UserMessage;

import java.util.List;

/**
 * @author ido
 * Date: 2018/4/10
 **/
public interface UserMessageService {

    List<UserMessage> findAll();
}
