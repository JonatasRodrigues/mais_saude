package br.com.civico.mais.saude.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jônatas Rodrigues on 25/09/2016.
 */
public class PostagemDTO {

    private String codPostagem;
    private String codAutor;
    private String comentario;
    private String dataPostagem;
    private float pontuacao;
    private String nomeAutor;

    public String getCodPostagem() {
        return codPostagem;
    }

    public void setCodPostagem(String codPostagem) {
        this.codPostagem = codPostagem;
    }

    public String getCodAutor() {
        return codAutor;
    }

    public void setCodAutor(String codAutor) {
        this.codAutor = codAutor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDataPostagem() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'BRST'");
            Date date = dateFormat.parse(dataPostagem);
            return new SimpleDateFormat("dd/MM/yyyy").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataPostagem;
    }

    public void setDataPostagem(String dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public float getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(float pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

}
