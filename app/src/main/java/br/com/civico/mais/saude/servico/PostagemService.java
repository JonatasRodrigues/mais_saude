package br.com.civico.mais.saude.servico;

import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.converter.StreamConverter;
import br.com.civico.mais.saude.dto.PostagemDTO;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 25/09/2016.
 */
public class  PostagemService {

    public List<PostagemDTO> buscarPostagensPorUnidade(String codigoUnidade,String token,Integer currentPage) {
        String result=null;
        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens/timeline?codObjetoDestino=" + codigoUnidade + "&codAplicativo=" +
                    ConstantesAplicacao.COD_APP_IDENTIFICADOR + "&pagina=" + currentPage;
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("appToken", token);

            HttpResponse httpresponse = httpclient.execute(httpget);

            if (httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_OK) {
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                     result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                return converterJsonParaObject(getJson(result));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<PostagemDTO> converterJsonParaObject(JSONArray jsonArray){
       List<PostagemDTO> lista = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                PostagemDTO dto = new PostagemDTO();

                JSONObject oneObject = jsonArray.getJSONObject(i);
                dto.setNomeAutor(URLDecoder.decode(oneObject.getString("nomeAutor"), "UTF-8"));
                dto.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray = oneObject.getJSONArray("conteudos");
                dto.setComentario(URLDecoder.decode(conteudoArray.getJSONObject(0).getString("texto"), "UTF-8"));
                dto.setPontuacao(Float.valueOf(conteudoArray.getJSONObject(0).getString("valor")));

                lista.add(dto);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public String cadastrarPostagem(String token, long codigoUsuario, String comentario, double pontuacao, long codigoUnidade) {

        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonAutor = new JSONObject();
            jsonAutor.accumulate("codPessoa", codigoUsuario);

            JSONObject jsonTipo = new JSONObject();
            jsonTipo.accumulate("codTipoPostagem", ConstantesAplicacao.COD_TIPO_POSTAGEM);

            JSONObject jsonObj = new JSONObject();
            jsonObj.accumulate("autor", jsonAutor);
            jsonObj.accumulate("tipo", jsonTipo);
            jsonObj.accumulate("codObjetoDestino", codigoUnidade);
            jsonObj.accumulate("codTipoObjetoDestino", ConstantesAplicacao.COD_TIPO_OBJETO);

            StringEntity se = new StringEntity(jsonObj.toString());
            post.setHeader("Content-type", "application/json");
            post.setHeader("appIdentifier", ConstantesAplicacao.COD_APP_IDENTIFICADOR);
            post.setHeader("appToken", token);
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

            if (httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO) {
                return cadastrarConteudoPostagem(token,comentario,pontuacao,httpresponse.getFirstHeader("location").getValue().toString());
            }else{
                return "Não foi possível realizar essa operação. Tente novamente mais tarde.";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String cadastrarConteudoPostagem(String token,String comentario,double pontuacao,String locationPostagem){
        try {
            String mensagem = URLEncoder.encode(comentario, "UTF-8");
            String json = "{\"texto\" : " + mensagem + ", \"valor\": "+pontuacao+"}";
            String url = locationPostagem + "/conteudos";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonObj = new JSONObject();
            jsonObj.accumulate("JSON", json);
            jsonObj.accumulate("texto", mensagem);
            jsonObj.accumulate("valor", pontuacao);

            StringEntity se = new StringEntity(jsonObj.toString());
            post.setHeader("Content-type", "application/json");
            post.setHeader("appToken", token);
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

            if (httpresponse.getStatusLine().getStatusCode() != ConstantesAplicacao.STATUS_CADASTRO_SUCESSO) {
                return "Não foi possível realizar essa operação. Tente novamente mais tarde.";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray getJson(String json) throws JSONException {
        return new JSONArray(json);
    }

}