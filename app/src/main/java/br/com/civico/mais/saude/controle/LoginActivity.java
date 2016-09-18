package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.servico.LoginService;
import br.com.civico.mais.saude.util.MensagemUtil;

public class LoginActivity extends Activity {

    public AutoCompleteTextView emailView;
    public EditText nome;
    public EditText password;
    private EditText passwordConfirm;
    private Button btnNovaConta;
    private Button btnLogin;
    private CheckBox chkCadastrado;
    private ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        nome = (EditText) findViewById(R.id.nome);
        password = (EditText) findViewById(R.id.password);
        passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
        btnNovaConta = (Button) findViewById(R.id.btnNovaConta );
        btnLogin = (Button) findViewById(R.id.btnLogin );
        chkCadastrado = (CheckBox) findViewById(R.id.chkCadastrado);
        context=this;

        chkCadastrado.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    nome.setVisibility(View.GONE);
                    passwordConfirm.setVisibility(View.GONE);
                    btnNovaConta.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                } else {
                    nome.setVisibility(View.VISIBLE);
                    passwordConfirm.setVisibility(View.VISIBLE);
                    btnNovaConta.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                }
            }
        });

        btnNovaConta.setOnClickListener(onClickCriarContaListener);
        btnLogin.setOnClickListener(onClickLoginListener);

    }

    public View.OnClickListener onClickLoginListener = new View.OnClickListener() {
        public void onClick(final View v) {

            final String nomeUsuario = nome.getText().toString();
            final String senhaUsuario = password.getText().toString();
            final String emailUsuario = emailView.getText().toString();

            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                String retorno;

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Processando...");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        LoginService service = new LoginService(nomeUsuario,senhaUsuario,emailUsuario);
                        retorno = service.autenticarUsuario();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        if(retorno == ConstantesAplicacao.MENSAGEM_SUCESSO){
                            showPopUpComentario();
                        }else {
                            MensagemUtil.exibirMensagemErro(getLayoutInflater(), context, retorno, (ViewGroup) findViewById(R.id.layout_erro));
                        }
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
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    String retorno;

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Processando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            LoginService service = new LoginService(nomeUsuario, senhaUsuario, emailUsuario);
                            retorno = service.cadastrarUsuario();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            if(retorno == ConstantesAplicacao.MENSAGEM_SUCESSO){
                                showPopUpComentario();
                            }else {
                                MensagemUtil.exibirMensagemErro(getLayoutInflater(), context, retorno, (ViewGroup) findViewById(R.id.layout_erro));
                            }
                        }
                    }
                };
                task.execute((Void[]) null);
            }
        }
    };


    public void showPopUpComentario(){

        View root = ((LayoutInflater)LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comentario, null);
        final RatingBar rat = (RatingBar)root.findViewById(R.id.ratingBar);
        rat.setNumStars(5);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
       // popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle(" Avaliação ");


        popDialog.setView(root);

        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();

    }

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

        if (TextUtils.isEmpty(nomeUsuario)) {
            nome.setError(getString(R.string.error_nome_obrigatorio));
            focusView = nome;
            isValido = false;
        }else if(isNomeValido(nomeUsuario)){
            nome.setError(getString(R.string.error_nome_invalido));
            focusView = nome;
            isValido = false;
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

        if (!isValido)
            focusView.requestFocus();

        return isValido;
    }

    private boolean isNomeValido(String nome) {
        Pattern p = Pattern.compile("[^A-Za-z]");
        Matcher m = p.matcher(nome);
        return m.find() || nome.length()<3;
    }

    private boolean isEmailValido(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String senha) {
        return senha.length() >= 6;
    }
}

