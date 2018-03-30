package com.ido.qna.util;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ido
 * Date: 2018/3/30
 **/
public class CacheMapTest {
    @Test
    public void testCacheMapBuilder() throws InterruptedException {
        CacheMap<Integer> cm = CacheMap.CacheMapBuilder.<Integer>builder()
                .map(new ConcurrentHashMap<Integer,Object>())
                .timeout(10)
                .beforeCleanUpCallBack((toRemove -> System.out.println(toRemove.get(1))))
                .build();
        cm.put(1,"this is to remove");
        Thread.sleep(1000*70);

    }
}
