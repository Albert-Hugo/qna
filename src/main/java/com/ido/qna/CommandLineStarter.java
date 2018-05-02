package com.ido.qna;

import com.rainful.util.DateUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.ido.qna.config.ScheduledTask.TODAY;

@Component
public class CommandLineStarter implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        TODAY = DateUtil.toYyyyMMdd(System.currentTimeMillis());
        System.out.println("initializing today string to "+TODAY);

    }
}
