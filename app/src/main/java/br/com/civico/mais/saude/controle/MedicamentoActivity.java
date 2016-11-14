package br.com.civico.mais.saude.controle;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import org.json.JSONException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListMedicamentoAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.medicamento.MedicamentoResponse;
import br.com.civico.mais.saude.servico.MedicamentoService;
import br.com.civico.mais.saude.util.ConexaoUtil;

public class  MedicamentoActivity extends BaseActivity {
    private ExpandableListView expListView;
    private EditText searchTextBox;
    private ProgressDialog progressDialog;
    private Context context;

    private int visibleThreshold = 0;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean isPesquisa = false;
    private String searchValue = new String("");
    private AsyncTask<Void, Void, MedicamentoResponse> task;
    private  Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicamento_consulta);
        expListView = (ExpandableListView) findViewById(R.id.medicamentoListView);
        searchTextBox = (EditText) findViewById(R.id.txtSearchMedicamento);
        context = this;

        Button btnSearchMedicamento = (Button) findViewById(R.id.btnSearchMedicamento);
        btnSearchMedicamento.setOnClickListener(onClickListenerMedicamento);

        btnVoltar = (Button) findViewById(R.id.btnVoltarMed);
        btnVoltar.setOnClickListener(onClickListenerVoltarMedicamento);

        expListView.setOnScrollListener(customScrollListener);
        expListView.setOnGroupExpandListener(groupExpandListener); //mantém apenas um group aberto

        carregaMedicamentos();
    }

    private ExpandableListView.OnGroupExpandListener groupExpandListener =  new ExpandableListView.OnGroupExpandListener() {
        @Override
        public void onGroupExpand(int groupPosition) {
            ExpandableListMedicamentoAdapter customExpandAdapter = (ExpandableListMedicamentoAdapter) expListView.getExpandableListAdapter();
            if (customExpandAdapter == null) {
                return;
            }
            for (int i = 0; i < customExpandAdapter.getGroupCount(); i++) {
                if (i != groupPosition) {
                    expListView.collapseGroup(i);
                }
            }
        }
    };


    private View.OnClickListener onClickListenerVoltarMedicamento = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnVoltarMed){
                btnVoltar.setVisibility(View.INVISIBLE);
                isPesquisa=false;
                searchTextBox.setText("");
                pesquisaMedicamento();
            }
        }
    };

    private View.OnClickListener onClickListenerMedicamento = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSearchMedicamento){
                btnVoltar.setVisibility(View.VISIBLE);
                isPesquisa=true;
                pesquisaMedicamento();
            }
        }
    };

    private void pesquisaMedicamento() {
        currentPage = 0;
        previousTotal = 0;
        if(searchValue != null && !searchValue.isEmpty() && String.valueOf(searchTextBox.getText()).isEmpty()){
            ExpandableListMedicamentoAdapter adapter = null;
            expListView.setAdapter(adapter);
        }
        searchValue = String.valueOf(searchTextBox.getText());
        hideKeyboard(context, searchTextBox);
        carregaMedicamentos();
    }

    private void carregaMedicamentos() {
        if(ConexaoUtil.hasConnection(context)){
            if(task == null || task != null && task.getStatus() == AsyncTask.Status.FINISHED){
                task = new AsyncTask<Void, Void, MedicamentoResponse>() {

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(MedicamentoActivity.this);
                        progressDialog.setMessage("Buscando medicamentos...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected MedicamentoResponse doInBackground(Void... voids) {
                        try {
                            MedicamentoService medicamentoService = new MedicamentoService();
                            return medicamentoService.consumirServicoTCU(currentPage, searchValue);
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

                        if (medicamentoResponse.getStatusCodigo() == ConstantesAplicacao.STATUS_OK) {
                            ExpandableListMedicamentoAdapter adapterMedicamento = (ExpandableListMedicamentoAdapter) expListView.getExpandableListAdapter();
                            if (adapterMedicamento == null) {
                                ExpandableListMedicamentoAdapter adapter = new ExpandableListMedicamentoAdapter(context, medicamentoResponse.getMedicamentoExpandableDTO().getListDataHeader(),
                                        medicamentoResponse.getMedicamentoExpandableDTO().getListDataChild());
                                configurarExpList();
                                expListView.setAdapter(adapter);
                            } else {
                                if (isPrimeiraPesquisaPorTexto()) {
                                    ExpandableListMedicamentoAdapter adapter = new ExpandableListMedicamentoAdapter(context, medicamentoResponse.getMedicamentoExpandableDTO().getListDataHeader(),
                                            medicamentoResponse.getMedicamentoExpandableDTO().getListDataChild());
                                    configurarExpList();
                                    expListView.setAdapter(adapter);
                                }else {
                                    adapterMedicamento.updateData(medicamentoResponse.getMedicamentoExpandableDTO().getListDataHeader(),
                                            medicamentoResponse.getMedicamentoExpandableDTO().getListDataChild());
                                    adapterMedicamento.notifyDataSetChanged();
                                }
                            }
                        } else {
                            exibirMsgErro(medicamentoResponse.getMensagem());
                            voltarMenu();
                        }
                    }

                };
                task.execute((Void[]) null);
            }
        }else{
            exibirMsgErro(ConstantesAplicacao.MENSAGEM_SEM_CONEXAO_INTERNET);
        }
    }


    private boolean isPrimeiraPesquisaPorTexto() {
        return searchValue != null && !searchValue.isEmpty() && currentPage == 0;
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
            if( task != null && task.getStatus() ==  AsyncTask.Status.FINISHED) {
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        isPesquisa=false;
                        previousTotal = totalItemCount;
                        currentPage++;
                    }
                }
                if (!isPesquisa && !loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    carregaMedicamentos();
                    loading = true;
                }
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
    @Override
    public void onBackPressed() {
        voltarMenu();
    }
}
