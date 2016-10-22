package br.com.civico.mais.saude.controle;

import  android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import org.json.JSONException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.unidade.UnidadeResponse;
import br.com.civico.mais.saude.util.ConexaoUtil;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.UnidadeService;

public class UnidadeActivity extends BaseActivity {

    private ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private Context context;
    private Location location;

    private int visibleThreshold = 0;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_consulta);
        context=this;
        expListView = (ExpandableListView) findViewById(R.id.unidadeListView);
        expListView.setOnScrollListener(customScrollListener);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
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
                return false;
            }
        });

        carregarUnidades();
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
                carregarUnidades();
                loading = true;
            }
         }
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    };

    private void carregarUnidades(){
        if(ConexaoUtil.hasConnection(context)){
            location = new GPSService(context).getLocation();
            if(location==null){
                exibirMsgErro(ConstantesAplicacao.MENSAGEM_NOT_FOUND_LOCATION);
                voltarMenu();
            }else {
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
                            return unidadeService.consumirServicoTCU(currentPage);
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

                            if(unidadeAdapter==null){
                                ExpandableListUnidadeAdapter adapter = new ExpandableListUnidadeAdapter(context, unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                        unidadeResponse.getExpandableUnidadeDTO().getListDataChild(), unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());
                                expListView.setAdapter(adapter);
                            }else{
                                unidadeAdapter.updateData(unidadeResponse.getExpandableUnidadeDTO().getListDataHeader(),
                                        unidadeResponse.getExpandableUnidadeDTO().getListDataChild(), unidadeResponse.getExpandableUnidadeDTO().getListMediaChild());
                                unidadeAdapter.notifyDataSetChanged();
                            }


                            //mantém apenas um group aberto
                            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                @Override
                                public void onGroupExpand(int groupPosition) {
                                    ExpandableListUnidadeAdapter customExpandAdapter = (ExpandableListUnidadeAdapter) expListView.getExpandableListAdapter();
                                    if (customExpandAdapter == null) {
                                        return;
                                    }
                                    for (int i = 0; i < customExpandAdapter.getGroupCount(); i++) {
                                        if (i != groupPosition) {
                                            expListView.collapseGroup(i);
                                        }
                                    }
                                }
                            });

                            configurarExpList();

                        } else {
                            exibirMsgErro(unidadeResponse.getMensagem());
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        voltarMenu();
    }
}
