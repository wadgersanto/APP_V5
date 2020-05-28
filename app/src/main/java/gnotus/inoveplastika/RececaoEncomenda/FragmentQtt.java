package gnotus.inoveplastika.RececaoEncomenda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import gnotus.inoveplastika.DataModels.DataModelEditActivity;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.R;

import static gnotus.inoveplastika.Dialogos.retiraDecimais;


public class FragmentQtt extends Fragment
{

    private DataModelEditActivity dataModelEditActivity;
    private TextView txt1b, txt2b, txt3b, txt22b, textView22a, txt4b,
            textView_21a, textView_4a, textView_3a, textView_2a, textView_1a, Kg_Sac;

    private RadioButton radioButton1, radioButton2;
    private EditText ed23a;
    private OnFragmentQtt onFragmentQtt;
    private RadioGroup radioGroup;

    private  Double qtdalt_lida, qtd_lida;

    private boolean UNIDADE_ALTERNATIVA = true;

    private String _qtt = "";


    private String design,fDesign,ref,fRef,lote,fLote,stock,fStock, fQttRec, fedtQtt, uni2qttPend, qttConv;

    public FragmentQtt() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_qtt, container, false);


        initialize(view);
        fillFragment();

        if(!_qtt.isEmpty())
            ed23a.setText(_qtt);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked)
            {
                switch (checked)
                {
                    case R.id.radioButton1:
                        radioButton1.setChecked(true);
                        radioButton2.setChecked(false);
                        UNIDADE_ALTERNATIVA = true;

                        Kg_Sac.setText(dataModelEditActivity.getUn2());

                        if(!ed23a.getText().toString().trim().isEmpty())
                        {
                            ed23a.setText(retiraDecimais(qtd_lida));
                            ed23a.setSelectAllOnFocus(true);
                            ed23a.setSelection(0, ed23a.length());

                            _qtt = ed23a.getText().toString().trim();
                        }
                    break;

                    case R.id.radioButton2:
                        radioButton2.setChecked(true);
                        radioButton1.setChecked(false);
                        UNIDADE_ALTERNATIVA = false;

                        Kg_Sac.setText(dataModelEditActivity.getUnidade());

                        if(!ed23a.getText().toString().trim().isEmpty())
                        {
                            ed23a.setText(retiraDecimais(qtdalt_lida));
                            ed23a.setSelectAllOnFocus(true);
                            ed23a.setSelection(0, ed23a.length());

                            _qtt = ed23a.getText().toString().trim();
                        }

                    break;
                }

            }
        });


        ed23a.requestFocus();
        EditTextOps.showKeyboard(getActivity());
        ed23a.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ed23a.setImeOptions(EditorInfo.IME_ACTION_SEND);
        ed23a.setSelectAllOnFocus(true);
        ed23a.setSelection(0, ed23a.length());

        ed23a.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(ed23a.getText().toString().trim().isEmpty() || ed23a.getText().toString().trim().equals("0") || ed23a.getText().toString().trim().equals("."))
                    return;

                if(UNIDADE_ALTERNATIVA)
                {
                    qtdalt_lida = Double.parseDouble(qttConv) * Double.parseDouble( ed23a.getText().toString() );

                    if(Double.parseDouble(ed23a.getText().toString().trim()) > Integer.parseInt(uni2qttPend))
                    {
                        Dialogos.dialogoErro("Erro","Quantidade superior ao stock", 2,getActivity(),false);
                        ed23a.setText("");
                        qtdalt_lida = 0.0;
                        return;
                    }
                }
                else
                {
                    qtd_lida  = (1 / Double.parseDouble(qttConv))* Double.parseDouble(ed23a.getText().toString());

                    if(Double.parseDouble(ed23a.getText().toString().trim()) > Integer.parseInt(stock))
                    {
                        Dialogos.dialogoErro("Erro","Quantidade superior ao stock", 2,getActivity(),false);
                        ed23a.setText("");
                        qtd_lida = 0.0;
                        return;
                    }

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ed23a.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                if ( actionId == EditorInfo.IME_ACTION_SEND ||  (event.getAction() == KeyEvent.ACTION_DOWN) && (actionId == KeyEvent.KEYCODE_ENTER))
                {
                    EditTextOps.hideKeyboard(getActivity(),ed23a);

                    if(ed23a.getText().toString().trim().isEmpty() || ed23a.getText().toString().trim().equals("0"))
                        Dialogos.dialogoErro("Erro","Quantidade invalida", 2,getActivity(),false);
                    else
                    {
                        if(UNIDADE_ALTERNATIVA)
                        {
                            qtdalt_lida = Double.parseDouble(qttConv) * Double.parseDouble( ed23a.getText().toString() );
                            onFragmentQtt.onQtt(qtdalt_lida,dataModelEditActivity.getUnidade());
                        }
                        else
                            onFragmentQtt.onQtt(Double.parseDouble(ed23a.getText().toString().trim()),Kg_Sac.getText().toString().trim());
                    }

                }

                return true;
            }
        });
//        ed23a.setOnKeyListener(new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent event)
//            {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
//                {
//                    EditTextOps.hideKeyboard(getActivity(),ed23a);
//                    if(Integer.parseInt(ed23a.getText().toString().trim()) > 20)
//                    {
//                        Dialogos.dialogoErro("Dados inválidos","O campo quantidade não pode estar vazio",2, getActivity(), false);
//                        return false;
//                    }
//
//                }
//
//
//                return true;
//            }
//        });

