package br.com.company.votacao.controller;

import br.com.company.votacao.dto.ResultadoResponseDTO;
import br.com.company.votacao.model.StatusSessao;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class ResultadoControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new NoOpCacheManager();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PautaService pautaService;

    @MockitoBean
    private SessaoVotacaoService sessaoVotacaoService;

    @MockitoBean
    private VotoService votoService;

    @MockitoBean
    private ResultadoService resultadoService;

    @Test
    void resultado_shouldReturn200WithCounts_whenSessaoAberta() throws Exception {
        when(resultadoService.obter(1L))
                .thenReturn(new ResultadoResponseDTO(3L, 1L, 4L, StatusSessao.ABERTA));

        mockMvc.perform(get("/v1/pautas/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simCount").value(3))
                .andExpect(jsonPath("$.naoCount").value(1))
                .andExpect(jsonPath("$.total").value(4))
                .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @Test
    void resultado_shouldReturn200WithCounts_whenSessaoEncerrada() throws Exception {
        when(resultadoService.obter(1L))
                .thenReturn(new ResultadoResponseDTO(0L, 2L, 2L, StatusSessao.ENCERRADA));

        mockMvc.perform(get("/v1/pautas/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simCount").value(0))
                .andExpect(jsonPath("$.naoCount").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.status").value("ENCERRADA"));
    }

    @Test
    void resultado_shouldReturn404_whenPautaDoesNotExist() throws Exception {
        when(resultadoService.obter(99L))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Pauta não encontrada"));

        mockMvc.perform(get("/v1/pautas/99/resultado"))
                .andExpect(status().isNotFound());
    }
}
