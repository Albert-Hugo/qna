package com.ido.qna.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author ido
 * Date: 2018/3/28
 **/
@Entity
@Builder
@Table(name="zan_record")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"id"})
public class ZanRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer replyId;
    Integer userId;


}
