package br.com.civico.mais.saude.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.cache.InternalStorage;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.controle.LoginActivity;
import br.com.civico.mais.saude.controle.MapsActivity;
import br.com.civico.mais.saude.dto.AvaliacaoResponse;

import android.widget.BaseExpandableListAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jônatas Rodrigues on 29/08/2016.
 */
public class ExpandableListUnidadeAdapter extends BaseExpandableListAdapter{
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private HashMap<String, List<String>> _listDataChild;
    private HashMap<String, AvaliacaoResponse> listMediaChild;
    private String codigoUnidade;
    private Double latitude;
    private Double longitute;
    private String nomeUnidade;
    private String valorPesquisa="";

    public ExpandableListUnidadeAdapter(Context context, List<String> listDataHeader,HashMap<String, List<String>> listChildData,
                                        HashMap<String, AvaliacaoResponse> listMediaChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.listMediaChild =listMediaChild;
    }

    static class ViewHolder {
        TextView descUnidade,qtdAvaliacao;
        Button btnMapa;
        Button btnComentario;
        RatingBar ratingBar;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    public AvaliacaoResponse getMediaChild(String codigoUnidade) {
        return this.listMediaChild.get(codigoUnidade);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if (childPosition == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            if(childPosition == 0){
                convertView = infalInflater.inflate(R.layout.customer_unidade_row_com_btn, parent, false);
                holder.btnComentario = (Button) convertView.findViewById(R.id.btnComentario);
                holder.btnMapa = (Button) convertView.findViewById(R.id.btnMapa);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBarMedia);
                holder.qtdAvaliacao = (TextView) convertView.findViewById(R.id.qtdComentarios);

                holder.btnComentario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        writeCache();
                        Intent intent = new Intent(_context, LoginActivity.class);
                        intent.putExtra("codigoUnidade", getCodigoUnidade());
                        intent.putExtra("nomeUnidade", getNomeUnidade());
                        intent.putExtra("valorPesquisa", getValorPesquisa());
                        _context.startActivity(intent);
                    }
                });

                holder.btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, MapsActivity.class);
                        intent.putExtra("latitude", getLatitude());
                        intent.putExtra("longitude", getLongitute());
                        intent.putExtra("nomeUnidade",getNomeUnidade());
                        _context.startActivity(intent);
                    }
                });

            }else {
                convertView = infalInflater.inflate(R.layout.customer_unidade_row_sem_btn, parent, false);
            }
            holder.descUnidade = (TextView) convertView.findViewById(R.id.descUnidade);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(childPosition == 0){
            AvaliacaoResponse avaliacaoResponse = getMediaChild(getCodigoUnidade());
            holder.ratingBar.setRating(avaliacaoResponse != null ? avaliacaoResponse.getMediaAvaliacao() : 0);
            holder.qtdAvaliacao.setText(avaliacaoResponse != null ? avaliacaoResponse.getQtdAvaliacao() : "0");
        }

        holder.descUnidade.setText(childText);
        return convertView;
    }

    private void writeCache(){
        try {
            InternalStorage.deleteCache(_context, ConstantesAplicacao.KEY_CACHE_UNIDADE);
            InternalStorage.deleteCache(_context, ConstantesAplicacao.KEY_CACHE_HEADER_UNIDADE);
            InternalStorage.deleteCache(_context, ConstantesAplicacao.KEY_CACHE_lIST_UNIDADE);
            InternalStorage.deleteCache(_context, ConstantesAplicacao.KEY_CACHE_MEDIA_UNIDADE);

            if(InternalStorage.getFreeSpace() >= ConstantesAplicacao.ESPACO_MINIMO_CACHE){
                InternalStorage.writeObject(_context, ConstantesAplicacao.KEY_CACHE_UNIDADE,ConstantesAplicacao.KEY_CACHE_UNIDADE);
                InternalStorage.writeObject(_context, ConstantesAplicacao.KEY_CACHE_HEADER_UNIDADE,_listDataHeader);
                InternalStorage.writeObject(_context, ConstantesAplicacao.KEY_CACHE_lIST_UNIDADE,_listDataChild);
                InternalStorage.writeObject(_context, ConstantesAplicacao.KEY_CACHE_MEDIA_UNIDADE,listMediaChild);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCodigoUnidade(String codigoUnidade){
        this.codigoUnidade=codigoUnidade;
    }

    public String getCodigoUnidade(){
        return this.codigoUnidade;
    }

    public Double getLongitute() {
        return longitute;
    }

    public void setLongitute(Double longitute) {
        this.longitute = longitute;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    public String getValorPesquisa() {
        return valorPesquisa;
    }

    public void setValorPesquisa(String valorPesquisa) {
        this.valorPesquisa = valorPesquisa;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List childList = _listDataChild.get(this._listDataHeader.get(groupPosition));
        if (childList != null && ! childList.isEmpty()) {
            return childList.size();
        }
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.customer_unidade_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setHeight(70);
        lblListHeader.setTextColor(_context.getResources().getColor(R.color.DodgerBlue));
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    public void updateData( List<String> listDataHeader,HashMap<String, List<String>> listChildData,
                            HashMap<String, AvaliacaoResponse> listMediaChild) {
        this._listDataHeader.addAll(listDataHeader);
        this._listDataChild.putAll(listChildData);
        this.listMediaChild.putAll(listMediaChild);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
