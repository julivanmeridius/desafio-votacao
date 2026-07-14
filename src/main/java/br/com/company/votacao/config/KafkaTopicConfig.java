package br.com.company.votacao.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static br.com.company.votacao.constants.VotacaoConstants.KAFKA_BOOTSTRAP_SERVERS_PROPERTY;
import static br.com.company.votacao.constants.VotacaoConstants.KAFKA_VOTACAO_TOPIC_PROPERTY;

@Configuration
public class KafkaTopicConfig {

    @Value(KAFKA_BOOTSTRAP_SERVERS_PROPERTY)
    private String bootstrapAddress;

    @Value(KAFKA_VOTACAO_TOPIC_PROPERTY)
    private String votacaoTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic buildVotacaoTopic() {
        return new NewTopic(votacaoTopicName, 1, (short) 1);
    }
}
