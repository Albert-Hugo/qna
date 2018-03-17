package com.ido.qna.controller.service;

import com.ido.qna.controller.TopicsController;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
public class TopicServiceImpl implements TopicService{

    @Override
    public  List getLatest(){

        return  Arrays.asList(TopicsController.TopicsDTO.builder()
                .id(1)
                .userName("ido")
                .title("这个怎么搞")
                .topic("游戏")
                .content("昨天三大贾老师...")
                .build(),TopicsController.TopicsDTO.builder()
                .id(2)
                .title("王者通关了")
                .userName("shary")
                .content("这个把要发达了...")
                .topic("游戏")
                .build());
    }
}
