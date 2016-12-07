package br.com.civico.mais.saude.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import br.com.civico.mais.saude.servico.PostagemService;

/**
 * Created by Jônatas Rodrigues on 01/10/2016.
 */
public class ListViewPostagemAdapter extends BaseAdapter {

        private Context context;
        private List<PostagemDTO> postagemDTOList;
        private int layoutResource;
        private ProgressDialog pd;

        public ListViewPostagemAdapter(Context context, int layoutResource, List<PostagemDTO> postagemDTOList) {
            this.context = context;
            this.postagemDTOList = postagemDTOList;
            this.layoutResource = layoutResource;
        }

    static class ViewHolder {
        TextView nomeAutor,dataPostagem,comentario,codigoPostagem;
        RatingBar rating;
        Button btnEditar,btnExcluir;
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
                holder.codigoPostagem = (TextView) view.findViewById(R.id.codigoPostagem);
                holder.nomeAutor = (TextView) view.findViewById(R.id.lblNomeAutor);
                holder.dataPostagem = (TextView) view.findViewById(R.id.lblDataPostagem);
                holder.comentario = (TextView) view.findViewById(R.id.lblPostagem);
                holder.rating = (RatingBar) view.findViewById(R.id.ratingBarIndicador);
                holder.btnEditar = (Button) view.findViewById(R.id.btnEditar);
                holder.btnExcluir = (Button) view.findViewById(R.id.btnExcluir);


                holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout)v.getParent();
                        TextView codigoPostagem = (TextView)rl.findViewById(R.id.codigoPostagem);
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                        final String auth_token_string = settings.getString("token", "");
                        excluirPostagem(v,auth_token_string, Long.parseLong(codigoPostagem.getText().toString()));
                    }
                });

                if (postagemDTO.getCodPostagem() != null)
                    holder.codigoPostagem.setText(postagemDTO.getCodPostagem());

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


        private void excluirPostagem(final View v,final String token,final long idPostagem) {

                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setMessage(ConstantesAplicacao.MSG_CONFIRMACAO_EXCLUIR);
                adb.setNegativeButton(ConstantesAplicacao.BTN_CANCELAR, null);
                adb.setPositiveButton(ConstantesAplicacao.BTN_OK, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

                            @Override
                            protected void onPreExecute() {
                                pd = new ProgressDialog(context);
                                pd.setMessage("Por favor, aguarde...");
                                pd.setCancelable(false);
                                pd.setIndeterminate(true);
                                pd.show();
                            }

                            @Override
                            protected String doInBackground(Void... arg0) {
                            return new PostagemService().excluirPostagem(token,idPostagem);
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                Toast.makeText(v.getContext(), result, Toast.LENGTH_SHORT).show();
                                if (pd != null) {
                                    pd.dismiss();
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
