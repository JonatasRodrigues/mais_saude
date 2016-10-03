package br.com.civico.mais.saude.constantes;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class ConstantesAplicacao {

    public static final String URL_BASE = "http://mobile-aceite.tcu.gov.br/mapa-da-saude";
    public static final String URL_BASE_METAMODELO = "http://mobile-aceite.tcu.gov.br:80/appCivicoRS";

    public static final String APP_IDENTIFICADOR="199";
    public static final long TIPO_POSTAGEM=79;


    public static final int STATUS_OK=200;
    public static final int STATUS_CADASTRO_SUCESSO=201;
    public static final int STATUS_PARAMETRO_INVALIDO=400;
    public static final int STATUS_CREDENCIAIS_INVALIDAS=401;
    public static final int STATUS_EMAIL_NAO_CADASTRADO=404;
    public static final int STATUS_SERVICO_NOT_FOUND_CADASTRO=404;

    public static final String MENSAGEM_SUCESSO="Sucesso";
    public static final String MENSAGEM_PARAMETRO_INVALIDO="Email já se encontra cadastrado";
    public static final String MENSAGEM_CRENDECIAIS_INVALIDAS="Usuário ou senha incorretos";
    public static final String MENSAGEM_EMAIL_NAO_CADASTRADO="Ocorreu um erro ao encontrar o e-mail informado";
    public static final String MENSAGEM_SERVICO_NOT_FOUND_CADASTRO="Ocorreu um erro no cadastro. Por favor, tente novamente mais tarde.";
    public static final String MENSAGEM_SESSAO_EXPIRADA="Sessão expirada";
}
