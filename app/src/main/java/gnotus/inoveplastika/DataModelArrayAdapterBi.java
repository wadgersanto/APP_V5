package gnotus.inoveplastika;


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

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Producao.IniciarOfActivity;

public class DataModelArrayAdapterBi extends ArrayAdapter<DataModelBi> {

    private Context context;
    private List<DataModelBi> bi;
    private String nomeAtividade;
    private String tipoInventario,tipoInvLoc, nomeListView,pickingType = "";
    //public String dmaabi_lin1,dmaabi_lin2a,dmaabi_lin2b,dmaabi_lin3a,dmaabi_lin3b;

    public DataModelArrayAdapterBi(Context context, int resource, ArrayList<DataModelBi> objects,
                                   Bundle bundle) {

        super(context, resource, objects);
        this.context = context;
        this.bi = objects;
        this.tipoInventario = bundle.getString("tipoinventario");
        this.tipoInvLoc = bundle.getString("tipoinvloc");

        // tipo é um parametro que permite identificar chamadas diferentes da mesma atividade
        if (bundle.getString("listview") != null) this.nomeListView = bundle.getString("listview");
        else this.nomeListView = "";

        if (! (bundle.getString("pickingType") == null)) {
            this.pickingType = bundle.getString("pickingType");
        }

        System.out.println("List view: "+nomeListView);

        //String tipoinventario, String tipoInvLoc

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public DataModelBi getItem(int position) {

        return bi.get(position);
    }

    @Override
    public int getCount() {

        return bi.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        DataModelBi data = bi.get(position);
        final DataModelArrayAdapterBi.ViewHolderItem viewHolder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_bi3lin, parent, false);

            viewHolder = new DataModelArrayAdapterBi.ViewHolderItem();
            viewHolder.textView_Lin1 = convertView.findViewById(R.id.textView_lin1);
            viewHolder.textView_Lin1b = convertView.findViewById(R.id.textView_lin1b);
            viewHolder.textView_Lin2a = convertView.findViewById(R.id.textView_lin2a);
            viewHolder.textView_Lin2b = convertView.findViewById(R.id.textView_lin2b);
            viewHolder.textView_Lin3a = convertView.findViewById(R.id.textView_lin3a);
            viewHolder.textView_Lin3b = convertView.findViewById(R.id.textView_lin3b);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (DataModelArrayAdapterBi.ViewHolderItem) convertView.getTag();
        }


