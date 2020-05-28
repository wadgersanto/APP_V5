package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

public class TurnoAdapter extends ArrayAdapter<DataModelTurno>  {

    private Context context;
    private List<DataModelTurno> listTurno;

    public TurnoAdapter(Context context, int resource, ArrayList<DataModelTurno> objects) {

        super(context, resource, objects);
        this.context = context;
        this.listTurno = objects;

        //String tipoinventario, String tipoInvLoc
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelTurno getItem(int position) {

        return listTurno.get(position);
    }

    @Override
    public int getCount() {

        return listTurno.size();
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelTurno data = listTurno.get(position);

        final TurnoAdapter.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new TurnoAdapter.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (TurnoAdapter.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {

            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";

            // txtLin1 =  (data.getCodigo()+"   ").substring(0,2)+" - " + data.getDescricao();
            String mDataIniciou = "";


            txtLin1 =  "Op "+ UserFunc.retiraZerosDireita(Double.parseDouble(data.getOperador())) +" iniciou turno às:";
            txtLin1b = data.getOusrdata().replace("T"," ").substring(0,16);;

            // txtLin2a =  DateFormat.format("dd-MM-yyyy hh:mm:ss", data.getDatahoraini()).toString();
            // txtLin2b =  DateFormat.format("dd-MM-yyyy hh:mm:ss", data.getDatahorafim()).toString();
            txtLin2a = "Ini: "+data.getDatahoraini().replace("T"," ").substring(0,16);
            txtLin3a= "Fim: "+data.getDatahorafim().replace("T"," ").substring(0,16);


            txtLin2b = "Horas extra: "+ data.getHorasextra();
            txtLin3b = "Tempo adic.:"+ data.getTempoadicional();


            viewHolder.textView_Lin1.setTextSize(16);
            viewHolder.textView_Lin1b.setTextSize(16);
            viewHolder.textView_Lin1.setTypeface(null, Typeface.NORMAL);
            viewHolder.textView_Lin1b.setTypeface(null, Typeface.NORMAL);

            viewHolder.textView_Lin2a.setTextSize(16);
            viewHolder.textView_Lin3a.setTextSize(16);
            viewHolder.textView_Lin2a.setTypeface(null, Typeface.BOLD);
            viewHolder.textView_Lin3a.setTypeface(null, Typeface.BOLD);

            viewHolder.textView_Lin2b.setTextSize(16);
            viewHolder.textView_Lin3b.setTextSize(16);

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


            Integer textColor = 0;

            if (data.getMinleftfimturno() > 0) {
                textColor = Color.parseColor(Globals.getInstance().getDefaultToolbarColour());
            }
            else {
                textColor = Color.RED;
            }

            viewHolder.textView_Lin1.setTextColor(textColor);
            viewHolder.textView_Lin1b.setTextColor(textColor);
            viewHolder.textView_Lin2a.setTextColor(textColor);
            viewHolder.textView_Lin2b.setTextColor(textColor);
            viewHolder.textView_Lin3a.setTextColor(textColor);
            viewHolder.textView_Lin3b.setTextColor(textColor);


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

