package com.ido.qna.controller;


import com.ido.qna.controller.response.ResponseDTO;
import com.ido.qna.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    /**
     * 个人中心
     * @param id 用户ID
     * @return
     */
    @GetMapping
    public ResponseDTO personalInfo(int id) {
        return ResponseDTO.succss(userInfoService.personalInfo(id));
    }

    /**
     *
     * @param userId
     * @param titleId
     * @return
     */
    @PostMapping("/changeTitle")
    public ResponseDTO changeTitle(int userId, int titleId) {
        userInfoService.changeTitle(userId,titleId);
        return ResponseDTO.succss(null);
    }

    /**
     *
     * @param userId
     * @param title
     * @return
     */
    @PostMapping("/createTitle")
    public ResponseDTO createTitle(int userId, String title) {
        userInfoService.createTitle(userId,title);
        return ResponseDTO.succss(null);
    }


    /**
     * 签到
     * @param userId
     * @return
     */
    @GetMapping("/signIn")
    public ResponseDTO signIn(int userId) {
        userInfoService.signIn(userId);
        return ResponseDTO.succss("sign in success");
    }

}
