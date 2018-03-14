package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.controller.service.TopicService;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Service;

@RestController
@RequestMapping("topics")
public class TopicsController {
    @Autowired
    private TopicService service;

    @Data
    @Builder
    public static class TopicsDTO{
        int id;
        String userName;
        String topic;
        String title;

    }

    @GetMapping("latest")
    public ResponseDTO latest(){
        return  ResponseDTO.succss(service.getLatest());
    }

}
