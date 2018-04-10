package com.ido.qna.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ido
 * Date: 2018/4/10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddScoreParam {
    private Integer userId;
    private Integer score;
}
