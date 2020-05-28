package gnotus.inoveplastika.ArrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelQualityControl;
import gnotus.inoveplastika.R;

import static gnotus.inoveplastika.Dialogos.retiraDecimais;

public class ArrayAdapterDataModelControlQuality extends ArrayAdapter<DataModelQualityControl> {

    private Context mContext;
    private List<DataModelQualityControl> dataModelQualityControls;

    public ArrayAdapterDataModelControlQuality(Context context, ArrayList<DataModelQualityControl> objects)
    {

        super(context, 0, objects);
        this.mContext = context;
        this.dataModelQualityControls = objects;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelQualityControl getItem(int position) {

        return dataModelQualityControls.get(position);
    }

    @Override
    public int getCount() {

        return dataModelQualityControls.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelQualityControl data = dataModelQualityControls.get(position);
        final ArrayAdapterDataModelControlQuality.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi5lin, parent, false);
            viewHolder  = new ArrayAdapterDataModelControlQuality.ViewHolderItem();

            viewHolder.textView_Lin1   = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b  = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a  = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b  = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a  = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b  = convertView.findViewById(R.id.textView_lin3b);
            viewHolder.textView_Lin4a  = convertView.findViewById(R.id.textView_lin4a);
            viewHolder.textView_Lin4b  = convertView.findViewById(R.id.textView_lin4b);
            viewHolder.textView_Lin5a  = convertView.findViewById(R.id.textView_lin5a);
            viewHolder.textView_Lin5b  = convertView.findViewById(R.id.textView_lin5b);
            viewHolder.textView_lin5ab = convertView.findViewById(R.id.textView_lin5ab);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterDataModelControlQuality.ViewHolderItem) convertView.getTag();
        }



        if (data != null)
        {
            int size = 16;
            viewHolder.textView_Lin1.setTextSize(size);
            viewHolder.textView_Lin4a.setTextSize(size);
            viewHolder.textView_Lin2a.setTextSize(size);
            viewHolder.textView_Lin2b.setTextSize(size);
            viewHolder.textView_Lin3a.setTextSize(size);
            viewHolder.textView_Lin3b.setTextSize(size);
            viewHolder.textView_Lin5a.setTextSize(size);
            viewHolder.textView_Lin5b.setTextSize(18);
            viewHolder.textView_Lin4b.setTextSize(size);



            viewHolder.textView_Lin1.setText("["+ data.getRef().trim()+"] "+data.getDesign().trim());
            viewHolder.textView_Lin1b.setText("");
            viewHolder.textView_Lin2a.setText(data.getNome().trim());
            viewHolder.textView_Lin2b.setText("V/D: "+data.getVossodoc().trim());
            viewHolder.textView_Lin3a.setText("Receção: "+ data.getData().substring(0, 10));
            viewHolder.textView_Lin3b.setText("Pedido: "+ data.getData_pedido().substring(0, 10));
            viewHolder.textView_Lin4a.setText("Lote Forn: "+ data.getLotefl().trim());
            viewHolder.textView_Lin4b.setText("Lote I9: "+ data.getLote().trim());
            viewHolder.textView_Lin5b.setText("Qtt: "+retiraDecimais(Double.parseDouble(data.getQtt())));


            viewHolder.textView_Lin1b.setVisibility(View.GONE);
            viewHolder.textView_lin5ab.setVisibility(View.GONE);
            viewHolder.textView_Lin5a.setVisibility(View.GONE);

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
        TextView textView_Lin4a;
        TextView textView_Lin4b;
        TextView textView_Lin5a;
        TextView textView_Lin5b;
        TextView textView_lin5ab;

    }
}
