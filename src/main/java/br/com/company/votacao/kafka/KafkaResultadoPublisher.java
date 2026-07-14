package br.com.company.votacao.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static br.com.company.votacao.constants.VotacaoConstants.KAFKA_VOTACAO_TOPIC_PROPERTY;
import static br.com.company.votacao.constants.VotacaoConstants.MSG_KAFKA_ERRO;
import static br.com.company.votacao.constants.VotacaoConstants.MSG_KAFKA_SUCESSO;

@Service
@RequiredArgsConstructor
public class KafkaResultadoPublisher implements ResultadoPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(KAFKA_VOTACAO_TOPIC_PROPERTY)
    private String topic;

    @Override
    public void publishResultado(String mensagem) {
        kafkaTemplate.send(topic, mensagem)
                .whenComplete(((result, ex) -> {
                    if (ex == null) {
                        System.out.println(MSG_KAFKA_SUCESSO +
                                result.getRecordMetadata().offset());
                    } else {
                        System.out.println(MSG_KAFKA_ERRO + ex.getMessage());
                    }
                }));
    }
}
