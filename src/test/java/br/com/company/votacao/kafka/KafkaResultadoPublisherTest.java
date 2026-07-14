package br.com.company.votacao.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaResultadoPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaResultadoPublisher kafkaResultadoPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaResultadoPublisher, "topic", "test-votacao-topic");
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishResultado_shouldSendToKafkaAndLogSuccess_whenSendSucceeds() {
        var mensagem = "{\"sim\":3,\"nao\":1,\"total\":4,\"status\":\"ENCERRADA\"}";
        var sendResult = mock(SendResult.class, RETURNS_DEEP_STUBS);
        when(sendResult.getRecordMetadata().offset()).thenReturn(42L);
        when(kafkaTemplate.send("test-votacao-topic", mensagem))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        kafkaResultadoPublisher.publishResultado(mensagem);

        verify(kafkaTemplate).send("test-votacao-topic", mensagem);
    }

    @Test
    void publishResultado_shouldSendToKafkaAndLogError_whenSendFails() {
        var mensagem = "{\"sim\":0,\"nao\":2,\"total\":2,\"status\":\"ENCERRADA\"}";
        when(kafkaTemplate.send("test-votacao-topic", mensagem))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("connection refused")));

        kafkaResultadoPublisher.publishResultado(mensagem);

        verify(kafkaTemplate).send("test-votacao-topic", mensagem);
    }
}
