package br.com.civico.mais.saude.dto.medicamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    public void setListDataHeader(List<String> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    public HashMap<String, List<String>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<String, List<String>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
