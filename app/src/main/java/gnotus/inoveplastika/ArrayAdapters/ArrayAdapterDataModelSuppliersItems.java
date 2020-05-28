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

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.R;

public class ArrayAdapterDataModelSuppliersItems extends ArrayAdapter<DataModelBi> {

    private Context mContext;
    private List<DataModelBi> mListDataModelBI;

    public ArrayAdapterDataModelSuppliersItems(Context context, ArrayList<DataModelBi> objects)
    {

        super(context, 0, objects);
        this.mContext = context;
        this.mListDataModelBI = objects;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBi getItem(int position) {

        return mListDataModelBI.get(position);
    }

    @Override
    public int getCount() {

        return mListDataModelBI.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBi data = mListDataModelBI.get(position);
        final ArrayAdapterDataModelSuppliersItems.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterDataModelSuppliersItems.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterDataModelSuppliersItems.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {





            viewHolder.textView_Lin1b.setTypeface(null, Typeface.BOLD);

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


            String[] qtt  = data.getQttpend().split("\\.");
            String[] qtt2 = data.getUni2qttpend().split("\\.");

            viewHolder.textView_Lin1.setText("["+data.getRef().trim()+"]-"+data.getDesign());
            viewHolder.textView_Lin1b.setText("");
            viewHolder.textView_Lin2a.setText(qtt[0]+" "+ data.getUnidade());
            viewHolder.textView_Lin2b.setText(qtt2[0]+" "+ data.getUni2());
            viewHolder.textView_Lin3a.setText("");
            viewHolder.textView_Lin3b.setText("");

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
