package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.Response;
import br.com.civico.mais.saude.servico.LoginService;
import br.com.civico.mais.saude.servico.PostagemService;
import br.com.civico.mais.saude.util.MensagemUtil;

public class LoginActivity extends Activity {

    private AutoCompleteTextView emailView;
    private EditText nome;
    private EditText password;
    private EditText passwordConfirm;
    private Button btnNovaConta;
    private Button btnLogin;
    private CheckBox chkCadastrado;
    private ProgressDialog progressDialog;
    private Context context;
    private SharedPreferences settings ;
    private SharedPreferences.Editor editor;
     AlertDialog.Builder popDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        popDialog = new AlertDialog.Builder(this);
        this.context=this;
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putString("codigoUnidade", getIntent().getStringExtra("codigoUnidade"));
        editor.putString("nomeUnidade", getIntent().getStringExtra("nomeUnidade"));
        String auth_token_string = settings.getString("token", "");

        if(auth_token_string==""){
            showPopUpAutenticacao();
        }else{
            editor.commit();
            Intent intent = new Intent(context, PostagemActivity.class);
            context.startActivity(intent);
        }
    }


    private void showPopUpAutenticacao(){

        View root = ((LayoutInflater)LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.login, null);

        emailView = (AutoCompleteTextView) root.findViewById(R.id.email);
        nome = (EditText) root.findViewById(R.id.nome);
        password = (EditText) root.findViewById(R.id.password);
        passwordConfirm = (EditText) root.findViewById(R.id.passwordConfirm);
        btnNovaConta = (Button) root.findViewById(R.id.btnNovaConta);
        btnLogin = (Button) root.findViewById(R.id.btnLogin);
        chkCadastrado = (CheckBox) root.findViewById(R.id.chkCadastrado);

        chkCadastrado.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    nome.setVisibility(View.GONE);
                    passwordConfirm.setVisibility(View.GONE);
                    btnNovaConta.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                } else {
                    nome.setVisibility(View.VISIBLE);
                    View focusView = nome;
                    focusView.requestFocus();
                    passwordConfirm.setVisibility(View.VISIBLE);
                    btnNovaConta.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                }
            }
        });

        btnNovaConta.setOnClickListener(onClickCriarContaListener);
        btnLogin.setOnClickListener(onClickLoginListener);

        String tempString="Autenticação";
        TextView myMsg = new TextView(this);
        SpannableString spanString = new SpannableString(tempString);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        myMsg.setText(spanString);

        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(20);
        myMsg.setTextColor(Color.WHITE);
        myMsg.setBackgroundColor(Color.GRAY);
        popDialog.setView(root);
        popDialog.setCustomTitle(myMsg);
        popDialog.create();

        popDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialog.dismiss();
                    voltar();
                    return true;
                }
                return false;
            }

        });

        popDialog.show();
    }


    public View.OnClickListener onClickLoginListener = new View.OnClickListener() {
        public void onClick(final View v) {

            final String nomeUsuario = nome.getText().toString();
            final String senhaUsuario = password.getText().toString();
            final String emailUsuario = emailView.getText().toString();

            AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Autenticando...");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }

                @Override
                protected Response doInBackground(Void... voids) {
                    try {
                        LoginService service = new LoginService(nomeUsuario,senhaUsuario,emailUsuario);
                        return service.autenticarUsuario();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Response result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if(ConstantesAplicacao.STATUS_OK==result.getStatusCodigo()){
                        editor.putString("token", result.getToken());
                        editor.putString("codigoUsuario", String.valueOf(result.getCodigoUsuario()));
                        editor.commit();

                        Intent intent = new Intent(context, PostagemActivity.class);
                        context.startActivity(intent);

                    }else {
                        exibirMsgErro(result.getMensagem());
                    }
                }
            };
            task.execute((Void[]) null);
        }
    };

    private View.OnClickListener onClickCriarContaListener = new View.OnClickListener() {
        public void onClick(final View v) {
            final String nomeUsuario = nome.getText().toString();
            final String senhaUsuario = password.getText().toString();
            final String emailUsuario = emailView.getText().toString();

            if(validarCampos(nomeUsuario,emailUsuario,senhaUsuario)) {
                AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Processando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected Response doInBackground(Void... voids) {
                        try {
                            LoginService service = new LoginService(nomeUsuario, senhaUsuario, emailUsuario);
                            return service.cadastrarUsuario();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Response result) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        if(ConstantesAplicacao.STATUS_OK==result.getStatusCodigo()){
                            editor.putString("token", result.getToken());
                            editor.putString("codigoUsuario", String.valueOf(result.getCodigoUsuario()));
                            editor.commit();

                            Intent intent = new Intent(context, PostagemActivity.class);
                            context.startActivity(intent);
                        }else {
                            exibirMsgErro(result.getMensagem());
                        }
                    }
                };
                task.execute((Void[]) null);
            }
        }
    };

    private void resetErrors(){
        nome.setError(null);
        emailView.setError(null);
        password.setError(null);
        passwordConfirm.setError(null);
    }

    private boolean validarCampos(String nomeUsuario,String email,String senha) {
        resetErrors();

        String senhaConfirmacao = passwordConfirm.getText().toString();

        boolean isValido = true;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(senha) && !isPasswordValid(senha)) {
            password.setError(getString(R.string.error_invalido_password));
            focusView = password;
            isValido = false;
        }else{
            if(!senha.equals(senhaConfirmacao)){
                passwordConfirm.setError(getString(R.string.error_password_nao_confere));
                focusView = passwordConfirm;
                isValido = false;
            }
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_email_obrigatorio));
            focusView = emailView;
            isValido = false;
        } else if (!isEmailValido(email)) {
            emailView.setError(getString(R.string.error_invalido_email));
            focusView = emailView;
            isValido = false;
        }

        if (TextUtils.isEmpty(nomeUsuario)) {
            nome.setError(getString(R.string.error_nome_obrigatorio));
            focusView = nome;
            isValido = false;
        }else if(isNomeValido(nomeUsuario)){
            nome.setError(getString(R.string.error_nome_invalido));
            focusView = nome;
            isValido = false;
        }

        if (!isValido)
            focusView.requestFocus();

        return isValido;
    }

    private boolean isNomeValido(String nome) {
        Pattern p = Pattern.compile("/^[A-Za-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]+$/");
        Matcher m = p.matcher(nome);
        return m.find() || nome.length()<3;
    }

    private boolean isEmailValido(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String senha) {
        return senha.length() >= 6;
    }

    private void exibirMsgErro(String mensagem){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_erro,(ViewGroup) findViewById(R.id.layout_erro));

        TextView text = (TextView) layout.findViewById(R.id.textErro);
        text.setText(mensagem);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    public void voltar() {
        Intent intent = new Intent(this, UnidadeActivity.class);
        startActivity(intent);
    }

}

