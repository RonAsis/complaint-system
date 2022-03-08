package com.craft.complaintmanagementms.web.annontation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DurationLogAspect {

    @Around(value = "@annotation(durationLogAnnotation)")
    public Object durationLog(ProceedingJoinPoint joinPoint, DurationLog durationLogAnnotation) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        long start = System.currentTimeMillis();
        try{
            log.info("In -> {}", joinPoint.getStaticPart().getSignature().toShortString());
            return joinPoint.proceed();
        }finally {
            log.info("Out -> {}, duration {} mSec", joinPoint.getStaticPart().getSignature().toShortString(),
                    System.currentTimeMillis() - start);
        }
    }
}
