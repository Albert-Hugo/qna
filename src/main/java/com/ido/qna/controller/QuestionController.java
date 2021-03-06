package com.ido.qna.controller;

import com.ido.qna.controller.request.HotQuestionReq;
import com.ido.qna.controller.request.ListQuestionReq;
import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.entity.QuestionImage;
import com.ido.qna.repo.QuestionImageRepo;
import com.ido.qna.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("question")
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionServ;
    @Autowired
    ReplyService replyService;
@   Autowired
    ZanService zanService;

    @Autowired
    FileUploadService uploadService;
    @Autowired
    TopicService topicService;
    @Autowired
    QuestionImageRepo questionImageRepo;

    @GetMapping("topics")
    public ResponseDTO topics() throws IOException {
        return ResponseDTO.succss(topicService.loadTopic());
    }

    @PostMapping("upload")
    public ResponseDTO upload( Integer userId, Integer questionId,MultipartFile file) throws IOException {
        String filePath = uploadService.upload(file.getOriginalFilename(),file.getInputStream(),userId);
        questionImageRepo.save(QuestionImage.builder()
                .imgUrl(filePath)
                .questionId(questionId)
                .build());
        log.info(filePath);
        return ResponseDTO.succss("ok");
    }

    @PostMapping("ask")
    public ResponseDTO ask( QuestionReq req, MultipartFile file) {
        return ResponseDTO.succss(questionServ.ask(req,file));
    }

    @PostMapping("reply")
    public ResponseDTO reply(@RequestBody ReplyReq req) {
        //TODO add reply ui in the front page
        return ResponseDTO.succss(replyService.reply(req));
    }

    @PostMapping("zan")
    public ResponseDTO zan(@RequestBody ZanReq req) {
        //TODO add reply ui in the front page
        zanService.zan(req);
        return ResponseDTO.succss("ok");
    }


    @PostMapping("vote")
    public ResponseDTO vote(@RequestBody VoteReq req) {
        questionServ.vote(req);
        return ResponseDTO.succss("vote ok");
    }

    @PostMapping("list")
    public ResponseDTO list(@RequestBody  ListQuestionReq req) {
        return ResponseDTO.succss(questionServ.findQuestions(req));
    }

    @PostMapping("hot")
    public ResponseDTO hotQuestions(@RequestBody HotQuestionReq req) {
        return ResponseDTO.succss(questionServ.hotestQuestions(req));
    }

    @DeleteMapping("delete")
    public ResponseDTO delete(Integer userId, Integer questionId){
        questionServ.delete(userId,questionId);
        return ResponseDTO.succss(null);
    }

    @GetMapping("detail")
    public ResponseDTO detail(DetailReq req) {
        return ResponseDTO.succss(questionServ.detail(req));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailReq {
        Integer questionId;
        Integer userId;


    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = {"id"})
    public static class ZanReq {
        Integer replyId;
        Integer id;
        Integer userId;

        public ZanReq(Integer replyId, Integer userId) {
            this.replyId = replyId;
            this.userId = userId;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteReq {
        Integer questionId;
        Integer id;
        Integer userId;
        Boolean like;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionReq {
        String content;
        String title;
        Integer topicId;
        Integer userId;
        String nickName;
        String avatarUrl;
        String phone;
        byte gender;
        String country;
        String province;
        String city;
//        UserBasicInfo userBasicInfo;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyReq {
        String content;
        Integer questionId;
        Integer userId;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo {
        String nickName;
        String avatarUrl;
        String phone;
        byte gender;
        String country;
        String province;
        String city;
    }

}
