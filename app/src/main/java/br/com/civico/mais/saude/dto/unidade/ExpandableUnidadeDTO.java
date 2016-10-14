package br.com.civico.mais.saude.dto.unidade;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public class ExpandableUnidadeDTO {
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<String, String> listMediaChild;

    public ExpandableUnidadeDTO(List<String> listDataHeader, HashMap<String, List<String>> listDataChild,
            HashMap<String, String> listMediaChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.listMediaChild =listMediaChild;
    }

    public List<String> getListDataHeader() {
        return listDataHeader;
    }

    public HashMap<String, List<String>> getListDataChild() {
        return listDataChild;
    }

    public HashMap<String, String> getListMediaChild() {
        return listMediaChild;
    }
}
