package br.com.civico.mais.saude.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jônatas Rodrigues on 25/09/2016.
 */
public class PostagemDTO {
    private String comentario;
    private String dataPostagem;
    private double pontuacao;
    private String nomeAutor;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDataPostagem() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'BRT'");
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
