package org.gymcrm.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LoggingAspect {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";

    private final MeterRegistry meterRegistry;

    @Pointcut("within(org.gymcrm.controller..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logHttpTraffic(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUri = request != null ? request.getRequestURI() : "UNKNOWN";

        log.info("{}[REQ_IN] HTTP REQUEST: [{}] {} | Handler: {}.{}() | Payload: {}{}",
                ANSI_BLUE, httpMethod, requestUri,
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs(), ANSI_RESET);

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();

            int statusCode = 200;
            if (result instanceof ResponseEntity<?> responseEntity) {
                statusCode = responseEntity.getStatusCode().value();

                if (statusCode >= 400) {
                    meterRegistry.counter("gym.custom.business.errors",
                            "uri", requestUri,
                            "exception", "HTTP_" + statusCode).increment();
                }
            }

            log.info("{}[RES_OUT] HTTP RESPONSE: [{}] {} | Status: {} | Body: {}{}",
                    ANSI_GREEN, httpMethod, requestUri, statusCode, result, ANSI_RESET);

            sample.stop(meterRegistry.timer("gym.custom.business.latency", "uri", requestUri, "method", httpMethod));

            return result;

        } catch (Exception e) {
            log.error("{}[ERR_SYS] HTTP ERROR: [{}] {} | Exception: {}{}",
                    ANSI_RED, httpMethod, requestUri, e.getMessage(), ANSI_RESET);

            meterRegistry.counter("gym.custom.business.errors",
                    "uri", requestUri,
                    "exception", e.getClass().getSimpleName()).increment();

            throw e;
        }
    }
}