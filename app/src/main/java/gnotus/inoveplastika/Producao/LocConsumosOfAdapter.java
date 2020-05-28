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
import gnotus.inoveplastika.InvOfActivity;
import gnotus.inoveplastika.R;

public class LocConsumosOfAdapter extends ArrayAdapter<DataModelBi> {

    private Context context;
    private List<DataModelBi> bi;

    public LocConsumosOfAdapter(Context context, int resource, ArrayList<DataModelBi> objects) {

        super(context, resource, objects);
        this.context = context;
        this.bi = objects;

        //String tipoinventario, String tipoInvLoc

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBi getItem(int position) {

        return bi.get(position);
    }

    @Override
    public int getCount() {

        return bi.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBi data = bi.get(position);
        final LocConsumosOfAdapter.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new LocConsumosOfAdapter.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (LocConsumosOfAdapter.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";

            System.out.println("context: "+context.getClass().getSimpleName());
            System.out.println("class: "+InvOfActivity.class.getSimpleName());

            txtLin1 = data.getRef()+" - "+data.getDesign();
            txtLin2a = "Localização consumo: ";

            if (data.getZona1().equals("") || data.getIdentificacao1().equals(""))
                txtLin2b = "(atribuir)";
            else
                txtLin2b = "["+data.getArmazem()+"]["+data.getZona1()+"]["+data.getIdentificacao1()+"]";


            viewHolder.textView_Lin1.setTypeface(null,Typeface.BOLD);
            viewHolder.textView_Lin2b.setTypeface(null,Typeface.BOLD);


            viewHolder.textView_Lin2a.setTextSize(14);
            viewHolder.textView_Lin2b.setTextSize(14);
            viewHolder.textView_Lin1.setTextSize(14);

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
