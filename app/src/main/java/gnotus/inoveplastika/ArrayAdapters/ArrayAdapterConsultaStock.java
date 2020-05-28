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

import gnotus.inoveplastika.ConsultaStocksActivity;
import gnotus.inoveplastika.DataModels.DataModelConsultaStock;
import gnotus.inoveplastika.InvOfActivity;
import gnotus.inoveplastika.PickingActivity;
import gnotus.inoveplastika.R;

public class ArrayAdapterConsultaStock extends ArrayAdapter<DataModelConsultaStock> {

    private Context context;
    private List<DataModelConsultaStock> bi;
    private String nomeAtividade;
    //public String dmaabi_lin1,dmaabi_lin2a,dmaabi_lin2b,dmaabi_lin3a,dmaabi_lin3b;

    public ArrayAdapterConsultaStock(Context context, int resource, ArrayList<DataModelConsultaStock> objects,
                                     Bundle bundle) {

        super(context, resource, objects);
        this.context = context;
        this.bi = objects;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelConsultaStock getItem(int position) {

        return bi.get(position);
    }

    @Override
    public int getCount() {

        return bi.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelConsultaStock data = bi.get(position);
        final ArrayAdapterConsultaStock.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterConsultaStock.ViewHolderItem();
            viewHolder.textView_Lin1 = (TextView) convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = (TextView) convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = (TextView) convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = (TextView) convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = (TextView) convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = (TextView) convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterConsultaStock.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";

            // se a classe que chamou foi InvOfActivity
            if (nomeAtividadeChamou.equals(ConsultaStocksActivity.class.getSimpleName())) {

                txtLin1 = data.getReferencia()+" - "+data.getDescricao();
                // txtLin1b = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";


                if (data.getStock() == data.getStock2())
                    txtLin2b = retiraDecimais(data.getStock())+" "+data.getUnidade();
                else
                    txtLin2b = retiraDecimais(data.getStock())+" "+data.getUnidade()+" / "+retiraDecimais(data.getStock2())+" "+data.getUni2();

                txtLin3a = "";
                txtLin3b = "";


                if(ConsultaStocksActivity.getTipoConsultaStock().toString().equals("localizacao")){

                    if (! data.getLote().toString().equals("")) txtLin2a = "(L) "+data.getLote();

                    viewHolder.textView_Lin1.setTextSize(12);
                    viewHolder.textView_Lin2a.setTextSize(14);
                    viewHolder.textView_Lin2b.setTextSize(14);

                }


                if(ConsultaStocksActivity.getTipoConsultaStock().toString().equals("referencia")){

                    if (! data.getLote().toString().equals("")) txtLin1b = "(L) "+data.getLote();

                    viewHolder.textView_Lin1.setTextSize(12);
                    viewHolder.textView_Lin1b.setTextSize(13);
                    viewHolder.textView_Lin2a.setTextSize(14);
                    viewHolder.textView_Lin2b.setTextSize(14);

                    if ( (data.getZona().equals("") || data.getZona().equals("G") ) && data.getAlveolo().equals("") || data.getAlveolo().equals("G") )
                        txtLin2a = "["+data.getArmazem()+"]";
                    else
                        txtLin2a = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";

                    // txtLin1b = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";
                    viewHolder.textView_Lin1b.setTypeface(null, Typeface.BOLD);
                }

            }

            // se a classe que chamou foi InvOfActivity
            if (nomeAtividadeChamou.equals(PickingActivity.class.getSimpleName())) {

                //txtLin1 = data.getReferencia()+" - "+data.getDescricao();
                // txtLin1b = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";


                if (data.getStock() == data.getStock2())
                    txtLin2b = retiraDecimais(data.getStock())+" "+data.getUnidade();
                else
                    txtLin2b = retiraDecimais(data.getStock())+" "+data.getUnidade()+" / "+retiraDecimais(data.getStock2())+" "+data.getUni2();

                txtLin3a = "";
                txtLin3b = "";


                if (! data.getLote().toString().equals("")) {
                    //txtLin1 = "(L) "+data.getLote();
                    txtLin1 = data.getLote();
                }



                viewHolder.textView_Lin1.setTextSize(16);
                viewHolder.textView_Lin1b.setTextSize(16);
                viewHolder.textView_Lin2a.setTextSize(16);
                viewHolder.textView_Lin2b.setTextSize(16);

                if ( (data.getZona().equals("") || data.getZona().equals("G") ) && data.getAlveolo().equals("") || data.getAlveolo().equals("G") )
                    txtLin2a = "["+data.getArmazem()+"]";
                else
                    txtLin2a = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";

                // txtLin1b = "["+data.getArmazem()+"]["+data.getZona()+"]["+data.getAlveolo()+"]";
                viewHolder.textView_Lin1b.setTypeface(null, Typeface.BOLD);
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

