package br.com.civico.mais.saude.servico;

import android.content.Context;

import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.converter.StreamConverter;
import br.com.civico.mais.saude.dto.ExpandableDTO;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class UnidadeService extends AbstractService {

    private Context context;
    private static final Integer RAIO = 30;
    private static UnidadeService unidadeService;

    public static UnidadeService getInstance(Context context){
        if(unidadeService==null){
            unidadeService= new UnidadeService(context);
        }
        return unidadeService;
    }

    private UnidadeService(Context context){
        this.context=context;
    }

    @Override
    public ExpandableDTO consumirServicoTCU() throws JSONException {
        String result="";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/estabelecimentos/latitude/" + getLatitude()
                    + "/longitude/" + getLongitude() + "/raio/" + RAIO;

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                result= StreamConverter.convertStreamToString(instream);
                instream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return converterJsonParaObject(getJson(result));
    }


    private ExpandableDTO converterJsonParaObject(JSONArray jsonArray){
        List<String> listaHeader = new ArrayList<String>();
        List<String> listaDados;
        HashMap<String, List<String>> listDataChild = new HashMap<>();
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                listaDados = new ArrayList<String>();
                JSONObject oneObject = jsonArray.getJSONObject(i);
                listaHeader.add(oneObject.getString("nomeFantasia"));
                listaDados.add(oneObject.getString("codUnidade"));
                listDataChild.put(listaHeader.get(i), listaDados);
            } catch (JSONException e) {
                // Oops
            }
        }
       return new ExpandableDTO(listaHeader,listDataChild);
    }

    private double getLongitude(){
        LocalizacaoService localizacaoService = new LocalizacaoService(context);
        return localizacaoService.getLongitude();
    }

    private double getLatitude(){
        LocalizacaoService localizacaoService = new LocalizacaoService(context);
        return localizacaoService.getLatitude();
    }


}
