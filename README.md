# desafio-votacao

Sistema REST de controle de votação construído com Spring Boot 4.1 e Java 21.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9 |
| Docker Desktop | 4.x |

---

## Início Rápido (desenvolvimento local)

A aplicação e a infraestrutura rodam em ambientes separados intencionalmente — o mesmo modelo usado em nuvem (app no cluster, banco no RDS, Kafka no MSK).

### 1. Subir a infraestrutura (banco + mensageria)

```bash
docker compose up -d
```

Aguarde os contêineres ficarem saudáveis:

```bash
docker compose ps
```

Saída esperada:

```
NAME                STATUS
votacao-postgres    Up (healthy)
votacao-kafka       Up
```

### 2. Executar a aplicação

**Opção A — Maven (recomendado para desenvolvimento):**

```bash
mvn spring-boot:run
```

**Opção B — JAR:**

```bash
mvn clean package -DskipTests
java -jar target/votacao-*.jar
```

**Opção C — Docker (simula o ambiente de nuvem localmente):**

```bash
docker build -t votacao-backend .

docker run --rm \
  -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  votacao-backend
```

> `host.docker.internal` resolve para o host a partir de dentro do contêiner (disponível no Docker Desktop para Mac e Windows; no Linux use `--add-host=host.docker.internal:host-gateway`).

### 3. Verificar a aplicação

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |
| Metrics (Prometheus) | http://localhost:8080/actuator/prometheus |

### 4. Parar a infraestrutura

```bash
docker compose down    
docker compose down -v
```

---

## Variáveis de ambiente

Todos os valores abaixo têm **default local** definido em `application.yaml`. Só precisam ser sobrescritos em ambientes externos.

| Variável | Default local | Descrição |
|---|---|---|
| `SERVER_PORT` | `8080` | Porta HTTP da aplicação |
| `ENVIRONMENT` | `local` | Rótulo do ambiente (usado em métricas) |
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `votacao` | Nome do banco de dados |
| `DB_USR` | `postgres` | Usuário do banco |
| `DB_PASS` | `masterkey` | Senha do banco |
| `HIBERNATE_DDL_AUTO` | `validate` | DDL do Hibernate (`validate` / `none`) |
| `JPA_SHOW_SQL` | `false` | Loga SQLs geradas |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Endereço(s) do Kafka |
| `KAFKA_VOTACAO_TOPIC` | `votacao-resultado` | Tópico de resultado de votação |
| `SESSAO_VOTACAO_ENCERRAMENTO_DELAY_MS` | `5000` | Intervalo do scheduler de encerramento (ms) |

---

## Testes

```bash
mvn test
mvn test -pl . -Dtest=NomeTest 
```

Cobertura de testes: 47 testes unitários (serviços, mappers, controladores e Kafka publisher).

---

## Deploy em nuvem (Kubernetes)

Os manifestos estão em `k8s/`. O modelo de isolamento é:

```
[ EKS / ECS ]         [ RDS PostgreSQL ]     [ MSK Kafka ]
  votacao-backend  →  (endpoint externo)  →  (endpoint externo)
```

### Sequência de aplicação

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### Antes de aplicar, edite:

**`k8s/configmap.yaml`** — substitua os placeholders pelos endpoints reais:
- `DB_HOST` → endpoint do RDS
- `KAFKA_BOOTSTRAP_SERVERS` → endpoint do MSK

**`k8s/secret.yaml`** — substitua usuário e senha do banco por valores reais (ou use AWS Secrets Manager / External Secrets Operator).

**`k8s/deployment.yaml`** — substitua `your-aws-account-id` pelo ID real da conta AWS:
```yaml
image: <account-id>.dkr.ecr.<region>.amazonaws.com/votacao-backend:latest
```

### Build e push da imagem

```bash
# autenticar no ECR
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin \
    <account-id>.dkr.ecr.us-east-1.amazonaws.com

# build e push
docker build -t votacao-backend .
docker tag votacao-backend:latest \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com/votacao-backend:latest
docker push \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com/votacao-backend:latest
```

---

## API REST

**Base path:** `/v1/pautas`

| # | Método | Endpoint | Descrição |
|---|---|---|---|
| 1 | `POST` | `/v1/pautas` | Cadastrar nova pauta |
| 2 | `POST` | `/v1/pautas/{pautaId}/sessoes` | Abrir sessão de votação |
| 3 | `POST` | `/v1/pautas/{pautaId}/votos` | Registrar voto |
| 4 | `GET` | `/v1/pautas/{pautaId}/resultado` | Obter resultado da votação |

Documentação interativa completa em: http://localhost:8080/swagger-ui.html

---

## Banco de dados

### Tabelas
- `pauta` — agenda de votação
- `sessao_votacao` — período de abertura de uma pauta
- `associado` — membro que pode votar
- `voto` — registro de voto de um associado em uma pauta

### Constraints
- `UNIQUE (pauta_id, associado_id)` em `voto` — garante um voto por associado por pauta
- `CHECK (voto IN ('Sim','Não'))` — restrição de valores válidos
- FK entre `voto.sessao_votacao_id` e `sessao_votacao.id`

