package com.ido.qna.config;

import com.ido.qna.controller.response.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 异常返回格式 {code:1000,msg:参数不正确}
 *
 * @author liliang
 * @description:
 * @datetime 18/2/1 下午1:09
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO handlerServiceOpException(RuntimeException ex) {
        return ResponseDTO.falied(ex.getMessage(), -1);
    }




}
