package gnotus.inoveplastika.Suporte;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelPickingDocs;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;

import static gnotus.inoveplastika.MainActivity_Lista.CRLF;

public class ArrayAdapterPa extends ArrayAdapter<DataModelPa>  {

    private Context context;
    private List<DataModelPa> listPa;
    private int tipoaccao;


    public ArrayAdapterPa(Context context, int resource, ArrayList<DataModelPa> objects, int tipoaccao) {


        super(context, resource, objects);
        this.context = context;
        this.listPa = objects;
        this.tipoaccao = tipoaccao;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelPa getItem(int position) {

        return listPa.get(position);
    }

    @Override
    public int getCount() {

        return listPa.size();
    }

    @SuppressLint("WrongConstant")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DataModelPa data = listPa.get(position);
        final ArrayAdapterPa.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterPa.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterPa.ViewHolderItem) convertView.getTag();
        }



        if (data != null) {

            // String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";


            txtLin1 = data.getTipo().trim()+" : "+data.getSerie().trim();
            txtLin1b = data.getSerie();


            txtLin3a = "PROBLEMA: "+data.getResumo();


            if (tipoaccao == 1) {
                txtLin1b = data.getPdata().substring(0,10)+" "+data.getPhora();
                txtLin2a = data.getPtipo();
                txtLin2b = data.getNref();
            }

            if (tipoaccao == 2) {

                txtLin1b = data.getDatat().substring(0,10)+" "+data.getPhora();
                txtLin2a = data.getPtipo().toUpperCase();
                txtLin2b = data.getNref();
                txtLin3b = data.getTimeopen();

            }


            if (data.getPsobs().length() > 0 && (tipoaccao == 1 || tipoaccao == 2)) {

                txtLin3a  = txtLin3a+ CRLF+"STATUS : ["+data.getStatus()+"]";
                txtLin3a  = txtLin3a+ CRLF+"INFO : "+data.getPsobs();

            }

            if ((tipoaccao == 3 || tipoaccao == 4)) {

                txtLin1b = data.getFdata().substring(0,10)+" "+data.getFhora();
                txtLin2a = data.getPtipo().toUpperCase();
                txtLin2b = data.getNref();
                txtLin3a  = txtLin3a+ CRLF+"RELATORIO : "+data.getSolucao();

            }


            viewHolder.textView_Lin1.setTextSize(15);
            viewHolder.textView_Lin1b.setTextSize(15);

            viewHolder.textView_Lin2a.setTextSize(15);
            viewHolder.textView_Lin2b.setTextSize(15);

            viewHolder.textView_Lin3a.setTextSize(15);
            viewHolder.textView_Lin3a.setMaxLines(10);


            // viewHolder.textView_Lin3a.setMovementMethod(ScrollingMovementMethod.getInstance());
            // viewHolder.textView_Lin3a.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            // viewHolder.textView_Lin3a.setVerticalScrollBarEnabled(true);
            // viewHolder.textView_Lin3a.setMaxLines(4);

            viewHolder.textView_Lin3a.setTextColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

            viewHolder.textView_Lin1.setText(txtLin1);
            viewHolder.textView_Lin1b.setText(txtLin1b);
            viewHolder.textView_Lin2a.setText(txtLin2a);
            viewHolder.textView_Lin2b.setText(txtLin2b);
            viewHolder.textView_Lin3a.setText(txtLin3a);
            viewHolder.textView_Lin3b.setText(txtLin3b);

//            if (viewHolder.textView_Lin2a.getText().toString().trim().equals("") &&
//                    viewHolder.textView_Lin2b.getText().toString().trim().equals("")){
//                viewHolder.textView_Lin2a.setVisibility(View.GONE);
//                viewHolder.textView_Lin2b.setVisibility(View.GONE);
//            }
//            if (viewHolder.textView_Lin3a.getText().toString().trim().equals("") &&
//                    viewHolder.textView_Lin3b.getText().toString().trim().equals("")){
//                viewHolder.textView_Lin3a.setVisibility(View.GONE);
//                viewHolder.textView_Lin3b.setVisibility(View.GONE);
//            }
//


//


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
