package com.ido.qna.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ido
 * Date: 2018/4/11
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplyReq {
    Integer fromUserId;
    Integer toUserId;
    Integer replyId;
    String content;

}
