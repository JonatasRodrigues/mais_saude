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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.unidade.UnidadeResponse;
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
        context=this;
        expListView = (ExpandableListView) findViewById(R.id.unidadeListView);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
                ExpandableListUnidadeAdapter customExpandAdapter = (ExpandableListUnidadeAdapter)expListView.getExpandableListAdapter();

                if(!parent.isGroupExpanded(groupPosition)){
                    final String childUnidade = (String) customExpandAdapter.getChild(groupPosition, 2);
                    final String[]codigoUnidade = childUnidade.split(":");
                    customExpandAdapter.setCodigoUnidade(codigoUnidade[1].trim());
                 }

                return false;
            }
        });

        if(hasPermissions()) {
            carregarUnidades();
        } else {
            ActivityCompat.requestPermissions(UnidadeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void carregarUnidades(){
        location = new GPSService(context).getLocation();
        if(location==null){
            exibirMsgErro("Ocorreu um erro ao tentar recuperar sua localização. Por favor, verifique sua conexão ou GPS.");
            voltarMenu();
        }else{
            AsyncTask<Void, Void, UnidadeResponse> task = new AsyncTask<Void, Void, UnidadeResponse>() {

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(UnidadeActivity.this);
                    progressDialog.setMessage("Carregando...");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }

                @Override
                protected UnidadeResponse doInBackground(Void... voids) {
                    try {
                        UnidadeService unidadeService = new UnidadeService(location);
                        return unidadeService.consumirServicoTCU();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(UnidadeResponse unidadeResponse) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if(unidadeResponse.getStatusCodigo()== ConstantesAplicacao.STATUS_OK){
                        ExpandableListUnidadeAdapter adapter = new ExpandableListUnidadeAdapter(context, unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                unidadeResponse.getExpandableUnidadeDTO().getListDataChild(),unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());

                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {
                                ExpandableListUnidadeAdapter customExpandAdapter = (ExpandableListUnidadeAdapter)expListView.getExpandableListAdapter();
                                if (customExpandAdapter == null) {return;}
                                for (int i = 0; i < customExpandAdapter.getGroupCount(); i++) {
                                    if (i != groupPosition) {
                                        expListView.collapseGroup(i);
                                    }
                                }
                            }
                        });

                        configurarExpList();
                        expListView.setAdapter(adapter);
                    }else{
                        exibirMsgErro(unidadeResponse.getMensagem());
                        voltarMenu();
                    }
                }
            };
            task.execute((Void[]) null);
        }
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
