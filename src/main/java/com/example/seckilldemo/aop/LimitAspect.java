package com.example.seckilldemo.aop;


import com.alibaba.fastjson.JSONObject;
import com.example.seckilldemo.aop.annotation.Limit;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect//定义为切面
@Component
public class LimitAspect {
    private final Map<String , RateLimiter> limiterMap = Maps.newConcurrentMap();

    @Around("@annotation(com.example.seckilldemo.aop.annotation.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        Limit limit = method.getAnnotation(Limit.class);//获取到方法上的Limit注解

        if(limit != null){
            RateLimiter rateLimiter = limiterMap.get(limit.key());
            if(null == rateLimiter){
                //创建当前key的RateLimiter
                rateLimiter = RateLimiter.create(limit.permitsPerSecond(),2, TimeUnit.SECONDS);
                limiterMap.put(limit.key(),rateLimiter);
                log.info("创建了新的令牌桶，key={},容量={}",limit.key(),limit.permitsPerSecond());
            }
            log.info("limiter速率" + rateLimiter.getRate());
            boolean success = rateLimiter.tryAcquire(limit.timeOut(), limit.timeUnit());
            if(!success){
                log.info("获取令牌失败,key={}",limit.key());
                this.responseFail(limit.message());
                return null;
            }
            log.info("获取令牌成功,key={}",limit.key());
        }
        return joinPoint.proceed();
    }

    private void responseFail(String message) throws IOException {
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        JSONObject object = new JSONObject();
        object.put("success",false);
        object.put("code",2001);
        object.put("message",message);
        pw.write(object.toJSONString());
        pw.flush();
        pw.close();
    }

}
