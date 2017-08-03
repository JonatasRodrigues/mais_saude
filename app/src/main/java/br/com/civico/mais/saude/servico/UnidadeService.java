package br.com.civico.mais.saude.servico;

 import android.location.Location;

import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
 import java.net.URLEncoder;
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.converter.StreamConverter;
 import br.com.civico.mais.saude.dto.AvaliacaoResponse;
 import br.com.civico.mais.saude.dto.unidade.ExpandableUnidadeDTO;
 import br.com.civico.mais.saude.dto.unidade.UnidadeResponse;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class UnidadeService {

    private Location location;
    private static final Integer RAIO = 10;

    public UnidadeService(){}

    public UnidadeService(Location location){
        this.location=location;
    }

    public AvaliacaoResponse getMediaAvaliacaoPorUnidade(String codigoUnidade) {
        String result=null;
        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens/tipopostagem/" +
                    ConstantesAplicacao.COD_TIPO_POSTAGEM + "/tipoobjeto/" + ConstantesAplicacao.COD_TIPO_OBJETO +
                    "/objeto/" + codigoUnidade;

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);

            HttpResponse httpresponse = httpclient.execute(httpget);

            if (httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_OK) {
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                return converterJsonParaObjectMedia(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AvaliacaoResponse converterJsonParaObjectMedia(String result){
        AvaliacaoResponse avaliacaoResponse = new AvaliacaoResponse();
        try {
            JSONObject obj = new JSONObject(result);
            avaliacaoResponse.setMediaAvaliacao(Float.parseFloat(obj.getString("media")));
            avaliacaoResponse.setQtdAvaliacao(obj.getString("contagem"));
            return avaliacaoResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UnidadeResponse consumirServicoTCU(Integer currentPage, String nomeUnidade) throws JSONException{
        UnidadeResponse unidadeResponse = new UnidadeResponse();
        String result="";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/estabelecimentos";

            if(nomeUnidade != null && !nomeUnidade.isEmpty()){
                url =  url.concat("?pagina=").concat(String.valueOf(currentPage)).concat("&quantidade=15")
                        .concat("&nomeFantasia=").concat(URLEncoder.encode(nomeUnidade, "UTF-8"));
            }else{
                url = url.concat("/latitude/").concat(String.valueOf(this.location.getLatitude())).concat("/longitude/")
                        .concat(String.valueOf(this.location.getLongitude())).concat("/raio/").concat(String.valueOf(RAIO))
                        .concat("?pagina=").concat(String.valueOf(currentPage)).concat("&quantidade=15");
            }

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
                String codigoUnidade = oneObject.getString("codUnidade");
                listaDados.add("Código: " + codigoUnidade);
                listaDados.add("Vinculo SUS: " + oneObject.getString("vinculoSus"));
                listaDados.add("Emergência:  " + oneObject.getString("temAtendimentoUrgencia")+ "       " + "Centro Cirúrgico: " + oneObject.getString("temCentroCirurgico"));
                listaDados.add("Ambulatório: " + oneObject.getString("temAtendimentoAmbulatorial")+ "       " + "Obstetria: " + oneObject.getString("temObstetra"));
                listaDados.add("Neo-Natal:     " + oneObject.getString("temNeoNatal") + "       " + "Diálise: " + oneObject.getString("temDialise"));
                listaDados.add("  ");

                if (oneObject.has("logradouro") && oneObject.has("numero")) {
                    listaDados.add("Logradouro: " + oneObject.getString("logradouro") + ", " + oneObject.getString("numero"));
                }else{
                    listaDados.add("Logradouro: - ");
                }

                if (oneObject.has("bairro")) {
                    listaDados.add("Bairro: " + oneObject.getString("bairro"));
                }else{
                    listaDados.add("Bairro: - ");
                }

                if (oneObject.has("cidade") && oneObject.has("uf")) {
                    listaDados.add("Cidade: " + oneObject.getString("cidade") + " - " + oneObject.getString("uf"));
                }else{
                    listaDados.add("Cidade: - ");
                }

                if (oneObject.has("telefone")) {
                    listaDados.add("Telefone: " + oneObject.getString("telefone"));
                }else{
                    listaDados.add("Telefone: - ");
                }
                listaDados.add("Latitude: " + oneObject.getString("lat") + "   /    " + "Longitude: " + oneObject.getString("long"));
                listaDados.add("  ");
                listaDados.add("Atendimento: " + oneObject.getString("turnoAtendimento"));

                listDataChild.put(listaHeader.get(i), listaDados);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ExpandableUnidadeDTO(listaHeader,listDataChild);
    }

    public JSONArray getJson(String json) throws JSONException {
        return new JSONArray(json);
    }

}
