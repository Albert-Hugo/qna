package com.ido.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ido
 * Date: 2018/4/10
 **/
@Data
@Entity
@Builder
@Table(name="user_message")
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String title;
    private String content;
    private Integer userId;
    private Date createTime;
}
