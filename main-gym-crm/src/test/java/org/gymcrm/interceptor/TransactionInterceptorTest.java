package org.gymcrm.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TransactionInterceptor interceptor;

    @Test
    void preHandle_SetsTransactionId() {
        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertNotNull(MDC.get(TransactionInterceptor.TRANSACTION_ID));
        assertEquals(8, MDC.get(TransactionInterceptor.TRANSACTION_ID).length());
    }

    @Test
    void afterCompletion_ClearsTransactionId() {
        MDC.put(TransactionInterceptor.TRANSACTION_ID, "test-id");
        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(MDC.get(TransactionInterceptor.TRANSACTION_ID));
    }
}
