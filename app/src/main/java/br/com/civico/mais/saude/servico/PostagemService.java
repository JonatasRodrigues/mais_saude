package br.com.civico.mais.saude.servico;

import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
public class PostagemService {

    public List<PostagemDTO> getMediaAvaliacaoPorUnidade(String codigoUnidade) {
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

                return converterJsonParaObject(getJson(result));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PostagemDTO> buscarPostagensPorUnidade(String codigoUnidade,String token) {
        String result=null;
        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens/timeline?codObjetoDestino=" + codigoUnidade + "&codAplicativo=" +
                    ConstantesAplicacao.COD_APP_IDENTIFICADOR;
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
                PostagemDTO dto1 = new PostagemDTO();
                PostagemDTO dto2 = new PostagemDTO();
                PostagemDTO dto3 = new PostagemDTO();
                PostagemDTO dto4 = new PostagemDTO();
                PostagemDTO dto5 = new PostagemDTO();
                JSONObject oneObject = jsonArray.getJSONObject(i);
                dto.setNomeAutor(oneObject.getString("nomeAutor"));
                dto.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray = oneObject.getJSONArray("conteudos");
                dto.setComentario(conteudoArray.getJSONObject(0).getString("texto"));
                dto.setPontuacao(Float.valueOf(conteudoArray.getJSONObject(0).getString("valor")));

                dto1.setNomeAutor(oneObject.getString("nomeAutor"));
                dto1.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray1 = oneObject.getJSONArray("conteudos");
                dto1.setComentario(conteudoArray1.getJSONObject(0).getString("texto"));
                dto1.setPontuacao(Float.valueOf(conteudoArray1.getJSONObject(0).getString("valor")));

                dto2.setNomeAutor(oneObject.getString("nomeAutor"));
                dto2.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray2 = oneObject.getJSONArray("conteudos");
                dto2.setComentario(conteudoArray2.getJSONObject(0).getString("texto"));
                dto2.setPontuacao(Float.valueOf(conteudoArray2.getJSONObject(0).getString("valor")));

                dto3.setNomeAutor(oneObject.getString("nomeAutor"));
                dto3.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray3 = oneObject.getJSONArray("conteudos");
                dto3.setComentario(conteudoArray3.getJSONObject(0).getString("texto"));
                dto3.setPontuacao(Float.valueOf(conteudoArray3.getJSONObject(0).getString("valor")));

                dto4.setNomeAutor(oneObject.getString("nomeAutor"));
                dto4.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray4 = oneObject.getJSONArray("conteudos");
                dto4.setComentario(conteudoArray4.getJSONObject(0).getString("texto"));
                dto4.setPontuacao(Float.valueOf(conteudoArray4.getJSONObject(0).getString("valor")));

                dto5.setNomeAutor(oneObject.getString("nomeAutor"));
                dto5.setDataPostagem(oneObject.getString("dataHoraPostagem"));
                JSONArray conteudoArray5 = oneObject.getJSONArray("conteudos");
                dto5.setComentario(conteudoArray5.getJSONObject(0).getString("texto"));
                dto5.setPontuacao(Float.valueOf(conteudoArray5.getJSONObject(0).getString("valor")));

                lista.add(dto);
                lista.add(dto1);
                lista.add(dto2);
                lista.add(dto3);
                lista.add(dto4);
                lista.add(dto5);


            } catch (JSONException e) {
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

            JSONObject jsonObj = new JSONObject();
            JSONObject jsonAutor = new JSONObject();
            jsonAutor.accumulate("codPessoa", codigoUsuario);
            jsonObj.accumulate("autor", jsonAutor);

            JSONObject jsonTipo = new JSONObject();
            jsonTipo.accumulate("codTipoPostagem", ConstantesAplicacao.COD_TIPO_POSTAGEM);

            jsonObj.accumulate("tipo", jsonTipo);
            jsonObj.accumulate("codObjetoDestino", codigoUnidade);

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
            String json = "{\"texto\" : "+comentario+", \"valor\": "+pontuacao+"}";
            String url = locationPostagem + "/conteudos";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonObj = new JSONObject();
            jsonObj.accumulate("JSON", json);
            jsonObj.accumulate("texto", comentario);
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