//        btnEnviar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("Pretende submeter a quantidade?");
//                builder.setCancelable(false);
//
//                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i)
//                    {
//                        dialogInterface.dismiss();
//                        onFragmentQtt.onQtt(ed23a.getText().toString().trim());
//                    }
//                });
//                builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i)
//                    {
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//            }
//        });
//
//
//
//


        return view;
    }



    private void initialize(View view)
    {
        txt1b          = view.findViewById(R.id.txt1b);
        txt2b          = view.findViewById(R.id.txt2b);
        txt3b          = view.findViewById(R.id.txt3b);
        txt22b         = view.findViewById(R.id.txt22b);
        txt4b          = view.findViewById(R.id.txt4b);
        ed23a          = view.findViewById(R.id.ed23a);
        textView22a    = view.findViewById(R.id.textView22a);
        textView_21a   = view.findViewById(R.id.textView_21a);
        textView_4a    = view.findViewById(R.id.textView_4a);
        textView_3a    = view.findViewById(R.id.textView_3a);
        textView_2a    = view.findViewById(R.id.textView_2a);
        textView_1a    = view.findViewById(R.id.textView_1a);
        radioButton1   = view.findViewById(R.id.radioButton1);
        radioButton2   = view.findViewById(R.id.radioButton2);
        radioGroup     = view.findViewById(R.id.radioGroup_visivel);
        Kg_Sac          = view.findViewById(R.id.Kg_Sac);

    }

    private void fillFragment()
    {

        qttConv = dataModelEditActivity.getU_qttconv();

        if(qttConv.trim().isEmpty() || qttConv.trim().equals("0") )
        {
            if(dataModelEditActivity.getUn2().trim().isEmpty())
                radioButton1.setVisibility(View.GONE);
            else
                radioButton1.setText(dataModelEditActivity.getUn2());

            if(dataModelEditActivity.getUnidade().trim().isEmpty())
                radioButton2.setVisibility(View.GONE);
            else
                radioButton2.setText(dataModelEditActivity.getUnidade());
        }

        Kg_Sac.setText(dataModelEditActivity.getUn2());

        design  = dataModelEditActivity.getTxt1b().trim();
        fDesign = dataModelEditActivity.getTextView_1a();

        ref     = dataModelEditActivity.getTxt2b().trim();
        fRef    = dataModelEditActivity.getTextView_2a();

        lote    = dataModelEditActivity.getTxt3b().trim();
        fLote   = dataModelEditActivity.getTextView_3a();

        stock   = dataModelEditActivity.getTxt4b().trim();
        fStock  = dataModelEditActivity.getTextView_4a();

        fedtQtt = dataModelEditActivity.getTextView_21a();

        fQttRec = dataModelEditActivity.getTextView22a();
        uni2qttPend = dataModelEditActivity.getTxt22b().trim();


//        ed23a.setText(uni2qttPend);
//        ed23a.setSelectAllOnFocus(true);
//        ed23a.setSelection(0, ed23a.length());



        if(design.isEmpty() || fDesign.isEmpty())
        {
            txt1b.setVisibility(View.GONE);
            textView_1a.setVisibility(View.GONE);
        }
        else
        {
            txt1b.setText(design);
            textView_1a.setText(fDesign);
        }

        if(ref.isEmpty() || fRef.isEmpty())
        {
            txt2b.setVisibility(View.GONE);
            textView_2a.setVisibility(View.GONE);
        }
        else
        {
            txt2b.setText(ref);
            textView_2a.setText(fRef);
        }

        if(lote.isEmpty() || fLote.isEmpty())
        {
            txt3b.setVisibility(View.GONE);
            textView_3a.setVisibility(View.GONE);
        }
        else
        {
            txt3b.setText(lote);
            textView_3a.setText(fLote);
        }

        if(stock.isEmpty() || fStock.isEmpty())
        {
            txt4b.setVisibility(View.GONE);
            textView_4a.setVisibility(View.GONE);
        }
        else
        {
            txt4b.setText(uni2qttPend+" "+dataModelEditActivity.getUn2()+" / "+ stock +" "+dataModelEditActivity.getUnidade());
            textView_4a.setText(fStock);
        }

        if(fedtQtt.isEmpty())
            textView_21a.setVisibility(View.GONE);
        else
            textView_21a.setText(fedtQtt);

        if(fQttRec.isEmpty() || uni2qttPend.isEmpty())
        {
            textView22a.setVisibility(View.GONE);
            txt22b.setVisibility(View.GONE);
        }
        else
        {
            textView22a.setText(fQttRec);
            txt22b.setText("");
        }

        radioButton1.setText(dataModelEditActivity.getUn2());
        radioButton2.setText(dataModelEditActivity.getUnidade());

        System.out.println(dataModelEditActivity.getUn2()+" "+uni2qttPend);
        System.out.println(dataModelEditActivity.getUnidade()+" "+ stock);
        System.out.println("conv "+ qttConv);


    }

    public void setOnFragmentQtt(OnFragmentQtt onFragmentQtt) {
        this.onFragmentQtt = onFragmentQtt;
    }

    /*************************interface**************************/
    public interface OnFragmentQtt
    {
        void onQtt(Double qtt, String unidade);

    }
    /*************************interface**************************/

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }



    /**************************************  setter***************************************/
    public void setDataModelEditActivity(DataModelEditActivity dataModelEditActivity) {
        this.dataModelEditActivity = dataModelEditActivity;
    }

    /**************************************  setter***************************************/

}
