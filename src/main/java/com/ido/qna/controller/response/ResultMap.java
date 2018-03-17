package com.ido.qna.controller.response;

import java.util.HashMap;

public class ResultMap {
    private HashMap<String,Object> result;
    private ResultMap(){
        this.result = new HashMap<>(5);
    }

    public static ResultMap resultMap(){
        return new ResultMap();
    }

    public ResultMap put(String  key , Object val){
        result.put(key,val);
        return this;
    }

    public HashMap<String,Object> build(){
        return this.result;
    }
}
