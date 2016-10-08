package br.com.civico.mais.saude.servico;

 import android.location.Location;

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
import br.com.civico.mais.saude.dto.unidade.ExpandableUnidadeDTO;
 import br.com.civico.mais.saude.dto.unidade.UnidadeResponse;
 import br.com.civico.mais.saude.exception.ErroServicoTCUException;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class UnidadeService extends AbstractService<UnidadeResponse> {

    private Location location;
    private static final Integer RAIO = 30;

    public UnidadeService(Location location){
        this.location=location;
    }

    @Override
    public UnidadeResponse consumirServicoTCU() throws JSONException{
        UnidadeResponse unidadeResponse = new UnidadeResponse();
        String result="";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/estabelecimentos/latitude/" + this.location.getLatitude()
                    + "/longitude/" + this.location.getLongitude() + "/raio/" + RAIO;

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);

            unidadeResponse.setStatusCodigo(response.getStatusLine().getStatusCode());

            if(unidadeResponse.getStatusCodigo() == ConstantesAplicacao.STATUS_OK){
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result= StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                unidadeResponse.setExpandableUnidadeDTO(converterJsonParaObject(getJson(result)));

            }else{
                unidadeResponse.setMensagem("Erro ao recuperar informações. Por favor, tente mais tarde.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unidadeResponse;
    }


    private ExpandableUnidadeDTO converterJsonParaObject(JSONArray jsonArray){
        List<String> listaHeader = new ArrayList<String>();
        List<String> listaDados;
        HashMap<String, List<String>> listDataChild = new HashMap<>();
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                listaDados = new ArrayList<String>();
                JSONObject oneObject = jsonArray.getJSONObject(i);
                listaHeader.add(oneObject.getString("nomeFantasia"));
                listaDados.add("  ");
                listaDados.add("Tipo Unidade: " + oneObject.getString("tipoUnidade"));
                listaDados.add("Código: " + oneObject.getString("codUnidade"));
                listaDados.add("Vinculo SUS: " + oneObject.getString("vinculoSus"));
                listaDados.add("Emergência:  " + oneObject.getString("temAtendimentoUrgencia")+ "       " + "Centro Cirúrgico: " + oneObject.getString("temCentroCirurgico"));
                listaDados.add("Ambulatório: " + oneObject.getString("temAtendimentoAmbulatorial")+ "       " + "Obstetria: " + oneObject.getString("temObstetra"));
                listaDados.add("Neo-Natal:     " + oneObject.getString("temNeoNatal") + "       " + "Diálise: " + oneObject.getString("temDialise"));
                listaDados.add("  ");
                listaDados.add("Logradouro: " + oneObject.getString("logradouro") + ", " + oneObject.getString("numero"));
                listaDados.add("Bairro: " + oneObject.getString("bairro"));
                listaDados.add("Cidade: " + oneObject.getString("cidade") + " - " + oneObject.getString("uf"));
                if (oneObject.has("telefone")) {
                    listaDados.add("Telefone: " + oneObject.getString("telefone"));
                }
                listaDados.add("Atendimento: " + oneObject.getString("turnoAtendimento"));
                listDataChild.put(listaHeader.get(i), listaDados);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ExpandableUnidadeDTO(listaHeader,listDataChild);
    }

}
