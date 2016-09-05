package br.com.civico.mais.saude.servico;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import br.com.civico.mais.saude.controle.UnidadeActivity;
import br.com.civico.mais.saude.dto.ExpandableDTO;

/**
 * Created by Jônatas Rodrigues on 30/08/2016.
 */
public abstract class AbstractService extends AsyncTask<String, Void, ExpandableDTO> implements Service{



    public abstract ExpandableDTO consumirServicoTCU()throws JSONException;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
     //   exibirMensagemProcessamento();
    }


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
    protected void onPostExecute(ExpandableDTO result) {
        super.onPostExecute(result);
    //    encerrarMensagemProcessamento();
    }

    @Override
    public JSONArray getJson(String json) throws JSONException {
        return new JSONArray(json);
    }
}
