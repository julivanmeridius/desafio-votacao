package br.com.company.votacao.constants;

public final class VotacaoConstants {

    private VotacaoConstants() {
    }

    public static final String CACHE_RESULTADO_POR_PAUTA = "resultadoPorPauta";
    public static final String CACHE_RESULTADO_FINAL_POR_PAUTA = "resultadoFinalPorPauta";

    public static final String CACHE_KEY_PAUTA_ID = "#pautaId";

    public static final String PAUTA_NAO_ENCONTRADA = "Pauta não encontrada";
    public static final String ASSOCIADO_NAO_ENCONTRADO = "Associado não encontrado";
    public static final String ASSOCIADO_JA_VOTOU = "Associado já votou nesta pauta";
    public static final String SESSAO_ATIVA_NAO_ENCONTRADA = "Nenhuma sessão ativa encontrada para esta pauta";

    public static final String VOTO_REGEX = "Sim|Não";
    public static final String VOTO_VALIDATION_MESSAGE = "Voto deve ser 'Sim' ou 'Não'";

    public static final String OPEN_API_TITLE = "Votacao API";
    public static final String OPEN_API_DESCRIPTION = "REST API para gerencianento de agendas voting agendas, voting sessions, associates, and votes.";
    public static final String OPEN_API_VERSION = "1.0.0";

    public static final String KAFKA_BOOTSTRAP_SERVERS_PROPERTY = "${spring.kafka.bootstrap-servers}";
    public static final String KAFKA_VOTACAO_TOPIC_PROPERTY = "${spring.kafka.votacao-topic}";
    public static final String SESSAO_ENCERRAMENTO_DELAY_PROPERTY = "${app.sessao-votacao.encerramento-delay-ms:5000}";

    public static final String MSG_KAFKA_SUCESSO = "Mensagem enviada com sucesso para Topico: ";
    public static final String MSG_KAFKA_ERRO = "Falha ao tentar enviar a mensagem para o Topico - Erro: ";

    public static final String RESULTADO_JSON_FORMAT = "{\"sim\":%d,\"nao\":%d,\"total\":%d,\"status\":\"%s\"}";

    public static final String API_PAUTAS_BASE_PATH = "/v1/pautas";
    public static final String API_SESSOES_PATH = "/{pautaId}/sessoes";
    public static final String API_VOTOS_PATH = "/{pautaId}/votos";
    public static final String API_RESULTADO_PATH = "/{pautaId}/resultado";

    public static final String TAG_PAUTAS_NAME = "Pautas";
    public static final String TAG_PAUTAS_DESCRIPTION = "Gerenciamento de pautas";

    public static final String OP_CRIAR_PAUTA = "Criar nova pauta para votação";
    public static final String OP_ABRIR_SESSAO = "Realizar a abertura de sessão de votação de uma pauta";
    public static final String OP_REGISTRAR_VOTO = "Registrar voto de um associado em uma pauta";
    public static final String OP_OBTER_RESULTADO = "Obter resultado da votação de uma pauta";

    public static final String TABLE_PAUTA = "pauta";
    public static final String TABLE_VOTO = "voto";
    public static final String TABLE_ASSOCIADO = "associado";
    public static final String TABLE_SESSAO_VOTACAO = "sessao_votacao";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITULO = "titulo";
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_CRIADO_EM = "criado_em";
    public static final String COLUMN_PAUTA_ID = "pauta_id";
    public static final String COLUMN_SESSAO_VOTACAO_ID = "sessao_votacao_id";
    public static final String COLUMN_ASSOCIADO_ID = "associado_id";
    public static final String COLUMN_VOTO = "voto";
    public static final String COLUMN_RECEBIDO_EM = "recebido_em";
    public static final String COLUMN_TEMPO_ABERTURA = "tempo_abertura";
    public static final String COLUMN_DURACAO_SEGUNDOS = "duracao_segundos";
    public static final String COLUMN_FECHA_ABERTURA = "fecha_abertura";
    public static final String COLUMN_ENCERRADA_EM = "encerrada_em";

    public static final String DEFAULT_NOW = "now()";
    public static final String DEFAULT_SESSAO_DURACAO_SEGUNDOS = "60";

    public static final String PARAM_PAUTA_ID = "pautaId";
}
