package gnotus.inoveplastika.PickingBoxes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.Producao.DataModelCxof;
import gnotus.inoveplastika.R;

class ArrayAdapterMultichoice extends ArrayAdapter<DataModelCxof> {

    private Context mContext;
    private List<DataModelCxof> dataModelPickingLines;

    public ArrayAdapterMultichoice(Context context, ArrayList<DataModelCxof> objects)
    {

        super(context, 0, objects);
        this.mContext = context;
        this.dataModelPickingLines = objects;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelCxof getItem(int position) {

        return dataModelPickingLines.get(position);
    }

    @Override
    public int getCount() {

        return dataModelPickingLines.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelCxof data = dataModelPickingLines.get(position);
        final ArrayAdapterMultichoice.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.activity_multichoice, parent, false);

            viewHolder = new ArrayAdapterMultichoice.ViewHolderItem();
            viewHolder.checkedTxtNumeroCaixa = convertView.findViewById(R.id.checkedTxtNumeroCaixa);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterMultichoice.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            String[] boxes = data.getNumcx().split("\\.");
            viewHolder.checkedTxtNumeroCaixa.setTypeface(null, Typeface.BOLD);

            viewHolder.checkedTxtNumeroCaixa.setTypeface(null,Typeface.NORMAL);

            viewHolder.checkedTxtNumeroCaixa.setText("Nr Caixa: "+ boxes[0]);

            if(data.getSelected())
                viewHolder.checkedTxtNumeroCaixa.setChecked(true);
            else
                viewHolder.checkedTxtNumeroCaixa.setChecked(false);
        }

        return convertView;

    }

    private static class ViewHolderItem {

        CheckedTextView checkedTxtNumeroCaixa;

    }
}
