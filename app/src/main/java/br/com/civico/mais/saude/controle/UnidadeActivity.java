package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.ExpandableDTO;
import br.com.civico.mais.saude.dto.Response;
import br.com.civico.mais.saude.exception.ErroServicoTCUException;
import br.com.civico.mais.saude.exception.GPSException;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.LoginService;
import br.com.civico.mais.saude.servico.UnidadeService;
import br.com.civico.mais.saude.util.MensagemUtil;

public class UnidadeActivity extends Activity {

    ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private Context context;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_consulta);
        expListView = (ExpandableListView) findViewById(R.id.unidadeListView);
         context=this;

        if(hasPermissions()) {
            location = new GPSService(context).getLocation();
            AsyncTask<Void, Void, ExpandableListAdapter> task = new AsyncTask<Void, Void, ExpandableListAdapter>() {

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(UnidadeActivity.this);
                    progressDialog.setMessage("Carregando...");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }

                @Override
                protected ExpandableListAdapter doInBackground(Void... voids) {
                    try {
                        ExpandableDTO dto = UnidadeService.getInstance(location).consumirServicoTCU();

                       return new ExpandableListUnidadeAdapter(context, dto.getListDataHeader(),
                                dto.getListDataChild());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ExpandableListAdapter result) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    configurarExpList();
                    expListView.setAdapter(result);
                }

            };
            task.execute((Void[]) null);

         //   } catch (ErroServicoTCUException e) {
         //       MensagemUtil.exibirMensagemErro(getLayoutInflater(), this, e.getMessage(), (ViewGroup) findViewById(R.id.layout_erro));
         //       voltarMenu();
         //   } catch (GPSException e) {
           //     MensagemUtil.exibirMensagemErro(getLayoutInflater(), this, e.getMessage(), (ViewGroup) findViewById(R.id.layout_erro));
             //   voltarMenu();
          //  } catch (JSONException e) {
            //    e.printStackTrace();
           // }

        } else {
            ActivityCompat.requestPermissions(UnidadeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void configurarExpList(){
        ColorDrawable linhaColor = new ColorDrawable(this.getResources().getColor(R.color.lime));
        expListView.setChildDivider(getResources().getDrawable(R.color.transparente));
        expListView.setDivider(linhaColor);
        expListView.setDividerHeight(2);
    }

    private void voltarMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean hasPermissions(){
        boolean permissionFineLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return (permissionCoarseLocation && permissionFineLocation);
    }

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
        //        return true;
        //    }

        return super.onOptionsItemSelected(item);
    }

}
