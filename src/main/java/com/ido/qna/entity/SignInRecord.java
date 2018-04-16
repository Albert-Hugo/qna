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
@Table(name="sign_in_record")
@NoArgsConstructor
@AllArgsConstructor
public class SignInRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer userId;
    Date signInDate;
}
