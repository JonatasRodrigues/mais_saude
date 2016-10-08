package br.com.civico.mais.saude.servico;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Jônatas Rodrigues on 29/08/2016.
 */
public interface Service<E> {

   public E consumirServicoTCU()throws JSONException;
   public JSONArray getJson(String json) throws JSONException;

}
