package org.gymcrm.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.gymcrm.security.M2mTokenService;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FeignClientConfigTest {

    @Test
    void testFeignRequestInterceptor() {
        M2mTokenService m2mTokenService = mock(M2mTokenService.class);
        when(m2mTokenService.generateSystemToken()).thenReturn("mocked_token");

        FeignClientConfig config = new FeignClientConfig(m2mTokenService);
        RequestInterceptor interceptor = config.feignRequestInterceptor();

        RequestTemplate template = new RequestTemplate();
        MDC.put("transactionId", "12345");

        interceptor.apply(template);

        assertTrue(template.headers().containsKey("Authorization"));
        assertTrue(template.headers().get("Authorization").contains("Bearer mocked_token"));
        assertTrue(template.headers().containsKey("X-Transaction-Id"));
        assertTrue(template.headers().get("X-Transaction-Id").contains("12345"));

        MDC.clear();
    }
}
