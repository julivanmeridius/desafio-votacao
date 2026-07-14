package br.com.company.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.company.votacao.constants.VotacaoConstants.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI votacaoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(OPEN_API_TITLE)
                        .description(OPEN_API_DESCRIPTION)
                        .version(OPEN_API_VERSION));
    }
}
