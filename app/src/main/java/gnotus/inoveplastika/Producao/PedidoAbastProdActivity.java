package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.DataModels.DataModelST;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditActivity;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;


public class PedidoAbastProdActivity   extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TIPO_ARMAZEM_61 = "Montagem (61)";
    private static final String TIPO_ARMAZEM_62 = "Montagem (62)";

    private static final String LEITURA_OPERADOR_INSERIR = "INSERIR";
    private static final String LEITURA_OPERADOR_ALTERAR = "ALTERAR";

    private static final String WSRESPONSEVAZIO = "\"[]\"";

    private ProgressDialog progDailog ;
    private Toolbar toolbar;
    private Activity thisActivity = PedidoAbastProdActivity.this;

    private TextView textViewOperador,textViewOf,textViewLocalizacao,textViewTitOperador,textViewTitOf,textViewTitLocalizacao;
    private TextView textViewInfo1a,textViewInfo1b,textViewInfo2a,textViewInfo2b;

    private EditText editText_leitura,editTextInput,inputPedeQtt;

    private ListView listViewPedidosAbast;
    private Button button_registar;

    private ConstraintLayout constraintLayoutDados;

    CharSequence[] opcoesTipoPedido = {TIPO_ARMAZEM_61,TIPO_ARMAZEM_62};

    AlertDialog alertDialogOpcoes;
    private String ref_lida = "",oldEditTextInput = "", operadorLido;

    private Integer armazem_destino = 0;
    Boolean mCalledOtherActivity = true;

    private ArrayList<DataModelBi> listPedidosAbast = new ArrayList<>();
    private AAdapterPedidosAbastProd adapterPedidosAbastProd;

    private ArrayList<DataModelST> arrayST = new ArrayList<>();

    private Bundle bundle;

    private AlertDialog alertDialogLerOperador;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLeituraBarCodeListener;
    private EditTextBarCodeReader ediTextLeituraBarCodeReader;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bi_userloc);


        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pedido Abast. Montagem");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        toolbar.setOnClickListener(this);

        //initNavigationDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewInfo1a = (TextView) findViewById(R.id.textView_Info1a);
        textViewInfo1b = (TextView) findViewById(R.id.textView_Info1b);
        textViewInfo2a = (TextView) findViewById(R.id.textView_Info2a);
        textViewInfo2b = (TextView) findViewById(R.id.textView_Info2b);

        textViewInfo1a.setText("");
        textViewInfo2a.setText("");
        textViewInfo1b.setText("");
        textViewInfo2b.setText("");


        textViewTitOperador = (TextView) findViewById(R.id.textView_carga_titulo_operador);
        textViewOperador = (TextView) findViewById(R.id.textView_carga_operador);
        textViewTitOperador.setVisibility(View.GONE);
        textViewOperador.setVisibility(View.GONE);

        textViewTitOf =(TextView) findViewById(R.id.textView_of_titulo);
        textViewOf =(TextView) findViewById(R.id.textView_of);
        textViewTitOf.setVisibility(View.GONE);
        textViewOf.setVisibility(View.GONE);

        textViewTitLocalizacao = (TextView) findViewById(R.id.textView_carga_titulo_localizacao);
        textViewLocalizacao = (TextView) findViewById(R.id.textView_carga_localizacao);

        editText_leitura = (EditText) findViewById(R.id.editText_leitura);
        editText_leitura.setOnClickListener(this);
        editText_leitura.setHint("Ler código artigo");

        defineEditTextReadBarCodeListener();
        ediTextLeituraBarCodeReader = new EditTextBarCodeReader(editText_leitura,thisActivity);
        ediTextLeituraBarCodeReader.setOnGetScannedTextListener(editTextLeituraBarCodeListener);

        listViewPedidosAbast = (ListView) findViewById(R.id.listView_linhasbi);

        button_registar = (Button) findViewById(R.id.button_biuserloc_terminar);
        button_registar.setText("Registar pedido");

        constraintLayoutDados = (ConstraintLayout) findViewById(R.id.constraintLayout_localizacao);
        constraintLayoutDados.setVisibility(View.VISIBLE);

        listViewPedidosAbast.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {


                bundle = new Bundle();

                String mBistamp = adapterPedidosAbastProd.getItem(position).getBistamp();

                // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
                bundle.putBoolean("editing_listview", true);
                bundle.putString("trf_mode", "");
                bundle.putString("bistamp_carga", mBistamp);
                bundle.putString("ref", adapterPedidosAbastProd.getItem(position).getRef());
                bundle.putString("lote", "");
                bundle.putString("design", arrayST.get(0).getDesign());

                Double qttPendente = adapterPedidosAbastProd.getItem(position).getQtt()-adapterPedidosAbastProd.getItem(position).getQtt2();

                bundle.putDouble("stock", qttPendente);
                bundle.putDouble("stockAlt", UserFunc.arredonda(qttPendente/arrayST.get(0).getFconversao(),4));


                bundle.putDouble("qtt",adapterPedidosAbastProd.getItem(position).getQtt());
                bundle.putDouble("qtt2",adapterPedidosAbastProd.getItem(position).getQtt2());


                // ao chamar a atividade de editar quantidade no inventário por of só queremos usar a unidade principal

                // bundle.putDouble("fconversao",arrayST.get(0).getFconversao());
                bundle.putDouble("fconversao",1.0);


                bundle.putDouble("qtd_lida",qttPendente);
                bundle.putDouble("qtdAlt_lida", UserFunc.arredonda(qttPendente/arrayST.get(0).getFconversao(),4) );

                bundle.putString("unidade", arrayST.get(0).getUnidade());
                bundle.putString("unidadeAlt", arrayST.get(0).getUni2());

                bundle.putBoolean("valida_stock", false);
                bundle.putString("atividade", thisActivity.getClass().getSimpleName());
                bundle.putString("armazem_ori",String.valueOf(adapterPedidosAbastProd.getItem(position).getArmazem()));


                lerOperador(LEITURA_OPERADOR_ALTERAR);


                return true;

            }
        });


        button_registar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (textViewInfo1a.getText().toString().isEmpty()) {
                    return;
                }

                lerOperador(LEITURA_OPERADOR_INSERIR);

            }

        });


        editText_leitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText editTextInput;


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
                alertDialog.setTitle("Introduza o artigo");

                editTextInput = new EditText(thisActivity);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                editTextInput.setInputType(InputType.TYPE_CLASS_TEXT);

                editTextInput.setLayoutParams(lp);

                editTextInput.setText("");

                alertDialog.setView(editTextInput);
                //alertDialog.setIcon(R.drawable.key);


                alertDialog.setPositiveButton("Submeter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                EditTextOps.hideKeyboard(thisActivity,editTextInput);
                                dialog.dismiss();
                                ref_lida = editTextInput.getText().toString();
                                lerSt(ref_lida);

                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditTextOps.hideKeyboard(thisActivity,editTextInput);
                                dialog.cancel();
                            }
                        });


                final AlertDialog dialog = alertDialog.create();

                //alertDialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                dialog.show();

            }
        });


                escolheTipoPedido();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCalledOtherActivity)
        {
            obterPedidosAberto();
            mCalledOtherActivity = false;
        }



    }

    private void registaPedidoAbastProd(Double j_qtt) {

        DataModelBi bi = new DataModelBi();

        bi.setReferencia(textViewInfo1a.getText().toString());
        bi.setQtt(j_qtt);
        bi.setArmazem(armazem_destino);

        switch (armazem_destino){
            case (61):
                bi.setNome(TIPO_ARMAZEM_61);
                break;
            case (62):
                bi.setNome(TIPO_ARMAZEM_62);
                break;
        }

        bi.setOperador(Integer.parseInt(operadorLido));


        Gson gson = new Gson();


        JSONObject jsonObjectBi;

        try {

            jsonObjectBi = new JSONObject(gson.toJson(bi));

        }
        catch (JSONException e) {
            Dialogos.dialogoErro("Erro na conversão para Json da classe processarPa",e.getMessage(),6,thisActivity,false);
            e.printStackTrace();
            return;
        }

        System.out.println(jsonObjectBi);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/registaPedidoAbastProd?";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectBi,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progDailog.dismiss();

                        try {

                            if (response.getInt("Codigo") == 0) {
                                obterPedidosAberto();
                                Dialogos.dialogoInfo("Sucesso","Pedido registado!",1.0,thisActivity,false);
                            }
                            else {
                                Dialogos.dialogoInfo("Erro",response.getString("Descricao") ,10.0,thisActivity,false);
                            }

                        }

                        catch (final JSONException e) {
                            Dialogos.dialogoErro("Erro",e.getMessage(),60,thisActivity, false);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage().toString(),10,thisActivity,false);

                progDailog.dismiss();
            }
        });

        progDailog.show();

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);


    }


    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

    }


    private void escolheTipoPedido(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Escolha o tipo de registo");

        builder.setItems(opcoesTipoPedido,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {


                CharSequence text = opcoesTipoPedido[item].toString();


                switch (text.toString()){
                    case (TIPO_ARMAZEM_61):
                        armazem_destino = 61;
                        break;
                    case (TIPO_ARMAZEM_62):
                        armazem_destino = 62;
                        break;
                }

                textViewLocalizacao.setText(text);



                alertDialogOpcoes.dismiss();

            }
        });

        alertDialogOpcoes = builder.create();
        alertDialogOpcoes.setCancelable(false);
        alertDialogOpcoes.show();

    }

    private void obterPedidosAberto() {

        if (!listPedidosAbast.isEmpty()) {
            listPedidosAbast.clear();
            adapterPedidosAbastProd.notifyDataSetChanged();
        }

        if (ref_lida.isEmpty()) return;

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterPedidosAbastProd?").buildUpon()
                .appendQueryParameter("referencia",ref_lida)
                .appendQueryParameter("armazem",armazem_destino.toString())
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();
                        listPedidosAbast = gson.fromJson(response,new TypeToken<ArrayList<DataModelBi>>() {}.getType());

                        adapterPedidosAbastProd = new AAdapterPedidosAbastProd(thisActivity,0,listPedidosAbast);
                        listViewPedidosAbast.setAdapter(adapterPedidosAbastProd);

                        adapterPedidosAbastProd.notifyDataSetChanged();



                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
            }
        });

        // Add the request to the RequestQueue.
        System.out.println(uri.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    private  void lerSt(String referencia) {

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSTStream?").buildUpon()
                .appendQueryParameter("referencia",referencia)
                .appendQueryParameter("filtro","")
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        textViewInfo1a.setText("");
                        textViewInfo2a.setText("");

                        if (!listPedidosAbast.isEmpty()) {
                            listPedidosAbast.clear();
                            adapterPedidosAbastProd.notifyDataSetChanged();
                        }



                        Gson gson = new Gson();
                        arrayST = gson.fromJson(response,new TypeToken<ArrayList<DataModelST>>() {}.getType());


                        if (arrayST.size() == 0) {
                            Dialogos.dialogoErro("Resultado da leitura","O código de artigo "+ref_lida+" não existe ",2,thisActivity,false);
                            return;
                        }


                            textViewInfo1a.setText(arrayST.get(0).getRef());
                            textViewInfo2a.setText(arrayST.get(0).getDesign());

                            obterPedidosAberto();



                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
            }
        });

        // Add the request to the RequestQueue.
        System.out.println(uri.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }

    private void lerOperador(final String tipo) {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
        alertDialog.setTitle("Ler operador");
        alertDialog.setMessage("");


        editTextInput = new EditText(thisActivity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // editTextInput.setInputType(InputType.TYPE_NULL);
        editTextInput.setLayoutParams(lp);

        // editTextInput.setFocusable(true);


        final EditTextBarCodeReader editTextLerOperadorBarcodeReader = new EditTextBarCodeReader(editTextInput,thisActivity);
        editTextLerOperadorBarcodeReader.setOnTouchListenerIsBlocked(true);

        editTextLerOperadorBarcodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                if (scannedText.length() < 4) {
                    editTextLerOperadorBarcodeReader.setText("");
                    Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActivity,false);
                    return;
                }


                if (scannedText.substring(0,4).equals("(OP)")) {


                    operadorLido = UserFunc.processaLeituraOperador(scannedText);

                    if (operadorLido.equals(""))
                    {
                        Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActivity,false);
                        editTextLerOperadorBarcodeReader.setText("");
                        /*oldEditTextInput = "";
                        editTextInput.setText("");*/
                        return;
                    }

                    if (tipo.equals(LEITURA_OPERADOR_INSERIR)) lerQuantidade();

                    if (tipo.equals(LEITURA_OPERADOR_ALTERAR)) {

                        bundle.putString("user",operadorLido);

                        Intent EditIntent = new Intent(thisActivity, EditActivity.class);
                        EditIntent.putExtras(bundle);
                        mCalledOtherActivity = true;
                        startActivity(EditIntent);

                    }

                    alertDialogLerOperador.dismiss();

                }
                else {

                    editTextLerOperadorBarcodeReader.setText("");
                    Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActivity,false);
                }

            }
        });



        /*editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                System.out.println("Charsequence:"+charSequence);
                System.out.println("OldEditText"+oldEditTextInput);

                String newtext;

                if (oldEditTextInput.equals(charSequence)) {
                    return;
                }


                if (oldEditTextInput.length() < charSequence.length()) {

                    if (charSequence.toString().length() < 4) {
                        Dialogos.dialogoErro("Leitura de operador inválida",charSequence.toString(),4,thisActivity,false);
                        oldEditTextInput = "";
                        editTextInput.setText("");
                        return;
                    }

                    if (charSequence.toString().substring(0,4).equals("(OP)")) {

                        newtext = charSequence.toString().substring(0, charSequence.length()-oldEditTextInput.length());
                        System.out.println(newtext);
                        operadorLido = UserFunc.processaLeituraOperador(newtext);

                        if (operadorLido.equals(""))
                        {
                            Dialogos.dialogoErro("Leitura de operador inválida",charSequence.toString(),4,thisActivity,false);
                            oldEditTextInput = "";
                            editTextInput.setText("");
                            return;
                        }

                        if (tipo.equals(LEITURA_OPERADOR_INSERIR)) lerQuantidade();

                        if (tipo.equals(LEITURA_OPERADOR_ALTERAR)) {

                            bundle.putString("user",operadorLido);

                            Intent EditIntent = new Intent(thisActivity, EditActivity.class);
                            EditIntent.putExtras(bundle);
                            mCalledOtherActivity = true;
                            startActivity(EditIntent);

                        }

                        alertDialogLerOperador.dismiss();

                    }
                    else {

                        oldEditTextInput = "";
                        editTextInput.setText("");
                        Dialogos.dialogoErro("Leitura de operador inválida",charSequence.toString(),4,thisActivity,false);
                    }


                }
                else {
                    oldEditTextInput = charSequence.toString();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        //inputPedeLocConsumo.setText("");
        // editTextInput.setSelectAllOnFocus(true);

        alertDialog.setView(editTextInput);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // vai ter override depois do show dialog
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        alertDialogLerOperador = alertDialog.create();


        //alertDialog.show();
        // alertDialogLerOperador.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialogLerOperador.setCancelable(false);

        alertDialogLerOperador.show();

        // impedir que o dialogo feche quando a leitura - Override ao click do próprio botão
        alertDialogLerOperador.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (editTextInput.getText().toString().isEmpty()) {
                    Dialogos.dialogoErro("Operador em falta","",3,thisActivity,false);
                }
                else  {
                    EditTextOps.hideKeyboard(thisActivity,editTextInput);
                    alertDialogLerOperador.dismiss();

                }

            }
        });

    }

    private void lerQuantidade() {

        if (textViewInfo1a.getText().toString().isEmpty()) {
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
        alertDialog.setTitle("Introduza a quantidade a pedir ");

        inputPedeQtt = new EditText(thisActivity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputPedeQtt.setInputType(InputType.TYPE_CLASS_NUMBER);

        inputPedeQtt.setLayoutParams(lp);

        inputPedeQtt.setText("");

        alertDialog.setView(inputPedeQtt);
        //alertDialog.setIcon(R.drawable.key);


        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        EditTextOps.hideKeyboard(thisActivity,inputPedeQtt);

                        Double j_qtt = 0.0;

                        if (! inputPedeQtt.getText().toString().isEmpty()) {
                            j_qtt =  Double.parseDouble(inputPedeQtt.getText().toString()) ;
                        }
                        // só regista produção se a quantidade for diferente de zero
                        if (! (j_qtt == 0.0) ) {
                            registaPedidoAbastProd(j_qtt);
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextOps.hideKeyboard(thisActivity,inputPedeQtt);
                        dialog.cancel();
                    }
                });


        final AlertDialog dialog = alertDialog.create();

        //alertDialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();

    }

    private void defineEditTextReadBarCodeListener() {


        editTextLeituraBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                editText_leitura.setText("");

                if (scannedText.equals("")) return;

                DataModelLeituraCB leituraCB = new DataModelLeituraCB(scannedText.toString());

                if (leituraCB.getTipocb().equals("R"))
                    ref_lida = leituraCB.getReferencia();
                else
                    ref_lida = scannedText.toString();

                lerSt(ref_lida);

            }
        };

    }
}
