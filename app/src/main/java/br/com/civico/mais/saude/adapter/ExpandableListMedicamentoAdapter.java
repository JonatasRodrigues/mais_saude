package br.com.civico.mais.saude.adapter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.controle.MedicamentoActivity;
import br.com.civico.mais.saude.util.StringUtil;

/**
 * Created by Jônatas Rodrigues on 29/08/2016.
 */
public class ExpandableListMedicamentoAdapter extends BaseExpandableListAdapter{
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private String principioAtivo;

    public ExpandableListMedicamentoAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    static class ViewHolder {
        TextView descMedicamento;
        Button btnBuscaPrincipioAtivo;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
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
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        final ViewHolder holder;
        LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            if(childPosition == 0){
                convertView = infalInflater.inflate(R.layout.customer_medicamento_row_com_btn, parent, false);
                holder.btnBuscaPrincipioAtivo = (Button) convertView.findViewById(R.id.btnBuscarPrincipioAtivo);
                holder.btnBuscaPrincipioAtivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, MedicamentoActivity.class);
                        intent.putExtra("principioAtivo", getPrincipioAtivo());
                        intent.putExtra("tipoPesquisaMedicamento",ConstantesAplicacao.SEARCH_MEDICAMENTOPOR_PRINCIPIO_ATIVO);
                        _context.startActivity(intent);
                    }
                });
            }else {
                convertView = infalInflater.inflate(R.layout.customer_medicamento_row_sem_btn, parent, false);
            }
            holder.descMedicamento = (TextView) convertView.findViewById(R.id.descMedicamento);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.descMedicamento.setText(Html.fromHtml(StringUtil.formatar(childText)));
        return convertView;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        Display display = parent.getDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        /**
         * Arredonda para Cima
         */
        int alturaTituloResultado = (int) Math.round(((double)height / ConstantesAplicacao.ROW_DISPLAY)+0.5d);
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.customer_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setHeight(alturaTituloResultado);
        lblListHeader.setTextColor(_context.getResources().getColor(R.color.DodgerBlue));
        lblListHeader.setText(headerTitle.split(ConstantesAplicacao.SPLIT_CARACTER)[0]);

        return convertView;
    }

    public void updateData( List<String> listDataHeader,HashMap<String, List<String>> listChildData) {
        this._listDataHeader.addAll(listDataHeader);
        this._listDataChild.putAll(listChildData);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

}
