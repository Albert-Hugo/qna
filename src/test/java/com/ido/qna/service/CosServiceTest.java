package com.ido.qna.service;

import com.rainful.service.CosService;
import com.rainful.service.CosServiceImpl;
import org.junit.Test;

/**
 * @author ido
 * Date: 2018/5/2
 **/

public class CosServiceTest {
    CosService cosService = new CosServiceImpl();
    @Test
    public void testGetViewUrl(){
        String result = cosService.getViewUrl("lufei.jpeg");
        System.out.println(result);

    }
}
