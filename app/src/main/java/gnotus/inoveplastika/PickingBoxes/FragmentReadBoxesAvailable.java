package gnotus.inoveplastika.PickingBoxes;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.BoxesAvailableForPickingWs;
import gnotus.inoveplastika.API.Logistica.RegisterPickingBoxesWs;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.Logistica.DataModelPickingLine;
import gnotus.inoveplastika.Producao.DataModelCxof;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

import static gnotus.inoveplastika.Dialogos.showToast;


public class FragmentReadBoxesAvailable extends Fragment {

    private ProgressDialog progressDialog;
    private onSelectItemListener mOnSelectItemListiner;

    private EditText edtLerCaixa;
    private TextView txtCaixasLidas, txtCaixasPorLer;
    private Button btnGuardar, btnMaunal , btnFiltro;
    private ListView listviewFragment;

    private String qqPorLer ="", lote = "", ref = "";

    private static int check = 0;

    private DataModelPickingLine dataModelPickingLine;

    private ArrayList<DataModelCxof> listPickedBoxes             = new ArrayList<>(); // nosso arrayList que será armazenado os nossos números de caixa lido
    private ArrayList<DataModelCxof> listAvailableBoxes          = new ArrayList<>(); // nosso arrayList que será armazenado todos os números de caixas disponíveis

    private ArrayList<DataModelCxof> listAvailableBoxesNotPicked = new ArrayList<>(); /* nosso arrayList que será armazenado todos os números de caixas disponíveis mas,
                                                                                          que vai ser removido de acordo com cada número de caixa lido*/

    public FragmentReadBoxesAvailable() {  }// construtor do nosso FragmentReadBoxesAvailable

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_fragment_show, container, false);

        listviewFragment = view.findViewById(R.id.listviewFragment);
        edtLerCaixa      = view.findViewById(R.id.edtLerCaixa);
        txtCaixasLidas   = view.findViewById(R.id.txtCaixasLidas);
        txtCaixasPorLer  = view.findViewById(R.id.txtCaixasPorLer);
        btnGuardar       = view.findViewById(R.id.btnGuardar);
        btnFiltro        = view.findViewById(R.id.btnFiltro);
        btnMaunal        = view.findViewById(R.id.btnMaunal);


        if(dataModelPickingLine == null)
            dataModelPickingLine = new DataModelPickingLine();

        lote = dataModelPickingLine.getLote().trim();
        ref  = dataModelPickingLine.getRef().trim();

        requeryBoxesAvailableForPicking();

        btnGuardar.setEnabled(false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Aguarde");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        qqPorLer = UserFunc.retiraZerosDireita(dataModelPickingLine.getNrboxes_pend());

        txtCaixasPorLer.setText("Por ler: "+ qqPorLer);
        edtLerCaixa.requestFocus();
        hideKeyBoard(edtLerCaixa);


        readBoxesAvailable(edtLerCaixa, false);

        removeItemListPickedBoxes();


        btnMaunal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                hideKeyBoard(edtLerCaixa);
                alertDialogManual();
            }
        });

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if((Integer.parseInt(qqPorLer) - listPickedBoxes.size()) == 0)
                {
                    showToast(getContext(),"Leitura de Caixa\nSem número de caixa para ler",2.0,20);
                    return;
                }
                else
                {
                    check = (Integer.parseInt(qqPorLer) - listPickedBoxes.size());
                    alertDialogFiltro();
                }
                hideKeyBoard(edtLerCaixa);

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Pretende submeter os números de caixas?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                        registerPickingBoxes();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        return view;

    }//fim onCreate


    private void registerPickingBoxes()
    {
        RegisterPickingBoxesWs registerPickingBoxesWs = new RegisterPickingBoxesWs(getActivity());
        registerPickingBoxesWs.setOnRegisterPickingBoxesListener(new RegisterPickingBoxesWs.OnRegisterPickingBoxesListener() {
            @Override
            public void onSuccess(DataModelWsResponse wsResponse) {

               showToast(getContext(),wsResponse.getDescricao(),3.0, 20);
               System.out.println("sucesso"+ wsResponse.getDescricao());
            }

            @Override
            public void onError(String error) {
//                showToast(getContext(),error,3.0, 20);
                System.out.println("error"+ error);
            }
        });
        registerPickingBoxesWs.execute(listPickedBoxes);
    }
    private void alertDialogManual()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("MANUAL");
        builder.setMessage("Caixas por Ler: "+ (Integer.parseInt(qqPorLer) - listPickedBoxes.size()));
        builder.setCancelable(false);

        final  EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setHint("Ler caixa disponível");
        builder.setView(editText);
        editText.requestFocus();

        showKeyboard(getActivity());

