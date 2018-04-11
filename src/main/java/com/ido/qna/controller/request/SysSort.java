package com.ido.qna.controller.request;

import com.rainful.dao.Sorter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liliang
 * @description:
 * @datetime 18/2/1 下午7:14
 */
@Getter
@Setter
public class SysSort implements Sorter {

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序类型
     */
    private SortEnum sortDirection;

    @Override
    public boolean isDesc() {
        return sortDirection.equals(SortEnum.DESC);
    }

    @Override
    public String sortField() {
        return sortField;
    }
}
