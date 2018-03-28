package com.ido.qna.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author ido
 * Date: 2018/3/28
 **/
@Entity
@Builder
@Table(name="question_like_record")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"id","like"})
public class QuestionLikeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer questionId;
    Integer userId;
    Boolean like;

    public static void main(String[] args){
        QuestionLikeRecord q1=  QuestionLikeRecord.builder()
                .id(1)
                .userId(1)
                .questionId(1)
                .like(false)
                .build();

        QuestionLikeRecord q2=  QuestionLikeRecord.builder()
                .id(2)
                .userId(1)
                .questionId(1)
                .like(true)
                .build();

        System.out.println(q1.equals(q2));
    }





}
