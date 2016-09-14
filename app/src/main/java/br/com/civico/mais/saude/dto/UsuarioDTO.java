package br.com.civico.mais.saude.dto;

/**
 * Created by Jônatas Rodrigues on 11/09/2016.
 */
public class UsuarioDTO {
    private String codigo;
    private String nome;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
