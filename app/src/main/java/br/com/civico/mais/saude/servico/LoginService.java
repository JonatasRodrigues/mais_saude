package br.com.civico.mais.saude.servico;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
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
        try {
            String url= ConstantesAplicacao.URL_BASE + "/rest/pessoas/autenticar";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonobj = new JSONObject();
            jsonobj.put("email", this.email);
            jsonobj.put("senha ", this.senha);

            StringEntity se = new StringEntity(jsonobj.toString());
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

             if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CREDENCIAIS_INVALIDAS)
                 return ConstantesAplicacao.MENSAGEM_CRENDECIAIS_INVALIDAS;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_EMAIL_NAO_CADASTRADO)
                return ConstantesAplicacao.MENSAGEM_EMAIL_NAO_CADASTRADO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_OK)
                return ConstantesAplicacao.MENSAGEM_SUCESSO;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }
        return null;
    }



    public String cadastrarUsuario() throws JSONException {
        try {

            String url= ConstantesAplicacao.URL_BASE + "/rest/pessoas";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            JSONObject jsonobj = new JSONObject();
            jsonobj.put("nomeCompleto", this.nome);
            jsonobj.put("email", this.email);
            jsonobj.put("senha ", this.senha);

            StringEntity se = new StringEntity(jsonobj.toString());
            post.setEntity(se);
            HttpResponse httpresponse = httpclient.execute(post);

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_CADASTRO_SUCESSO)
                return ConstantesAplicacao.MENSAGEM_SUCESSO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_PARAMETRO_INVALIDO)
                return ConstantesAplicacao.MENSAGEM_PARAMETRO_INVALIDO;

            if(httpresponse.getStatusLine().getStatusCode() == ConstantesAplicacao.STATUS_SERVICO_NOT_FOUND_CADASTRO)
                return ConstantesAplicacao.MENSAGEM_SERVICO_NOT_FOUND_CADASTRO;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }

        return null;
    }
}
