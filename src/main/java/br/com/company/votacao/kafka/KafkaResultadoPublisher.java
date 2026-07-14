package br.com.company.votacao.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static br.com.company.votacao.constants.VotacaoConstants.KAFKA_VOTACAO_TOPIC_PROPERTY;

@Service
@RequiredArgsConstructor
public class KafkaResultadoPublisher implements ResultadoPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaResultadoPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(KAFKA_VOTACAO_TOPIC_PROPERTY)
    private String topic;

    @Override
    public void publishResultado(String mensagem) {
        kafkaTemplate.send(topic, mensagem)
                .whenComplete(((result, ex) -> {
                    if (ex == null) {
                        var metadata = result.getRecordMetadata();
                        log.info("Resultado publicado em Kafka - topic={}, partition={}, offset={}",
                                topic, metadata.partition(), metadata.offset());
                    } else {
                        log.error("Falha ao publicar em Kafka - topic={}", topic, ex);
                    }
                }));
    }
}
