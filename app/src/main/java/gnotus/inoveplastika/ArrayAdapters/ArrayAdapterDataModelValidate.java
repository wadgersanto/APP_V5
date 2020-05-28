package gnotus.inoveplastika.ArrayAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.DataModels.DataModelValidate;
import gnotus.inoveplastika.R;

public class ArrayAdapterDataModelValidate extends ArrayAdapter<DataModelValidate> {

    private Context mContext;
    private List<DataModelValidate> dataModelValidates;

    public ArrayAdapterDataModelValidate(Context context, ArrayList<DataModelValidate> objects)
    {

        super(context, 0, objects);
        this.mContext           = context;
        this.dataModelValidates = objects;

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelValidate getItem(int position) {

        return dataModelValidates.get(position);
    }

    @Override
    public int getCount() {

        return dataModelValidates.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelValidate data = dataModelValidates.get(position);
        final ArrayAdapterDataModelValidate.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.activity_validate, parent, false);

            viewHolder = new ArrayAdapterDataModelValidate.ViewHolderItem();
            viewHolder.txtDescricao            = convertView.findViewById(R.id.txtDescricao);
            viewHolder.txtObrigacao            = convertView.findViewById(R.id.txtObrigacao);
//            viewHolder.linear                  = convertView.findViewById(R.id.linear);
            viewHolder.constraint_Layout       = convertView.findViewById(R.id.constraint_Layout);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ArrayAdapterDataModelValidate.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            viewHolder.txtDescricao.setTypeface(null, Typeface.BOLD);

            viewHolder.txtDescricao.setTypeface(null,Typeface.NORMAL);
            viewHolder.txtObrigacao.setTypeface(null,Typeface.NORMAL);

            viewHolder.txtDescricao.setVisibility(View.VISIBLE);
//            viewHolder.txtObrigacao.setVisibility(View.VISIBLE);

            viewHolder.txtDescricao.setText(data.getDescricao().trim());

            if(!data.getChoosed().trim().isEmpty())
            {
                viewHolder.txtObrigacao.setVisibility(View.VISIBLE);
                viewHolder.txtObrigacao.setText(data.getChoosed().trim());
                viewHolder.txtObrigacao.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }



                if(data.isAllChecked())
                    viewHolder.constraint_Layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorCheck));
                else
                    viewHolder.constraint_Layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));





        }

        return convertView;

    }

    private static class ViewHolderItem {

        TextView txtDescricao;
        TextView txtObrigacao;
        LinearLayout linear;
        ConstraintLayout constraint_Layout;

    }
}
