package com.ido.qna.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ido
 * Date: 2018/3/30
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class CacheMapTest {
    @Value("${filterReg}")
    String filterReg;
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

    @Test
    public void testFilter(){
       String result =  "叼你老板，习近平？fuck".replaceAll("(习近平|叼)","*");
       log.info(result);
    }


    @Test
    public void testLoadValue() throws UnsupportedEncodingException {
        log.info(filterReg);
    }
}
