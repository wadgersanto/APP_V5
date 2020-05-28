package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import gnotus.inoveplastika.API.Producao.GetInfoOfWs;
import gnotus.inoveplastika.AsyncRequest;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.JSONconverter;
import gnotus.inoveplastika.MainActivity_Lista;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;
import gnotus.inoveplastika.WsExecute;

import static gnotus.inoveplastika.MainActivity_Lista.CRLF;

public class EntradaProducaoActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity thisActitity = EntradaProducaoActivity.this;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private EditText editText_lercodigo, editTextLeCbPeca;
    private TextView textView_referencia, textView_of, textView_caixa, textView_qt, textView_operador, textView_Info1;
    private Button button_submeter;
    private ImageView imageView_editarCaixa, imageView_procura,imageView_editarQtd;
    private Activity thisActivity = EntradaProducaoActivity.this;

    private EditText input;

    private static final String clear = "";


    private static final String QUERY_PARAM_LOTE = "lote";
    private static final String QUERY_PARAM_REF = "ref";

    private long mLastSubmittButtonClickTime = 0;

    private static final String WSRESPONSEVAZIO = "\"[]\"";

    private boolean flag_pode_submeter = false, leCaixaCb = false;

    private String ref = "",lote = "", cbPecaLido = "";
    private String oldDialogInput;
    private String qt = "";
    private String caixa = "";
    private String mAsyncDescErro ="", opTurnoStamp = "";

    private String FLAG_TIPO_LEITURA = "REF";
    private JSONArray jSTlida;

    private ArrayList<DataModelTurno> turnoOperador = new ArrayList<>();

    private EditTextBarCodeReader.OnGetScannedTextListener editTextReadBarCodeListener;

    private boolean tem_lim_dias,tem_lim_qtt;
    private int volleyRegistaProdQeueCount;

    private AlertDialog dialogPedeCaixa;

    private ProgressDialog progDailog ;
    private InfoOfDataModel ofLida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada_producao);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registo Produção");
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

        textView_referencia = findViewById(R.id.textView_ref);
        textView_of =  findViewById(R.id.textView_of);
        textView_caixa = findViewById(R.id.textView_motivo);
        textView_qt = findViewById(R.id.textView_qtt);
        textView_operador = findViewById(R.id.textView_operador);
        textView_Info1 = findViewById(R.id.textViewEpInfo1);
        textView_Info1.setText("");

        imageView_editarCaixa = findViewById(R.id.imageView_ep_editaCaixa);
        imageView_editarQtd = findViewById(R.id.imageView_ep_editaQtd);
        imageView_procura = findViewById(R.id.imageView_ep_procura);

        textView_referencia.setOnClickListener(this);
        textView_of.setOnClickListener(this);
        textView_caixa.setOnClickListener(this);
        textView_qt.setOnClickListener(this);
        textView_operador.setOnClickListener(this);
        imageView_editarCaixa.setOnClickListener(this);
        imageView_editarQtd.setOnClickListener(this);
        imageView_procura.setOnClickListener(this);

        button_submeter = (Button) findViewById(R.id.button_submeter);

        editText_lercodigo = (EditText) findViewById(R.id.editText_ep_lercodigo);

        /*editText_lercodigo.setInputType(InputType.TYPE_NULL);
        editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));*/

        // define o listener para a editTextLerCodigo - Ação a executar depois de processar a leitura de código de barras
        setEditTextLerCodigoBarCodeListener();
        EditTextBarCodeReader editTextBarCodeReaderLerCodigo = new EditTextBarCodeReader(editText_lercodigo,this);
        editTextBarCodeReaderLerCodigo.setOnGetScannedTextListener(editTextReadBarCodeListener);


        MainActivity_Lista.carregaParametros(thisActitity);

        volleyRegistaProdQeueCount = 0;

        button_submeter.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastSubmittButtonClickTime < 1000){
                    return;
                }
                mLastSubmittButtonClickTime = SystemClock.elapsedRealtime();

                System.out.println("Registar Producao");

                ImageView image = new ImageView(getApplication());
                image.setImageResource(R.mipmap.ic_notok);

                // if (flag_pode_submeter)

                if (canSubmittProduction())
                {

                    // valida se caso seja obrigatorio o stamp do turno ele está de facto preenchido
                    // alteração em 16-07-2019

                    if (Globals.PARAM_OBRIGA_TURNO_EP.equals(true)) {
                        if (opTurnoStamp.trim().isEmpty()) {
                            Dialogos.dialogoErro("Validação do turno","Não foi possivel obter a informação do turno. Proceda novamente à leitura do operador",
                                    4,thisActivity,false);
                            textView_operador.setText("");
                            defineEditTextHint(false);
                            return;

                        }

                    }

                    if ( ! ofLida.getU_cbpeca().isEmpty() && cbPecaLido.isEmpty() ){
                        Dialogos.dialogoErro("Dados em falta ", "Não foi efetuada a leitura do código de barras da peça e " +
                                "este é obrigatório", 5, thisActivity, false);
                        defineEditTextHint(false);
                        return;
                    }


                    progDailog.show();
                    progDailog.setMessage("A registar leitura de caixa");

                    RequestQueue requestQueue = Volley.newRequestQueue(thisActivity);

                    String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/registaProducao?";

                    JSONObject json = new JSONObject();


                    try {

                        json.put("Referencia", textView_referencia.getText().toString());
                        json.put("Lote", textView_of.getText().toString());
                        json.put("Barcode","");
                        json.put("Qtt", Integer.parseInt(textView_qt.getText().toString()) );
                        json.put("Armazem",10);
                        json.put("Caixa", Integer.parseInt(textView_caixa.getText().toString()));
                        json.put("Operador",Integer.parseInt(textView_operador.getText().toString()));
                        json.put("Cbpeca",cbPecaLido);
                        json.put("Turnostamp",opTurnoStamp);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.println(json.toString());

                    Gson gson = new Gson();

                    System.out.println(gson.toJson(json));



                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    volleyRegistaProdQeueCount = volleyRegistaProdQeueCount -1;

                                    progDailog.dismiss();

                                    try {

                                        if (response.getInt("Codigo") == 0) {

                                            Dialogos.dialogoInfo("Sucesso","Produção registada!",1.0,thisActitity,false);
                                            textView_referencia.setText(clear);
                                            defineEditTextHint(false);


                                        }
                                        else {
                                            Dialogos.dialogoInfo("Erro",response.getString("Descricao") ,10.0,thisActitity,false);
                                            defineEditTextHint(false);
                                        }

                                        MainActivity_Lista.carregaParametros(thisActitity);
                                    }

                                    catch (final JSONException e) {

                                        Dialogos.dialogoErro("Erro",e.getMessage(),60,thisActitity, false);

                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            volleyRegistaProdQeueCount = volleyRegistaProdQeueCount -1;

                            Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),10,thisActitity,false);

                            progDailog.dismiss();
                        }
                    });


                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                            0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    jsonObjectRequest.setShouldCache(false);
                    requestQueue.getCache().clear();
                    requestQueue.add(jsonObjectRequest);

                    System.out.println(url);
                    // controlar as chamadas simultaneas aos registos de produção ;
                    volleyRegistaProdQeueCount = volleyRegistaProdQeueCount + 1;


                } else {

                    Dialogos.dialogoErro("Dados em falta","Por favor leia todos os elementos",2,thisActivity,false);

                }

            }


        });

    }

    @Override
    public void onClick(View v) {


        switch(v.getId()) {

            case R.id.imageView_ep_procura:
                textView_referencia.setText(clear);
                defineEditTextHint(true);
                break;

            case R.id.editText_ep_lercodigo:

                textView_referencia.setText(clear);
                defineEditTextHint(true);
                break;

            case R.id.textView_ref:

                textView_referencia.setText(clear);
                defineEditTextHint(true);
                break;

            case R.id.textView_of:
                textView_of.setText(clear);
                defineEditTextHint(true);
                break;

            case R.id.textView_motivo:
                textView_caixa.setText(clear);
                defineEditTextHint(true);
                break;

            case R.id.imageView_ep_editaCaixa:

                textView_caixa.setText(clear);
                defineEditTextHint(true);

                break;

            case R.id.imageView_ep_editaQtd:

                if ( !textView_referencia.equals("") && ! textView_of.equals("")) {
                    pedeQuantidade();
                    return;
                }

                break;


            case R.id.textView_qtt:

                if ( !textView_referencia.equals("") && ! textView_of.equals("")) {
                    pedeQuantidade();
                    return;
                }

                break;

            case R.id.textView_operador:
                textView_operador.setText(clear);
                defineEditTextHint(true);
                break;
        }

        defineEditTextHint(false);


    }

    private boolean validaCodigo() {

        if (editText_lercodigo.getText().toString().trim().isEmpty()) {

            editText_lercodigo.requestFocus();
            return false;

        }


        return true;
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.navigation_inicio:

                        finish();
                        drawerLayout.closeDrawers();

                        break;

                    case R.id.navigation_option3:

                        drawerLayout.closeDrawers();

                        break;


                    case R.id.navigation_logout:

                        finishAffinity();
                        finish();

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(3).setChecked(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {

                super.onDrawerOpened(v);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void onBackPressed() {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(getApplication(), MainActivity.class));
        //finishAffinity();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {


        onBackPressed();
        return true;
    }


    private String processaLote(String str) {

        String loteF = str;

        try {

            if (str.substring(0,2).equals("OF")) {

                loteF = str.substring(2, str.length());

            }

            if (str.indexOf('/') < 0) {

                loteF = loteF.substring(0,loteF.length()-2) + "/" + loteF.substring(loteF.length()-2,loteF.length());

            }

        }catch (Exception StringIndexOutOfBoundsException) {

            editText_lercodigo.setText(clear);
            ;
            editText_lercodigo.requestFocus();
        }


        System.out.println("OF: " + loteF);

        return loteF;
    }

    private void dialogoErro(String title, String subtitle, int tempo) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(EntradaProducaoActivity.this);


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

        AlertDialog.Builder builderInfo = new AlertDialog.Builder(EntradaProducaoActivity.this);
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


    private void defineEditTextHint(boolean mostraDialogoInfo) {

        editText_lercodigo.requestFocus();

        editText_lercodigo.setText(clear);

        flag_pode_submeter = false;


        if (textView_referencia.getText().equals("")) {

            volleyRegistaProdQeueCount = 0;

            cbPecaLido = "";
            textView_of.setText(clear);
            textView_qt.setText(clear);
            textView_caixa.setText(clear);
            textView_operador.setText(clear);
            textView_Info1.setText(clear);
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler código artigo");
            FLAG_TIPO_LEITURA = "REF";
            if (mostraDialogoInfo) dialogoInfo("Acção", "Ler código artigo",1);

            return;
        }

        if (textView_of.getText().equals("")) {

            textView_qt.setText(clear);
            textView_caixa.setText(clear);
            textView_operador.setText(clear);
            textView_Info1.setText(clear);
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler OF");
            FLAG_TIPO_LEITURA = "OF";
            if (mostraDialogoInfo) dialogoInfo("Acção", "Ler OF",1);
            return;
        }

        if (textView_qt .getText().equals("")) {

            // textView_caixa.setText(clear);
            textView_operador.setText(clear);
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler Quantidade");
            FLAG_TIPO_LEITURA = "QUANTIDADE";
            // if (mostraDialogoInfo) dialogoInfo("Acção", "Ler Quantidade",1);
            if (mostraDialogoInfo) pedeQuantidade();

            return;
        }

        if (textView_caixa .getText().equals("")) {
            textView_operador.setText(clear);
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler nº Caixa");

            FLAG_TIPO_LEITURA = "NRCAIXA";
            // if (mostraDialogoInfo) dialogoInfo("Acção", "Ler nº caixa",1);
            if (mostraDialogoInfo) calculateNextBoxToReadBox(null);

            return;
        }

        if (textView_operador .getText().equals("")) {
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler operador");
            FLAG_TIPO_LEITURA = "OPERADOR";
            if (mostraDialogoInfo) dialogoInfo("Acção", "Ler operador",1);
            return;
        } else {

            editText_lercodigo.setHint("OK - Submeter");
            if (mostraDialogoInfo) dialogoInfo("Acção", "OK - Submeter",1);

        }



       /* if (textView_operador .getText().equals("")) {
            editText_lercodigo.setText("");
            editText_lercodigo.setHint("Ler operador");
            FLAG_TIPO_LEITURA = "OPERADOR";
            if (mostraDialogoInfo) dialogoInfo("Acção", "Ler operador",1);
            return;
        } else flag_pode_submeter = true;

        editText_lercodigo.setHint("OK - Submeter");
        if (mostraDialogoInfo) dialogoInfo("Acção", "OK - Submeter",1);*/

    }

    private void pedeNrCaixa(final int boxNumberToRead, boolean showAlertFlag){


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradaProducaoActivity.this);

        alertDialogBuilder.setTitle("Leitura de caixa");
        alertDialogBuilder.setMessage("Ler caixa nº"+boxNumberToRead);
        alertDialogBuilder.setCancelable(false);

        input = new EditText(EntradaProducaoActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final EditTextBarCodeReader inputBarcodeReader = new EditTextBarCodeReader(input,thisActivity);

        inputBarcodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, final EditText editText) {

                // verifica se a caixa é a ultima caixa e no caso se ser valida a leitura da caixa

                try {

                    if ( !(input.getText().toString().isEmpty()) && boxNumberToRead == Integer.parseInt(input.getText().toString()) ) {

                        if (! leCaixaCb) {
                            EditTextOps.hideKeyboard(thisActivity,editText);
                        }

                        dialogPedeCaixa.dismiss();
                        textView_caixa.setText(input.getText().toString());
                        defineEditTextHint(true);


                    } else {

                        dialogPedeCaixa.dismiss();
                        pedeNrCaixa(boxNumberToRead,true);

                    }

                }
                catch (Exception ex) {

                    dialogPedeCaixa.dismiss();
                    pedeNrCaixa(boxNumberToRead,true);

                }



            }
        });


        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        if (leCaixaCb) {
            inputBarcodeReader.setOnTouchListenerIsBlocked(true);
            inputBarcodeReader.setReplaceTextOnBarCodeReading(true);
        }
        else {
            inputBarcodeReader.setAllowUserInput(true);
            input.setOnClickListener(new EditText.OnClickListener() {
                @Override
                public void onClick(View view) {
                    input.setText("");
                }
            });
        }

        input.setLayoutParams(lp);

        alertDialogBuilder.setView(input);


        //alertDialogBuilder.setIcon(R.drawable.key);

        alertDialogBuilder.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {

                            if ( !(input.getText().toString().isEmpty())
                                    && boxNumberToRead == Integer.parseInt(input.getText().toString()) ) {

                                if (! leCaixaCb) {
                                    EditTextOps.hideKeyboard(thisActivity,input);
                                }
                                dialogPedeCaixa.dismiss();
                                textView_caixa.setText(input.getText().toString());
                                defineEditTextHint(true);

                            } else {

                                dialogPedeCaixa.dismiss();
                                pedeNrCaixa(boxNumberToRead,true);

                            }

                        }
                        catch (Exception ex) {
                            dialogPedeCaixa.dismiss();
                            pedeNrCaixa(boxNumberToRead,true);
                        }



                    }

                });


        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextOps.hideKeyboard(thisActivity,input); // esconde o teclado
                        dialog.dismiss();
                    }
                });



        dialogPedeCaixa = alertDialogBuilder.create();

        if (! leCaixaCb) dialogPedeCaixa.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if (showAlertFlag) {
            dialogPedeCaixa.setIcon(R.mipmap.ic_notok);
            dialogPedeCaixa.setTitle("Leitura de caixa inválida");
        }

        dialogPedeCaixa.show();

        // if (! leCaixaCb) EditTextOps.showKeyboard(thisActivity);


    }

    private void processaFatorConversao(){

        /* depois de ler a referencia e lote (através de CB unicou ou em duplo passo), vamos ler a informação do lote
         *  e processar o fator de conversao*/

        if (textView_qt.getText().equals("")) {


            ref = textView_referencia.getText().toString();

            JSONArray jsonArray = new JSONArray();
            Double fConversao = 1.0;

            jsonArray = WsExecute.lerSE(ref,lote,"",EntradaProducaoActivity.this);

            try {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject c = jsonArray.getJSONObject(i);
                    fConversao = c.getDouble("fconversao");
                }
            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
            }

            if (fConversao != 1.0) {

                qt = fConversao.toString();
                textView_qt.setText( Dialogos.retiraDecimais(fConversao));

                calculateNextBoxToReadBox(null);
                defineEditTextHint(false);
            }
            else {
                defineEditTextHint(true);
            }

        }
        else
        {
            defineEditTextHint(false);
            calculateNextBoxToReadBox(null);
        }

    }



    private void lerCbPeca(final String cbpeca) {
        //

        // variavel que guarda a informação que estava no AlertDialog antes da nova leitura de codigo de barras

        oldDialogInput = "";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EntradaProducaoActivity.this);
        alertDialog.setTitle("Validação de dados");
        alertDialog.setMessage("Ler código de barras da peça");
        alertDialog.setCancelable(false);

        editTextLeCbPeca = new EditText(thisActitity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editTextLeCbPeca.setInputType(InputType.TYPE_NULL);
        editTextLeCbPeca.setLayoutParams(lp);

        alertDialog.setView(editTextLeCbPeca);

        EditTextBarCodeReader cbPecaBarCodeReader = new EditTextBarCodeReader(editTextLeCbPeca,thisActivity);

        cbPecaBarCodeReader.setReplaceTextOnBarCodeReading(true);


        /*editTextLeCbPeca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                System.out.println("Charsequence: "+charSequence);
                System.out.println("OldDialog: "+oldDialogInput);

                String newtext;

                if (oldDialogInput.equals(charSequence)) {
                    oldDialogInput = "";
                    return;
                }

                if (oldDialogInput.length() < charSequence.length()) {

                    newtext = charSequence.toString().substring(0, charSequence.length()-oldDialogInput.length());
                    System.out.println(newtext);
                    oldDialogInput = newtext;
                    editTextLeCbPeca.setText(newtext);

                }
                else {
                    oldDialogInput = charSequence.toString();
                }

                // para que os botões de cancelar submeter não fiquem com o focus

                Dialogos.dialogoInfo("Codigo de barras lido",charSequence.toString(),0.2,thisActitity, false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/



        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        textView_referencia.setText("");
                        defineEditTextHint(true );

                    }
                });

        final AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.show();

        // dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(false);
        // dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setFocusable(false);



        // Override ao click do próprio botão
        // impedir que o dialogo feche sempre que se clica no botão

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String mCbPeca = editTextLeCbPeca.getText().toString();

                // se o código de barras lido não for igual à peça
                System.out.println("Indice de leitura: "+mCbPeca.indexOf(cbpeca));

                //  if (!cbpeca.equals(mCbPeca) cbpeca.indexOf(mCbPeca)) {
                if ( ! (mCbPeca.indexOf(cbpeca) == 0)) {
                    cbPecaLido = "";
                    editTextLeCbPeca.setText("");
                    Dialogos.dialogoErro("Leitura inválida","O código de barras lido não corresponde ao codigo de barras da peça",5,thisActivity, false);

                }
                else {

                    cbPecaLido = mCbPeca;
                    processaFatorConversao();
                    dialog.dismiss();

                }

            }
        });

    }


    private  void pedeQuantidade() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EntradaProducaoActivity.this);
        alertDialog.setTitle("Quantidade");
        alertDialog.setMessage("Inserir quantidade");

        input = new EditText(EntradaProducaoActivity.this);
        input.setText(textView_qt.getText());
        input.setSelectAllOnFocus(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextOps.hideKeyboard(thisActivity,input);
                        textView_qt.setText(input.getText().toString());
                        defineEditTextHint(true);
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        EditTextOps.hideKeyboard(thisActivity,input);
                        dialog.cancel();

                    }
                });

        AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        EditTextOps.showKeyboard(thisActivity);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }


    private void lerTurnoOperador(final String pOperador) {

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerTurnoOperador?").buildUpon()
                .appendQueryParameter("operador",pOperador)
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();

                        turnoOperador = gson.fromJson(response,new TypeToken<ArrayList<DataModelTurno>>() {}.getType());


                        if (turnoOperador.size() == 0) {
                            Dialogos.dialogoErro("Validação de dados","O operador "+pOperador+" não tem turno iniciado",2,thisActivity,false);
                            textView_operador.setText("");
                            defineEditTextHint(false);
                            return;
                        }



                        if ( turnoOperador.get(0).getMinleftrp() < 0) {
                            Dialogos.dialogoErro("Validação de dados","O operador "+pOperador+" atingiu o fim do turno às "+turnoOperador.get(0).getDatahorafim(),5,thisActivity,false);
                            textView_operador.setText("");
                            defineEditTextHint(false);
                            return;
                        }

                        opTurnoStamp = turnoOperador.get(0).getBostamp();

                        textView_operador.setText(pOperador);

                        if (canSubmittProduction()) button_submeter.callOnClick();
                        else defineEditTextHint(true);


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

        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);


    }

    private  void setEditTextLerCodigoBarCodeListener() {

        editTextReadBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {


                editText.setText("");
                if (scannedText.isEmpty()) return;

                // conforme o tipo de leitura vamos proceder à validação da informação carregada

                switch (FLAG_TIPO_LEITURA) {

                    case "REF":
                        // se o que foi lido começa por (R) então estamos a ler um cb completo ref+lote+qtd

                        // String textoLido = editText_lercodigo.getText().toString();

                        DataModelLeituraCB leituraCB = new DataModelLeituraCB(scannedText);

                        if (leituraCB.getTipocb().equals("R")) {
                            // se o tipo de leitura for R então assumimos que lemos um codigo de barras com ref+lote+qtt


                            ref = leituraCB.getReferencia();
                            lote = leituraCB.getLote();
                            qt = leituraCB.getQtt().toString();

                            textView_referencia.setText(ref);
                            textView_of.setText(lote);

                            textView_referencia.setText(ref);
                            textView_of.setText(lote);

                            if (leituraCB.getQtt() == 0) textView_qt.setText("");
                            else textView_qt.setText(UserFunc.retiraZerosDireita(Double.parseDouble(qt)));

                            volleyLerInfoOf(ref,lote);

                        }

                        else{

                            volleyVerificaRef(scannedText);

                        }

                        break;

                    case "OF": {


                        // lote = processaLote(editText_lercodigo.getText().toString());
                        lote = processaLote(scannedText);
                        textView_of.setText(lote);

                        volleyLerInfoOf(textView_referencia.getText().toString(),textView_of.getText().toString());
                        break;
                    }

                    case "QUANTIDADE": {

                        // String txt_qtd_lida = editText_lercodigo.getText().toString();

                        Integer val;
                        try {
                            val = Integer.valueOf(scannedText);
                            if (val == null) {
                                dialogoErro("Erro", "A quantidade lida" + scannedText + "não é válida",2);
                                defineEditTextHint(false);
                                return;
                            }
                            else {
                                qt = val.toString();
                                textView_qt.setText(val.toString());
                                defineEditTextHint(true);
                            }



                        } catch (NumberFormatException e) {
                            dialogoErro("Erro", "A quantidade lida" + scannedText + "não é válida",2);
                            defineEditTextHint(false);
                            return;
                        }



                        break;
                    }

                    case "NRCAIXA":{

                        /*if (! Pattern.matches("[0-9]+", editText_lercodigo.getText().toString().replace("\n",""))) {
                            dialogoErro("Erro","Leitura de caixa inválida",2);
                            textView_caixa.setText("");
                            defineEditTextHint(false);
                            break;
                        }*/

                        if (! Pattern.matches("[0-9]+", scannedText.replace("\n",""))) {
                            dialogoErro("Erro","Leitura de caixa inválida",2);
                            textView_caixa.setText("");
                            defineEditTextHint(false);
                            break;
                        }

                        calculateNextBoxToReadBox(scannedText);

                        break;
                    }

                    case "OPERADOR":{
                        // se o que foi lido começa por (OP)

                        opTurnoStamp = "";

                        // String txtOpLido = editText_lercodigo.getText().toString();

                        String mNrOperador = UserFunc.processaLeituraOperador(scannedText);

                        if (! mNrOperador.equals("")) {
                            // Verifica se existe turno aberto para este operador. Se existir devolve o stamp

                            // se obrigar a ler o turno do operador então vamos ler o turno do operador

                            if (Globals.PARAM_OBRIGA_TURNO_EP) {
                                lerTurnoOperador(mNrOperador);
                            }
                            else {

                                // caso contrário vamos adicionar o operador e dar permissao para registar
                                textView_operador.setText(mNrOperador);
                                if (canSubmittProduction()) button_submeter.callOnClick();
                                else defineEditTextHint(true);


                            }


                        } else {
                            dialogoErro("Erro","Leitura de operador inválida",2);
                            textView_operador.setText("");
                            defineEditTextHint(false);
                        }


                        break;
                    }

                }



            }
        };


    }

    public void calculateNextBoxToReadBox(final String readBoxNumber) {


        // caso estejamos a invocar esta função passando um nº de caixa lido (readboxNumber <> null)
        // significa que o utilizador leu a caixa na editText de leitura de código de barras

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/devolveUltcaixa?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_REF,textView_referencia.getText().toString())
                .appendQueryParameter(QUERY_PARAM_LOTE,textView_of.getText().toString())
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        try {

                            if (readBoxNumber != null) {
                                // se o nº de caixa lido for igual ao próximo número de caixa podemos submeter o registo de produção
                                if (Integer.parseInt(response) == Integer.parseInt(readBoxNumber)) {
                                    textView_caixa.setText(response);
                                    defineEditTextHint(true);
                                }
                                else {
                                    pedeNrCaixa(Integer.parseInt(response),true);
                                }

                            }
                            else {
                                pedeNrCaixa(Integer.parseInt(response),false);
                            }



                        } catch (Exception NumberFormatException) {

                            dialogoErro("Erro","Sintaxe de código não válida",3);
                            defineEditTextHint(false);
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

        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);


    }

    private void volleyLerInfoOf(String pRef, final String pLote) {


        GetInfoOfWs wsLerOf = new GetInfoOfWs(thisActivity);
        wsLerOf.setOnGetInfoOfListener(new GetInfoOfWs.OnGetInfoOfListener() {
            @Override
            public void onSuccess(InfoOfDataModel mInfoOf) {

                ofLida = mInfoOf;

                if (ofLida == null) {
                    Dialogos.dialogoErro("Leitura de OF", "Não encontrei no sistema a OF " + pLote, 5, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                String prodution_deadline;
                int minutes_to_deadline,minutes_to_alert;

                minutes_to_deadline =ofLida.getMinutes_to_deadline();
                minutes_to_alert =ofLida.getMinutes_to_alert();


                Boolean complyIntervalBetweenBoxes = ofLida.getComplyIntervalBetweenBoxes();
                Integer secsBetweenBoxes = ofLida.getSecsBetweenBoxes();

                tem_lim_dias = ofLida.getTem_lim_dias();
                tem_lim_qtt = ofLida.getTem_lim_qtt();

                // prodution_deadline = camposOF.getString("prodution_deadline").substring(0,16).replace("T"," ");
                prodution_deadline = ofLida.getProduction_deadline() ;

                String txtSubTitMess;


                if (ofLida.getFinalizada()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+ofLida.getNumof()+" já está finalizada", 4, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                if (ofLida.getFechada()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+ofLida.getNumof()+" já está fechada", 4, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                if (! ofLida.getIniciada()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+ofLida.getNumof()+" não foi iniciada", 4, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                if (ofLida.getArmazem() == 0 || ofLida.getZona().isEmpty() || ofLida.getAlveolo().isEmpty() ) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+ofLida.getNumof()+" não está associada a nenhuma máquina", 4, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;

                }

                if (ofLida.getOs_corretivas_open() > 0)  {

                    txtSubTitMess = "O molde associado à OF "+ofLida.getRef()+" tem ordens de manutençao corretivas em aberto. Não pode ser declarada produção";
                    Dialogos.dialogoErro("Leitura de OF", txtSubTitMess, 10, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;

                }

                if (!complyIntervalBetweenBoxes) {

                    txtSubTitMess = "Ainda não foi atingido o tempo mínimo de "+secsBetweenBoxes+" segundos entre registo de caixas";
                    Dialogos.dialogoErro("Leitura de OF", txtSubTitMess, 6, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }


                if ( tem_lim_dias &&  minutes_to_deadline < 0 ) {

                    txtSubTitMess = "A OF "+pLote+" ultrapassou o a data limite de produção "+prodution_deadline+")";

                    Dialogos.dialogoErro("Leitura de OF", txtSubTitMess, 10, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                if ( tem_lim_qtt && ofLida.getQttprevista() <= ofLida.getQttprod()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+pLote+" ultrapassou a quantidade prevista de produção " +
                            "de "+ofLida.getQttprevista(), 4, thisActivity, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                // colocar a informação da data limite de produção

                // 2019-07-27
                if ( tem_lim_dias ) {

                    Integer days_to_end, hours_to_end,min_to_end;
                    String time_to_deadline = "";

                    days_to_end = minutes_to_deadline / 1440 ;
                    hours_to_end = ( minutes_to_deadline - days_to_end * 1440) / 60;
                    min_to_end =  (minutes_to_deadline - days_to_end * 1440 - hours_to_end * 60 );

                    time_to_deadline = days_to_end + "d  "+ hours_to_end + "h "+min_to_end +"m" ;



                    textView_Info1.setText("Data limite:   "+ prodution_deadline + CRLF + "Tempo restante:     "+time_to_deadline );

                    if ( minutes_to_deadline < minutes_to_alert )
                        textView_Info1.setTextColor(Color.RED);
                    else
                        textView_Info1.setTextColor(Color.BLACK);

                }

                // se a leitura de caixa é feita por código de barras ou pode ser introduzida manualmente
                leCaixaCb = ofLida.getLe_caixa_cb();

                if (!ofLida.getU_cbpeca().isEmpty()) {
                    lerCbPeca(ofLida.getU_cbpeca());

                }
                else processaFatorConversao();


                // ************************************************************************

            }

            @Override
            public void onError(VolleyError error) {
                lote = "";
                ref = "";
                textView_referencia.setText("");
                textView_of.setText("");
                defineEditTextHint(false);
            }

        });
        wsLerOf.execute(pRef,pLote);

    }

    private void volleyVerificaRef(final String pRef) {

        progDailog.show();

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/verificaRef?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_REF,pRef)
                .build();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progDailog.dismiss();

                        if (!response.equals("0")) {
                            ref = pRef;
                            textView_referencia.setText(pRef);
                            defineEditTextHint(true );
                        } else {
                            dialogoErro("Erro","A referência ["+ pRef+"] não é válida ou não existe",4);
                            defineEditTextHint(false);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
                defineEditTextHint(false);
            }
        });

        System.out.println(uri.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setShouldCache(false);
        queue.getCache().clear();
        queue.add(stringRequest);

    }

    private boolean canSubmittProduction() {

        if ( !textView_referencia.getText().toString().trim().isEmpty()
                && ! textView_of.getText().equals("")
                && ! textView_qt .getText().equals("")
                && ! textView_caixa .getText().equals("")
                && ! textView_operador .getText().equals("") ) {
            return true;
        }
        else return false;

    }

}
