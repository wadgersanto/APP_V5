package gnotus.inoveplastika.ArrayAdapters;

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

import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.R;

public class ArrayAdapterPdaLoads extends ArrayAdapter<DataModelBo>  {

    private Context context;
    private List<DataModelBo> leituraPda;

    public ArrayAdapterPdaLoads(Context context, int resource, ArrayList<DataModelBo> objects) {

        super(context, resource, objects);
        this.context = context;
        this.leituraPda = objects;



    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBo getItem(int position) {

        return leituraPda.get(position);
    }

    @Override
    public int getCount() {

        return leituraPda.size();
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBo data = leituraPda.get(position);
        final ArrayAdapterPdaLoads.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterPdaLoads.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterPdaLoads.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {




            viewHolder.textView_Lin1.setTextSize(18);
            viewHolder.textView_Lin1b.setTextSize(16);
            viewHolder.textView_Lin1.setTypeface(null, Typeface.BOLD);
            viewHolder.textView_Lin1b.setTypeface(null, Typeface.BOLD);

            viewHolder.textView_Lin2a.setTextSize(16);
            viewHolder.textView_Lin2b.setTextSize(16);


            viewHolder.textView_Lin1.setText("Operador nº "+data.getU_operador());
            viewHolder.textView_Lin1b.setText(data.getDataobra().substring(0, 10));
            viewHolder.textView_Lin2a.setText(data.getNmdos() +" nº "+ data.getObrano());
            viewHolder.textView_Lin2b.setText("");
            viewHolder.textView_Lin3a.setText("");
            viewHolder.textView_Lin3b.setText("");

            viewHolder.textView_Lin2b.setVisibility(View.GONE);
            viewHolder.textView_Lin3a.setVisibility(View.GONE);
            viewHolder.textView_Lin3b.setVisibility(View.GONE);


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
