package com.ido.qna.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class UserInfoServiceTest {
    @Autowired
    private UserInfoService service;
    @Test
    public void testSignIN(){

        service.signIn(9999);

        boolean result =service.alreadySignToday(9999);

        Assert.assertTrue(result);
    }
}
