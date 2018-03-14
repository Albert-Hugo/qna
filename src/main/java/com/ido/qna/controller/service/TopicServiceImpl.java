package com.ido.qna.controller.service;

import com.ido.qna.controller.TopicsController;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
public class TopicServiceImpl implements TopicService{
    public  List getLatest(){

        return  Arrays.asList(TopicsController.TopicsDTO.builder()
                .id(1)
                .userName("ido")
                .topic("游戏")
                .build(),TopicsController.TopicsDTO.builder()
                .id(2)
                .userName("shary")
                .topic("游戏")
                .build());
    }
}
