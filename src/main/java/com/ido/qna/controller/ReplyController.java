package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.service.ReplyService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reply")
@Slf4j
public class ReplyController {
    @Autowired
    ReplyService replyService;

    @GetMapping()
    public ResponseDTO findReply(@RequestBody ReplyListReq replyReq) {
        return ResponseDTO.succss(replyService.getReply(replyReq));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyListReq {
        Integer questionId;
        Pageable pageable;
    }
}
