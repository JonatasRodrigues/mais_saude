package br.com.civico.mais.saude.dto;

import java.util.Date;

/**
 * Created by Jônatas Rodrigues on 25/09/2016.
 */
public class PostagemDTO {
    private String comentario;
    private Date dataPostagem;
    private double pontuacao;
    private String nomeAutor;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getDataPostagem() {
        return dataPostagem;
    }

    public void setDataPostagem(Date dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }
}
