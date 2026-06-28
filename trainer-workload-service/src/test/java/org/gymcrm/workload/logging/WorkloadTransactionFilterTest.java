package org.gymcrm.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class WorkloadTransactionFilterTest {

    private WorkloadTransactionFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new WorkloadTransactionFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void doFilter_WithTransactionIdHeader_ShouldSetMdcAndCallChain() throws IOException, ServletException {
        // Arrange
        String transactionId = "test-txn-123";
        when(request.getHeader("X-Transaction-Id")).thenReturn(transactionId);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        // Because MDC is cleared in the finally block, it should be null here
        assertNull(MDC.get(WorkloadTransactionFilter.TRANSACTION_ID_KEY));
    }

    @Test
    void doFilter_WithTransactionIdHeader_MdcIsAvailableDuringChain() throws IOException, ServletException {
        // Arrange
        String transactionId = "test-txn-123";
        when(request.getHeader("X-Transaction-Id")).thenReturn(transactionId);
        
        doAnswer(invocation -> {
            assertEquals(transactionId, MDC.get(WorkloadTransactionFilter.TRANSACTION_ID_KEY));
            return null;
        }).when(filterChain).doFilter(request, response);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithoutTransactionIdHeader_ShouldCallChainWithoutMdc() throws IOException, ServletException {
        // Arrange
        when(request.getHeader("X-Transaction-Id")).thenReturn(null);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get(WorkloadTransactionFilter.TRANSACTION_ID_KEY));
    }
}
