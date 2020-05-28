package gnotus.inoveplastika.RececaoEncomenda;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.R;

import static gnotus.inoveplastika.Dialogos.retiraDecimais;


public class FragmentVerificarConformidade extends Fragment
{

//    private DataModelBi dataModelBi;
//    private TextView txt_design,txt_lote,txt_qtt;
//
//    private RadioButton radioButton_v_ok, radioButton_v_naoOk, radioButton_L_ok, radioButton_L_naoOk;
//    private OnFragmentVerificarConformidade onFragmentVerificarConformidade;
//    private RadioGroup radioGroup_visivel, radioButton_Legivel;
//    private Button btn_submeter;
//
//    private String legivel="", visivel="";
//
//
//
//    public FragmentVerificarConformidade() {
//        // Required empty public constructor
//    }
//
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState)
//    {
//        View view =  inflater.inflate(R.layout.activity_validate_list, container, false);
//
//        initialize(view);
//        fillFragment();
//
//
//        radioGroup_visivel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checked)
//            {
//                switch (checked)
//                {
//                    case R.id.radioButton_v_ok:
//                        radioButton_v_ok.setChecked(true);
//                        radioButton_v_naoOk.setChecked(false);
//                        visivel = "ok";
//                        break;
//
//                    case R.id.radioButton_v_naoOk:
//                        radioButton_v_naoOk.setChecked(true);
//                        radioButton_v_ok.setChecked(false);
//                        visivel = "não ok";
//                        break;
//                }
//
//            }
//        });
//
//        radioButton_Legivel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checked)
//            {
//                switch (checked)
//                {
//                    case R.id.radioButton_L_ok:
//                        radioButton_L_ok.setChecked(true);
//                        radioButton_L_naoOk.setChecked(false);
//                        legivel = "ok";
//                        break;
//
//                    case R.id.radioButton_L_naoOk:
//                        radioButton_L_naoOk.setChecked(true);
//                        radioButton_L_ok.setChecked(false);
//                        legivel = "não ok";
//                        break;
//                }
//
//            }
//        });
//
//
//        btn_submeter.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if(!legivel.isEmpty() || !legivel.isEmpty())
//                {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setMessage("Pretende submeter?");
//                    builder.setCancelable(false);
//
//                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i)
//                        {
//                            dialogInterface.dismiss();
//                            onFragmentVerificarConformidade.onVerificarConformidade(visivel,legivel);
//                        }
//                    });
//                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i)
//                        {
//                            dialogInterface.dismiss();
//                        }
//                    });
//
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//                else
//                    Dialogos.dialogoInfo("VERIFICAÇÃO DE CONFOR. DIZ:","Verf. conformidade das embalagens",3.0,getActivity(),false);
//
//            }
//        });
//        return view;
//    }
//
//
//
//    private void initialize(View view)
//    {
//        txt_design            = view.findViewById(R.id.txt_design);
//        txt_lote              = view.findViewById(R.id.txt_lote);
//        txt_qtt               = view.findViewById(R.id.txt_qtt);
//        radioButton_v_ok      = view.findViewById(R.id.radioButton_v_ok);
//        radioButton_v_naoOk   = view.findViewById(R.id.radioButton_v_naoOk);
//        radioButton_L_ok      = view.findViewById(R.id.radioButton_L_ok);
//        radioButton_L_naoOk   = view.findViewById(R.id.radioButton_L_naoOk);
//        radioGroup_visivel    = view.findViewById(R.id.radioGroup_visivel);
//        radioButton_Legivel   = view.findViewById(R.id.radioButton_Legivel);
//        btn_submeter          = view.findViewById(R.id.btn_submeter);
//    }
//
//    private void fillFragment()
//    {
//
//        txt_design.setText("["+dataModelBi.getReferencia()+"]-"+ dataModelBi.getDesign());
//        txt_lote.setText("Lote do fornecedor: "+dataModelBi.getLobs());
//        txt_qtt.setText("Quantidade: "+retiraDecimais(dataModelBi.getQtt()) +" "+dataModelBi.getUnidade());
//
//    }
//
//    public void setOnFragmentVerificarConformidade(OnFragmentVerificarConformidade onFragmentVerificarConformidade) {
//        this.onFragmentVerificarConformidade = onFragmentVerificarConformidade;
//    }
//
//    /*************************interface**************************/
//    public interface OnFragmentVerificarConformidade
//    {
//        void onVerificarConformidade(String visivel, String legivel);
//
//    }
//    /*************************interface**************************/
//
//    @Override
//    public void onAttach(Context context)
//    {
//        super.onAttach(context);
//    }
//
//
//    public void setDataModelBi(DataModelBi dataModelBi) {
//        this.dataModelBi = dataModelBi;
//    }
}
