package br.com.civico.mais.saude.controle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.adapter.ListViewPostagemAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.PostagemDTO;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.util.ConexaoUtil;
import br.com.civico.mais.saude.servico.PostagemService;

public class  PostagemActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private Context context;
    private ListView listView;
    private TextView lblSemComentario;
    private TextView tituloComentario;
    private float pontuacao=5;

    private int visibleThreshold = 0;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;

    private  Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentario_list);
        context = this;
        this.listView = (ListView) findViewById(R.id.listComentario);
        this.listView.setOnScrollListener(customScrollListener);

        this.lblSemComentario = (TextView) findViewById(R.id.semComentario);
        this.lblSemComentario.setVisibility(View.GONE);
        this.tituloComentario = (TextView) findViewById(R.id.tituloComentario);

        if(ConexaoUtil.hasConnection(context)){
            carregarPostagens();
        }else{
            exibirMsgErro(ConstantesAplicacao.MENSAGEM_SEM_CONEXAO_INTERNET);
        }

        Button btnCriarPostagem = (Button) findViewById(R.id.btnNovoComentario);
        btnCriarPostagem.setOnClickListener(onCriarPostagemListener);
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
                carregarPostagens();
                loading = true;
            }
        }
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    };

    private void carregarPostagens(){
        this.lblSemComentario.setVisibility(View.GONE);
        AsyncTask<Void, Void, List<PostagemDTO>> task = new AsyncTask<Void, Void, List<PostagemDTO>>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(PostagemActivity.this);
                progressDialog.setMessage("Carregando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected List<PostagemDTO> doInBackground(Void... voids) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                String auth_token_string = settings.getString("token", "");
                String codigoUnidade = settings.getString("codigoUnidade", "");
                return new PostagemService().buscarPostagensPorUnidade(codigoUnidade, auth_token_string,currentPage);

            }

            @Override
            protected void onPostExecute(List<PostagemDTO> result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                tituloComentario.setText("Comentários");

                ListViewPostagemAdapter listAdapter = (ListViewPostagemAdapter) listView.getAdapter();

                if(listAdapter==null){
                    if(result==null){
                        lblSemComentario.setVisibility(View.VISIBLE);
                    }else{
                        ListViewPostagemAdapter adapter = new ListViewPostagemAdapter(context,R.layout.customer_postagem_row, result);
                        listView.setAdapter(adapter);
                    }
                }else{
                    if(result!=null ){
                        listAdapter.updateData(result);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        task.execute((Void[]) null);
    }

    private View.OnClickListener onCriarPostagemListener = new View.OnClickListener() {
        public void onClick(final View v) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String auth_token_string = settings.getString("token", "");
            Long codigoUsuario = Long.valueOf(settings.getString("codigoUsuario", ""));
            Long codigoUnidade = Long.valueOf(settings.getString("codigoUnidade", ""));
            String nomeUnidade = settings.getString("nomeUnidade", "");
            showPopUpComentario(auth_token_string, codigoUsuario,codigoUnidade,nomeUnidade);
        }
    };

    public void showPopUpComentario(final String token, final long codigoUsuario,final Long codigoUnidade, final String nomeUnidade){
        final View root = ((LayoutInflater)PostagemActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comentario, null);
        RatingBar rat = (RatingBar)root.findViewById(R.id.ratingBar);
        rat.setNumStars(5);
        rat.setRating(5);
        pontuacao = 5;
        TextView labelUnidade = (TextView) root.findViewById(R.id.labelUnidade);
        labelUnidade.setText(nomeUnidade);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

        popDialog.setView(root);
        popDialog.setCancelable(false);
        rat.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                pontuacao = rating;
            }
        });

        // Button OK
        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                // Button Cancel
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = popDialog.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText comentarioText = (EditText) root.findViewById(R.id.comentario);
                final String comentario = comentarioText.getText().toString();
                location = new GPSService(context).getLocation();
                if (TextUtils.isEmpty(comentario)) {
                    comentarioText.setError(getString(R.string.error_comentario_obrigatorio));
                } else {
                    AsyncTask<Void, Void, List<PostagemDTO>> task = new AsyncTask<Void, Void, List<PostagemDTO>>() {

                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(PostagemActivity.this);
                            progressDialog.setMessage("Enviando...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }

                        @Override
                        protected List<PostagemDTO> doInBackground(Void... voids) {
                            PostagemService postagemService = new PostagemService(location);
                            postagemService.cadastrarPostagem(token, codigoUsuario, comentario, pontuacao, codigoUnidade);
                            return new PostagemService().buscarPostagensPorUnidade(codigoUnidade.toString(), token,0);
                        }

                        @Override
                        protected void onPostExecute(List<PostagemDTO> result) {
                            ListViewPostagemAdapter adapter = new ListViewPostagemAdapter(context,R.layout.customer_postagem_row, result);
                            listView.setAdapter(adapter);
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                          /*  if (result == null || "null".equals(result)) {
                                carregarPostagens();
                            } else {
                                exibirMsgErro(result);
                            }*/
                        }
                    };
                    task.execute((Void[]) null);
                    dialog.dismiss();
                }
            }
        });
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
        voltar();
    }

    private void voltar(){
        Intent intent = new Intent(this, UnidadeActivity.class);
        if(getIntent().hasExtra("valorPesquisa")){
            intent.putExtra("valorPesquisa", getIntent().getStringExtra("valorPesquisa"));
        }
        startActivity(intent);
    }
}
