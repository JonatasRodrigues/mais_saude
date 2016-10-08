package br.com.civico.mais.saude.dto.unidade;

/**
 * Created by Jônatas Rodrigues on 07/10/2016.
 */
public class UnidadeResponse {

    private int statusCodigo;
    private String mensagem;
    private ExpandableUnidadeDTO expandableUnidadeDTO;

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

    public ExpandableUnidadeDTO getExpandableUnidadeDTO() {
        return expandableUnidadeDTO;
    }

    public void setExpandableUnidadeDTO(ExpandableUnidadeDTO expandableUnidadeDTO) {
        this.expandableUnidadeDTO = expandableUnidadeDTO;
    }
}
