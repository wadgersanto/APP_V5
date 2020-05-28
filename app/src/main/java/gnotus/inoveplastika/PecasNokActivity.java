package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import gnotus.inoveplastika.API.Producao.GetInfoOfWs;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.Producao.AAdapterPnokDef;
import gnotus.inoveplastika.Producao.DmRPnok;
import gnotus.inoveplastika.Producao.DmPnokDef;
import gnotus.inoveplastika.Producao.InfoOfDataModel;

public class PecasNokActivity
        extends AppCompatActivity
        implements View.OnClickListener, AsyncRequest.OnAsyncRequestComplete {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private EditText editText_lercodigo,inputPedeQttDefeito;
    private TextView textView_referencia, textView_of, textView_operador, textView_titulo, textView_localizacao,
    textView_leg_localizacao;

    private Button button_submeter, button_lerRef, button_lerLote;

    // Vamos usar o modelo de dados DataModelBi para as linhas com os tipos de defeitos e quantidades
    private ArrayList<DmPnokDef> listaDefeitos = new ArrayList<>();

    private AAdapterPnokDef adapterPnokDef;

    private ProgressDialog progDailog ;

    private ListView listViewDefeitos;

    private EditText inputPedeValor;

    private static final String clear = "";

    private String ref = "";
    private String lote = "";
    private String armazem = "0",zona = "",alveolo = "";

    private Integer tiporegisto;

    private String FLAG_TIPO_LEITURA = "REF";

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLerCodigoBarCodeListener;
    private EditTextBarCodeReader ediTextLerCodigoBarCodeReader;

    private int volleyRegistaPnokCount;

    private long mLastSubmittButtonClickTime = 0;

    Bundle bundle = new Bundle();

    Activity thisActivity = PecasNokActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pecas_nok);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registar peças NOK");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        //initNavigationDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        textView_titulo = (TextView) findViewById(R.id.textView_titulo);
        textView_referencia = (TextView) findViewById(R.id.textView_ref);
        textView_of = (TextView) findViewById(R.id.textView_of);
        textView_operador = (TextView) findViewById(R.id.textView_operador);

        textView_localizacao = (TextView) findViewById(R.id.textView_localizacao);

        textView_leg_localizacao = (TextView) findViewById(R.id.textView_localizacao_leg);

        textView_referencia.setOnClickListener(this);
        textView_of.setOnClickListener(this);
        textView_operador.setOnClickListener(this);
        textView_localizacao.setOnClickListener(this);

        listViewDefeitos = (ListView) findViewById(R.id.listViewDefeitos);

        button_submeter = (Button) findViewById(R.id.button_submeter);
        button_submeter.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        button_submeter.setTextColor(Color.WHITE);

        button_lerRef = (Button) findViewById(R.id.button_lerRef);
        button_lerRef.setOnClickListener(this);

        button_lerLote = (Button) findViewById(R.id.button_lerLote);
        button_lerLote.setOnClickListener(this);

        editText_lercodigo = (EditText) findViewById(R.id.editText_ep_lercodigo);

        // editText_lercodigo.setInputType(InputType.TYPE_NULL);
        // editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));
        defineEditTextReadBarCodeListener();
        ediTextLerCodigoBarCodeReader = new EditTextBarCodeReader(editText_lercodigo,thisActivity);
        ediTextLerCodigoBarCodeReader.setOnGetScannedTextListener(editTextLerCodigoBarCodeListener);
        editText_lercodigo.setOnClickListener(this);


        volleyRegistaPnokCount = 0;

        bundle = this.getIntent().getExtras();

        tiporegisto = bundle.getInt("pnok_tiporegisto");

        // Tipo de registo... 0 = Em produção; 1 = Já registadas
        if (tiporegisto == 0){
            textView_titulo.setText("Registo peças NOK - Em produção");
            textView_localizacao.setVisibility(View.GONE);
            textView_leg_localizacao.setVisibility(View.GONE);

        }
        else {
            textView_titulo.setText("Registo peças NOK - Já declaradas");
        }

        addItemsOnSpinner();

        button_submeter.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if (SystemClock.elapsedRealtime() - mLastSubmittButtonClickTime < 1000){
                    return;
                }
                mLastSubmittButtonClickTime = SystemClock.elapsedRealtime();


                ImageView image = new ImageView(getApplication());
                image.setImageResource(R.mipmap.ic_notok);

                String mUser = textView_operador.getText().toString().trim();

                Integer pSomaTotalDefeitos = 0;

                if (!listaDefeitos.isEmpty()) {

                    for (int i = 0; i < listaDefeitos.size(); i++) {
                        pSomaTotalDefeitos = pSomaTotalDefeitos + listaDefeitos.get(i).getQtt().intValue();

                        if (listaDefeitos.get(i).getCDefOutros() &&  ! (listaDefeitos.get(i).getQtt() == 0) && TextUtils.isEmpty(listaDefeitos.get(i).getDetalheOutros())) {
                            Dialogos.dialogoErro("Atenção","Existem defeitos do tipo [OUTROS] sem descrição atribuida", 5,PecasNokActivity.this,false);
                            return;
                        }

                    }
                }

                if (ref.equals("") || lote.equals("") || pSomaTotalDefeitos.equals(0) ||  mUser.equals("")){
                    Dialogos.dialogoErro("Atenção","Existem variáveis em falta", 5,PecasNokActivity.this,false);
                    return;
                }

                if (tiporegisto == 1 && textView_localizacao.getText().equals("")) {
                    Dialogos.dialogoErro("Atenção","Existem variáveis em falta", 5,PecasNokActivity.this,false);
                    return;
                }


                String txtMessage = "Deseja registar os defeitos?";

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setMessage(txtMessage);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        registaPnok();


                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder.setCancelable(false);

                AlertDialog myDialog = builder.create();
                myDialog.show();


            }

        });

        listViewDefeitos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
                alertDialog.setTitle("Introduza o número de defeitos ");
                alertDialog.setMessage(adapterPnokDef.getItem(position).getDescricao());

                inputPedeQttDefeito = new EditText(thisActivity);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputPedeQttDefeito.setInputType(InputType.TYPE_CLASS_NUMBER);

                Integer tmpQtt = adapterPnokDef.getItem(position).getQtt().intValue();

                if (! tmpQtt.equals(0) ) {
                    inputPedeQttDefeito.setText(UserFunc.retiraZerosDireita(adapterPnokDef.getItem(position).getQtt()));
                }

                inputPedeQttDefeito.setLayoutParams(lp);


                inputPedeQttDefeito.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                //inputPedeLocConsumo.setText("");
                inputPedeQttDefeito.setSelectAllOnFocus(true);

                alertDialog.setView(inputPedeQttDefeito);
                //alertDialog.setIcon(R.drawable.key);

                alertDialog.setPositiveButton("Submeter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.cancel();

                                String j_descricao = adapterPnokDef.getItem(position).getDescricao();

                                Double j_qtt = 0.0;

                                if (! inputPedeQttDefeito.getText().toString().isEmpty()) {
                                    j_qtt =  Double.parseDouble(inputPedeQttDefeito.getText().toString()) ;
                                }

                                listaDefeitos.get(position).setQtt(j_qtt);

                                adapterPnokDef.notifyDataSetChanged();

                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                defineEditTextHint(false);
                            }
                        });


                final AlertDialog dialog = alertDialog.create();

                //alertDialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                dialog.show();

            }
        });

        listViewDefeitos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {


                if(listaDefeitos.get(position).getCDefOutros()) {

                    String alertDialogTitle = "Descreva o motivo OUTROS:";
                    String alertDialogMessage = "";

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PecasNokActivity.this);
                    alertDialog.setTitle(alertDialogTitle);
                    alertDialog.setMessage(alertDialogMessage);

                    inputPedeValor = new EditText(PecasNokActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    inputPedeValor.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputPedeValor.setLayoutParams(lp);

                    inputPedeValor.setText(listaDefeitos.get(position).getDetalheOutros());

                    alertDialog.setView(inputPedeValor);

                    alertDialog.setPositiveButton("Submeter",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    listaDefeitos.get(position).setDetalheOutros(inputPedeValor.getText().toString());
                                    listaDefeitos.get(position).setDescricao(listaDefeitos.get(position).getDescOriginal() + " ["+listaDefeitos.get(position).getDetalheOutros()+"]");
                                    adapterPnokDef.notifyDataSetChanged();

                                }
                            });

                    alertDialog.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = alertDialog.create();

                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    dialog.show();
                    requestFocus(inputPedeValor);
                    inputPedeValor.setSelectAllOnFocus(true);


                }


                return true;
            }
        });
        defineEditTextHint(true);
    }

    @Override
    public void onClick(View v) {


        switch(v.getId()) {

            case R.id.editText_leitura:
                textView_referencia.setText(clear);
                break;

            case R.id.textView_ref:
                textView_referencia.setText(clear);
                break;

            case R.id.textView_of:
                textView_of.setText(clear);
                break;

            case R.id.textView_operador:
                textView_operador.setText(clear);
                break;

            case R.id.textView_localizacao:
                textView_localizacao.setText(clear);
                break;


            case R.id.button_lerRef:
                FLAG_TIPO_LEITURA = "REF";
                textView_of.setText("");
                pedeString();
                return;

            case R.id.button_lerLote:
                if (ref.equals("")) {
                    Dialogos.dialogoInfo("Info","O código do artigo não foi lido",3.0,thisActivity,false);
                    return;
                }

                FLAG_TIPO_LEITURA = "OF";
                pedeString();
                return;

        }

        defineEditTextHint(false);

    }

    private boolean validaCodigo() {

        if (editText_lercodigo.getText().toString().trim().isEmpty()) {
            requestFocus(editText_lercodigo);
            return false;
        }

        return true;
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

    private void dialogoErro(String title, String subtitle, int tempo) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(PecasNokActivity.this);


        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface d, int arg1) {
                d.dismiss();
            };
        });

        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(true);


        builder1.setView(image);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();

        if (tempo > 0) {
            new CountDownTimer(tempo*1000, 500) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub

                    alertErro.dismiss();
                }
            }.start();
        }


    }

    private void dialogoInfo(String title, String subtitle, int tempo) {

        ImageView image_info = new ImageView(getApplication());

        image_info.setImageResource(R.mipmap.ic_info);

        AlertDialog.Builder builderInfo = new AlertDialog.Builder(PecasNokActivity.this);
        builderInfo.setTitle(title);
        builderInfo.setMessage(subtitle);
        builderInfo.setCancelable(false);

        builderInfo.setView(image_info);

        final AlertDialog alertInfo = builderInfo.create();
        alertInfo.show();

        new CountDownTimer(tempo*1000, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                alertInfo.dismiss();
            }
        }.start();

    }

    @Override
    public void asyncResponse(String response, int op) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);


        System.out.println("antes:"+editText_lercodigo.getText().toString());

        if (response == null) {
            editText_lercodigo.setText(clear);
            requestFocus(editText_lercodigo);
            dialogoErro("ERRO","Erro no processamento de pedidos ao WebService",3);
            defineEditTextHint(false);
            return;
        }

        // verificaRef
        if (op == 2) {

            System.out.println("antes:"+editText_lercodigo.getText().toString());

            if (!response.equals(0)) {

                textView_referencia.setText(editText_lercodigo.getText().toString());
                ref = textView_referencia.getText().toString().trim();



            } else {
                dialogoErro("Erro","Referência não válida ou não existe",4);
            }

            defineEditTextHint(true);

        }

        // ConverteLote
        if (op == 3) {

            String mResIniOf  = response.substring(1,response.length()-1);
            lote = mResIniOf.replace("\\/", "/");
            textView_of.setText(lote);

            if (tiporegisto == 0) {
                lerInfoOf(ref, lote);
                return;
            }

            if (tiporegisto == 1) {
                lerSe(ref,lote);
                return;
            }


        }

        // Processamento do Ws LerSe - Datatable com a informação do lote lido

        if (op == 6) {


            JSONconverter jsonConverter = new JSONconverter();
            response = jsonConverter.ConvertJSON(response);

            // se os dados vierem vazios
            if (response.equals("[]")) {
                Dialogos.dialogoErro("Validação do lote", "Não encontrei no sistema o lote " + lote+ " para " +
                        "a referencia "+ref, 10, thisActivity, false);
                textView_of.setText("");
                lote = "";
                defineEditTextHint(false);
                return;
            }

            try {

                JSONArray arraySE = new JSONArray(response);

                if (arraySE.length() == 0) {
                    Dialogos.dialogoErro("Validação do lote", "Não encontrei no sistema o lote " + lote+ " para " +
                            "a referencia "+ref, 10, thisActivity, false);
                    textView_of.setText("");
                    lote = "";
                    defineEditTextHint(false);
                    return;
                }
                else {
                    defineEditTextHint(false);
                }

            }
            catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro na validaçaõ do lote", e.getMessage(), 10, thisActivity, false);
                return;
            }

        }

    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        editText_lercodigo.setText("");
        requestFocus(editText_lercodigo);
        volleyRegistaPnokCount = 0;

        if (textView_localizacao.getText().equals("")) {
            armazem = "0";
            zona = "";
            alveolo = "";
        }

        if (textView_referencia.getText().equals("")) {
            textView_of.setText(clear);
            //textView_localizacao.setText(clear);
            //textView_operador.setText(clear);
            //textview_qtd.setText(clear);
            editText_lercodigo.setHint("Ler referência");
            FLAG_TIPO_LEITURA = "REF";
            ref = "";
            lote = "";

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);

            return;
        }

        if (textView_of.getText().equals("")) {
            //textView_localizacao.setText(clear);
            //textView_operador.setText(clear);
            //textview_qtd.setText(clear);
            //spinnerMotivosRej.setSelection(0);
            editText_lercodigo.setHint("Ler OF/Lote");
            FLAG_TIPO_LEITURA = "OF";
            lote = "";
            //armazem = "0";
            //zona = "";
            //alveolo = "";

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);
            return;
        }


        if (tiporegisto == 1 && textView_localizacao.getText().equals("")){
            editText_lercodigo.setHint("Ler localização");

            FLAG_TIPO_LEITURA = "LOCALIZACAO";

            armazem = "0";
            zona = "";
            alveolo = "";

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);
            return;

        }

        if (textView_operador.getText().equals("")) {

            editText_lercodigo.setHint("Ler Operador");
            FLAG_TIPO_LEITURA = "OPERADOR";
            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);
            return;
        }

        Integer pSomaTotalDefeitos = 0;

        if (!listaDefeitos.isEmpty()) {

            for (int i = 0; i < listaDefeitos.size(); i++) {
                pSomaTotalDefeitos = pSomaTotalDefeitos + listaDefeitos.get(i).getQtt().intValue();
            }
        }

        // se ainda não tem defeitos registados
        if (pSomaTotalDefeitos == 0) {
            editText_lercodigo.setHint("Ler quantidade");
            FLAG_TIPO_LEITURA = "QUANTIDADE";
            return;
        }


        // chegados aqui temos todos os dados preenchidos

        editText_lercodigo.setHint("Registar peças NOK");

        if (mostraDialogoInfo) dialogoInfo("Acção","OK, submeter",1);
        return;

    }

    private boolean validaLeituraLocalizacao(String textoLido) {

        String armazem_lido, zona_lida, alveolo_lido = "";

        System.out.println("Localização lida " + textoLido);

        // A localização começa por (ARM) ou (ALV), ou seja, tem de ter 5 caracteres

        if (textoLido.length() < 5) {
            return false;
        }

        String prefixo = textoLido.substring(0, 5);

        switch (prefixo) {
            case ("(ARM)"):
                armazem_lido = textoLido.substring(5);
                try {
                    Integer val = Integer.valueOf(armazem_lido);
                    if (val == null) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                armazem = armazem_lido;
                zona = "";
                alveolo ="";

                // textView_localizacao.setText(armazem_lido);
                return true;

            case ("(ALV)"):
                System.out.println("Começa por ALV");


                // vamos contar se existem só duas hastags
                int tagscount = textoLido.length() - textoLido.replace("#", "").length();
                if (tagscount != 2) {
                    return false;
                }

                int pos_tag1, pos_tag2 = 0;
                pos_tag1 = textoLido.indexOf("#");
                pos_tag2 = textoLido.indexOf("#", pos_tag1 + 1);
                System.out.println("Pos tag_1" + pos_tag1);
                System.out.println("Pos tag_2" + pos_tag2);

                if (pos_tag1 >= pos_tag2) {
                    return false;
                }

                armazem_lido = textoLido.substring(5, pos_tag1);
                zona_lida = textoLido.substring(pos_tag1 + 1, pos_tag2);
                alveolo_lido = textoLido.substring(pos_tag2 + 1);

                try {
                    Integer val = Integer.valueOf(armazem_lido);
                    if (val == null) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }

                textView_localizacao.setText("[" + armazem_lido + "] [" + zona_lida + "] [" + alveolo_lido + "]");
                armazem = armazem_lido;
                zona = zona_lida;
                alveolo = alveolo_lido;


                return true;

            default: {
                return false;
            }

        }

    }


    // add items into spinner dynamically

    public void addItemsOnSpinner() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetTiposDefeitoPNOK";

        progDailog.show();

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        listaDefeitos = gson.fromJson(response,new TypeToken<ArrayList<DmPnokDef>>() {}.getType());

                        // carregar a descrição inicial do tipo de defeito "OUTROS"

                        adapterPnokDef = new AAdapterPnokDef(thisActivity,0,listaDefeitos);
                        listViewDefeitos.setAdapter(adapterPnokDef);
                        adapterPnokDef.notifyDataSetChanged();

                        progDailog.dismiss();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),3,thisActivity,true);
            }
        });


        System.out.println(url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    private void pedeString(){

        String alertDialogTitle = "Ler dados";
        String alertDialogMessage = "";

        if (FLAG_TIPO_LEITURA.equals("REF")) {
            alertDialogMessage = "Inserir código";
        }

        if (FLAG_TIPO_LEITURA.equals("OF")) {
            alertDialogMessage = "Inserir lote";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PecasNokActivity.this);
        alertDialog.setTitle(alertDialogTitle);
        alertDialog.setMessage(alertDialogMessage);

        inputPedeValor = new EditText(PecasNokActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputPedeValor.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPedeValor.setLayoutParams(lp);

        alertDialog.setView(inputPedeValor);

        //requestFocus(inputPedeQtd);
        //inputPedeQtd.setSelectAllOnFocus(true);

        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editText_lercodigo.setText(inputPedeValor.getText().toString().toUpperCase());
                        // KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER);
                        KeyEvent keyEvent = null;
                        EditTextOps.hideKeyboard(thisActivity,editText_lercodigo);
                        ediTextLerCodigoBarCodeReader.onEditorAction(editText_lercodigo,editText_lercodigo.getImeOptions(),keyEvent);
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
        requestFocus(inputPedeValor);


    }

    private  void lerInfoOf(String pRef, String pLote) {

        GetInfoOfWs getInfoOfWs = new GetInfoOfWs(thisActivity);
        getInfoOfWs.setOnGetInfoOfListener(new GetInfoOfWs.OnGetInfoOfListener() {
            @Override
            public void onSuccess(InfoOfDataModel mInfoOf) {


                if (mInfoOf == null) {
                    Dialogos.dialogoErro("Erro na leitura da OF","A OF "+mInfoOf.getNumof()+"não existe " +
                            "para a referencia "+mInfoOf.getRef(),5,thisActivity, false);
                    textView_of.setText("");
                    textView_referencia.setText("");
                    lote = "";
                    ref = "";
                    defineEditTextHint(false);
                    return;
                }

                if (mInfoOf.getFechada()) {
                    Dialogos.dialogoErro("Informação","A OF "+mInfoOf.getNumof()+" já está fechada",5,thisActivity, false);
                    textView_of.setText("");
                    textView_referencia.setText("");
                    lote = "";
                    ref = "";
                    defineEditTextHint(false);
                    return;
                }


                if (!mInfoOf.getIniciada()) {
                    Dialogos.dialogoErro("Informação","A OF "+mInfoOf.getNumof()+" não foi iniciada",5,thisActivity, false);
                    textView_of.setText("");
                    textView_referencia.setText("");
                    lote = "";
                    ref = "";
                    defineEditTextHint(false);
                    return;
                }

                if (mInfoOf.getFinalizada()) {
                    Dialogos.dialogoErro("Informação","A OF "+mInfoOf.getNumof()+" já foi finalizada",5,thisActivity, false);
                    textView_of.setText("");
                    textView_referencia.setText("");
                    lote = "";
                    ref = "";
                    defineEditTextHint(false);
                    return;
                }

                defineEditTextHint(true);


            }

            @Override
            public void onError(VolleyError error) {

                textView_of.setText("");
                textView_referencia.setText("");
                lote = "";
                ref = "";
                defineEditTextHint(false);

            }
        });

        getInfoOfWs.execute(pRef,pLote);



        AsyncRequest mAsynRequest = new AsyncRequest(PecasNokActivity.this,5 );

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/GetInfoOf?").buildUpon()
                .appendQueryParameter("referencia",pRef)
                .appendQueryParameter("lote",pLote)
                .build();

        mAsynRequest.execute(builtURI_processaPedido.toString());

    }


    private  void lerSe(String pRef, String pLote) {

        String filtro = "";

        AsyncRequest asyncRequest = new AsyncRequest(thisActivity, 6);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                .appendQueryParameter("referencia", pRef)
                .appendQueryParameter("lote", pLote)
                .appendQueryParameter("filtro", filtro)
                .build();

        asyncRequest.execute(builtURI_processaPedido.toString());

    }


    private  void registaPnok() {

        Integer pSomaTotalDefeitos = 0;

        ArrayList<DmPnokDef> listadefs = new ArrayList<>();

        if (!listaDefeitos.isEmpty()) {

            for (int i = 0; i < listaDefeitos.size(); i++) {
                pSomaTotalDefeitos = pSomaTotalDefeitos + listaDefeitos.get(i).getQtt().intValue();

                if(listaDefeitos.get(i).getQtt() > 0) {


                    DmPnokDef defeito = new DmPnokDef();

                    defeito.setCodigo(listaDefeitos.get(i).getCodigo());
                    defeito.setDescricao(listaDefeitos.get(i).getDescricao());
                    defeito.setQtt(listaDefeitos.get(i).getQtt());

                    listadefs.add(defeito);

                }
            }
        }

        DmRPnok pnokDef = new DmRPnok();

        pnokDef.setReferencia(ref);
        pnokDef.setLote(lote);
        pnokDef.setArmazem(Integer.parseInt(armazem));
        pnokDef.setZona(zona);
        pnokDef.setAlveolo(alveolo);
        pnokDef.setOperador(Integer.parseInt(textView_operador.getText().toString().trim()) );

        pnokDef.setTiporegisto(tiporegisto);
        pnokDef.setDefList(listadefs);

        Gson gson = new Gson();
        final String json = gson.toJson(pnokDef);

        System.out.println(json);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RegistaPecasNok?";

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        final String requestBody = json;

        if (volleyRegistaPnokCount > 0) return;

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                volleyRegistaPnokCount = volleyRegistaPnokCount - 1;

                Log.i("VOLLEY", response);

                progDailog.dismiss();

                try {

                    JSONObject jsonResponse = new JSONObject(response);

                    // Se for sucesso
                    if (jsonResponse.getInt("Codigo") == 0) {

                        Dialogos.dialogoInfo("Sucesso","Defeitos registados",3.0,thisActivity,false);

                        textView_referencia.setText(clear);
                        textView_localizacao.setText(clear);
                        textView_operador.setText(clear);

                        listaDefeitos.clear();
                        adapterPnokDef.notifyDataSetChanged();
                        addItemsOnSpinner();

                        defineEditTextHint(false);
                    }
                    else
                    {
                        Dialogos.dialogoErro("Erro",jsonResponse.getString("Descricao"),60,PecasNokActivity.this,false);
                    }

                }
                    catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro na validaçaõ do lote", e.getMessage(), 10, thisActivity, false);
                return;

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyRegistaPnokCount = volleyRegistaPnokCount - 1;
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido", error.toString(), 10, thisActivity, false);
                Log.e("VOLLEY", error.toString());

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        System.out.println(url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
               0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volleyRegistaPnokCount = volleyRegistaPnokCount + 1;
        requestQueue.add(stringRequest);

    }

    private void defineEditTextReadBarCodeListener() {

        editTextLerCodigoBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                System.out.println("LIDO: " + editText_lercodigo.getText().toString());

                if (!validaCodigo()) return;

                // conforme o tipo de leitura vamos proceder à validação da informação carregada

                // se o comprimento for >= 3 e se for um código começado por R então vamos ler a referencia e a OF

                if (editText_lercodigo.getText().toString().length() >= 3) {
                    if (FLAG_TIPO_LEITURA.equals("REF") || FLAG_TIPO_LEITURA.equals("OF")) {

                        String textolido = editText_lercodigo.getText().toString();

                        DataModelLeituraCB leituraCB = new DataModelLeituraCB(textolido);

                        if (leituraCB.getTipocb().equals("R")) {
                            // se o tipo de leitura for R então assumimos que lemos um codigo de barras com ref+lote+qtt

                            ref = leituraCB.getReferencia();
                            lote = leituraCB.getLote();

                            textView_referencia.setText(ref);
                            textView_of.setText(lote);

                            if (tiporegisto == 0) {
                                lerInfoOf(ref, lote);
                                return;
                            }

                            defineEditTextHint(true);
                            return;

                        }

                    }
                }

                switch (FLAG_TIPO_LEITURA) {

                    case "REF":
                        // se o que foi lido começa por (R) então estamos a ler um cb completo ref+lote+qtd

                        if (editText_lercodigo.getText().toString().length() < 3){
                            Dialogos.dialogoErro("Erro","Leitura inválida: "+editText_lercodigo.getText().toString(),3,PecasNokActivity.this,false);
                            defineEditTextHint(false    );
                            return;
                        }

                        // array com a consulta da referencia lida
                        String mReflida = editText_lercodigo.getText().toString().trim();

                        JSONArray myStArray = WsExecute.lerST(mReflida,PecasNokActivity.this);

                        // array null erro na consulta da referencia
                        if (myStArray.equals(null)){
                            textView_referencia.setText("");
                            defineEditTextHint(false);
                            return;
                        }

                        if (myStArray.length() == 0) {
                            Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + mReflida, 10, PecasNokActivity.this, false);
                            textView_referencia.setText("");
                            defineEditTextHint(false);
                            return;
                        } else {

                            try {
                                JSONObject camposST = myStArray.getJSONObject(0);
                                ref = camposST.getString("ref").trim();
                                textView_referencia.setText(ref);
                                defineEditTextHint(true);
                                return;
                            } catch (final JSONException e) {
                                Log.e("", "Erro Conversão: " + e.getMessage());
                                ref = "";
                                textView_referencia.setText("");
                                defineEditTextHint(true);
                            }
                        }

                        break;

                    case "DEFEITO":

                        editText_lercodigo.setText(clear);
                        defineEditTextHint(false);
                        break;

                    case "LOCALIZACAO":

                        if (! validaLeituraLocalizacao(editText_lercodigo.getText().toString()))
                        {
                            dialogoErro("Erro","Leitura de localização inválida",3);
                            editText_lercodigo.setText(clear);
                            defineEditTextHint(false);
                        }

                        else defineEditTextHint(true);

                        break;

                    case "OF":

                        // se estamos a ler o lote então vamos converter o lote caso seja PA ou PI
                        // depois de converter o lote vamos validar se o lote existe

                        AsyncRequest mAsynRequest = new AsyncRequest(PecasNokActivity.this,3 );

                        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ConverteLote?").buildUpon()
                                .appendQueryParameter("referencia",ref)
                                .appendQueryParameter("lote",editText_lercodigo.getText().toString())
                                .build();

                        mAsynRequest.execute(builtURI_processaPedido.toString());

                        break;

                    case "OPERADOR":
                        // se o que foi lido começa por (OP)

                        if (editText_lercodigo.getText().toString().substring(0,4).equals("(OP)")) {

                            String mNrOperador = editText_lercodigo.getText().toString().substring(4,editText_lercodigo.getText().toString().length()) ;

                            if (!Pattern.matches("[0-9]+",mNrOperador)) {
                                dialogoErro("Erro","Leitura de operador inválida",3);
                                defineEditTextHint(false);
                            }
                            else{
                                textView_operador.setText(mNrOperador);
                                defineEditTextHint(true);
                            }

                        } else {
                            dialogoErro("Erro","Leitura de operador inválida",3);
                            defineEditTextHint(false);
                        }

                        break;

                }




            }
        };


    }
}



