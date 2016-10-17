package br.com.civico.mais.saude.controle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ListViewPostagemAdapter;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.dto.PostagemDTO;
import br.com.civico.mais.saude.util.ConexaoUtil;
import br.com.civico.mais.saude.servico.PostagemService;

public class PostagemActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private Context context;
    private ListView listView;
    private TextView lblSemComentario;
    private TextView tituloComentario;
    private float pontuacao=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentario_list);
        context = this;
        this.listView = (ListView) findViewById(R.id.listComentario);
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

    private void carregarPostagens(){
        this.lblSemComentario.setVisibility(View.GONE);
        AsyncTask<Void, Void, ListViewPostagemAdapter> task = new AsyncTask<Void, Void, ListViewPostagemAdapter>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(PostagemActivity.this);
                progressDialog.setMessage("Carregando...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected ListViewPostagemAdapter doInBackground(Void... voids) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                String auth_token_string = settings.getString("token", "");
                String codigoUnidade = settings.getString("codigoUnidade", "");
                List<PostagemDTO> result =new PostagemService().buscarPostagensPorUnidade(codigoUnidade, auth_token_string);
                return new ListViewPostagemAdapter(context,R.layout.customer_postagem_row, result);
            }

            @Override
            protected void onPostExecute(ListViewPostagemAdapter adapter) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                tituloComentario.setText("Comentários(" + adapter.getCount() + ")");
                if(adapter.getCount()==0){
                    lblSemComentario.setVisibility(View.VISIBLE);
                }else{
                    listView.setAdapter(adapter);
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
            showPopUpComentario(auth_token_string,codigoUsuario,codigoUnidade,nomeUnidade);
        }
    };

    public void showPopUpComentario(final String token, final long codigoUsuario,final Long codigoUnidade, final String nomeUnidade){
        final View root = ((LayoutInflater)PostagemActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comentario, null);
        final RatingBar rat = (RatingBar)root.findViewById(R.id.ratingBar);
        rat.setNumStars(5);
        rat.setRating(5);
        TextView labelUnidade = (TextView) root.findViewById(R.id.labelUnidade);
        labelUnidade.setText(nomeUnidade);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

        popDialog.setView(root);

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
                if (TextUtils.isEmpty(comentario)) {
                    comentarioText.setError(getString(R.string.error_comentario_obrigatorio));
                } else {
                    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(PostagemActivity.this);
                            progressDialog.setMessage("Enviando...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            PostagemService postagemService = new PostagemService();
                            postagemService.cadastrarPostagem(token, codigoUsuario, comentario, pontuacao, codigoUnidade);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            if (result == null) {
                                carregarPostagens();
                            }else{
                                exibirMsgErro(result);
                            }
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
        startActivity(intent);
    }
}
