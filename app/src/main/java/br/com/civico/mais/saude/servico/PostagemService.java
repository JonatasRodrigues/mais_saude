package br.com.civico.mais.saude.servico;

import android.util.Log;

import com.loopj.android.http.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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


    public List<PostagemDTO> buscarPostagensPorUnidade(String codigoUnidade,String token,String codigoAplicativo) {

        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens/timeline?codObjetoDestino=" + codigoUnidade + "&codAplicativo=" +
                    ConstantesAplicacao.APP_IDENTIFICADOR;
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("appToken", token);

            HttpResponse httpresponse = null;

            httpresponse = httpclient.execute(httpget);
/**
            response.setStatusCodigo(httpresponse.getStatusLine().getStatusCode());

            if (response.getStatusCodigo() == ConstantesAplicacao.STATUS_CREDENCIAIS_INVALIDAS)
                response.setMensagem(ConstantesAplicacao.MENSAGEM_CRENDECIAIS_INVALIDAS);

            if (response.getStatusCodigo() == ConstantesAplicacao.STATUS_EMAIL_NAO_CADASTRADO)
                response.setMensagem(ConstantesAplicacao.MENSAGEM_EMAIL_NAO_CADASTRADO);

            if (response.getStatusCodigo() == ConstantesAplicacao.STATUS_OK) {
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                response.setToken(httpresponse.getFirstHeader("apptoken").getValue().toString());
                response.setCodigoUsuario(getCodigoUsuario(result));
                response.setMensagem(ConstantesAplicacao.MENSAGEM_SUCESSO);
            }**/
        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
    }
    public void cadastrarPostagem(String token, long codigoUsuario, String comentario, double pontuacao, long codigoUnidade) {

        try {
            String url = ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/postagens";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonObj = new JSONObject();
            JSONObject jsonAutor = new JSONObject();
            jsonAutor.accumulate("codPessoa", codigoUsuario);
            jsonObj.accumulate("autor", jsonAutor);

            JSONObject jsonTipo = new JSONObject();
            jsonTipo.accumulate("codTipoPostagem", ConstantesAplicacao.TIPO_POSTAGEM);

            jsonObj.accumulate("tipo", jsonTipo);
            jsonObj.accumulate("codObjetoDestino", codigoUnidade);

            StringEntity se = new StringEntity(jsonObj.toString());
            post.setHeader("Content-type", "application/json");
            post.setHeader("appIdentifier", ConstantesAplicacao.APP_IDENTIFICADOR);
            post.setHeader("appToken", token);
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

            if (httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO) {
                String result = "";
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }

                cadastrarConteudoPostagem(token,comentario,pontuacao,httpresponse.getFirstHeader("location").getValue().toString());
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void cadastrarConteudoPostagem(String token,String comentario,double pontuacao,String locationPostagem){
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

            if (httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO) {
                String result = "";
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}