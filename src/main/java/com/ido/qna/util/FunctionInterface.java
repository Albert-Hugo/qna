package com.ido.qna.util;

import java.util.Map;

public interface FunctionInterface {

    @FunctionalInterface
    public interface BeforeCleanUp<K>{
        public void beforeCleanUp(Map<K,Object> toRemove);
    }
}
