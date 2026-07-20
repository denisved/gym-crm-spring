package org.gymcrm.workload.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.service.WorkloadService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageListener {

    private final WorkloadService workloadService;
    private final JmsTemplate jmsTemplate;

    @Value("${jms.queue.workload.dlq:trainer.workload.dlq}")
    private String dlqName;

    @JmsListener(destination = "${jms.queue.workload}", containerFactory = "jmsListenerContainerFactory")
    public void receiveWorkloadMessage(@Payload TrainerWorkloadRequest request,
                                       @Header(value = "transactionId", required = false) String transactionId) {
        try {
            if (transactionId != null) {
                MDC.put("transactionId", transactionId);
            }
            log.info("Received workload message from ActiveMQ for trainer: {}", request.getUsername());

            if (request.getUsername() == null || request.getActionType() == null || request.getTrainingDuration() == null) {
                throw new IllegalArgumentException("Missing required information (username, actionType or duration)");
            }

            workloadService.processWorkload(request);
            log.info("Message processed successfully");

        } catch (IllegalArgumentException e) {
            log.error("Invalid message received. Routing to DLQ. Reason: {}", e.getMessage());
            sendToDeadLetterQueue(request, transactionId, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", e.getMessage());
            throw e;
        } finally {
            MDC.remove("transactionId");
        }
    }

    private void sendToDeadLetterQueue(TrainerWorkloadRequest request, String transactionId, String errorReason) {
        jmsTemplate.convertAndSend(dlqName, request, message -> {
            if (transactionId != null) {
                message.setStringProperty("transactionId", transactionId);
            }
            message.setStringProperty("dlqReason", errorReason);
            return message;
        });
    }
}