package br.com.civico.mais.saude.constantes;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class ConstantesAplicacao {

    public static final String URL_BASE = "http://mobile-aceite.tcu.gov.br/mapa-da-saude";
    public static final String URL_BASE_METAMODELO = "http://mobile-aceite.tcu.gov.br:80/appCivicoRS";

    public static final String COD_APP_IDENTIFICADOR ="199";
    public static final long COD_TIPO_POSTAGEM =79;
    public static final long COD_TIPO_OBJETO =100;


    public static final int STATUS_OK=200;
    public static final int STATUS_CADASTRO_SUCESSO=201;
    public static final int STATUS_PARAMETRO_INVALIDO=400;
    public static final int STATUS_CREDENCIAIS_INVALIDAS=401;
    public static final int STATUS_EMAIL_NAO_CADASTRADO=404;
    public static final int STATUS_SERVICO_NOT_FOUND_CADASTRO=404;

    public static final String MENSAGEM_SUCESSO="Sucesso";
    public static final String MENSAGEM_PARAMETRO_INVALIDO="Email já se encontra cadastrado";
    public static final String MENSAGEM_CRENDECIAIS_INVALIDAS="Usuário ou senha incorretos";
    public static final String MENSAGEM_EMAIL_NAO_CADASTRADO="E-mail não encontrado";
    public static final String MENSAGEM_SERVICO_NOT_FOUND_CADASTRO="Ocorreu um erro no cadastro. Por favor, tente novamente mais tarde.";
    public static final String MENSAGEM_SESSAO_EXPIRADA="Sessão expirada";
    public static final String MENSAGEM_NOT_FOUND_LOCATION="Por favor, verifique se o serviço de localização do aparelho está ativo em \'Configurar -> Localização\'.";
    public static final String MENSAGEM_SEM_CONEXAO_INTERNET="Verifique sua conexão com a internet e tente novamente.";
    public static final String MENSAGEM_COMENTARIO_EXCLUSAO_SUCESSO = "Comentário excluído com sucesso.";
    public static final String MENSAGEM_EMAI_ENVIADO_SUCESSO = "Email enviado com sucesso.";

    public static final String SPLIT_CARACTER="_";
    public static final long QTD_RETORNO_SERVICO =15;
    public static final String SEARCH_MEDICAMENTOPOR_CODBARRA = "0";
    public static final String SEARCH_MEDICAMENTOPOR_LISTARTODOS = "1";

    public static final String KEY_CACHE_UNIDADE="KEY_UNIDADE";
    public static final String KEY_CACHE_HEADER_UNIDADE="KEY_HEADER_UNIDADE";
    public static final String KEY_CACHE_lIST_UNIDADE="KEY_LIST_UNIDADE";

    public static final long ESPACO_MINIMO_CACHE= (1024*1024) * 3; //3MB
    public static final int ROW_DISPLAY = 16;

    public static final String MSG_CONFIRMACAO_EXCLUIR="Deseja excluir este comentário? ";
    public static final String BTN_CANCELAR ="Cancelar";
    public static final String BTN_OK="OK";

    public static final String MSG_PROGRESS_DIALOG ="Acessando base de dados do TCU...";

}
