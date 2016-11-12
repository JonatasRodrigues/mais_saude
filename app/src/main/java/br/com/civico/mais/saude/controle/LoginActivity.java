package br.com.civico.mais.saude.controle;

import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.LoginResponse;
import br.com.civico.mais.saude.servico.LoginService;

public class LoginActivity extends BaseActivity {

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
    private AlertDialog.Builder popDialog;
    private DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.popDialog = new AlertDialog.Builder(this);
        this.context=this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = settings.edit();
        this.editor.putString("codigoUnidade", getIntent().getStringExtra("codigoUnidade"));
        this.editor.putString("nomeUnidade", getIntent().getStringExtra("nomeUnidade"));
        String auth_token_string = settings.getString("token", "");
        String dataExpiracao =  settings.getString("dataExpiracaoToken", "");
        controlarAutenticacao(dataExpiracao,auth_token_string);

    }

    private void controlarAutenticacao(String dataExpiracao,String auth_token_string){
        if(!dataExpiracao.isEmpty() && !auth_token_string.isEmpty()){
            try {
                Calendar hoje = Calendar.getInstance();
                Calendar dataExpiracaoToken  = Calendar.getInstance();
                dataExpiracaoToken.setTime(sdf.parse(dataExpiracao));
                if(hoje.compareTo(dataExpiracaoToken)<=0){
                    editor.commit();
                    Intent intent = new Intent(context, PostagemActivity.class);
                    context.startActivity(intent);
                }else{
                    editor.clear();     // CLEAR ALL FIELDS
                    editor.commit();    // COMMIT CHANGES

                    TextView myMsg = new TextView(this);
                    myMsg.setText(ConstantesAplicacao.MENSAGEM_SESSAO_EXPIRADA);
                    myMsg.setPadding(10, 10, 10, 10);
                    myMsg.setGravity(Gravity.CENTER);
                    myMsg.setTextSize(20);
                    myMsg.setTextColor(Color.BLACK);

                    new AlertDialog.Builder(context)
                     .setView(myMsg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showPopUpAutenticacao();
                        }
                     }).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            showPopUpAutenticacao();
        }
    }

    private void showPopUpAutenticacao(){
        View root = ((LayoutInflater)LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.login, null);

        this.emailView = (AutoCompleteTextView) root.findViewById(R.id.email);
        this.nome = (EditText) root.findViewById(R.id.nome);
        this.password = (EditText) root.findViewById(R.id.password);
        this.passwordConfirm = (EditText) root.findViewById(R.id.passwordConfirm);
        this.btnNovaConta = (Button) root.findViewById(R.id.btnNovaConta);
        this.btnLogin = (Button) root.findViewById(R.id.btnLogin);
        this.chkCadastrado = (CheckBox) root.findViewById(R.id.chkCadastrado);

        this.chkCadastrado.setOnClickListener(onClickListenerCheck);
        this.btnNovaConta.setOnClickListener(onClickListenerCriarConta);
        this. btnLogin.setOnClickListener(onClickListenerLogin);

        String titulo="Autenticação";
        TextView myMsg = new TextView(this);
        SpannableString spanString = new SpannableString(titulo);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        myMsg.setText(spanString);

        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(20);
        myMsg.setTextColor(Color.WHITE);
        myMsg.setBackgroundColor(Color.BLUE);
        popDialog.setView(root);
        popDialog.setCancelable(false);
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

    private View.OnClickListener onClickListenerCheck = new View.OnClickListener() {
        public void onClick(final View v) {
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
    };


    private View.OnClickListener onClickListenerLogin = new View.OnClickListener() {
        public void onClick(final View v) {

            final String nomeUsuario = nome.getText().toString();
            final String senhaUsuario = password.getText().toString();
            final String emailUsuario = emailView.getText().toString();

            AsyncTask<Void, Void, LoginResponse> task = new AsyncTask<Void, Void, LoginResponse>() {

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Autenticando...");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }

                @Override
                protected LoginResponse doInBackground(Void... voids) {
                    try {
                        LoginService service = new LoginService(nomeUsuario,senhaUsuario,emailUsuario);
                        return service.autenticarUsuario();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(LoginResponse result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if(ConstantesAplicacao.STATUS_OK==result.getStatusCodigo()){
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, +7);//Token é válido por 07 dias
                        editor.putString("dataExpiracaoToken",sdf.format(calendar.getTime()));
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

    private View.OnClickListener onClickListenerCriarConta = new View.OnClickListener() {
        public void onClick(final View v) {
            final String nomeUsuario = nome.getText().toString();
            final String senhaUsuario = password.getText().toString();
            final String emailUsuario = emailView.getText().toString();

            if(validarCampos(nomeUsuario,emailUsuario,senhaUsuario)) {
                AsyncTask<Void, Void, LoginResponse> task = new AsyncTask<Void, Void, LoginResponse>() {

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Processando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected LoginResponse doInBackground(Void... voids) {
                        try {
                            LoginService service = new LoginService(nomeUsuario, senhaUsuario, emailUsuario);
                            return service.cadastrarUsuario();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(LoginResponse result) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        if(result != null && ConstantesAplicacao.STATUS_OK==result.getStatusCodigo()){
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, +7);//Token é válido por 07 dias
                            editor.putString("dataExpiracaoToken",sdf.format(calendar.getTime()));
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
        if (TextUtils.isEmpty(senha) || !isPasswordValid(senha)) {
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
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(nome);
        return m.find() || nome.length()<3;
    }

    private boolean isEmailValido(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String senha) {
        return senha.length() >= 6;
    }

    private void voltar() {
        Intent intent = new Intent(this, UnidadeActivity.class);
        startActivity(intent);
    }

}

