package br.com.civico.mais.saude.servico;

import android.location.Location;
import android.os.NetworkOnMainThreadException;

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
import br.com.civico.mais.saude.exception.ErroServicoTCUException;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MedicamentoService extends AbstractService  {

    private static MedicamentoService medicamentoService ;

    public static MedicamentoService getInstance(){
        if(medicamentoService==null){
            medicamentoService= new MedicamentoService();
        }
        return medicamentoService;
    }

    @Override
    public ExpandableDTO consumirServicoTCU() throws JSONException {
        String result="";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/remedios?quantidade=30";

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);

            if(response.getStatusLine().getStatusCode() != ConstantesAplicacao.STATUS_OK)
                throw new ErroServicoTCUException("Erro ao recuperar informações. Por favor, tente mais tarde.");

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                result= StreamConverter.convertStreamToString(instream);
                instream.close();
            }
        } catch (NetworkOnMainThreadException e){
            result="[{\n" +
                    "  \"produto\": \"Produto A\",\n" +
                    "  \"principioAtivo\": \"Principio Ativo A\",\n" +
                    "  \"laboratorio\": \"Laboratorio A\",\n" +
                    "  \"ultimaAlteracao\": \"Ultima Alteracao A\"\n" +
                    "}," +
                    "{\n" +
                    "  \"produto\": \"Produto B\",\n" +
                    "  \"principioAtivo\": \"Principio Ativo B\",\n" +
                    "  \"laboratorio\": \"Laboratorio B\",\n" +
                    "  \"ultimaAlteracao\": \"Ultima Alteracao B\"\n" +
                    "}," +
                    "{\n" +
                    "  \"produto\": \"Produto C\",\n" +
                    "  \"principioAtivo\": \"Principio Ativo C\",\n" +
                    "  \"laboratorio\": \"Laboratorio C\",\n" +
                    "  \"ultimaAlteracao\": \"Ultima Alteracao C\"\n" +
                    "}]";
        }
        catch (IOException e) {
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
                listaHeader.add(oneObject.getString("produto"));
                listaDados.add("  ");
                listaDados.add("Principio Ativo: " + oneObject.getString("principioAtivo"));
                listaDados.add("Laboratorio: " + oneObject.getString("laboratorio"));
                listaDados.add("Ultima Alteracao: " + oneObject.getString("ultimaAlteracao"));
                listDataChild.put(listaHeader.get(i), listaDados);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ExpandableDTO(listaHeader,listDataChild);
    }
}
