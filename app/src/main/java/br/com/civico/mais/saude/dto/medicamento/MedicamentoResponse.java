package br.com.civico.mais.saude.dto.medicamento;

/**
 * Created by Jônatas Rodrigues on 07/10/2016.
 */
public class MedicamentoResponse {

    private int statusCodigo;
    private String mensagem;
    private MedicamentoExpandableDTO medicamentoExpandableDTO;

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

    public MedicamentoExpandableDTO getMedicamentoExpandableDTO() {
        return medicamentoExpandableDTO;
    }

    public void setMedicamentoExpandableDTO(MedicamentoExpandableDTO medicamentoExpandableDTO) {
        this.medicamentoExpandableDTO = medicamentoExpandableDTO;
    }
}
