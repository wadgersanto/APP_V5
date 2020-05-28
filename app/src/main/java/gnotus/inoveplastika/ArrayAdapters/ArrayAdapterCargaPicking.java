package gnotus.inoveplastika.ArrayAdapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelCargaPicking;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

public class ArrayAdapterCargaPicking extends ArrayAdapter<DataModelCargaPicking> {

    private Context context;
    private List<DataModelCargaPicking> bi;
    private String nomeAtividade;
    private String nomeListView,pickingType = "";

    public ArrayAdapterCargaPicking(Context context, int resource, ArrayList<DataModelCargaPicking> objects,
                                    Bundle bundle) {

        super(context, resource, objects);
        this.context = context;
        this.bi = objects;

        // tipo é um parametro que permite identificar chamadas diferentes da mesma atividade
        if (bundle.getString("listview") != null) this.nomeListView = bundle.getString("listview");
        else this.nomeListView = "";

        if( ! (bundle.getString("pickingType") == null))
                pickingType = bundle.getString("pickingType");


        System.out.println("List view: "+nomeListView);

        //String tipoinventario, String tipoInvLoc

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelCargaPicking getItem(int position) {

        return bi.get(position);
    }

    @Override
    public int getCount() {

        return bi.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelCargaPicking data = bi.get(position);
        final ArrayAdapterCargaPicking.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterCargaPicking.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterCargaPicking.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            Double fconversao = data.getFconversao();

            String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";


            // Atividade que chamou = PickingActivity e ListView = ListViewCarga


            txtLin1 = data.getReferencia()+" - "+data.getDescricao();
            txtLin1b = "  "+data.getLote();

            txtLin2a = "["+data.getArmazem()+"]["+data.getZona1()+"]["+data.getAlveolo1()+"]";
            txtLin2b = data.getQtt()+ " "+data.getUnidade();

            txtLin2b = UserFunc.retiraZerosDireita (data.getQtt())+ " "+data.getUnidade();

            if ( fconversao != 1) {
                txtLin2b = txtLin2b+"/"+UserFunc.retiraZerosDireita (data.getQtt()/fconversao)+ " "+data.getUnidad2();
            }


            txtLin3a = data.getClientepicking();

            viewHolder.textView_Lin1b.setTypeface(null,Typeface.BOLD);

            viewHolder.textView_Lin1.setTextSize(16);
            viewHolder.textView_Lin1b.setTextSize(16);
            viewHolder.textView_Lin2a.setTextSize(16);
            viewHolder.textView_Lin2b.setTextSize(16);
            viewHolder.textView_Lin3a.setTextSize(15);


            viewHolder.textView_Lin1.setTypeface(null,Typeface.BOLD);
            viewHolder.textView_Lin1b.setTypeface(null,Typeface.BOLD);

            viewHolder.textView_Lin2a.setTypeface(null,Typeface.NORMAL);
            viewHolder.textView_Lin2b.setTypeface(null,Typeface.NORMAL);
            viewHolder.textView_Lin3a.setTypeface(null,Typeface.NORMAL);


            if (pickingType.equals("ABASTPROD")) {
                txtLin3a = "OF: "+data.getObranome();
                viewHolder.textView_Lin3a.setTypeface(null,Typeface.BOLD);
            }




            viewHolder.textView_Lin1.setText(txtLin1);
            viewHolder.textView_Lin1b.setText(txtLin1b);
            viewHolder.textView_Lin2a.setText(txtLin2a);
            viewHolder.textView_Lin2b.setText(txtLin2b);
            viewHolder.textView_Lin3a.setText(txtLin3a);
            viewHolder.textView_Lin3b.setText(txtLin3b);

            if (viewHolder.textView_Lin2a.getText().toString().trim().equals("") &&
                    viewHolder.textView_Lin2b.getText().toString().trim().equals("")){
                viewHolder.textView_Lin2a.setVisibility(View.GONE);
                viewHolder.textView_Lin2b.setVisibility(View.GONE);
            }

            if (viewHolder.textView_Lin3a.getText().toString().trim().equals("") &&
                    viewHolder.textView_Lin3b.getText().toString().trim().equals("")){
                viewHolder.textView_Lin3a.setVisibility(View.GONE);
                viewHolder.textView_Lin3b.setVisibility(View.GONE);
            }




        }

        return convertView;

    }

    private static class ViewHolderItem {

        TextView textView_Lin1;
        TextView textView_Lin1b;
        TextView textView_Lin2a;
        TextView textView_Lin2b;
        TextView textView_Lin3a;
        TextView textView_Lin3b;

    }


}
