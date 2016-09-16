package br.com.civico.mais.saude.controle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.servico.LoginService;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView emailView;
    private EditText nome;
    private EditText password;
    private EditText passwordConfirm;
    private View mProgressView;
    private View mLoginFormView;
    private Button btnNovaConta;
    private Button btnLogin;
    private CheckBox chkCadastrado;

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

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private View.OnClickListener onClickCriarContaListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if(validarCampos()){
                showProgress(true);
                LoginService service = new LoginService();
                service.cadastrarUsuario();
                showProgress(false);
                showPopUpComentario();
            }
        }
    };


    public void showPopUpComentario(){

        View root = ((LayoutInflater)LoginActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comentario, null);
        final RatingBar rat = (RatingBar)root.findViewById(R.id.ratingBar);
        rat.setNumStars(5);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle(" Avaliação ");


        popDialog.setView(root);

        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                     //   txtView.setText(String.valueOf(rating.getProgress()));
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


    /*

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }*/

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
      /**  if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }*/
    }

    private void resetErrors(){
        emailView.setError(null);
        password.setError(null);
        passwordConfirm.setError(null);
    }

    private boolean validarCampos() {
        resetErrors();

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String senha = password.getText().toString();
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
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalido_email));
            focusView = emailView;
            isValido = false;
        }

        if (!isValido)// {
            focusView.requestFocus();
      //  } else {
         //   showProgress(true);
         //   mAuthTask = new UserLoginTask(email, password);
         //   mAuthTask.execute((Void) null);
      //  }
        return isValido;
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String senha) {
        return senha.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

  /**  @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

*/

}

