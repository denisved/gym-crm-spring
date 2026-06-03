package org.gymcrm.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";

    @Pointcut("within(org.gymcrm.controller..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logHttpTraffic(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUri = request != null ? request.getRequestURI() : "UNKNOWN";

        log.info("{}HTTP REQUEST: [{}] {} | Handler: {}.{}() | Payload: {}{}",
                ANSI_BLUE,
                httpMethod, requestUri,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs(),
                ANSI_RESET);

        try {
            Object result = joinPoint.proceed();

            int statusCode = 200;
            if (result instanceof ResponseEntity<?> responseEntity) {
                statusCode = responseEntity.getStatusCode().value();
            }

            log.info("{}HTTP RESPONSE: [{}] {} | Status: {} | Body: {}{}",
                    ANSI_GREEN,
                    httpMethod, requestUri, statusCode, result,
                    ANSI_RESET);

            return result;

        } catch (Exception e) {
            log.error("{}HTTP ERROR: [{}] {} | Exception: {}{}",
                    ANSI_RED,
                    httpMethod, requestUri, e.getMessage(),
                    ANSI_RESET);

            throw e;
        }
    }
}