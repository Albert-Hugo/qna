package com.ido.qna.config;

import com.ido.qna.entity.Question;
import com.ido.qna.entity.UserMessage;
import com.ido.qna.repo.UserMessageRepo;
import com.ido.qna.service.QuestionService;
import com.ido.qna.service.UserInfoService;
import com.ido.qna.service.domain.AddScoreParam;
import com.rainful.dao.SqlAppender;
import com.rainful.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ido.qna.QnaApplication.toUpdateUserInfo;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;

/**
 * Created by ido
 * Date: 2018/1/8
 **/
@Component
@EnableScheduling
public class ScheduledTask {

    private final Logger log = LoggerFactory.getLogger("schedule_task");
    private static final int SECOND = 1000;
    private static final int MINS = 60 * SECOND;
    private static final int HOUR = 60 * MINS;
    private static final int DAY = 24 * HOUR;
    public static String TODAY = "";
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserMessageRepo userMessageRepo;
    @Autowired
    private UserInfoService userService;

    @Autowired
    @Qualifier("mysqlManager")
    EntityManager em;



    @Scheduled(fixedRate = 12 * HOUR)
    public void checkDate() {
        log.info("checking if to update user info when asking question");
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);
        if(day == 1 || day == 6){
            log.info("today is day {}, update user info ",day);
            toUpdateUserInfo = true;
        }else{
            log.info("today is day {}, not to update user info ",day);
        }
    }

    /**
     * 每天凌晨一点执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void setTodayString() {
        TODAY = DateUtil.toYyyyMMdd(System.currentTimeMillis());
        log.info("updating today string to "+TODAY);
    }


    @Scheduled(fixedRate =  MINS,initialDelay=MINS * 3)
    @Transactional(rollbackFor = Throwable.class)
    public void checkUserNewMessage() {
        LocalDateTime now = LocalDateTime.now();
        if(now.getHour() >3 ){
            log.info("checking if user get new message need to generate ");

            // try to do the checking during mid-night
            List<Map<String, Object>> questionList = questionService.checkQuestionsNeedToGenerateReputation();
            if(questionList== null || questionList.size() == 0){
                return ;
            }

            userService.addScore(questionList.stream().map((Map<String, Object> q) -> AddScoreParam.builder()
                    .userId((Integer) q.get("userId"))
                    .score(20)
                    .build()).collect(Collectors.toList()));

            //update question state
            questionList.forEach(q->{
                Integer qid = (int) q.get("id");
                new SqlAppender(em)
                        .update("question")
                        .final_set("reputation_sent","reputation_sent",1)
                        .update_where_1e1()
                        .update_where_and("id","id",qid)
                        .execute_update();
            });


            // store the message
            List<UserMessage> messages = new ArrayList<>(questionList.size());
            questionList.forEach(q->{
                messages.add(UserMessage.builder()
                        .content("恭喜获得20声望")
                        .title("声望 get!")
                        .userId((Integer) q.get("userId"))
                        .build());
            });
            log.info("{} new messages generated",messages.size());
            userMessageRepo.save(messages);

        }else{
            log.info(" now is {} o'clock , not the time to generate messages ",now);
        }


    }


    public static void main(String[] agrs){
        int day = LocalDateTime.now().getDayOfWeek().get(DAY_OF_WEEK);

        System.out.println(LocalDateTime.now().getHour()+"");
        System.out.println(day+"");
    }

}
