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

import gnotus.inoveplastika.DataModels.DataModelPickingDocs;
import gnotus.inoveplastika.R;

public class ArrayAdapterPickingDocs extends ArrayAdapter<DataModelPickingDocs>  {

    private Context context;
    private List<DataModelPickingDocs> pickingDocs;
    private String nomeAtividade,pickingType;


    public ArrayAdapterPickingDocs(Context context, int resource, ArrayList<DataModelPickingDocs> objects,
                                   Bundle bundle) {

        super(context, resource, objects);
        this.context = context;
        this.pickingDocs = objects;

        pickingType = bundle.getString("pickingType");

        //String tipoinventario, String tipoInvLoc

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelPickingDocs getItem(int position) {

        return pickingDocs.get(position);
    }

    @Override
    public int getCount() {

        return pickingDocs.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelPickingDocs data = pickingDocs.get(position);
        final ArrayAdapterPickingDocs.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterPickingDocs.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterPickingDocs.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";


            viewHolder.textView_Lin1.setTextSize(16);
            viewHolder.textView_Lin1b.setTextSize(16);

            viewHolder.textView_Lin2a.setTextSize(16);
            viewHolder.textView_Lin2b.setTextSize(16);


            if (pickingType.equals("ABASTPROD")) {

                txtLin1 = data.getNome();
                txtLin2a = data.getData().substring(0,10);
                txtLin1b = "Armazem: "+data.getArmazem();


                // txtLin1b = data.getRefprod();
                // if (data.getNome().length() > 25) txtLin2a = data.getNome().substring(0,25);
                // txtLin2b = data.getData().substring(0,10);

                viewHolder.textView_Lin1b.setTypeface(null, Typeface.BOLD);

            }


            if (pickingType.equals("EXP") || pickingType.equals("DEVEXP")) {

                txtLin1 = data.getNome();
                txtLin1b = data.getNome2();
                txtLin2a = data.getData().substring(0,10);


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
