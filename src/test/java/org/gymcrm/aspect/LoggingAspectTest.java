package org.gymcrm.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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

    @InjectMocks
    private LoggingAspect loggingAspect;

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

        RequestContextHolder.resetRequestAttributes();
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
        
        RuntimeException ex = new RuntimeException("Error");
        when(joinPoint.proceed()).thenThrow(ex);

        assertThrows(RuntimeException.class, () -> loggingAspect.logHttpTraffic(joinPoint));

        RequestContextHolder.resetRequestAttributes();
    }
}
