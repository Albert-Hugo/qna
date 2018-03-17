package com.ido.qna.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO {
    private Object data;
    private String msg;
    private int code;
    public static ResponseDTO succss(Object d){

        return ResponseDTO.builder()
                .data(d)
                .build();
    }

    public static ResponseDTO falied(String msg,int code){

        return ResponseDTO.builder()
                .msg(msg)
                .code(code)
                .build();
    }
}
