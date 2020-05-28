package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

public class AAdapterPedidosAbastProd extends ArrayAdapter<DataModelBi>  {

    private Context context;
    private List<DataModelBi> listAbastProd;

    public AAdapterPedidosAbastProd(Context context, int resource, ArrayList<DataModelBi> objects) {

        super(context, resource, objects);
        this.context = context;
        this.listAbastProd = objects;

        //String tipoinventario, String tipoInvLoc
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBi getItem(int position) {

        return listAbastProd.get(position);
    }

    @Override
    public int getCount() {

        return listAbastProd.size();
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBi data = listAbastProd.get(position);
        final AAdapterPedidosAbastProd.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new AAdapterPedidosAbastProd.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (AAdapterPedidosAbastProd.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";

            txtLin1 =  data.getRdata().substring(0,10);
            txtLin1b = "";

            txtLin2a = "Qtd pedida / satisfeita: ";
            txtLin2b  =  UserFunc.retiraZerosDireita(data.getQtt())  + " / "+  UserFunc.retiraZerosDireita(data.getQtt2()) ;
            txtLin3a = "Qtd por satisfazer: ";
            txtLin3b = ""+(UserFunc.retiraZerosDireita((data.getQtt()- data.getQtt2())));

            viewHolder.textView_Lin1.setTextSize(16);
            viewHolder.textView_Lin1b.setTextSize(16);
            viewHolder.textView_Lin1.setTypeface(null, Typeface.NORMAL);
            viewHolder.textView_Lin1b.setTypeface(null, Typeface.NORMAL);

            viewHolder.textView_Lin2a.setTextSize(16);
            viewHolder.textView_Lin2b.setTextSize(16);

            viewHolder.textView_Lin3a.setTextSize(16);
            viewHolder.textView_Lin3b.setTextSize(16);



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
