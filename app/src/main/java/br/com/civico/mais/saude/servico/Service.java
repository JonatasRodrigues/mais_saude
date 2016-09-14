package br.com.civico.mais.saude.servico;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import br.com.civico.mais.saude.dto.ExpandableDTO;

/**
 * Created by Jônatas Rodrigues on 29/08/2016.
 */
public interface Service<E> {

   public E consumirServicoTCU()throws JSONException;
   public JSONArray getJson(String json) throws JSONException;

}
