package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.servico.GPSService;

public class MainActivity extends Activity {

    ProgressDialog progressDialog;
     Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animation = AnimationUtils.loadAnimation(this,R.anim.anim_scale);
        Button  btnUnidade = (Button) findViewById(R.id.btnUnidade);
        btnUnidade.setOnClickListener(onClickListener);

        Button  btnMedicamento = (Button) findViewById(R.id.btnMedicamento);
        btnMedicamento.setOnClickListener(onClickListenerMedicamento);

    }

    private View.OnClickListener onClickListenerMedicamento = new View.OnClickListener() {
        public void onClick(View view) {
            if(view.getId() == R.id.btnMedicamento){
                view.startAnimation(animation);
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

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnUnidade){
                v.startAnimation(animation);
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
                    protected Void doInBackground(Void... arg0) {
                        Intent intent = new Intent(MainActivity.this, UnidadeActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    //    if (id == R.id.action_settings) {
      //      return true;
      //  }

        return super.onOptionsItemSelected(item);
    }
}
