package org.gymcrm.workload.config;

import jakarta.jms.ConnectionFactory;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JmsConfigTest {

    private JmsConfig jmsConfig;

    @Mock
    private ConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        jmsConfig = new JmsConfig();
    }

    @Test
    void testJacksonJmsMessageConverter() {
        MessageConverter converter = jmsConfig.jacksonJmsMessageConverter();

        assertNotNull(converter);
        assertTrue(converter instanceof MappingJackson2MessageConverter);

        MappingJackson2MessageConverter mappingConverter = (MappingJackson2MessageConverter) converter;
        assertNotNull(mappingConverter);
    }

    @Test
    void testJmsListenerContainerFactory() {
        MessageConverter converter = jmsConfig.jacksonJmsMessageConverter();
        DefaultJmsListenerContainerFactory factory = jmsConfig.jmsListenerContainerFactory(connectionFactory, converter);

        assertNotNull(factory);
    }
}
