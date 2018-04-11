package com.ido.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ido
 * Date: 2018/4/11
 **/
@Data
@Entity
@Builder
@Table(name="user_title")
@NoArgsConstructor
@AllArgsConstructor
public class UserTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer userId;
    String title;
    String titleColor;
    Date createTime;
}
