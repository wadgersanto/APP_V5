package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


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
import java.util.List;

import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

public class IniciarTurnoActivity  extends AppCompatActivity
        implements View.OnClickListener {

    private android.support.v7.widget.Toolbar toolbar;
    private ProgressDialog progDailog;
    private Button button_submeter;

    private Activity thisActivity = IniciarTurnoActivity.this;

    private AlertDialog alertDialogLerOperador,alertDialogTurnos;
    private EditText editText_leitura,editTextInput;

    private ArrayList<DataModelTurno> turnosArrayList = new ArrayList<>();
    private ArrayList<DataModelTurno> listaTurnos = new ArrayList<>();
    private ListView listViewTurnos;
    private TurnoAdapter adapterTurnos;
    private ConstraintLayout constraintLayoutDados;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLeituraBarCodeListener;
    private EditTextBarCodeReader ediTextLeituraBarCodeReader;

    private String oldEditTextInput="",operadorLido;


    @Override
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

        getSupportActionBar().setTitle("Registo de turnos");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        toolbar.setOnClickListener(this);

        //initNavigationDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        editText_leitura = (EditText) findViewById(R.id.editText_leitura);
        editText_leitura.setOnClickListener(this);
        editText_leitura.setHint("Ler operador");

        ediTextLeituraBarCodeReader = new EditTextBarCodeReader(editText_leitura,thisActivity);
        ediTextLeituraBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                editText_leitura.setText("");

                if (scannedText.equals("")) return;

                operadorLido = UserFunc.processaLeituraOperador(scannedText);

                if (! operadorLido.equals(""))
                {
                    button_submeter.setVisibility(View.VISIBLE);
                    button_submeter.setText("OPERADOR "+UserFunc.retiraZerosDireita(Double.parseDouble(operadorLido))+ " - INICIAR TURNO");
                    lerTurnoOperador(operadorLido);
                }
                else {
                    Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActivity,false);
                }

            }
        });




        /*editText_leitura.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) return;

                operadorLido = UserFunc.processaLeituraOperador(s.toString());

                if (! operadorLido.equals(""))
                {
                    button_submeter.setVisibility(View.VISIBLE);
                    button_submeter.setText("OPERADOR "+UserFunc.retiraZerosDireita(Double.parseDouble(operadorLido))+ " - INICIAR TURNO");
                    lerTurnoOperador(operadorLido);
                }
                else {
                    Dialogos.dialogoErro("Leitura de operador inválida",s.toString(),4,thisActivity,false);
                }

                editText_leitura.setText("");

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {


            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });*/


        listViewTurnos = findViewById(R.id.listView_linhasbi);


        button_submeter = findViewById(R.id.button_biuserloc_terminar);
        button_submeter.setText("Iniciar turno");

        button_submeter.setVisibility(View.INVISIBLE);

        button_submeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (! turnosArrayList.isEmpty()) {


                    if (turnosArrayList.get(0).getMinleftfimturno() > 0 ) {
                        Dialogos.dialogoErro("Operação inválida","A hora de fim de turno ainda não foi atingida",3,thisActivity,false);
                        return;
                    }

                 }

                escolherTurno();

            }
        });

        constraintLayoutDados = findViewById(R.id.constraintLayout_localizacao);
        constraintLayoutDados.setVisibility(View.GONE);


        editText_leitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        listViewTurnos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                AlertDialog alertDialogOpAltTurno;

                CharSequence[] opcoesAlteracaoTurno = {"Adicionar Horas Extra","Tempo adicional","Alterar turno"};

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

                builder.setTitle("Escolha a ação");

                builder.setItems(opcoesAlteracaoTurno,  new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        switch(item)
                        {


                            case 0:

                                // horas extra

                                if (turnosArrayList.get(0).getTempoadicional() > 0) {
                                    Dialogos.dialogoErro("Opção não permitida","O turno já tem tempo adicional.",4,thisActivity,false);
                                    return;
                                }

                                if (turnosArrayList.get(0).getHorasextra() > 6) {
                                    Dialogos.dialogoErro("Opção não permitida","Já foi ultrapassado o màximo de horas extra " +
                                            "possíveis de adicionar ao turno",4,thisActivity,false);
                                }
                                else {
                                    adicionarHorasExtra();
                                }

                                break;
                            case 1:

                                // tempo adicional

                                if (turnosArrayList.get(0).getHorasextra() > 0) {
                                    Dialogos.dialogoErro("Opção não permitida","O turno já tem horas extra.",4,thisActivity,false);
                                    return;
                                }

                                if (turnosArrayList.get(0).getTempoadicional() > 0) {
                                    Dialogos.dialogoErro("Opção não permitida","Já foi adicionado tempo adicional ao turno",4,thisActivity,false);
                                    return;
                                }

                                adiconarTempo();

                                break;


                            case 2:

                                if (turnosArrayList.get(0).getHorasextra() > 0 || turnosArrayList.get(0).getTempoadicional() > 0 ) {
                                    Dialogos.dialogoErro("Opção não permitida","O turno já tem horas extra ou tempo adicional",4,thisActivity,false);
                                    return;
                                }

                                escolherTurnoAlterar(operadorLido);
                                break;

                        }


                    }
                });

                alertDialogOpAltTurno = builder.create();
                alertDialogOpAltTurno.show();

                return false;


            }


        });
        // lerOperador();

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

    private void lerOperador(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
        alertDialog.setTitle("Ler operador");
        alertDialog.setMessage("");

        editTextInput = new EditText(thisActivity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editTextInput.setInputType(InputType.TYPE_NULL);
        editTextInput.setLayoutParams(lp);
        editTextInput.setFocusable(true);


        editTextInput.addTextChangedListener(new TextWatcher() {
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

                    newtext = charSequence.toString().substring(0, charSequence.length()-oldEditTextInput.length());
                    operadorLido = UserFunc.processaLeituraOperador(newtext);

                    if (! operadorLido.equals(""))
                    {

                        lerTurnoOperador(operadorLido);
                    }
                    else {

                        Dialogos.dialogoErro("Leitura de operador inválida",charSequence.toString(),4,thisActivity,false);
                        oldEditTextInput = "";
                        editTextInput.setText("");
                        return;

                    }

                    alertDialogLerOperador.dismiss();


                }
                else {
                    oldEditTextInput = charSequence.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
        alertDialogLerOperador.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                    alertDialogLerOperador.dismiss();
                }

            }
        });
    }

    private void lerTurnoOperador(String operador) {

        if (!turnosArrayList.isEmpty()) {
            turnosArrayList.clear();
            adapterTurnos.notifyDataSetChanged();
        }

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerTurnoOperador?").buildUpon()
                .appendQueryParameter("operador",operadorLido)
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();
                        turnosArrayList = gson.fromJson(response,new TypeToken<ArrayList<DataModelTurno>>() {}.getType());

                        adapterTurnos = new TurnoAdapter(thisActivity,0, turnosArrayList);
                        listViewTurnos.setAdapter(adapterTurnos);

                        adapterTurnos.notifyDataSetChanged();

                        if (turnosArrayList.size() == 0) {
                            Dialogos.dialogoInfo("Operador "+operadorLido,"Não encontrei turnos ativos",1.0,thisActivity,false);
                        }


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

    private void escolherTurno() {

        // primeiro vanmos obter o array dos turnos disponiveis para seleccionar


        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterListaTurnos?").buildUpon()
                .appendQueryParameter("operador",operadorLido)
                .appendQueryParameter("tipo","0")
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();

                        listaTurnos = gson.fromJson(response,new TypeToken<ArrayList<DataModelTurno>>() {}.getType());


                        if (listaTurnos.size() == 0) {
                            Dialogos.dialogoInfo("Iniciar turno","Não existem turnos que possam ser iniciados neste momento para o operador "+operadorLido,4.0,thisActivity,false);
                            return;
                        }


                        System.out.println(listaTurnos.get(0).getDatahoraini());


                        // final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(thisActivity, android.R.layout.select_dialog_item);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(thisActivity, android.R.layout.select_dialog_singlechoice);


                        for (int i = 0; i < listaTurnos.size(); i++) {
                            arrayAdapter.add(listaTurnos.get(i).getDescricao());
                        }


                        // CharSequence[] opcoesTipoPedido = listaTurnos.toArray(new CharSequence[listaTurnos.size()]);


                        // vamos agora apresentar ao utilizador uma escolha do turno

                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setTitle("Escolha o turno ");

                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(thisActivity);
                                builderInner.setTitle("Quer inicia o turno "+strName +" ?");

                                final DataModelTurno mTurno = listaTurnos.get(which);

                                builderInner.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {

                                        mTurno.setOperador(operadorLido);
                                        iniciarTurno(mTurno);
                                        dialog.dismiss();
                                    }
                                });

                                builderInner.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                    }
                                });


                                builderInner.show();

                                // alertDialogTurnos.dismiss();

                            }
                        });

                        alertDialogTurnos = builder.create();
                        alertDialogTurnos.setCancelable(true);
                        alertDialogTurnos.show();


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

    private void iniciarTurno(DataModelTurno turno) {

        Gson gson = new Gson();

        JSONObject jsonObjectTurno;

        try {

            jsonObjectTurno = new JSONObject(gson.toJson(turno));

        }
        catch (JSONException e) {
            Dialogos.dialogoErro("Erro na conversão para Json da classe processarPa",e.getMessage(),6,thisActivity,false);
            e.printStackTrace();
            return;
        }

        System.out.println(jsonObjectTurno);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/IniciarTurno?";

        System.out.println(url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectTurno,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progDailog.dismiss();

                        try {

                            if (response.getInt("Codigo") == 0) {
                                lerTurnoOperador(operadorLido);
                                Dialogos.dialogoInfo("Sucesso","Turno iniciado!",1.0,thisActivity,false);
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

    private void escolherTurnoAlterar(String operador) {

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterListaTurnos?").buildUpon()
                .appendQueryParameter("operador",operador)
                .appendQueryParameter("tipo","1")
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();

                        listaTurnos = gson.fromJson(response,new TypeToken<ArrayList<DataModelTurno>>() {}.getType());


                        if (listaTurnos.size() == 0) {
                            Dialogos.dialogoInfo("Alterar turno","Não é possivel alterar o turno atual",4.0,thisActivity,false);
                            return;
                        }

                        System.out.println(listaTurnos.get(0).getDatahoraini());


                        // final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(thisActivity, android.R.layout.select_dialog_item);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(thisActivity, android.R.layout.select_dialog_singlechoice);


                        for (int i = 0; i < listaTurnos.size(); i++) {
                            arrayAdapter.add(listaTurnos.get(i).getDescricao());
                        }


                        // CharSequence[] opcoesTipoPedido = listaTurnos.toArray(new CharSequence[listaTurnos.size()]);

                        // vamos agora apresentar ao utilizador uma escolha do turno

                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        builder.setTitle("Escolha o turno ");

                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(thisActivity);
                                builderInner.setTitle("Quer alterar o turno atual para o turno "+strName +" ?");

                                final DataModelTurno mTurno = listaTurnos.get(which);

                                builderInner.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {

                                        mTurno.setBostamp(turnosArrayList.get(0).getBostamp());
                                        alterarTurno(mTurno);
                                        dialog.dismiss();

                                    }
                                });

                                builderInner.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                    }
                                });


                                builderInner.show();

                                // alertDialogTurnos.dismiss();

                            }
                        });


                        alertDialogTurnos = builder.create();
                        alertDialogTurnos.setCancelable(true);
                        alertDialogTurnos.show();


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

    private void alterarTurno(DataModelTurno turno) {

        Gson gson = new Gson();

        JSONObject jsonObjectTurno;

        try {

            jsonObjectTurno = new JSONObject(gson.toJson(turno));

        }
        catch (JSONException e) {
            Dialogos.dialogoErro("Erro na conversão para Json da classe processarPa",e.getMessage(),6,thisActivity,false);
            e.printStackTrace();
            return;
        }

        System.out.println(jsonObjectTurno);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/AtualizarTurno?";

        System.out.println(url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectTurno,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progDailog.dismiss();

                        try {

                            if (response.getInt("Codigo") == 0) {
                                lerTurnoOperador(operadorLido);
                                Dialogos.dialogoInfo("Sucesso","Transação efetuada!",1.0,thisActivity,false);
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

    private void adicionarHorasExtra() {


        final Double hrextra = turnosArrayList.get(0).getHorasextra();

        if (hrextra >= 6) {

            Dialogos.dialogoErro("Opção não permita","O nº de horas extra atingiu o limite neste turno ",4,thisActivity,false);
            return;
        }


        AlertDialog alertDialogSelHorasExtra;

        List<String> listItems = new ArrayList<String>();

        for (int i = 1; i <= 6 - turnosArrayList.get(0).getHorasextra().intValue(); i++) {
            listItems.add(String.valueOf(i));
        }

        final CharSequence[] charSeqHorasExtra = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        builder.setTitle("Escolha o nº de horas extra a adicionar");

        builder.setItems(charSeqHorasExtra,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, final int item) {




                AlertDialog.Builder builderInner = new AlertDialog.Builder(thisActivity);
                builderInner.setTitle("Quer adicionar "+charSeqHorasExtra[item].toString()+"+horas extra ao turno");

                builderInner.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {

                        DataModelTurno mTurno = turnosArrayList.get(0);
                        mTurno.setHorasextra(hrextra+ Integer.valueOf(charSeqHorasExtra[item].toString()));
                        alterarTurno(mTurno);
                        dialog.dismiss();

                    }
                });

                builderInner.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });


                builderInner.show();

            }
        });


        alertDialogSelHorasExtra = builder.create();
        alertDialogSelHorasExtra.show();



    }

    private void adiconarTempo() {


        final Double tempoadicional = turnosArrayList.get(0).getTempoadicional();

        if (tempoadicional >= 1) {

            Dialogos.dialogoErro("Opção não permita","O nº de horas extra atingiu o limite neste turno ",4,thisActivity,false);
            return;
        }

        AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        builder.setTitle("Quer adicionar 1/2 hora à hora limite do turno?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {

                DataModelTurno mTurno = turnosArrayList.get(0);
                mTurno.setTempoadicional(0.5);
                alterarTurno(mTurno);
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();



    }


}
