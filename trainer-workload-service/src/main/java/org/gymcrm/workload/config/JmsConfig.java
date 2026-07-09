package org.gymcrm.workload.config;

import org.gymcrm.workload.dto.TrainerWorkloadRequest; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import jakarta.jms.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

@EnableJms
@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        
        typeIdMappings.put("org.gymcrm.dto.WorkloadRequest", TrainerWorkloadRequest.class);

        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency("3-10");

        return factory;
    }
}