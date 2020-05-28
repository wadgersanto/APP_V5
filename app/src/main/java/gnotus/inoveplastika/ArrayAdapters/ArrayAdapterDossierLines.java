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

public class ArrayAdapterDossierLines extends ArrayAdapter<DataModelBo> {

    private Context mContext;
    private List<DataModelBo> mListDataModelBO;

    public ArrayAdapterDossierLines(Context context, ArrayList<DataModelBo> objects)
    {

        super(context, 0, objects);
        this.mContext = context;
        this.mListDataModelBO = objects;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBo getItem(int position) {

        return mListDataModelBO.get(position);
    }

    @Override
    public int getCount() {

        return mListDataModelBO.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBo data = mListDataModelBO.get(position);
        final ArrayAdapterDossierLines.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new ArrayAdapterDossierLines.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterDossierLines.ViewHolderItem) convertView.getTag();
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


            viewHolder.textView_Lin1.setText(String.valueOf(data.getNmdos()).trim());
            viewHolder.textView_Lin1b.setText("nº "+String.valueOf(data.getNdos()).trim());


            viewHolder.textView_Lin2b.setText(data.getDataobra().substring(0,10));
            viewHolder.textView_Lin2a.setText("");
            viewHolder.textView_Lin3a.setText("5");
            viewHolder.textView_Lin3b.setText("6");

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