//        readBoxesAvailable( editText, true);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(editText.getText().toString().trim().isEmpty() || editText.getText().toString().trim().equals("0"))
                {
                    dialogInterface.dismiss();
                    hideKeyBoard(edtLerCaixa);

                    showToast(getContext(),"Introduza um número de caixa",2.0,20);

                    alertDialogManual();
                    return;

                }
                if((Integer.parseInt(qqPorLer) - listPickedBoxes.size()) == 0)
                {
                    showToast(getContext(),"Leitura de Caixa\nQuantidade superior",2.0,20);
                    return;
                }

                hideKeyBoard(editText);
                addItemArrayList(editText.getText().toString().trim().toLowerCase(), true);
                dialogInterface.dismiss();


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                hideKeyBoard(editText);
                dialogInterface.dismiss();
                edtLerCaixa.requestFocus();
            }
        });

       AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void  alertDialogFiltro()
    {

        listAvailableBoxesNotPicked.clear();
        for (DataModelCxof box: listAvailableBoxes) {

            if (! box.getSelected()) {
                listAvailableBoxesNotPicked.add(box);
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Números de caixas Por ler: "+(Integer.parseInt(qqPorLer) - listPickedBoxes.size()));

        final ArrayAdapterMultichoice adapterMultichoice = new ArrayAdapterMultichoice(getContext(), listAvailableBoxesNotPicked);
        final ListView listView = new ListView(getContext());
        listView.setAdapter(adapterMultichoice);
        adapterMultichoice.notifyDataSetChanged();

        builder.setView(listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l)
            {

                if(check == 0)
                {
                    if(adapterMultichoice.getItem(position).getSelected())
                    {
                        adapterMultichoice.getItem(position).setSelected(false);
                        check++;
                    }
                    else
                        showToast(getContext(),"Leitura de Caixa\nQuantidade superior",2.0,20);

                }
                else
                {
                    if(adapterMultichoice.getItem(position).getSelected())
                    {
                        adapterMultichoice.getItem(position).setSelected(false);
                        check++;
                    }
                    else
                    {
                        adapterMultichoice.getItem(position).setSelected(true);
                        check--;
                    }
                }

                adapterMultichoice.notifyDataSetChanged();
                System.out.println("->"+ check);

            }
        });


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                for (DataModelCxof box: listAvailableBoxesNotPicked)
                {
                    if (box.getSelected())
                        addBoxToReadedBoxes(box.getNumcx());
                }
                notifyDataChanged();
                dialogInterface.dismiss();
                edtLerCaixa.requestFocus();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                for (DataModelCxof box: listAvailableBoxesNotPicked)
                {
                    if (box.getSelected())
                        box.setSelected(false);
                }
                dialogInterface.dismiss();
                edtLerCaixa.requestFocus();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void readBoxesAvailable(final EditText inputEditText, final boolean showAlertDialog)
    {

        EditTextBarCodeReader editTextBarCodeReader = new EditTextBarCodeReader(inputEditText,getActivity());

        inputEditText.setLongClickable(true);
        inputEditText.setShowSoftInputOnFocus(true);
        inputEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        editTextBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener()
        {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                if(editText.getText().toString().trim().isEmpty() || editText.getText().toString().trim().equals("0"))
                    showToast(getContext(),"Leitura de Caixa\nIntroduza um número de caixa",4.0,20);

                else
                    addItemArrayList(scannedText, false);

                editText.setText("");
                editText.requestFocus();
                hideKeyBoard(editText);

            }
        });


    }


    private void addItemArrayList(String nrmCaixa, boolean showAlertDialog)
    {
        if(! nrmCaixa.equals("0"))// vamos verificar se o valor introduzido é diferente de 0
        {

            for(DataModelCxof cxof : listPickedBoxes) // vamos percorrer o nosso listPickedBoxes
            {
                if(cxof.getNumcx().equals(nrmCaixa))// e verificar se já existe o nrCaixa
                {
                    showToast(getContext(),"Leitura de Caixa\nEste número de caixa já foi lido",2.0,20);

                    if(showAlertDialog)
                        alertDialogManual();
                    return;
                }
            }

            if(listPickedBoxes.size() == dataModelPickingLine.getNrboxes_pend())
                showToast(getContext(),"Leitura de Caixa\nQuantidade superior",2.0,20);
            else
                addBoxToReadedBoxes(nrmCaixa);
        }
        notifyDataChanged();
        if(showAlertDialog)
            alertDialogManual();

    }

    private void notifyDataChanged()
    {
        AdapterListPickingBoxes adapterListPickingBoxes = new AdapterListPickingBoxes(getContext(), listPickedBoxes);
        adapterListPickingBoxes.notifyDataSetChanged();
        listviewFragment.setAdapter(adapterListPickingBoxes);
        txtCaixasLidas.setText(""+ listPickedBoxes.size());

        edtLerCaixa.setText("");
        edtLerCaixa.requestFocus();

        buttonState();


    }

    private void buttonState()
    {
        if(listPickedBoxes.size() == dataModelPickingLine.getNrboxes_pend())
        {
            btnGuardar.setEnabled(true);
        }
        else
        {
            btnGuardar.setEnabled(false);
        }
    }


    private void removeItemListPickedBoxes()
    {
        listviewFragment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int itemSelect, final long l)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Deseja remover o número de caixa?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        removeBoxFromReadedBoxes(itemSelect);

                        notifyDataChanged();


                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        edtLerCaixa.requestFocus();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }

    public void hideKeyBoard(EditText view)
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setOnSelectItemListener(onSelectItemListener onSelectItemListener)
    {
        this.mOnSelectItemListiner  = onSelectItemListener;
    }

    public void showKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    public interface onSelectItemListener
    {
        void onSelectedItem();
    }


    public void setDataModelPickingLine(DataModelPickingLine dataModelPickingLine) {
        this.dataModelPickingLine = dataModelPickingLine;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    private void requeryBoxesAvailableForPicking()
    {
        BoxesAvailableForPickingWs boxesAvailableForPickingWs = new BoxesAvailableForPickingWs(getActivity());
        boxesAvailableForPickingWs.setOnBoxesAvailable(new BoxesAvailableForPickingWs.OnBoxesAvailable() {
            @SuppressLint("LongLogTag")
            @Override
            public void onWsResponseBoxesAvailable(ArrayList<DataModelCxof> arrayListdataModelCxofs)
            {
                listAvailableBoxes          = arrayListdataModelCxofs;
                listAvailableBoxesNotPicked = new ArrayList<>(arrayListdataModelCxofs);

                Log.d("tamanho de caixas disponiveis", listAvailableBoxes.size()+"");
                Log.d("tamanho de caixas disponiveis não picada", listAvailableBoxesNotPicked.size()+"");

            }
        });

        boxesAvailableForPickingWs.execute(ref, lote);


    }

    private void addBoxToReadedBoxes(String boxnr) {

        String[] nrBoxes = boxnr.split("\\.");

        for (int i=0; i<=listAvailableBoxes.size()-1; i++)
        {
            String[] boxes = listAvailableBoxes.get(i).getNumcx().split("\\.");
            if(boxes[0].equals(nrBoxes[0]))
            {
                DataModelCxof dataModelCxof = new DataModelCxof();
                dataModelCxof.setNumcx(boxes[0]);
                listPickedBoxes.add(0,dataModelCxof);
                listAvailableBoxes.get(i).setSelected(true);
                return;
            }

        }


        showToast(getContext(),"Leitura de Caixa\nO número de caixa "+boxnr+" não existe nas caixas disponíveis",2.0,20);

    }

    private void removeBoxFromReadedBoxes(int position) {

        String[] positions = listPickedBoxes.get(position).getNumcx().split("\\.");

        for (int i=0;i<=listAvailableBoxes.size()-1;i++)
        {
            String[] boxesAvailable = listAvailableBoxes.get(i).getNumcx().split("\\.");
            if(boxesAvailable[0].equals(positions[0]))
            {
                listPickedBoxes.remove(position);
                listAvailableBoxes.get(i).setSelected(false);

                return;
            }
        }

    }


}






