### Índices
- `voto(pauta_id, associado_id)` — criado pela constraint UNIQUE
- `voto(sessao_votacao_id)` — contagem rápida por sessão
- `sessao_votacao(pauta_id, tempo_abertura)` — consulta de sessão ativa

### Migrations com Flyway

Todo o versionamento de schema fica em `src/main/resources/db/migration/`. Ao subir a aplicação, o Flyway compara o conteúdo dessa pasta com a tabela `flyway_schema_history` no banco e aplica automaticamente o que ainda não rodou.

**Estrutura:**

```
src/main/resources/db/
├── migration/
│   └── V1__create_schema.sql    ← aplicado pelo Flyway na inicialização
└── seed/
    └── dev-seed.sql             ← dados de exemplo, uso manual em dev
```

**Convenção de nomes:** `V<N>__<descricao_com_underscores>.sql`, com `N` crescente.
- ✅ `V2__adiciona_indice_voto_recebido_em.sql`
- ❌ `V2.sql`, `add-index.sql`, `V2_add_index.sql` (falta o duplo underscore)

**Como adicionar uma nova migration:**
1. Crie `V<próximo_número>__descricao.sql` em `db/migration/`
2. Suba a aplicação — o Flyway aplica automaticamente
3. Commite o arquivo

**Regras importantes:**
- ⚠️ **Nunca editar uma migration que já foi aplicada** em algum ambiente. `validate-on-migrate: true` recalcula o checksum a cada boot; qualquer alteração no conteúdo trava a inicialização.
- Para reverter algo, crie **uma nova migration** (`V<N+1>__revert_....sql`). Não existe rollback automático.
- Em produção, `clean-disabled: true` impede `flyway clean` acidental.
- `baseline-on-migrate: false` — o banco deve estar vazio na primeira execução (ou ter o histórico do Flyway já populado).

**Configurações relevantes** (`application.yaml`):

| Propriedade | Valor | Efeito |
|---|---|---|
| `spring.flyway.enabled` | `true` | Habilita o Flyway |
| `spring.flyway.locations` | `classpath:db/migration` | Onde procurar migrations |
| `spring.flyway.validate-on-migrate` | `true` | Confere checksums antes de aplicar |
| `spring.flyway.baseline-on-migrate` | `false` | Não cria baseline automático em bancos existentes |
| `spring.flyway.clean-disabled` | `true` (prod) | Bloqueia `flyway clean` |

### Seed de desenvolvimento

`src/main/resources/db/seed/dev-seed.sql` contém dados de exemplo (2 pautas, 3 associados, 2 sessões abertas e 3 votos). **Não é executado pelo Flyway** — é uso manual, apenas em ambiente local, para testar rapidamente via Swagger.

Para carregar (com a infra de docker-compose no ar):

```bash
docker exec -i votacao-postgres psql -U postgres -d votacao \
  < src/main/resources/db/seed/dev-seed.sql
```

Ou via `psql` local:

```bash
psql -h localhost -U postgres -d votacao \
  -f src/main/resources/db/seed/dev-seed.sql
```

---

## Decisões arquiteturais

1. **Sem cache Redis** — PostgreSQL com índices aguenta dezenas de milhares de leituras/segundo. Cache adicionaria complexidade operacional sem ganho justificável neste contexto.
2. **Versionamento via URL** — `/v1/...` é simples, explícito e compatível com proxies e gateways sem configuração adicional.
3. **DTOs com `record`** — imutabilidade, eliminação de boilerplate, serialização automática.
4. **Kafka para resultado de sessão** — ao encerrar uma sessão, o resultado é publicado de forma assíncrona (post-commit via `TransactionSynchronization`) para desacoplar consumidores da operação de encerramento.
5. **Threads virtuais (Project Loom)** — habilitadas via `spring.threads.virtual.enabled: true` para maior throughput em operações de I/O.
6. **Scheduler de encerramento** — `SessaoVotacaoScheduler` roda a cada `SESSAO_VOTACAO_ENCERRAMENTO_DELAY_MS` ms verificando sessões expiradas, evitando estado inconsistente sem depender do cliente chamar o encerramento.

---

## Tarefas Bônus

### Tarefa Bônus 01 — Integração de CPF
Validar se o associado pode votar via API externa:
```
GET https://user-info.herokuapp.com/users/{cpf}
→ { "status": "ABLE_TO_VOTE" | "UNABLE_TO_VOTE" }
```
Não foi implementado mas faria com Spring WebFlux com Circuit Breaker, Retry

### Tarefa Bônus 02 — Mensageria
Resultado publicado em tópico Kafka (`votacao-resultado`) ao encerrar cada sessão.

### Tarefa Bônus 03 — Performance
Foram criados índices para aumento de eficiência e velocidade, threads virtuais e producer Kafka com batch/lz4 para suportar alto volume de votos simultâneos.
Poderia realizar a separação do banco de dados com o de escrita CQRS ( se no futuro aumentasse exponencialmente o volume ) separando as lógicas distintas;
Também poderia ser criado um load balancer na aplicação para garantir para resolver o balanceamento de carga em caso de necessidade de tasks de ECS ou POD

### Tarefa Bônus 04 — Versionamento da API
Estratégia via URL: `/v1/...` mas também poderia ser em dados de Header - campo version por exemplo
