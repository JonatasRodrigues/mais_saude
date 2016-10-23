package br.com.civico.mais.saude.dto;

/**
 * Created by Jônatas Rodrigues on 23/10/2016.
 */
public class AvaliacaoResponse {

    private float mediaAvaliacao;
    private String qtdAvaliacao;

    public float getMediaAvaliacao() {
        return mediaAvaliacao;
    }

    public void setMediaAvaliacao(float mediaAvaliacao) {
        this.mediaAvaliacao = mediaAvaliacao;
    }

    public String getQtdAvaliacao() {
        return qtdAvaliacao;
    }

    public void setQtdAvaliacao(String qtdAvaliacao) {
        this.qtdAvaliacao = qtdAvaliacao;
    }
}
