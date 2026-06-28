package org.gymcrm.workload.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WorkloadTransactionFilter implements Filter {

    public static final String TRANSACTION_ID_KEY = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String transactionId = req.getHeader("X-Transaction-Id");

        if (transactionId != null) {
            MDC.put(TRANSACTION_ID_KEY, transactionId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
}