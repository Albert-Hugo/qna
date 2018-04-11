package com.ido.qna.controller.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    @Getter
    @Setter
    int limit = 10;

    @Getter
    @Setter
    private List<SysSort> sort;

    @Getter
    @Setter
    private int offset;
}
