package br.com.civico.mais.saude.controle;

import  android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.cache.InternalStorage;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.AvaliacaoResponse;
import br.com.civico.mais.saude.dto.unidade.UnidadeResponse;
import br.com.civico.mais.saude.util.ConexaoUtil;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.UnidadeService;

public class UnidadeActivity extends BaseActivity {

    private String TAG = "UnidadeActivity";

    private ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private Context context;
    private Location location;

    private int visibleThreshold = 0;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean isPesquisa = false;

    private EditText searchTextBox;
    private String searchValue = new String("");
    private AsyncTask<Void, Void, UnidadeResponse> task;

    private Button btnVoltar;

    private int ultimoExpandido = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_consulta);
        context=this;
        searchTextBox = (EditText) findViewById(R.id.SearchUnidade);
        Button btnSearchUnidade = (Button) findViewById(R.id.btnSearchUnidade);
        btnSearchUnidade.setOnClickListener(onClickListenerBuscarUnidade);

        btnVoltar = (Button) findViewById(R.id.btnVoltarUnd);
        btnVoltar.setOnClickListener(onClickListenerVoltarUnidade);

        expListView = (ExpandableListView) findViewById(R.id.unidadeListView);
        expListView.setOnScrollListener(customScrollListener);
        expListView.setOnGroupClickListener(onGroupClickListener);

        if(getIntent().hasExtra("valorPesquisa") && !"".equals(getIntent().getStringExtra("valorPesquisa"))){
            previousTotal = 0;
            searchTextBox.setText(getIntent().getStringExtra("valorPesquisa"));
            btnVoltar.setVisibility(View.VISIBLE);
        }

        carregarUnidades();
    }

    private View.OnClickListener onClickListenerVoltarUnidade = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnVoltarUnd){
                btnVoltar.setVisibility(View.INVISIBLE);
                isPesquisa=false;
                searchTextBox.setText("");
                ExpandableListUnidadeAdapter adapter = null;
                expListView.setAdapter(adapter);
                pesquisaUnidade();
            }
        }
    };

    private View.OnClickListener onClickListenerBuscarUnidade = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSearchUnidade){
                btnVoltar.setVisibility(View.VISIBLE);
                isPesquisa=true;
                pesquisaUnidade();
            }
        }
    };

    private void pesquisaUnidade() {
        currentPage = 0;
        previousTotal = 0;
        if(searchValue != null && !searchValue.isEmpty() && String.valueOf(searchTextBox.getText()).isEmpty()){
            ExpandableListUnidadeAdapter adapter = null;
            expListView.setAdapter(adapter);
        }
        searchValue = String.valueOf(searchTextBox.getText());
        hideKeyboard(context,searchTextBox);
        limparCache();
        carregarUnidades();
    }

    private void limparCache(){
        InternalStorage.deleteCache(context, ConstantesAplicacao.KEY_CACHE_UNIDADE);
        InternalStorage.deleteCache(context, ConstantesAplicacao.KEY_CACHE_HEADER_UNIDADE);
        InternalStorage.deleteCache(context, ConstantesAplicacao.KEY_CACHE_lIST_UNIDADE);
        InternalStorage.deleteCache(context, ConstantesAplicacao.KEY_CACHE_MEDIA_UNIDADE);
    }

    private ExpandableListView.OnGroupClickListener  onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v,int groupPosition, long id) {
            ExpandableListUnidadeAdapter customExpandAdapter = (ExpandableListUnidadeAdapter)expListView.getExpandableListAdapter();

            if(!parent.isGroupExpanded(groupPosition)){
                final String childUnidade = (String) customExpandAdapter.getChild(groupPosition, 2);
                final String childLatLong = (String) customExpandAdapter.getChild(groupPosition, 12);
                String latLong[] = childLatLong.split("/");

                final String[]codigoUnidade = childUnidade.split(":");
                final String[]latitude = latLong[0].split(":");
                final String[]longitude = latLong[1].split(":");

                customExpandAdapter.setNomeUnidade((String) customExpandAdapter.getGroup(groupPosition));
                customExpandAdapter.setCodigoUnidade(codigoUnidade[1].trim());
                customExpandAdapter.setLatitude(Double.valueOf(latitude[1].trim()));
                customExpandAdapter.setLongitute(Double.valueOf(longitude[1].trim()));
            }

            if(ultimoExpandido != -1){
                expListView.collapseGroup(ultimoExpandido);
            }

            if(ultimoExpandido!=groupPosition){
                expListView.expandGroup(groupPosition);
                ultimoExpandido = groupPosition;
            }else{
                ultimoExpandido=-1;
            }

            return true;
        }
    };

    private AbsListView.OnScrollListener customScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    isPesquisa=false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!isPesquisa &&!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                carregarUnidades();
                loading = true;
            }
        }
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    };

    private void carregarUnidades(){
        if(InternalStorage.hasCache(context,ConstantesAplicacao.KEY_CACHE_UNIDADE)){
            carregarCache();
            limparCache();
        }else{
            carregarPeloServico();
        }
    }

    private void carregarCache(){
        try {
            List<String> listDataHeader= (List<String>) InternalStorage.readObject(context, ConstantesAplicacao.KEY_CACHE_HEADER_UNIDADE);
            HashMap<String, List<String>> listDataChild = (HashMap<String, List<String>>) InternalStorage.readObject(context,ConstantesAplicacao.KEY_CACHE_lIST_UNIDADE);
            HashMap<String, AvaliacaoResponse> listMediaChild = (HashMap<String, AvaliacaoResponse>) InternalStorage.readObject(context, ConstantesAplicacao.KEY_CACHE_MEDIA_UNIDADE);

            if(listDataChild.size() < ConstantesAplicacao.QTD_RETORNO_SERVICO){
                expListView.setOnScrollListener(null);
            }

            ExpandableListUnidadeAdapter adapter = new ExpandableListUnidadeAdapter(context,listDataHeader,listDataChild,listMediaChild );
            expListView.setAdapter(adapter);
            configurarExpList();

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void carregarPeloServico(){
        if(ConexaoUtil.hasConnection(context)){
            location = new GPSService(context).getLocation();
            if(location==null){
                exibirMsgErro(ConstantesAplicacao.MENSAGEM_NOT_FOUND_LOCATION);
                voltarMenu();
            }else {
                if (task == null || task.getStatus() == AsyncTask.Status.FINISHED) {

                    task = new AsyncTask<Void, Void, UnidadeResponse>() {

                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(UnidadeActivity.this);
                            progressDialog.setMessage("Buscando unidades...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }

                        @Override
                        protected UnidadeResponse doInBackground(Void... voids) {
                            try {
                                UnidadeService unidadeService = new UnidadeService(location);
                                return unidadeService.consumirServicoTCU(currentPage, searchValue);
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

                            if (unidadeResponse.getStatusCodigo() == ConstantesAplicacao.STATUS_OK) {
                                ExpandableListUnidadeAdapter unidadeAdapter = (ExpandableListUnidadeAdapter) expListView.getExpandableListAdapter();

                                if (unidadeAdapter == null) {
                                    ExpandableListUnidadeAdapter adapter = new ExpandableListUnidadeAdapter(context, unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                            unidadeResponse.getExpandableUnidadeDTO().getListDataChild(), unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());
                                    expListView.setAdapter(adapter);
                                } else {
                                    if (isPrimeiraPesquisaPorTexto()) {
                                        ExpandableListUnidadeAdapter adapter = new ExpandableListUnidadeAdapter(context, unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                                unidadeResponse.getExpandableUnidadeDTO().getListDataChild(), unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());
                                        adapter.setValorPesquisa(searchValue);
                                        expListView.setAdapter(adapter);
                                    } else {
                                        unidadeAdapter.updateData(unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                                unidadeResponse.getExpandableUnidadeDTO().getListDataChild(), unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());
                                        unidadeAdapter.notifyDataSetChanged();
                                    }
                                }
                                configurarExpList();

                            } else {
                                exibirMsgErro(unidadeResponse.getMensagem());
                                voltarMenu();
                            }
                        }
                    };
                    task.execute((Void[]) null);
                }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        voltarMenu();
    }
}
