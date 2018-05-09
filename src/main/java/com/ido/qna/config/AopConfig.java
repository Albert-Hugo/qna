package com.ido.qna.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Created by ido
 * Date: 2018/1/8
 **/
@Configuration
@Aspect
public class AopConfig {
    private final Logger logger = LoggerFactory.getLogger("analytics");

    /**
     * Controller层切点
     */
    @Pointcut("within(com.ido.qna.controller..*)")
    public void controllerAspect() {
    }



    @Around("@annotation(TimeCounter)")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        logger.info("Going to call the method.:{}",pjp.getSignature().toShortString());
        Object output = pjp.proceed();
        logger.info("Method execution completed.");
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("Method execution time: " + elapsedTime + " milliseconds.");
        return output;
    }



    @AfterThrowing(
            pointcut = "controllerAspect()",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        //todo 捕捉相关的错误信息，并作出处理
        logger.info(error.getMessage(),error);
//        if (!(error instanceof BaseCustomException)) {
//            throw new UnhandleSystemException(error.toString(), error);
//        }

    }

    @Before("controllerAspect() ")
    public void beforeOp(JoinPoint joinPoint){
        logger.info("Method Name :" + joinPoint.toShortString());
        for(Object arg : joinPoint.getArgs()){
            if(arg!= null){
                logger.info("Args => "+arg.toString());
            }
        }

    }


}
