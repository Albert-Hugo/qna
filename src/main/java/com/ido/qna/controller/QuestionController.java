package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.controller.service.TopicService;
import com.ido.qna.service.QuestionService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionServ;

    @PostMapping("ask")
    public ResponseDTO ask(QuestionReq req) {
        questionServ.ask(req);
        log.info(req.toString());
        return ResponseDTO.succss("ok");
    }

    @GetMapping("latest")
    public ResponseDTO latest(Pageable page) {
        //TODO 处理返回值类型，加上用户名等信息
        return ResponseDTO.succss(questionServ.getLatest(page));
    }

    @Data
    public static class QuestionReq {
        String content;
        String title;
        Integer topicId;
        Integer userId;
        String nickName;
        String avatar;
        String phone;
        byte gender;
        String country;
        String province;
        String city;


    }

}
