package gnotus.inoveplastika.Logistica;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.R;

public class DataModelArrayAdapterCarga extends ArrayAdapter<DataModelBi> {

    private Context context;
    private List<DataModelBi> artigos;

    public DataModelArrayAdapterCarga(Context context, int resource, ArrayList<DataModelBi> objects) {

        super(context, resource, objects);
        this.context = context;
        this.artigos = objects;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBi getItem(int position) {

        return artigos.get(position);
    }

    @Override
    public int getCount() {

        return artigos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBi data = artigos.get(position);
        final DataModelArrayAdapterCarga.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_carga, parent, false);

            viewHolder = new DataModelArrayAdapterCarga.ViewHolderItem();

            viewHolder.textView_ref = (TextView) convertView.findViewById(R.id.textView_of_leg);
            viewHolder.textView_lote = (TextView) convertView.findViewById(R.id.textView_3a);
            viewHolder.textView_localizacao = (TextView) convertView.findViewById(R.id.textView_list_carga_localizacao);
            viewHolder.textView_qt = (TextView) convertView.findViewById(R.id.textView_list_carga_qt);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (DataModelArrayAdapterCarga.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String nomeAtividadeChamou = context.getClass().getSimpleName();


            String txtLocalizacao = "["+String.valueOf(data.getArmazem()).trim()+"]";

            if (! data.getZona1().trim().equals("")) {
                txtLocalizacao = txtLocalizacao+"["+data.getZona1().trim()+"]"+"["+data.getAlveolo1().trim()+"]";
            }

            viewHolder.textView_ref.setTextSize(16);
            viewHolder.textView_lote.setTextSize(16);
            viewHolder.textView_localizacao.setTextSize(16);
            viewHolder.textView_qt.setTextSize(16);


            viewHolder.textView_ref.setText(data.getRef().trim()+" - "+data.getDesign().trim());
            viewHolder.textView_lote.setText(data.getLote());
            viewHolder.textView_localizacao.setText(txtLocalizacao);
            viewHolder.textView_qt.setText(retiraDecimais(data.getQtt()) + " " + data.getUnidade());

            if(data.getUni2qtt()!=0.0) {
                String mQttAlt = retiraDecimais(data.getUni2qtt())+" "+ data.getUnidad2().trim();

                viewHolder.textView_qt.setText(viewHolder.textView_qt.getText()+ "/"+mQttAlt);

            }


        }

        return convertView;

    }

    private static class ViewHolderItem {


        TextView textView_ref;
        TextView textView_lote;
        TextView textView_localizacao;
        TextView textView_qt;


    }

    private String retiraDecimais(Double numero){

        String strnumero;

        strnumero = String.format("%.3f",numero).replace(",",".");

        boolean processa = true;

        while (strnumero.indexOf(".") >= 0 && ( strnumero.substring(strnumero.length()-1).equals("0") || strnumero.substring(strnumero.length()-1).equals(".") )) {
            strnumero  = strnumero.substring(0,strnumero.length()-1);
        }

        return strnumero;

    }

}
