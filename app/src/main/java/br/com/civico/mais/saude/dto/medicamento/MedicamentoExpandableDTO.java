package br.com.civico.mais.saude.dto.medicamento;

import java.util.HashMap;
import java.util.List;

public class MedicamentoExpandableDTO {
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public MedicamentoExpandableDTO(List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public List getListDataHeader() {
        return listDataHeader;
    }

    public HashMap<String, List<String>> getListDataChild() {
        return listDataChild;
    }

}
