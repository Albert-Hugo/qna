package com.ido.qna.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.ido.qna.QnaApplication.toUpdateUserInfo;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;

/**
 * Created by ido
 * Date: 2018/1/8
 **/
@Component
public class ScheduledTask {
    private static final int SECOND = 1000;
    private static final int MINS = 60 * SECOND;
    private static final int HOUR = 60 * MINS;
    private static final int DAY = 24 * HOUR;
    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);


    @Scheduled(fixedRate = 1 * DAY)
    public void checkDate() {
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);
        if(day == 1 || day == 5){
            toUpdateUserInfo = true;
        }
    }


    public static void main(String[] agrs){
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);
        System.out.println(day+"");
    }

}
