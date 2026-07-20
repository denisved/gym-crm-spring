package org.gymcrm.workload.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.gymcrm.workload.dto.ActionType;
import org.gymcrm.workload.dto.TrainerWorkloadRequest;
import org.gymcrm.workload.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageListenerTest {

    @Mock
    private WorkloadService workloadService;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private WorkloadMessageListener listener;

    @Captor
    private ArgumentCaptor<MessagePostProcessor> postProcessorCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "dlqName", "trainer.workload.dlq");
    }

    @Test
    void testReceiveWorkloadMessage_Success() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("testuser");
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(60);

        String transactionId = "txn-123";

        assertDoesNotThrow(() -> listener.receiveWorkloadMessage(request, transactionId));

        verify(workloadService).processWorkload(request);
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void testReceiveWorkloadMessage_SuccessNoTransactionId() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("testuser");
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(60);

        assertDoesNotThrow(() -> listener.receiveWorkloadMessage(request, null));

        verify(workloadService).processWorkload(request);
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void testReceiveWorkloadMessage_MissingUsername_ShouldRouteToDLQ() throws JMSException {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(60);

        String transactionId = "txn-123";

        assertDoesNotThrow(() -> listener.receiveWorkloadMessage(request, transactionId));

        verifyNoInteractions(workloadService);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload.dlq"), eq(request), postProcessorCaptor.capture());

        Message mockMessage = mock(Message.class);
        postProcessorCaptor.getValue().postProcessMessage(mockMessage);

        verify(mockMessage).setStringProperty("transactionId", transactionId);
        verify(mockMessage).setStringProperty("dlqReason", "Missing required information (username, actionType or duration)");
    }

    @Test
    void testReceiveWorkloadMessage_MissingUsername_NoTransactionId_ShouldRouteToDLQ() throws JMSException {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(60);

        assertDoesNotThrow(() -> listener.receiveWorkloadMessage(request, null));

        verifyNoInteractions(workloadService);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload.dlq"), eq(request), postProcessorCaptor.capture());

        Message mockMessage = mock(Message.class);
        postProcessorCaptor.getValue().postProcessMessage(mockMessage);

        verify(mockMessage).setStringProperty("dlqReason", "Missing required information (username, actionType or duration)");
        verify(mockMessage, never()).setStringProperty(eq("transactionId"), any());
    }

    @Test
    void testReceiveWorkloadMessage_UnexpectedException_ShouldThrow() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("testuser");
        request.setActionType(ActionType.ADD);
        request.setTrainingDuration(60);

        String transactionId = "txn-123";

        doThrow(new RuntimeException("DB error")).when(workloadService).processWorkload(request);

        assertThrows(RuntimeException.class, () -> listener.receiveWorkloadMessage(request, transactionId));

        verify(workloadService).processWorkload(request);
        verifyNoInteractions(jmsTemplate);
    }
}
