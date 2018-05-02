package com.ido.qna.controller;

import com.ido.qna.controller.response.ResponseDTO;
import com.qcloud.cos.http.HttpMethodName;
import com.rainful.service.CosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ido
 * Date: 2018/4/24
 **/
@RestController
@RequestMapping("cos")
public class CosController {
    @Autowired
    private CosService cosService;

    @GetMapping("signature")
    @CrossOrigin(allowedHeaders = "*")
    public ResponseDTO signature(String filePath, @RequestParam(required = false) Long expiredTime, HttpMethodName method){
        return ResponseDTO.succss(cosService.getSignedStr(method,filePath,expiredTime));
    }
}
