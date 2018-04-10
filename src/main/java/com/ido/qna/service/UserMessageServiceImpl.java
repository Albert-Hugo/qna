package com.ido.qna.service;

import com.ido.qna.entity.UserMessage;
import com.ido.qna.repo.UserMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ido
 * Date: 2018/4/10
 **/
@Service
public class UserMessageServiceImpl implements UserMessageService{
    @Autowired
    private UserMessageRepo messageRepo;
    @Override
    public List<UserMessage> findAll() {
        return messageRepo.findAll();
    }
}
