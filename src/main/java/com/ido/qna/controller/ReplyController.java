package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.service.ReplyService;
import com.rainful.dao.Sorter;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    /**
     * fetch question reply by id
     * @param
     * @return
     */
    @GetMapping()
    public ResponseDTO findReply( Integer questionId,Integer userId,Pageable pageable) {

        return ResponseDTO.succss(replyService.getReply(new ReplyListReq(questionId, userId, pageable, new Sorter() {
            @Override
            public boolean isDesc() {
                return false;
            }

            @Override
            public String sortField() {
                //TODO 添加根据 赞的数量来排序
                return null;
            }
        })));
    }




    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReplyListReq {
        Integer questionId;
        Integer userId;
        Pageable pageable;
        Sorter sorter;
    }
}
