package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ido
 * Date: 2018/4/10
 **/
@RestController
@RequestMapping("message")
@Slf4j
public class MessageController {
    @Autowired
    private UserMessageService messageService;
    @GetMapping("all")
    public ResponseDTO getAllMessages(){
        return ResponseDTO.succss(messageService.findAll());
    }
}
