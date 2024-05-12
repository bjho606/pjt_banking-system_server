package com.ssafy.dongsanbu;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopConfig{
    private static final int MAX_RETRIES = 1000;
    private static final int RETRY_DELAY_MS = 100;
    private static final Logger logger = LoggerFactory.getLogger(AopConfig.class);

    @Pointcut("execution(* com.ssafy.dongsanbu.domain.point.service.*(..))")
    public void retry() {
    }

    @Around("retry()")
    public Object retryOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Exception exceptionHolder = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.println("retrying..");
                return joinPoint.proceed();
            } catch (RuntimeException e) {
                exceptionHolder = e;
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw exceptionHolder;
    }

}