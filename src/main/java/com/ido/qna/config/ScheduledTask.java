package com.ido.qna.config;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ScheduledTask {
    private static final int SECOND = 1000;
    private static final int MINS = 60 * SECOND;
    private static final int HOUR = 60 * MINS;
    private static final int DAY = 24 * HOUR;

    @Scheduled(fixedRate = 12 * HOUR)
    public void checkDate() {
        log.info("checking if to update user info when asking question");
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);
        if(day == 1 || day == 5){
            log.info("today is day {}, update user info ",day);
            toUpdateUserInfo = true;
        }else{
            log.info("today is day {}, not to update user info ",day);
        }
    }


    public static void main(String[] agrs){
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);
        System.out.println(day+"");
    }

}
