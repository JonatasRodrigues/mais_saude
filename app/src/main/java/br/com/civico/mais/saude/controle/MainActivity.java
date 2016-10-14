package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import br.com.civico.mais.saude.R;

public class MainActivity extends Activity {

    private ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context=this;

        Button  btnUnidade = (Button) findViewById(R.id.btnUnidade);
        btnUnidade.setOnClickListener(onClickListenerUnidade);

        Button  btnMedicamento = (Button) findViewById(R.id.btnMedicamento);
        btnMedicamento.setOnClickListener(onClickListenerMedicamento);

        Button  btnSobre = (Button) findViewById(R.id.btnSobre);
        btnSobre.setOnClickListener(onClickListenerSobre);

        Button  btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(onClickListenerSair);

    }

    private View.OnClickListener onClickListenerMedicamento = new View.OnClickListener() {
        public void onClick(View view) {
            if(view.getId() == R.id.btnMedicamento){
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Processando...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        Intent intent = new Intent(MainActivity.this, MedicamentoActivity.class);
                        startActivity(intent);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                };
                task.execute((Void[]) null);
            }
        }
    };

    private View.OnClickListener onClickListenerSobre = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSobre){
                Intent intent = new Intent(MainActivity.this, SobreActivity.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener onClickListenerUnidade = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnUnidade){
                Intent intent = new Intent(MainActivity.this, UnidadeActivity.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener onClickListenerSair = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSair){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.clear();     // CLEAR ALL FILEDS
                                editor.commit();    // COMMIT CHANGES

                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(startMain);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deseja encerrar o aplicativo?").setPositiveButton("Ok", dialogClickListener)
                        .setNegativeButton("Cancelar", dialogClickListener).show();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}
}
