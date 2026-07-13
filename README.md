# desafio-votacao
Desenvolvimento de um sistema de controle de votacao

# API documentations local endpoints
### http://localhost:8080/swagger-ui.html
### http://localhost:8080/v3/api-docs

# Decisoes arquiteturais tomadas para o contexto 

## Modelagem e Regras mantidas no PostgreSQL ( Fonte da Verdade )

---
## Backend ( Spring boot + Java 21)
### Estrutura
- **Controller (REST):** valida formato e chama serviços.
- **Service:** regras de negócio (abrir sessão, receber voto, obter resultado).
- **Repository (JPA):** persistência.
- **DTOs:** separa payloads da entidade JPA.
---

## API REST

### 01) Cadastrar nova pauta
- **POST** `/pautas`
- body: `titulo`, `descricao`
- retorna: `pautaId`

### 02) Abrir sessão de votação
- **POST** `/pautas/{pautaId}/sessoes`
- body opcional: `duracaoSegundos` (default = 60)
- retorna: `sessaoId`, `tempoAbertura`, `fechaAbertura`

### 03) Receber votos
- **POST** `/pautas/{pautaId}/votos`
- body: `associadoId`, `voto` (“Sim”/“Não”)
- comportamento:
   - resolve sessão ativa da pauta
   - insere voto com `sessao_votacao_id`
   - se já votou → retorna “conflito”

### 04) Resultado da votação
- **GET** `/pautas/{pautaId}/resultado`
- retorna: `simCount`, `naoCount`, `total`, `status (aberta/encerrada)`
- implementação: query `GROUP BY voto` ou contagens filtradas por sessão ativa.

---

## Banco de dados 
   ### Tabelas:
     - **pauta**
     - **sessao_votacao**       
     - **associado**
     - **voto**
   ### Constraints(criadas para evitar over engineering e garantir invariantes)
     * UNIQUE (pauta_id, associado_id) em voto === garante “vota 1 vez por pauta”.
     * CHECK (voto IN ('Sim','Não')).
     * FK entre voto.sessao_votacao_id e sessao_votacao.id.
   ### Trigger 
     - Visando impedir insert se a sessão já estiver encerrada (`encerrada_em` ou `now() >= fecha_abertura`).
     - Visando impedir que ocorra o mismatch de `voto.pauta_id` com `sessao_votacao.pauta_id`.
   ### Índices
   - `voto(pauta_id, associado_id)` → nesse caso criado pela instrução UNIQUE
   - `voto(sessao_votacao_id)` → para a contagem rápida por sessão
   - `sessao_votacao(pauta_id, tempo_abertura)` → para as consultas de sessão ativa
   ### Persistência e Migrações
   - Persistência via PostgreSQL por ter alguma familiaridade
   - Versionamento realizado com **Flyway** para facilitar versionamanto e manutenções futuras
   - Nada em memória → tudo reconstituído do banco de dados
---
##  Nuvem
  - Containerização da API com Docker criada
  - Banco Postgres passaria a ser gerenciado em qualquer Cloud Provider
  - Criação de variáveis de ambiente para credenciais/config
  - Adicionados parâmetros de health checks, readiness and liveness
  - Logs estruturados usando Micrometer registry prometheus para posterior monitoramento

## Decisões arquiteturais tomadas para o contexto
1)  No meu entendimento o fato de termos um cache como Redis não faz muito sentido, pois o banco de dados
PostgreSQL com índices aguenta dezenas de milhares de leituras/segundo sem Redis.
2) Versionamento da API sera feito via URL.
3) Uso de record para imutabilidade dos dados, eliminação de boilerplate
