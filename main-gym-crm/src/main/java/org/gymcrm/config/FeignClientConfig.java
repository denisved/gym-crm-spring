package org.gymcrm.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.interceptor.TransactionInterceptor; 
import org.gymcrm.security.M2mTokenService;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final M2mTokenService m2mTokenService;

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            log.debug("Injecting M2M JWT token and Transaction ID into outbound Feign request");

            
            String token = m2mTokenService.generateSystemToken();
            requestTemplate.header("Authorization", "Bearer " + token);

            
            String transactionId = MDC.get(TransactionInterceptor.TRANSACTION_ID);
            if (transactionId != null) {
                requestTemplate.header("X-Transaction-Id", transactionId);
            }
        };
    }
}