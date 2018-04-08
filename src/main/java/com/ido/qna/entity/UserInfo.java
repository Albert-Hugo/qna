package com.ido.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name="user_info")
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String openID;
    private String nickName;
    private String avatarUrl;
    private String phone;
    private byte gender;
    private String country;
    private String province;
    private String city;
    /**
     * 积分
     */
    private Integer score;
    /**
     * 称谓
     * todo 加上可以让用户用积分来自定义title 的功能
     */
    private String title;
}
