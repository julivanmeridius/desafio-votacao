package br.com.company.votacao.controller;

import br.com.company.votacao.dto.VotoResponseDTO;
import br.com.company.votacao.service.PautaService;
import br.com.company.votacao.service.ResultadoService;
import br.com.company.votacao.service.SessaoVotacaoService;
import br.com.company.votacao.service.VotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class VotoControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new NoOpCacheManager();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PautaService pautaService;

    @MockitoBean
    private SessaoVotacaoService sessaoVotacaoService;

    @MockitoBean
    private VotoService votoService;

    @MockitoBean
    private ResultadoService resultadoService;

    @Test
    void votar_shouldReturn201WithVotoData_whenValidRequest() throws Exception {
        var recebidoEm = OffsetDateTime.parse("2026-01-01T10:00:00Z");
        when(votoService.votar(eq(1L), any())).thenReturn(new VotoResponseDTO(100L, recebidoEm));

        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", "Sim"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.votoId").value(100L))
                .andExpect(jsonPath("$.recebidoEm").value("2026-01-01T10:00:00Z"));
    }

    @Test
    void votar_shouldReturn400_whenAssociadoIdIsNull() throws Exception {
        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("voto", "Sim"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("associadoId"));
    }

    @Test
    void votar_shouldReturn400_whenVotoIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", "Talvez"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("voto"));
    }

    @Test
    void votar_shouldReturn400_whenVotoIsBlank() throws Exception {
        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void votar_shouldReturn404_whenPautaDoesNotExist() throws Exception {
        when(votoService.votar(eq(99L), any()))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Pauta não encontrada"));

        mockMvc.perform(post("/v1/pautas/99/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", "Sim"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void votar_shouldReturn422_whenNoActiveSessao() throws Exception {
        when(votoService.votar(eq(1L), any()))
                .thenThrow(new ResponseStatusException(UNPROCESSABLE_CONTENT, "Nenhuma sessão ativa encontrada para esta pauta"));

        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", "Não"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void votar_shouldReturn409_whenAssociadoAlreadyVoted() throws Exception {
        when(votoService.votar(eq(1L), any()))
                .thenThrow(new ResponseStatusException(CONFLICT, "Associado já votou nesta pauta"));

        mockMvc.perform(post("/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("associadoId", 10L, "voto", "Sim"))))
                .andExpect(status().isConflict());
    }
}
