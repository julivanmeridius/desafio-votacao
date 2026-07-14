package br.com.company.votacao.kafka;

public interface ResultadoPublisher {
    void publishResultado(String mensagem);
}
