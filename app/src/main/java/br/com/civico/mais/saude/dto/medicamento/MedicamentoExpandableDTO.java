package br.com.civico.mais.saude.dto.medicamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MedicamentoExpandableDTO {
    private Set<String> listDataHeader;
    private HashMap<String, Set<String>> listDataChild;

    public MedicamentoExpandableDTO(Set<String> listDataHeader, HashMap<String, Set<String>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public Set<String> getSetDataHeader() {
        return listDataHeader;
    }

    public List getListDataHeader() {
        List retorno = new ArrayList<String>(listDataHeader);
        return retorno;
    }

    public void setListDataHeader(Set<String> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    public HashMap<String, Set<String>> getSetDataChild() {
        return listDataChild;
    }

    public HashMap<String, List<String>> getListDataChild() {
        HashMap<String, List<String>> retorno = new HashMap<>();
        for(String key: listDataChild.keySet()){
            retorno.put(key,new ArrayList<String>(listDataChild.get(key)));
        }
        return retorno;
    }

    public void setListDataChild(HashMap<String, Set<String>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
