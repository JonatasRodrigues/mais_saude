package br.com.civico.mais.saude.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.controle.PostagemActivity;
import br.com.civico.mais.saude.controle.UnidadeActivity;
import br.com.civico.mais.saude.dto.PostagemDTO;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.PostagemService;

/**
 * Created by Jônatas Rodrigues on 01/10/2016.
 */
public class ListViewPostagemAdapter extends BaseAdapter {

    private ProgressDialog progressDialog;
    private Context context;
    private List<PostagemDTO> postagemDTOList;
    private int layoutResource;
    private float pontuacao=5;

    private Location location;

    public ListViewPostagemAdapter(Context context, int layoutResource, List<PostagemDTO> postagemDTOList) {
        this.context = context;
        this.postagemDTOList = postagemDTOList;
        this.layoutResource = layoutResource;
    }

    static class ViewHolder {
        TextView nomeAutor,dataPostagem,comentario,codigoPostagem,codConteudoPostagem;
        RatingBar rating;
        Button btnEditar,btnExcluir;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;

        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(layoutResource, null);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        PostagemDTO postagemDTO = postagemDTOList.get(position);

        if (postagemDTO != null) {

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final Long codigoUsuario = Long.valueOf(settings.getString("codigoUsuario", ""));
            final String auth_token_string = settings.getString("token", "");
            final Long codigoUnidade = Long.valueOf(settings.getString("codigoUnidade", ""));
            final String nomeUnidade = settings.getString("nomeUnidade", "");

            holder.codConteudoPostagem = (TextView)view.findViewById(R.id.codigoConteudoPostagem);
            holder.codigoPostagem = (TextView) view.findViewById(R.id.codigoPostagem);
            holder.nomeAutor = (TextView) view.findViewById(R.id.lblNomeAutor);
            holder.dataPostagem = (TextView) view.findViewById(R.id.lblDataPostagem);
            holder.comentario = (TextView) view.findViewById(R.id.lblPostagem);
            holder.rating = (RatingBar) view.findViewById(R.id.ratingBarIndicador);
            holder.btnEditar = (Button) view.findViewById(R.id.btnEditar);
            holder.btnExcluir = (Button) view.findViewById(R.id.btnExcluir);
            holder.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayoutBtnComentario);

            if(codigoUsuario.intValue() == Integer.parseInt(postagemDTO.getCodAutor())){

                if (postagemDTO.getCodConteudo() != null)
                    holder.codConteudoPostagem.setText(postagemDTO.getCodConteudo());

                if (postagemDTO.getCodPostagem() != null)
                    holder.codigoPostagem.setText(postagemDTO.getCodPostagem());

                holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout)v.getParent();
                        TextView codigoPostagem = (TextView)rl.findViewById(R.id.codigoPostagem);
                        excluirPostagem(v, auth_token_string, Long.parseLong(codigoPostagem.getText().toString()));
                    }
                });

                holder.btnEditar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        RelativeLayout rl2 = (RelativeLayout) rl.getParent();
                        TextView codigoPostagem = (TextView) rl.findViewById(R.id.codigoPostagem);
                        TextView codigoConteudo = (TextView) rl.findViewById(R.id.codigoConteudoPostagem);
                        RatingBar pontuacaoAtual = (RatingBar) rl2.findViewById(R.id.ratingBarIndicador);
                        TextView comentarioAtual = (TextView) rl2.findViewById(R.id.lblPostagem);
                        editarPostagem(v, auth_token_string, codigoUnidade, nomeUnidade, Long.parseLong(codigoConteudo.getText().toString()),
                                Long.parseLong(codigoPostagem.getText().toString()),pontuacaoAtual.getRating(), comentarioAtual.getText().toString());
                    }
                });
            }else{
                holder.codConteudoPostagem.setVisibility(View.GONE);
                holder.codigoPostagem.setVisibility(View.GONE);
                holder.btnEditar.setVisibility(View.GONE);
                holder.btnExcluir.setVisibility(View.GONE);
                holder.relativeLayout.setVisibility(View.GONE);
            }

            if (postagemDTO.getNomeAutor() != null)
                holder.nomeAutor.setText(postagemDTO.getNomeAutor());

            if (postagemDTO.getDataPostagem() != null)
                holder.dataPostagem.setText(postagemDTO.getDataPostagem());

            if (postagemDTO.getComentario() != null)
                holder.comentario.setText(postagemDTO.getComentario());

            holder.rating.setRating(postagemDTO.getPontuacao());

            view.setTag(holder);
        }
        return view;
    }


    public void editarPostagem(final View view,final String token,final Long codigoUnidade, final String nomeUnidade,
                               final long codigoConteudo, final long codigoPostagem,float pontuacaoAtual,String comentarioAtual){
        final View root = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.comentario, null);
        RatingBar rat = (RatingBar)root.findViewById(R.id.ratingBar);
        rat.setNumStars(5);
        rat.setRating(pontuacaoAtual);
        pontuacao = pontuacaoAtual;
        TextView labelUnidade = (TextView) root.findViewById(R.id.labelUnidade);
        labelUnidade.setText(nomeUnidade);
        EditText comentarioText = (EditText) root.findViewById(R.id.comentario);
        comentarioText.setText(comentarioAtual);

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);

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
                    comentarioText.setError(context.getString(R.string.error_comentario_obrigatorio));
                } else {
                    AsyncTask<Void, Void, List<PostagemDTO>> task = new AsyncTask<Void, Void, List<PostagemDTO>>() {

                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Enviando...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }

                        @Override
                        protected List<PostagemDTO> doInBackground(Void... voids) {
                            PostagemService postagemService = new PostagemService(location);
                            postagemService.editarConteudoPostagem(token, codigoPostagem, codigoConteudo, comentario, pontuacao);
                            return new PostagemService().buscarPostagensPorUnidade(codigoUnidade.toString(), token,0);
                        }

                        @Override
                        protected void onPostExecute(List<PostagemDTO> result) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }

                            Intent intent = new Intent(context, PostagemActivity.class);
                            if(((Activity) context).getIntent().hasExtra("valorPesquisa")){
                                intent.putExtra("valorPesquisa", ((Activity) context).getIntent().getStringExtra("valorPesquisa"));
                            }
                            view.getContext().startActivity(intent);
                        }
                    };
                    task.execute((Void[]) null);
                    dialog.dismiss();
                }
            }
        });
    }

    private void excluirPostagem(final View v,final String token,final long idPostagem) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setMessage(ConstantesAplicacao.MSG_CONFIRMACAO_EXCLUIR);
        adb.setNegativeButton(ConstantesAplicacao.BTN_CANCELAR, null);
        adb.setPositiveButton(ConstantesAplicacao.BTN_OK, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Por favor, aguarde...");
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... arg0) {
                        return new PostagemService().excluirPostagem(token,idPostagem);
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        Intent intent = new Intent(context, PostagemActivity.class);
                        if(((Activity) context).getIntent().hasExtra("valorPesquisa")){
                            intent.putExtra("valorPesquisa", ((Activity) context).getIntent().getStringExtra("valorPesquisa"));
                        }
                        v.getContext().startActivity(intent);
                    }

                };
                task.execute((Void[]) null);
            }
        });
        adb.show();
    }

    public void updateData(List<PostagemDTO> postagemDTOList) {
        this.postagemDTOList.addAll(postagemDTOList);
    }

    @Override
    public int getCount() {
        if(postagemDTOList==null){
            return 0;
        }
        return postagemDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
