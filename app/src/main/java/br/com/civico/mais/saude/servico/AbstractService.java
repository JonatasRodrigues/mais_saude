package br.com.civico.mais.saude.servico;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import br.com.civico.mais.saude.dto.ExpandableDTO;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public abstract class AbstractService extends AsyncTask<String, Void, ExpandableDTO> implements Service{

    public abstract ExpandableDTO consumirServicoTCU()throws JSONException;

    @Override
    protected ExpandableDTO doInBackground(String... params) {
        try {
            return consumirServicoTCU();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray getJson(String json) throws JSONException {
        return new JSONArray(json);
    }
}