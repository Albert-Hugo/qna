package com.ido.qna.controller;


import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    /**
     * 个人中兴
     * @param id 用户ID
     * @return
     */
    @GetMapping
    public ResponseDTO personalInfo(int id) {
        return ResponseDTO.succss(userInfoService.personalInfo(id));
    }
}
