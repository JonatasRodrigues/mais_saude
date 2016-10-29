package br.com.civico.mais.saude.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.dto.PostagemDTO;

/**
 * Created by Jônatas Rodrigues on 01/10/2016.
 */
public class ListViewPostagemAdapter extends BaseAdapter {

        private Context context;
        private List<PostagemDTO> postagemDTOList;
        private int layoutResource;

        public ListViewPostagemAdapter(Context context, int layoutResource, List<PostagemDTO> postagemDTOList) {
            this.context = context;
            this.postagemDTOList = postagemDTOList;
            this.layoutResource = layoutResource;
        }

    static class ViewHolder {
        TextView nomeAutor,dataPostagem,comentario;
        RatingBar rating;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

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
                holder.nomeAutor = (TextView) view.findViewById(R.id.lblNomeAutor);
                holder.dataPostagem = (TextView) view.findViewById(R.id.lblDataPostagem);
                holder.comentario = (TextView) view.findViewById(R.id.lblPostagem);
                holder.rating = (RatingBar) view.findViewById(R.id.ratingBarIndicador);

                if ( holder.nomeAutor != null) {
                    holder.nomeAutor.setText(postagemDTO.getNomeAutor());
                }

                if ( holder.dataPostagem != null) {
                    holder.dataPostagem.setText(postagemDTO.getDataPostagem());
                }

                if ( holder.comentario != null) {
                    holder.comentario.setText(postagemDTO.getComentario());
                }

                if ( holder.rating != null) {
                    holder.rating.setRating(postagemDTO.getPontuacao());
                }

                view.setTag(holder);
            }

            return view;
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
