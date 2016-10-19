package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListMedicamentoAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.medicamento.MedicamentoResponse;
import br.com.civico.mais.saude.servico.MedicamentoService;

public class  MedicamentoActivity extends Activity {
    private ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private Context context;

    private int visibleThreshold = 0;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicamento_consulta);
        expListView = (ExpandableListView) findViewById(R.id.medicamentoListView);
        context = this;
        carregaMedicamentos();
        expListView.setOnScrollListener(customScrollListener);
    }

    private void carregaMedicamentos() {
        AsyncTask<Void, Void, MedicamentoResponse> task = new AsyncTask<Void, Void, MedicamentoResponse>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MedicamentoActivity.this);
                progressDialog.setMessage("Carregando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected MedicamentoResponse doInBackground(Void... voids) {
                try {
                    MedicamentoService medicamentoService = new MedicamentoService();
                    return medicamentoService.consumirServicoTCU(currentPage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(MedicamentoResponse medicamentoResponse) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                if(medicamentoResponse.getStatusCodigo()== ConstantesAplicacao.STATUS_OK){
                    ExpandableListMedicamentoAdapter adapterMedicamento = (ExpandableListMedicamentoAdapter) expListView.getExpandableListAdapter();
                    if(adapterMedicamento == null) {
                        ExpandableListMedicamentoAdapter adapter = new ExpandableListMedicamentoAdapter(context, medicamentoResponse.getMedicamentoExpandableDTO().getListDataHeader(),
                                medicamentoResponse.getMedicamentoExpandableDTO().getListDataChild());
                        configurarExpList();
                        expListView.setAdapter(adapter);
                    }else{
                        adapterMedicamento.updateData(medicamentoResponse.getMedicamentoExpandableDTO().getListDataHeader(),
                                medicamentoResponse.getMedicamentoExpandableDTO().getListDataChild());
                        adapterMedicamento.notifyDataSetChanged();
                    }
                }else{
                    exibirMsgErro(medicamentoResponse.getMensagem());
                    voltarMenu();
                }
            }
        };
        task.execute((Void[]) null);
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

    private void voltarMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void configurarExpList(){
        ColorDrawable linhaColor = new ColorDrawable(this.getResources().getColor(R.color.lime));
        expListView.setChildDivider(getResources().getDrawable(R.color.transparente));
        expListView.setDivider(linhaColor);
        expListView.setDividerHeight(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private AbsListView.OnScrollListener customScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                carregaMedicamentos();
                loading = true;
            }
        }
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
