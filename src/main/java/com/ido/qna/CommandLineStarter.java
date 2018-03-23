package com.ido.qna;

import com.ido.qna.service.MemoryCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineStarter implements CommandLineRunner {
    @Autowired
    MemoryCacheManager memoryCacheManager;

    @Override
    public void run(String... strings) throws Exception {


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            memoryCacheManager.cleanUp();
        }));

    }
}
