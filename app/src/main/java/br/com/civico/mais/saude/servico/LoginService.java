package br.com.civico.mais.saude.servico;

import android.util.Log;

import com.loopj.android.http.HttpGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.converter.StreamConverter;
import br.com.civico.mais.saude.dto.Response;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Jônatas Rodrigues on 11/09/2016.
 */
public class LoginService {


    private String nome;
    private String email;
    private String senha;

    public LoginService(String nome,String senha,String email){
        this.nome=nome;
        this.email=email;
        this.senha=senha;
    }


    public Response autenticarUsuario() throws JSONException {
        Response response = new Response();
        response.setMensagem("Ocorreu um erro. Tente Novamente.");
        String result="";

        try {

            String url= ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/pessoas/autenticar";
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("email", this.email);
            httpget.setHeader("senha", this.senha);

            HttpResponse httpresponse = httpclient.execute(httpget);

            response.setStatusCodigo(httpresponse.getStatusLine().getStatusCode());

             if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_CREDENCIAIS_INVALIDAS)
                 response.setMensagem(ConstantesAplicacao.MENSAGEM_CRENDECIAIS_INVALIDAS);

            if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_EMAIL_NAO_CADASTRADO)
                response.setMensagem(ConstantesAplicacao.MENSAGEM_EMAIL_NAO_CADASTRADO);

            if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_OK){
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result= StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                response.setToken(httpresponse.getFirstHeader("apptoken").getValue().toString());
                response.setCodigoUsuario(getCodigoUsuario(result));
                response.setMensagem(ConstantesAplicacao.MENSAGEM_SUCESSO);
            }
         } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }
        return response;
    }



    public Response cadastrarUsuario() throws JSONException {
        Response response = new Response();
        response.setMensagem("Ocorreu um erro. Tente Novamente.");
        try {

            String url= ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/pessoas";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonobj = new JSONObject();
            jsonobj.accumulate("email", this.email);
            jsonobj.accumulate("nomeCompleto", this.nome);
            jsonobj.accumulate("nomeUsuario", this.nome);
            jsonobj.accumulate("senha", this.senha);

            StringEntity se = new StringEntity(jsonobj.toString());
            post.setHeader("Content-type", "application/json");
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

            response.setStatusCodigo(httpresponse.getStatusLine().getStatusCode());

            if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO){
                String result="";
                HttpEntity entity = httpresponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result= StreamConverter.convertStreamToString(instream);
                    instream.close();
                }
                response.setToken(httpresponse.getFirstHeader("apptoken").getValue().toString());
                response.setCodigoUsuario(getCodigoUsuario(result));
                response.setMensagem(ConstantesAplicacao.MENSAGEM_SUCESSO);
            }

            if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_PARAMETRO_INVALIDO)
                response.setMensagem(ConstantesAplicacao.MENSAGEM_PARAMETRO_INVALIDO);

            if(response.getStatusCodigo() == ConstantesAplicacao.STATUS_SERVICO_NOT_FOUND_CADASTRO)
                response.setMensagem(ConstantesAplicacao.MENSAGEM_SERVICO_NOT_FOUND_CADASTRO);

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }

        return response;
    }

    private Long getCodigoUsuario(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        return Long.valueOf(obj.getString("cod"));
    }

}
