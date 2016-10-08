package br.com.civico.mais.saude.servico;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public abstract class AbstractService<T> extends AsyncTask<String, Void, T> implements Service<T>{



    public abstract T consumirServicoTCU()throws JSONException;


    @Override
    protected T doInBackground(String... params) {
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
