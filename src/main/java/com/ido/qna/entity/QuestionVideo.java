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
@Table(name="question_video")
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVideo {
    @Id
    Integer questionId;
    private String videoUrl;
    private String videoPosterUrl;
    private Date createTime;

}
