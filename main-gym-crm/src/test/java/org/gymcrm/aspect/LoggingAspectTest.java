package org.gymcrm.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private HttpServletRequest request;

    private MeterRegistry meterRegistry;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        loggingAspect = new LoggingAspect(meterRegistry);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logHttpTraffic_Success() throws Throwable {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(Object.class);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        ResponseEntity<String> response = ResponseEntity.ok("Success");
        when(joinPoint.proceed()).thenReturn(response);

        Object result = loggingAspect.logHttpTraffic(joinPoint);

        assertEquals(response, result);
        verify(joinPoint).proceed();
        assertEquals(1, meterRegistry.timer("gym.custom.business.latency", "uri", "/api/test", "method", "POST").count());
    }

    @Test
    void logHttpTraffic_Exception() throws Throwable {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/error");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(Object.class);
        when(signature.getName()).thenReturn("errorMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        RuntimeException ex = new RuntimeException("Error");
        when(joinPoint.proceed()).thenThrow(ex);

        assertThrows(RuntimeException.class, () -> loggingAspect.logHttpTraffic(joinPoint));
        assertEquals(1, meterRegistry.counter("gym.custom.business.errors", "uri", "/api/error", "exception", "RuntimeException").count());
    }
}
