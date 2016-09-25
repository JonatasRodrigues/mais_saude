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


    public String autenticarUsuario() throws JSONException {
        String mensagem="Ocorreu um erro. Tente Novamente.";

        try {

            String url= ConstantesAplicacao.URL_BASE_METAMODELO + "/rest/pessoas/autenticar";
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("email", this.email);
            httpget.setHeader("senha", this.senha);

            HttpResponse httpresponse = httpclient.execute(httpget);

             if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CREDENCIAIS_INVALIDAS)
                 mensagem= ConstantesAplicacao.MENSAGEM_CRENDECIAIS_INVALIDAS;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_EMAIL_NAO_CADASTRADO)
                mensagem= ConstantesAplicacao.MENSAGEM_EMAIL_NAO_CADASTRADO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_OK)
                mensagem= ConstantesAplicacao.MENSAGEM_SUCESSO;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }
        return mensagem;
    }



    public String cadastrarUsuario() throws JSONException {
        String mensagem="Ocorreu um erro. Tente Novamente.";

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

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO)
                mensagem= ConstantesAplicacao.MENSAGEM_SUCESSO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_PARAMETRO_INVALIDO)
                mensagem= ConstantesAplicacao.MENSAGEM_PARAMETRO_INVALIDO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_SERVICO_NOT_FOUND_CADASTRO)
                mensagem= ConstantesAplicacao.MENSAGEM_SERVICO_NOT_FOUND_CADASTRO;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }

        return mensagem;
    }
}
