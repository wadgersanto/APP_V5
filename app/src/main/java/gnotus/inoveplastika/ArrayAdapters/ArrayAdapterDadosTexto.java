package gnotus.inoveplastika.ArrayAdapters;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelDadosTexto;
import gnotus.inoveplastika.DataModels.DataModelMenuList;
import gnotus.inoveplastika.R;

public class ArrayAdapterDadosTexto extends ArrayAdapter<DataModelDadosTexto> {

    private Context context;
    private List<DataModelDadosTexto> dadostexto;


    public ArrayAdapterDadosTexto(Context context, int resource, ArrayList<DataModelDadosTexto> objects,
                                  Bundle bundle) {

        super(context, resource, objects);
        this.context = context;
        this.dadostexto = objects;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelDadosTexto getItem(int position) {

        return dadostexto.get(position);
    }

    @Override
    public int getCount() {

        return dadostexto.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelDadosTexto data = dadostexto.get(position);
        final ArrayAdapterDadosTexto.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();


        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterDadosTexto.ViewHolderItem();
            viewHolder.textView_Lin1 = (TextView) convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = (TextView) convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = (TextView) convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = (TextView) convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = (TextView) convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = (TextView) convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterDadosTexto.ViewHolderItem) convertView.getTag();
        }


        viewHolder.textView_Lin2a.setVisibility(View.GONE);
        viewHolder.textView_Lin2b.setVisibility(View.GONE);
        viewHolder.textView_Lin3a.setVisibility(View.GONE);
        viewHolder.textView_Lin3b.setVisibility(View.GONE);

        viewHolder.textView_Lin1.setTextSize(16);
        viewHolder.textView_Lin1b.setTextSize(16);

        if (data != null) {


            String nomeAtividadeChamou = context.getClass().getSimpleName();


            viewHolder.textView_Lin1.setText(data.getTitulo());
            viewHolder.textView_Lin1b.setText(data.getValor());




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

