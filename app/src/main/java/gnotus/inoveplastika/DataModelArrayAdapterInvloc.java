package gnotus.inoveplastika;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelCarga;

public class DataModelArrayAdapterInvloc extends ArrayAdapter<DataModelCarga> {

    private Context context;
    private List<DataModelCarga> artigos;

    public DataModelArrayAdapterInvloc(Context context, int resource, ArrayList<DataModelCarga> objects) {

        super(context, resource, objects);
        this.context = context;
        this.artigos = objects;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelCarga getItem(int position) {

        return artigos.get(position);
    }

    @Override
    public int getCount() {

        return artigos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DataModelCarga data = artigos.get(position);
        final DataModelArrayAdapterInvloc.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();


        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_invloc, parent, false);

            viewHolder = new DataModelArrayAdapterInvloc.ViewHolderItem();
            viewHolder.textView_ref_lote = (TextView) convertView.findViewById(R.id.textView_of_leg);
            viewHolder.TextView_localizacao = (TextView) convertView.findViewById(R.id.textView_3a);
            viewHolder.textView_stock = (TextView) convertView.findViewById(R.id.textView_4a);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (DataModelArrayAdapterInvloc.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String txtRefLote = data.getReferencia().trim();

            if (data.getDescricao().length() > 17){
                txtRefLote = txtRefLote+" "+data.getDescricao().substring(0,17);
            }

            if (! data.getLote().isEmpty())  txtRefLote = txtRefLote +"     L["+data.getLote().trim()+"]";

            String txtlocalizacao = "["+data.getArmazem().trim()+"]";
            if (!data.getZona2().isEmpty()) txtlocalizacao = txtlocalizacao+ "["+data.getZona2().trim()+"]";
            if (!data.getAlveolo2().isEmpty()) txtlocalizacao = txtlocalizacao+ "["+data.getAlveolo2().trim()+"]";

            String txtStock = "";
            txtStock = String.valueOf(data.getQt()) + " " + data.getUnidade();

            if(data.getQtAlt()!=0.0) {
                txtStock = txtStock+" / "+String.valueOf(data.getQtAlt() + " " + data.getUnidadeAlt());
            }

            viewHolder.textView_ref_lote.setText(txtRefLote);
            viewHolder.TextView_localizacao.setText(txtlocalizacao);
            viewHolder.textView_stock.setText(txtStock);

        }

        return convertView;

    }

    private static class ViewHolderItem {

        TextView textView_ref_lote;
        TextView TextView_localizacao;
        TextView textView_stock;

    }


}
