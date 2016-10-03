package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.dto.ExpandableDTO;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.UnidadeService;

public class UnidadeActivity extends Activity {

    private ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private Context context;
    private Location location;

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

    private boolean hasPermissions(){
        boolean permissionFineLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return (permissionCoarseLocation && permissionFineLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        voltarMenu();
    }

    private void voltarMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
