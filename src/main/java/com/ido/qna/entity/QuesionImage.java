package com.ido.qna.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name="question")
@NoArgsConstructor
@AllArgsConstructor
public class QuesionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer questionId;
    private String imgUrl;

}
