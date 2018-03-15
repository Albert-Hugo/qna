package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.controller.service.TopicService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {


    @PostMapping("ask")
    public ResponseDTO latest(QuestionReq req){
        log.info(req.toString());
        return  ResponseDTO.succss("ok");
    }

    @Data
    public static class QuestionReq{
        String content;
        String title;

    }

}
