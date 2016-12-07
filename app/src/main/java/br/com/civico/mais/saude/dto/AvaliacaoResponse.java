package br.com.civico.mais.saude.dto;

import java.io.Serializable;

/**
 * Created by Jônatas Rodrigues on 23/10/2016.
 */
public class AvaliacaoResponse implements Serializable{

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
