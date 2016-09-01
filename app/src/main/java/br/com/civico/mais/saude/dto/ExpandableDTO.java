package br.com.civico.mais.saude.dto;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public class ExpandableDTO {
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public ExpandableDTO(List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public List<String> getListDataHeader() {
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
