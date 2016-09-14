package br.com.civico.mais.saude.servico;

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
import br.com.civico.mais.saude.dto.UsuarioDTO;
import br.com.civico.mais.saude.exception.EmailJaCadastradoException;
import br.com.civico.mais.saude.exception.ErroServicoTCUException;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 11/09/2016.
 */
public class LoginService implements Service<UsuarioDTO>{

    private String url;
    private String email;

    public void cadastrarUsuario(){
      //  try {
            this.url= ConstantesAplicacao.URL_BASE + "/rest/pessoas?email=" + this.email;
         //   UsuarioDTO dto = consumirServicoTCU();
          //  if(dto==null){

         //   }else{
             //   throw new EmailJaCadastradoException("Email já cadastrado!");
         //   }
     //   } catch (JSONException e) {
     //      e.printStackTrace();
      //  }
    }


    @Override
    public UsuarioDTO consumirServicoTCU() throws JSONException {
        String result="";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(this.url);
            HttpResponse response = httpclient.execute(httpget);

            if(response.getStatusLine().getStatusCode() != ConstantesAplicacao.STATUS_OK)
                throw new ErroServicoTCUException("Erro ao recuperar informações. Por favor, tente mais tarde.");

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

    private UsuarioDTO converterJsonParaObject(JSONArray jsonArray){
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
        return new UsuarioDTO();
    }


    @Override
    public JSONArray getJson(String json) throws JSONException {
        return null;
    }
}
