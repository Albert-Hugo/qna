package com.ido.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@Table(name="question")
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    private String title;
    private String content;
    private Integer topicId;
    private Integer userId;
    private Integer readCount;
    private Date createTime;
    private Date updateTime;
    /**
     * tag represent the reputation related to this question if already add to user
     */
    private Integer reputationSent;
}
