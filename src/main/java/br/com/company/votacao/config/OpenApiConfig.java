package br.com.company.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI votacaoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Votacao API")
                        .description("REST API para gerencianento de agendas voting agendas, voting sessions, associates, and votes.")
                        .version("1.0.0"));
    }
}