        if (data != null) {


            String nomeAtividadeChamou = context.getClass().getSimpleName();
            String txtLin1 = "",txtLin1b = "",txtLin2a= "", txtLin2b ="",txtLin3a = "", txtLin3b = "";

            System.out.println("context: "+context.getClass().getSimpleName());
            System.out.println("class: "+InvOfActivity.class.getSimpleName());

            // se a classe que chamou foi InvOfActivity
            if (nomeAtividadeChamou.equals(InvOfActivity.class.getSimpleName())) {


                viewHolder.textView_Lin1.setTextSize(14);
                viewHolder.textView_Lin1b.setTextSize(14);
                viewHolder.textView_Lin2a.setTextSize(14);
                viewHolder.textView_Lin3b.setTextSize(14);
                viewHolder.textView_Lin3a.setTextSize(14);

                txtLin1 = data.getRef().trim()+" - "+data.getDesign().trim();
                if (! data.getLote().trim().equals("")) txtLin1b = "   L: "+data.getLote();

                viewHolder.textView_Lin1b.setTypeface(null,Typeface.NORMAL);

                if(data.getJacontou() || data.getQtt() != 0 )
                    txtLin2a = "Contagem: "+data.getQtt()+ " "+data.getUnidade();
                else
                    txtLin2a = "Contagem: ";

                // txtLin3b = "Consumo: "+UserFunc.arredonda(data.getNum1(),3) + " "+data.getUnidade();

                txtLin3a = "Stock:    "+data.getQtt2()+ " "+data.getUnidade();

            }

            // se a classe que chamou foi Inventario
            if (nomeAtividadeChamou.equals(InventarioActivity.class.getSimpleName())) {

                Double fconversao = data.getFconversao();

                System.out.println("Tipo Inventario: "+ tipoInventario);

                switch (tipoInventario) {


                    case "REF":

                        txtLin1 = data.getReferencia()+" - "+data.getDescricao();
                        txtLin1b = "";
                        if (! data.getLote().equals("")) txtLin2a = "L ["+data.getLote()+"]";
                        txtLin2b = "["+data.getArmazem()+"]["+data.getZona1()+"]["+data.getAlveolo1()+"]";
                        txtLin3a = "Contagem: "+retiraDecimais(data.getQtt())+ " "+data.getUnidade();
                        txtLin3b = "Stk: "+retiraDecimais(data.getQtt2())+ " "+data.getUnidade();

                        viewHolder.textView_Lin1.setTextSize(13);
                        viewHolder.textView_Lin2a.setTextSize(13);
                        viewHolder.textView_Lin2b.setTextSize(13);
                        viewHolder.textView_Lin3a.setTextSize(13);
                        viewHolder.textView_Lin3b.setTextSize(13);

                        if ( fconversao != 1) {
                            txtLin3a = txtLin3a+"/"+retiraDecimais(data.getQtt()/fconversao)+ " "+data.getUnidad2();
                            txtLin3b = txtLin3b+"/"+retiraDecimais(data.getQtt2()/fconversao)+ " "+data.getUnidad2();
                        }

                        break;

                    case "ALV":


                        viewHolder.textView_Lin1.setTextSize(13);
                        viewHolder.textView_Lin1b.setTextSize(13);
                        viewHolder.textView_Lin2a.setTextSize(13);
                        viewHolder.textView_Lin3a.setTextSize(13);

                        // viewHolder.textView_Lin1.setTypeface(null,Typeface.NORMAL);

                        txtLin1 = data.getReferencia()+" - "+data.getDescricao();

                        if (! data.getLote().trim().equals("")) txtLin1b =  "L ["+data.getLote()+"]";


                        // definir como vai aparecer a quantidade contada. Em certos casos vamos limpar de modo a obrigar o operador a ler,
                        // como nos casos do inventário de localização de consumos de of

                        if(data.getJacontou() || data.getQtt() != 0 ) {
                            txtLin2a = "Contagem: " + retiraDecimais(data.getQtt()) + " " + data.getUnidade();
                            if ( fconversao != 1) {
                                txtLin2a = txtLin2a+" / "+retiraDecimais(data.getQtt()/fconversao)+ " "+data.getUnidad2();
                            }
                        }

                        else {
                            txtLin2a = "Contagem: ";
                        }


                        txtLin3a = "Stock: "+retiraDecimais(data.getQtt2())+ " "+data.getUnidade();

                        if ( fconversao != 1) {
                            // txtLin2a = txtLin2a+"/"+retiraDecimais(data.getQtt()/fconversao)+ " "+data.getUnidad2();
                            txtLin3a = txtLin3a+" / "+retiraDecimais(data.getQtt2()/fconversao)+ " "+data.getUnidad2();
                        }

                }


            }

            // se a classe que chamou foi InvOfActivity
            if (nomeAtividadeChamou.equals(IniciarOfActivity.class.getSimpleName())) {

                txtLin1 = data.getReferencia()+" - "+data.getDescricao();
                txtLin2a = "Localização consumo: ";

                if (data.getZona1().equals("") || data.getAlveolo1().equals(""))
                    txtLin2b = "(atribuir)";
                else
                    txtLin2b = "["+data.getArmazem()+"]["+data.getZona1()+"]["+data.getAlveolo1()+"]";


                viewHolder.textView_Lin2b.setTypeface(null,Typeface.BOLD);
                viewHolder.textView_Lin1.setTypeface(null,Typeface.NORMAL);

                viewHolder.textView_Lin2a.setTextSize(14);
                viewHolder.textView_Lin2b.setTextSize(14);
                viewHolder.textView_Lin1.setTextSize(14);

            }

            // se a classe que chamou foi Picking e o pedido foi da listViewPicking List

            if (nomeAtividadeChamou.equals(PickingActivity.class.getSimpleName()) && nomeListView.equals("listViewPickingList")) {

                txtLin1 = data.getReferencia()+" - "+data.getDescricao();

                if (pickingType.equals("EXP")) {
                    txtLin2a = "Qtd por satisfazer:      "+UserFunc.retiraZerosDireita(data.getQtt()-data.getBinum1())+ " "+data.getUnidade();
                }

                if (pickingType.equals("ABASTPROD")) {
                    txtLin2a = "Qtd por satisfazer:      "+UserFunc.retiraZerosDireita(data.getQtt()-data.getQtt2())
                            + " "+data.getUnidade();

                    // colocar as unidades alternativas
                    if (data.getFconversao() != 1) {
                        txtLin2a = txtLin2a + "/"+
                                UserFunc.retiraZerosDireita(
                                        UserFunc.arredonda(
                                                (data.getQtt()-data.getQtt2()) / data.getFconversao(),2
                                        )
                                )+ " " + data.getUnidad2();
                    }

                }

                if (pickingType.equals("DEVEXP")) {
                    txtLin2a = "Stock zona expedição:      "+UserFunc.retiraZerosDireita(data.getBinum1()-data.getQtt2())+ " "+data.getUnidade();
                }

                viewHolder.textView_Lin2a.setTextSize(16);
                viewHolder.textView_Lin2b.setTextSize(16);
                viewHolder.textView_Lin1.setTextSize(16);

            }

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

    private String retiraDecimais(Double numero){

        String strnumero;

        strnumero = String.format("%.3f",numero).replace(",",".");

        boolean processa = true;

        while (strnumero.indexOf(".") >= 0 && ( strnumero.substring(strnumero.length()-1).equals("0") || strnumero.substring(strnumero.length()-1).equals(".") )) {
            strnumero  = strnumero.substring(0,strnumero.length()-1);
        }

        return strnumero;

    }


}
