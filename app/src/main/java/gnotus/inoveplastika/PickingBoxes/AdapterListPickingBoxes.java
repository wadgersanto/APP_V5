package gnotus.inoveplastika.PickingBoxes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gnotus.inoveplastika.Producao.DataModelCxof;
import gnotus.inoveplastika.R;

public class AdapterListPickingBoxes extends ArrayAdapter<DataModelCxof> {


    private Context mContext;
    private ArrayList<DataModelCxof> mListPickingBoxes;


    public AdapterListPickingBoxes(Context context, ArrayList<DataModelCxof> objects) {

        super(context, 0, objects);
        this.mContext = context;
        this.mListPickingBoxes = objects;


    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelCxof getItem(int position) {

        return mListPickingBoxes.get(position);
    }

    @Override
    public int getCount() {

        return mListPickingBoxes.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DataModelCxof data = mListPickingBoxes.get(position);
        final AdapterListPickingBoxes.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);
//
        viewHolder = new AdapterListPickingBoxes.ViewHolderItem();
        viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
        viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
        viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
        viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
        viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
        viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

        viewHolder.textView_Lin2b.setVisibility(View.GONE);
        viewHolder.textView_Lin2a.setVisibility(View.GONE);
        viewHolder.textView_Lin3b.setVisibility(View.GONE);
        viewHolder.textView_Lin3a.setVisibility(View.GONE);
        convertView.setTag(viewHolder);

        viewHolder.textView_Lin1.setTextSize(18);

        viewHolder.textView_Lin1.setText(String.valueOf(data.getNumcx()));
        viewHolder.textView_Lin1b.setText("Linha "+(mListPickingBoxes.size() - position)+"");


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
