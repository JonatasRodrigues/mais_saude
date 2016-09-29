package br.com.civico.mais.saude.dto;

/**
 * Created by Jônatas Rodrigues on 25/09/2016.
 */
public class Response {

    private int statusCodigo;
    private String mensagem;
    private String token;
    private long codigoUsuario;

    public int getStatusCodigo() {
        return statusCodigo;
    }

    public void setStatusCodigo(int statusCodigo) {
        this.statusCodigo = statusCodigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(long codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }
}
