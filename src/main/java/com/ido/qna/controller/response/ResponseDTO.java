package com.ido.qna.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDTO {
    private Object data;
    private String msg;
    public static ResponseDTO succss(Object d){

        return ResponseDTO.builder()
                .data(d)
                .build();
    }
}
