package br.com.civico.mais.saude.servico;

import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;

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
import br.com.civico.mais.saude.dto.medicamento.MedicamentoExpandableDTO;
import br.com.civico.mais.saude.dto.medicamento.MedicamentoResponse;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MedicamentoService {

    public MedicamentoService() {

    }

    public MedicamentoResponse buscarPorPrincipioAtivo(int pagina,String principioAtivo) {
        MedicamentoResponse responseMedicamento = new MedicamentoResponse();
        String result = "";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/remedios?quantidade" + ConstantesAplicacao.QTD_RETORNO_SERVICO + "&pagina=" + pagina;
            url = url.concat("&campos=produto,ultimaAlteracao,codBarraEan,apresentacao," +
                    "registro,classeTerapeutica,principioAtivo,cnpj,laboratorio,codBarraEan,pmc0");
            url = url.concat("&principioAtivo=").concat(URLEncoder.encode(principioAtivo, "UTF-8"));

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            responseMedicamento.setStatusCodigo(response.getStatusLine().getStatusCode());

            if (responseMedicamento.getStatusCodigo() == ConstantesAplicacao.STATUS_OK) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                responseMedicamento.setMedicamentoExpandableDTO(converterJsonParaObject(getJson(result)));

            } else {
                responseMedicamento.setMensagem("Erro ao recuperar informações. Por favor, tente mais tarde.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMedicamento;
    }

    public MedicamentoResponse consumirServicoTCU(int pagina, String produto) throws JSONException {
        MedicamentoResponse medicamentoResponse = new MedicamentoResponse();
        String result = "";
        try {
            String url = ConstantesAplicacao.URL_BASE + "/rest/remedios?quantidade=" + ConstantesAplicacao.QTD_RETORNO_SERVICO + "&pagina=" + pagina;
            url = url.concat("&campos=produto,ultimaAlteracao,codBarraEan,apresentacao," +
                    "registro,classeTerapeutica,principioAtivo,cnpj,laboratorio,codBarraEan,pmc0");
            if (produto != null && !produto.isEmpty()) {
                //Se for numérico
                boolean digitsOnly = TextUtils.isDigitsOnly(produto);
                if (!digitsOnly) {
                    url = url.concat("&produto=").concat(URLEncoder.encode(produto, "UTF-8"));
                } else {
                    url = url.concat("&codBarraEan=").concat(URLEncoder.encode(produto, "UTF-8"));
                }
            }

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);

            medicamentoResponse.setStatusCodigo(response.getStatusLine().getStatusCode());

            if (medicamentoResponse.getStatusCodigo() == ConstantesAplicacao.STATUS_OK) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                medicamentoResponse.setMedicamentoExpandableDTO(converterJsonParaObject(getJson(result)));

            } else {
                medicamentoResponse.setMensagem("Erro ao recuperar informações. Por favor, tente mais tarde.");
            }
        } catch (NetworkOnMainThreadException e) {
            result = "[{\n" +
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

            medicamentoResponse.setMedicamentoExpandableDTO(converterJsonParaObject(getJson(result)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return medicamentoResponse;
    }

    private MedicamentoExpandableDTO converterJsonParaObject(JSONArray jsonArray) {
        List<String> listaHeader = new ArrayList<>();
        List<String> listaDados;
        HashMap<String, List<String>> listDataChild = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                listaDados = new ArrayList<>();
                JSONObject oneObject = jsonArray.getJSONObject(i);
                String produto = oneObject.getString("produto");
                String codigoBarra = oneObject.getString("codBarraEan");
                String idHash = produto + ConstantesAplicacao.SPLIT_CARACTER + codigoBarra;
                listaHeader.add(idHash);
                listaDados.add("CNPJ: " + oneObject.getString("cnpj"));
                listaDados.add("Laboratório: " + oneObject.getString("laboratorio"));
                listaDados.add("");
                listaDados.add("Registro: " + oneObject.getString("registro"));
                listaDados.add("Classe Terapêutica: " + oneObject.getString("classeTerapeutica"));
                listaDados.add("Princípio Ativo: " + oneObject.getString("principioAtivo"));
                listaDados.add("Apresentação: " + oneObject.getString("apresentacao"));
                listaDados.add("Código de Barra: " + oneObject.getString("codBarraEan"));
                listaDados.add("Última Alteração: " + oneObject.getString("ultimaAlteracao"));
                listaDados.add("");
                String pmc0 = oneObject.getString("pmc0");
                if (!pmc0.equalsIgnoreCase("0.0")) {
                    listaDados.add("*Preço Máximo ao Consumidor: R$" + pmc0.replace(".", ","));
                } else {
                    listaDados.add("*Preço Máximo ao Consumidor: Não informado");
                }
                listDataChild.put(idHash, listaDados);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new MedicamentoExpandableDTO(listaHeader, listDataChild);
    }

    public JSONArray getJson(String json) throws JSONException {
        return new JSONArray(json);
    }
}
