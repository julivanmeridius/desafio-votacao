package br.com.company.votacao.controller;

import br.com.company.votacao.dto.PautaResponseDTO;
import br.com.company.votacao.dto.SessaoVotacaoResponseDTO;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

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
    void criar_shouldReturn201WithPautaId_whenValidRequest() throws Exception {
        when(pautaService.criar(any())).thenReturn(new PautaResponseDTO(1L));

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("titulo", "Titulo", "descricao", "Descricao"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(1L));
    }

    @Test
    void criar_shouldReturn400WithFieldError_whenTituloIsBlank() throws Exception {
        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("titulo", "", "descricao", "Descricao"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("titulo"))
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }

    @Test
    void criar_shouldReturn400WithFieldError_whenDescricaoIsBlank() throws Exception {
        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("titulo", "Titulo", "descricao", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("descricao"))
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }

    @Test
    void criar_shouldReturn400WithTwoErrors_whenBothFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("titulo", "", "descricao", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void abrirSessao_shouldReturn201WithSessaoData_whenValidRequest() throws Exception {
        var tempoAbertura = OffsetDateTime.parse("2026-01-01T10:00:00Z");
        var fechaAbertura = OffsetDateTime.parse("2026-01-01T10:02:00Z");

        when(sessaoVotacaoService.abrir(eq(1L), any()))
                .thenReturn(new SessaoVotacaoResponseDTO(10L, tempoAbertura, fechaAbertura));

        mockMvc.perform(post("/v1/pautas/1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("duracaoSegundos", 120L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessaoId").value(10L))
                .andExpect(jsonPath("$.tempoAbertura").value("2026-01-01T10:00:00Z"))
                .andExpect(jsonPath("$.fechaAbertura").value("2026-01-01T10:02:00Z"));
    }

    @Test
    void abrirSessao_shouldReturn201WithSessaoData_whenRequestBodyIsMissing() throws Exception {
        var tempoAbertura = OffsetDateTime.parse("2026-01-01T10:00:00Z");
        var fechaAbertura = OffsetDateTime.parse("2026-01-01T10:01:00Z");

        when(sessaoVotacaoService.abrir(eq(1L), eq(null)))
                .thenReturn(new SessaoVotacaoResponseDTO(10L, tempoAbertura, fechaAbertura));

        mockMvc.perform(post("/v1/pautas/1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessaoId").value(10L))
                .andExpect(jsonPath("$.tempoAbertura").value("2026-01-01T10:00:00Z"))
                .andExpect(jsonPath("$.fechaAbertura").value("2026-01-01T10:01:00Z"));
    }

    @Test
    void abrirSessao_shouldReturn400WithFieldError_whenDuracaoSegundosIsNegative() throws Exception {
        mockMvc.perform(post("/v1/pautas/1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("duracaoSegundos", -1L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("duracaoSegundos"))
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }

    @Test
    void abrirSessao_shouldReturn404_whenPautaDoesNotExist() throws Exception {
        when(sessaoVotacaoService.abrir(eq(99L), any()))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Pauta não encontrada"));

        mockMvc.perform(post("/v1/pautas/99/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("duracaoSegundos", 120L))))
                .andExpect(status().isNotFound());
    }
}
