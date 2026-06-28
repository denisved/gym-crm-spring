package org.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class M2mTokenServiceTest {

    @Test
    void testGenerateSystemToken() {
        M2mTokenService service = new M2mTokenService();
        ReflectionTestUtils.setField(service, "m2mSecret", "SuperSecretKeyForInternalMicroservicesCommunication12345!");
        
        String token = service.generateSystemToken();
        
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }
}
