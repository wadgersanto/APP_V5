package gnotus.inoveplastika.Suporte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;

import gnotus.inoveplastika.API.Producao.LerInfoOf2Ws;
import gnotus.inoveplastika.Producao.InfoOfDataModel;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

public class InserirPatActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private static final String SPINNER_EQUIP_VAZIO = "(Escolha tipo equip.)";
    private static final String SPINNER_NRSERIE_VAZIO = "(Escolha equipamento)";
    private static final String SPINNER_TIPOPAT_VAZIO = "(Escolha tipo de manutenção)";

    private static  final Integer TIPO_CONTADOR_INJ = 1;
    private static  final Integer TIPO_CONTADOR_HORAS = 2;

    private EditText editText_lercodigo,editTextInput;
    private Button button_submeter;


    private TextView textViewContador, textViewLegContador, textViewProblema,textViewOf;

    private ArrayList<DataModelNrSerie> listaNrSerie = new ArrayList<>();
    private ArrayList<DataModelTipoEquip> listaTipoEquip = new ArrayList<>();
    private ArrayList<InfoOfDataModel> listaInfoOf = new ArrayList<>();

    private ArrayList<String> aTipoEquip =new ArrayList<>();
    private ArrayList<String> aNSeries =new ArrayList<>();
    private ArrayList<String> aTipoPedido =new ArrayList<>();

    private ArrayList<DataModelCm4> listaTecnicos = new ArrayList<>();

    private String operadorLido,oldEditTextInput;
    private Activity thisActitity = InserirPatActivity.this;

    private ConstraintLayout cLayoutContador;

    // 1 = Lê Injecoes, 2 - Lê Nr Horas
    private Integer tipoContador = 0;

    private Integer nrInjecoesEquip= 0;
    private Integer nrHorasEquip = 0;
    private Integer nrInjecoesLidas = 0; // nº de injecoes de lidas pelo operador ?? será que é preciso?
    private Integer nrHorasLidas = 0; // nº de injecoes de lidas pelo operador ?? será que é preciso?

    Spinner spinnerTipoEq;
    Spinner spinnerTipoPedido;
    SearchableSpinner spinnerSerie;


    private ProgressDialog progDailog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduzir_pat);


        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registar Pedido");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        //initNavigationDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        button_submeter = (Button) findViewById(R.id.button_intro_pat);
        button_submeter.setText("Registar pedido");
        button_submeter.setOnClickListener(this);
        button_submeter.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        button_submeter.setTextColor(Color.WHITE);


        editText_lercodigo = (EditText) findViewById(R.id.editText_iniof_lercodigo);
        editText_lercodigo.setInputType(InputType.TYPE_NULL);
        editText_lercodigo.setOnClickListener(this);
        editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));

        textViewContador = (TextView) findViewById(R.id.textViewNrInjecoes);
        textViewContador.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        textViewContador.setOnClickListener(this);

        textViewLegContador = (TextView) findViewById(R.id.textViewLegInj);

        textViewProblema = (TextView) findViewById(R.id.textViewProblema);
        textViewProblema.setOnClickListener(this);

        textViewOf = (TextView) findViewById(R.id.textViewOf);
        textViewOf.setVisibility(View.GONE);
        textViewOf.setOnClickListener(this);


        cLayoutContador = (ConstraintLayout) findViewById(R.id.constraintLayoutContadores);
        cLayoutContador.setVisibility(View.GONE);

        aTipoEquip.add(SPINNER_EQUIP_VAZIO);

        aNSeries.add(SPINNER_NRSERIE_VAZIO);

        aTipoPedido.add(SPINNER_TIPOPAT_VAZIO);
        aTipoPedido.add(Globals.MAN_TIPO_PEDIDO_CORRETIVA);
        aTipoPedido.add(Globals.MAN_TIPO_PEDIDO_PREVENTIVA);

        spinnerTipoEq = (Spinner) findViewById(R.id.spinner1_pat);
        spinnerTipoEq.setOnItemSelectedListener(new ItemSelectedListenerTipoEq());

        spinnerSerie = findViewById(R.id.spinner2_pat);
        spinnerSerie.setOnItemSelectedListener(new ItemSelectedListenerEquipamento());

        spinnerSerie.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        spinnerTipoPedido = findViewById(R.id.spinner3);
        spinnerTipoPedido.setOnItemSelectedListener(new ItemSelectedListenerTipoPedido());

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aTipoEquip);
        spinnerTipoEq.setAdapter(adapter);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aNSeries);
        spinnerSerie.setAdapter(adapter);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aTipoPedido);
        spinnerTipoPedido.setAdapter(adapter);


        lerTiposEquipamentos(); // -->  lerNrSerie(); --> volleyObterListaTecnicos();
        // lerNrSerie();
        // volleyObterListaTecnicos();

        button_submeter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (! existemDadosEmFalta()) lerOperador();
            }

        });


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.textViewNrInjecoes:

                getContador();
                break;
            case R.id.textViewOf:
                lerOf();
                break;
            case R.id.textViewProblema:
                getProblema();
                break;


        }

    }


    public void requestFocus(View view) {
    }

    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    private class MyTextWatcher implements TextWatcher {


        private View view;


        private MyTextWatcher(View view) {

            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            switch (view.getId()) {

                case R.id.editText_iniof_lercodigo:

                    String valorlido = editText_lercodigo.getText().toString();

                    if (!valorlido.equals("")) {
                        editText_lercodigo.setText("");
                    }
                    else
                        return;


                    System.out.println("LIDO: " + valorlido);

                    break;
            }

        }

        public void afterTextChanged(Editable editable) {

        }

    }

    private void getContador(){

        // se o tipo de contador for de injeções não vamos pedir os valores porque são carregados automaticamente pelo pela consulta
        if (tipoContador.equals(TIPO_CONTADOR_INJ)) return;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle(textViewLegContador.getText().toString());

        alertDialog.setCancelable(false);

        final EditText editTextResultado = new EditText(thisActitity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (tipoContador == TIPO_CONTADOR_INJ)  {
            editTextResultado.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertDialog.setMessage("Leitura anterior :"+nrInjecoesEquip);
        }

        // if (tipoContador == 2) editTextResultado.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (tipoContador == TIPO_CONTADOR_HORAS) {
            alertDialog.setMessage("Leitura anterior :"+nrHorasEquip);
            editTextResultado.setInputType(InputType.TYPE_CLASS_NUMBER);
        }


        editTextResultado.setText(textViewContador.getText().toString().replaceAll("[^0-9,.]",""));
        editTextResultado.setSelectAllOnFocus(true);


        editTextResultado.setLayoutParams(lp);
        alertDialog.setView(editTextResultado);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (tipoContador == TIPO_CONTADOR_INJ) {

                            if (editTextResultado.getText().length() > 10) {
                                Dialogos.dialogoErro("Valor inválido", "O valor introduzido é demasiado grande", 3, thisActitity, false);
                                nrInjecoesLidas = 0;
                                return;
                            }


                            nrInjecoesLidas = Integer.parseInt(editTextResultado.getText().toString());

                        }

                        if (tipoContador == TIPO_CONTADOR_HORAS) {
                            if (editTextResultado.getText().length() > 6) {
                                Dialogos.dialogoErro("Valor inválido", "O valor introduzido é demasiado grande", 3, thisActitity, false);
                                nrHorasLidas = 0;
                                return;
                            }
                            else nrHorasLidas = Integer.parseInt(editTextResultado.getText().toString());

                        }

                        Integer valorlido = Integer.parseInt(editTextResultado.getText().toString());

                        textViewContador.setText(NumberFormat.getIntegerInstance().format(valorlido));

                        dialog.dismiss();

                    }

                });


        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    private void lerOf(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle("Ler OF");
        alertDialog.setMessage("");
        alertDialog.setCancelable(false);

        editTextInput = new EditText(thisActitity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        editTextInput.setInputType(InputType.TYPE_CLASS_TEXT);

        EditTextBarCodeReader editTextInputOf = new EditTextBarCodeReader(editTextInput,thisActitity);

        editTextInputOf.setReplaceTextOnBarCodeReading(true);

        // editTextInput.setText(textViewOf.getText().toString());
        // editTextInput.setSelectAllOnFocus(true);

        editTextInput.setLayoutParams(lp);
        alertDialog.setView(editTextInput);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        LerInfoOf2Ws lerInfoOf2Ws = new LerInfoOf2Ws(thisActitity);

                        lerInfoOf2Ws.setOnLerInfoOf2Listener(new LerInfoOf2Ws.OnLerInfoOf2Listener() {
                            @Override
                            public void onSuccess(InfoOfDataModel infoOf) {

                                textViewOf.setText("");

                                if (infoOf == null) {

                                    Dialogos.dialogoErro("Erro na leitura da OF","A OF "+editTextInput.getText().toString()+" não existe",5,thisActitity,false);
                                    editTextInput.setText("");
                                    return;

                                }

                                if(! infoOf.getIniciada()) {

                                    Dialogos.dialogoErro("Erro na leitura da OF","A OF "+editTextInput.getText().toString()+" não foi iniciada",5,thisActitity,false);
                                    editTextInput.setText("");
                                    return;

                                }

                                if(infoOf.getFechada()) {

                                    Dialogos.dialogoErro("Erro na leitura da OF","A OF "+editTextInput.getText().toString()+"está fechada",5,thisActitity,false);
                                    editTextInput.setText("");
                                    return;

                                }

                                textViewOf.setText(infoOf.getNumof());

                            }
                        });

                        lerInfoOf2Ws.execute(editTextInput.getText().toString());

                        EditTextOps.hideKeyboard(thisActitity,editTextInput);
                        dialog.dismiss();

                    }

                });


        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextOps.hideKeyboard(thisActitity,editTextInput);
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    private void getResumo(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle("Nº injeções");
        alertDialog.setMessage("Inserir número de injeções");
        alertDialog.setCancelable(false);

        final EditText editTextResultado = new EditText(thisActitity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        editTextResultado.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        editTextResultado.setLayoutParams(lp);
        alertDialog.setView(editTextResultado);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        textViewProblema.setText(editTextResultado.getText().toString());
                        dialog.dismiss();
                    }

                });


        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }


    public class ItemSelectedListenerTipoEq implements AdapterView.OnItemSelectedListener {

        //get strings of first item

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            System.out.println(spinnerTipoEq.getSelectedItem());


            for (DataModelTipoEquip temp : listaTipoEquip) {

                if (temp.getTipoequip().equals(spinnerTipoEq.getSelectedItem())) {

                    tipoContador = temp.getTipoContador();

                    if (tipoContador == 0) cLayoutContador.setVisibility(View.GONE);

                    if (tipoContador == TIPO_CONTADOR_INJ) {
                        cLayoutContador.setVisibility(View.VISIBLE);
                        textViewLegContador.setText("Nº Injeções");
                        textViewContador.setText("");
                    }

                    if (tipoContador == TIPO_CONTADOR_HORAS) {
                        cLayoutContador.setVisibility(View.VISIBLE);
                        textViewLegContador.setText("Nº horas");
                        textViewContador.setText("");
                    }

                    break;

                }

            }

            loadArrayNrSerie();

            // EditTextOps.hideKeyboard(thisActitity,editTextInput);
            // EditTextOps.hideKeyboard(thisActitity,editText_lercodigo);


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {


        }

    }

    public class ItemSelectedListenerTipoPedido implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            System.out.println(spinnerTipoPedido.getSelectedItem());

            if (spinnerTipoPedido.getSelectedItem().equals(Globals.MAN_TIPO_PEDIDO_CORRETIVA))
                textViewOf.setVisibility(View.VISIBLE);
            else {
                textViewOf.setText("");
                textViewOf.setVisibility(View.GONE);
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }

    public class ItemSelectedListenerEquipamento implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            EditTextOps.hideKeyboard(thisActitity,view);

            // vamos limpar o contador
            textViewContador.setText("");

            if (spinnerSerie.getSelectedItem().equals(SPINNER_EQUIP_VAZIO)) {
                nrHorasEquip = 0;
                nrInjecoesEquip = 0;
            }
            else
            {
                for (DataModelNrSerie temp : listaNrSerie) {

                    if (temp.getTipoEquip().equals(spinnerTipoEq.getSelectedItem()) && temp.getNrSerie().equals(spinnerSerie.getSelectedItem())) {
                        nrHorasEquip = temp.getCtHoras();
                        nrInjecoesEquip = temp.getCtInjecoes();

                        if (temp.getTipoEquip().equals("MOLDE") ) {
                            textViewContador.setText(temp.getInjActuais().toString());
                        }

                        break;

                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

            EditTextOps.hideKeyboard(thisActitity,spinnerTipoEq);

        }


    }

    private void lerTiposEquipamentos() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerTiposEquipManutencao?";

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();
                        listaTipoEquip = gson.fromJson(response,new TypeToken<ArrayList<DataModelTipoEquip>>() {}.getType());
                        loadArrayTiposEquip();

                        lerNrSerie();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActitity,false);
            }
        });

        // Add the request to the RequestQueue.
        System.out.println(url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);


    }

    private void lerNrSerie() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerNrSerie?";

        progDailog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Gson gson = new Gson();
                        listaNrSerie = gson.fromJson(response,new TypeToken<ArrayList<DataModelNrSerie>>() {}.getType());

                        progDailog.dismiss();

                        loadArrayNrSerie();

                        volleyObterListaTecnicos();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActitity,false);
            }
        });

        // Add the request to the RequestQueue.
        System.out.println(url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);

    }

    private void loadArrayNrSerie () {
        // carrega os numeros de série em função do tipo de equipamento seleccionado

        aNSeries.clear();
        aNSeries.add(SPINNER_NRSERIE_VAZIO);

        for (DataModelNrSerie temp : listaNrSerie) {
            if (temp.getTipoEquip().equals(spinnerTipoEq.getSelectedItem()))
            {
                aNSeries.add(temp.getNrSerie());
            }
        }

        spinnerSerie.setSelection(0);
    }

    private void loadArrayTiposEquip () {
        // carrega os numeros de série em função do tipo de equipamento seleccionado

        aTipoEquip.clear();
        aTipoEquip.add(SPINNER_EQUIP_VAZIO);

        for (DataModelTipoEquip temp : listaTipoEquip) {
            aTipoEquip.add(temp.getTipoequip());
        }

        spinnerTipoEq.setSelection(0);

    }


    private void registarPat() {

        progDailog.show();

        if (tipoContador == TIPO_CONTADOR_INJ) {
            nrInjecoesLidas = Integer.parseInt(textViewContador.getText().toString());
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/registarPat?";

        JSONObject json = new JSONObject();


        try {
                json.put("Tipo", spinnerTipoEq.getSelectedItem().toString());
                json.put("Serie", spinnerSerie.getSelectedItem().toString());
                json.put("Ctinj",nrInjecoesLidas.toString());
                json.put("Cthoras", nrHorasLidas.toString());
                json.put("Ptipo",spinnerTipoPedido.getSelectedItem().toString());
                json.put("Resumo", textViewProblema.getText().toString());
                json.put("Problema", textViewProblema.getText().toString());
                json.put("Pquem",operadorLido);
                json.put("Numof",textViewOf.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(json.toString());

        JSONArray jsonArray = new JSONArray();

        jsonArray.put(json);
        jsonArray.put(json);

        System.out.println(jsonArray.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progDailog.dismiss();

                        try {

                            if (response.getInt("Codigo") == 0) {

                                Dialogos.dialogoInfo("Sucesso",response.getString("Descricao") ,3.0,thisActitity,false);

                                spinnerTipoPedido.setSelection(0);
                                spinnerSerie.setSelection(0);
                                spinnerTipoEq.setSelection(0);

                                textViewContador.setText("");
                                textViewProblema.setText("");

                                // voltar a carregar os numeros de série para ter atualização dos contadores
                                lerNrSerie();
                            }
                            else {

                                Dialogos.dialogoErro("Erro no processamento do pedido",
                                        response.getString("Descricao"),6,thisActitity,false);
                            }


                        }

                        catch (final JSONException e) {

                        Log.e("", "Erro Conversão: " + e.getMessage());
                        Dialogos.dialogoErro("Erro",e.getMessage(),60,thisActitity, false);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage().toString(),10,thisActitity,false);

                progDailog.dismiss();
            }
        });


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);


    }


    private void lerOperador() {

        if (1 == 0) {
            operadorLido = "100";
            registarPat();
        return;
        }

        volleyObterListaTecnicos();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle("Ler operador");
        alertDialog.setMessage("");
        alertDialog.setCancelable(false);

        oldEditTextInput = "";

        editTextInput = new EditText(thisActitity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // editTextInput.setInputType(InputType.TYPE_NULL);

        final EditTextBarCodeReader editTextInputBarCodeReader = new EditTextBarCodeReader(editTextInput,thisActitity);
        editTextInputBarCodeReader.setOnTouchListenerIsBlocked(true);
        editTextInputBarCodeReader.setReplaceTextOnBarCodeReading(true);
        editTextInputBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                if (scannedText.length() < 4 || scannedText.isEmpty()) {
                    editTextInputBarCodeReader.setText("");
                    Dialogos.dialogoErro("Leitura de operador inválida","",4,thisActitity,false);
                    return;
                }

                if (scannedText.substring(0,4).equals("(OP)")) {

                    operadorLido = UserFunc.processaLeituraOperador(editTextInput.getText().toString());
                    editTextInputBarCodeReader.setText(operadorLido);

                    DataModelCm4 tecnico = new DataModelCm4();

                    for (DataModelCm4 dmCm4 : listaTecnicos ) {
                        if (UserFunc.isInteger(operadorLido)) {
                            if (dmCm4.getCm() == Integer.valueOf(operadorLido)) {
                                tecnico = dmCm4;
                                break;
                            }
                        }
                    }

                    System.out.println("tecnico: "+ tecnico.getCm());
                    if (tecnico.getCm() == 0) {
                        Dialogos.dialogoErro("O operador ["+operadorLido+"] não está registado como técnico","",4,thisActitity,false);
                        editTextInputBarCodeReader.setText("");
                        return;
                    }

                    // if OS is Corrective and the operator has no permissions to open Corrective OS
                    Boolean isCorrectiveOs= false;
                    if (spinnerTipoPedido.getSelectedItem().equals(Globals.MAN_TIPO_PEDIDO_CORRETIVA)) isCorrectiveOs = true;


                    if (isCorrectiveOs && spinnerTipoEq.getSelectedItem().equals("MOLDE") && ! tecnico.isU_openosc()) {
                        Dialogos.dialogoErro("Permissões inválidas","O operador ["+operadorLido+"] não tem permissões " +
                                "para abrir ordens serviço corretivas em moldes",4,thisActitity,false);
                        editTextInputBarCodeReader.setText("");
                        return;
                    }
                    else {
                        editTextInputBarCodeReader.setText(operadorLido);
                    }

                }
                else {
                    editTextInputBarCodeReader.setText("");
                    Dialogos.dialogoErro("Leitura de operador inválida","",4,thisActitity,false);
                }


            }
        });


        editTextInput.setLayoutParams(lp);
        alertDialog.setView(editTextInput);


        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog dialog = alertDialog.create();

        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();


        // impedir que o dialogo feche quando a leitura - Override ao click do próprio botão
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (editTextInput.getText().toString().isEmpty()) {
                    Dialogos.dialogoErro("Operador em falta","",3,thisActitity,false);
                }
                else  {
                    registarPat();
                    dialog.dismiss();
                }

            }
        });


    }

    private boolean existemDadosEmFalta()
    {

        if ( !(tipoContador == 0) && textViewContador.getText().toString().isEmpty()  ) {

            Dialogos.dialogoInfo("Validação de dados","Tem de preencher dados por preencher (contador)",3.0,thisActitity,false);
            return true;
        }


        if ( spinnerSerie.getSelectedItem().equals(SPINNER_EQUIP_VAZIO)
                || spinnerSerie.getSelectedItem().equals(SPINNER_NRSERIE_VAZIO)
                || spinnerTipoPedido.getSelectedItem().equals(SPINNER_TIPOPAT_VAZIO)
                || textViewProblema.getText().toString().isEmpty()
                )
        {
            Dialogos.dialogoInfo("Validação de dados","Existem dados por preencher",3.0,thisActitity,false);
            return true;
        }

        // valida se o contador acumulado de injecoes e verificar se o valor lido é inferior a esse valor
        // desativada em 2019-09-18 quando passou a ser registado o numero de injeções automaticamente

      /*  if (tipoContador == TIPO_CONTADOR_INJ) {

            for (DataModelNrSerie temp : listaNrSerie) {
                if (temp.getTipoEquip().equals(spinnerTipoEq.getSelectedItem()) && temp.getNrSerie().equals(spinnerSerie.getSelectedItem())) {

                    if ( nrInjecoesEquip > nrInjecoesLidas ) {
                        Dialogos.dialogoErro("Dados inválidos","Nr de injeções lida "+nrInjecoesLidas+" inferior à ultima contagem "+nrInjecoesEquip,4,thisActitity, false);
                        return true;
                    }
                    break;
                }
            }
        }*/

        // valida se o contador acumulado de injecoes e verificar se o valor lido é inferior a esse valor
        if (tipoContador == TIPO_CONTADOR_HORAS) {

            for (DataModelNrSerie temp : listaNrSerie) {
                if (temp.getTipoEquip().equals(spinnerTipoEq.getSelectedItem()) && temp.getNrSerie().equals(spinnerSerie.getSelectedItem())) {

                    if ( nrHorasEquip > nrHorasLidas ) {
                        Dialogos.dialogoErro("Dados inválidos","Nr de horas lida "+nrHorasLidas+" inferior à ultima contagem "+temp.getCtHoras(),4,thisActitity, false);
                        return true;
                    }
                    break;
                }
            }
        }

        if (textViewOf.getVisibility() == (View.VISIBLE) && textViewOf.getText().toString().isEmpty()) {
            Dialogos.dialogoErro("Dados em falta","Falta ler a OF",4,thisActitity, false);
            return true;
        }
        return false;

    }

    private void getProblema() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Problema/descrição intervenção");

        final EditText input = new EditText(this);
        input.setText(textViewProblema.getText());
        input.setLines(2);
        input.setMaxLines(10);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        builder.setView(input);
        builder.setCancelable(false);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                textViewProblema.setText(input.getText().toString());
                EditTextOps.hideKeyboard(thisActitity,input);
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditTextOps.hideKeyboard(thisActitity,input);
                dialog.cancel();
            }
        });

        AlertDialog dialog;

        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        input.setSelection(input.getText().length());
        input.requestFocus();



    }

    private  void volleyObterListaTecnicos() {

        RequestQueue queue = Volley.newRequestQueue(this);

        Uri builtURI_asyncRequest = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterListaTecnicos?").buildUpon()
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, builtURI_asyncRequest.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();
                        Gson gson = new Gson();
                        listaTecnicos = gson.fromJson(response,new TypeToken<ArrayList<DataModelCm4>>() {}.getType());


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActitity,false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        System.out.println(builtURI_asyncRequest);

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
