package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.controller.service.TopicService;
import com.ido.qna.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionServ;

    @PostMapping("ask")
    public ResponseDTO ask(@RequestBody QuestionReq req) {
        questionServ.ask(req);
        log.info(req.toString());
        return ResponseDTO.succss("ok");
    }

    @GetMapping("latest")
    public ResponseDTO latest(Pageable page) {
        return ResponseDTO.succss(questionServ.getLatest(page));
    }

    @GetMapping("detail")
    public ResponseDTO detail(int id) {
        return ResponseDTO.succss(questionServ.detail(id));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReq {
        String content;
        String title;
        Integer topicId;
        Integer userId;
        UserBasicInfo userBasicInfo;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo{
        String nickName;
        String avatarUrl;
        String phone;
        byte gender;
        String country;
        String province;
        String city;
    }

}
