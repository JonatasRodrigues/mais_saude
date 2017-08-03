package br.com.civico.mais.saude.dto.unidade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import br.com.civico.mais.saude.dto.AvaliacaoResponse;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public class ExpandableUnidadeDTO implements Serializable{

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public ExpandableUnidadeDTO(List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public List<String> getListDataHeader() {
        return listDataHeader;
    }

    public HashMap<String, List<String>> getListDataChild() {
        return listDataChild;
    }

}
