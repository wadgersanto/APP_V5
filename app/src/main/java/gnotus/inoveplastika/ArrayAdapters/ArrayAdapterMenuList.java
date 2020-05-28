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
import gnotus.inoveplastika.DataModels.DataModelMenuList;
import gnotus.inoveplastika.PickingActivity;
import gnotus.inoveplastika.R;

public class ArrayAdapterMenuList extends ArrayAdapter<DataModelMenuList> {

    private Context context;
    private List<DataModelMenuList> menuList;


    public ArrayAdapterMenuList(Context context, int resource, ArrayList<DataModelMenuList> objects) {

        super(context, resource, objects);
        this.context = context;
        this.menuList = objects;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelMenuList getItem(int position) {

        return menuList.get(position);
    }

    @Override
    public int getCount() {

        return menuList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelMenuList data = menuList.get(position);
        final ArrayAdapterMenuList.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();


        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_menu, parent, false);

            viewHolder = new ArrayAdapterMenuList.ViewHolderItem();
            viewHolder.textView_Lin1 = (TextView) convertView.findViewById(R.id.textView_lin1);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterMenuList.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "";

            txtLin1 = data.getMenuopcao();
            viewHolder.textView_Lin1.setText(txtLin1);

        }

        return convertView;

    }

    private static class ViewHolderItem {

        TextView textView_Lin1;

    }


}

