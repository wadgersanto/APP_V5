package gnotus.inoveplastika.Logistica;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.BatchUnloadWs;
import gnotus.inoveplastika.API.Logistica.GetPdaLoadsWs;
import gnotus.inoveplastika.API.Logistica.LerAlveoloWs;
import gnotus.inoveplastika.API.Phc.CreateBoWs;
import gnotus.inoveplastika.API.Phc.DeleteBoWs;
import gnotus.inoveplastika.API.Phc.GetBiWs;
import gnotus.inoveplastika.API.Phc.GetBoWs;
import gnotus.inoveplastika.API.Phc.GetSeWs;
import gnotus.inoveplastika.API.Phc.GetStWs;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterPdaLoads;
import gnotus.inoveplastika.AsyncRequest;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelBo;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.DataModels.DataModelST;
import gnotus.inoveplastika.DataModels.DataModelSe;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditActivity;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.JSONconverter;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.ViewsBackStackManager;

import static gnotus.inoveplastika.Dialogos.showToast;


public class TransferenciaActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete, View.OnClickListener {

    private static final String clear = "";
    private static final String vazio = "\"[]\"";
    private static final String carateresEspeciais = "[!#$%&/()=?'.,-_/*-+@£§{}')´~^ºª]";

    private static final String QUERY_PARAM_REF     = "ref";
    private static final String QUERY_PARAM_LOTE    = "lote";
    private static final String QUERY_PARAM_ARMAZEM = "armazem";
    private static final String QUERY_PARAM_ZONA    = "zona";
    private static final String QUERY_PARAM_ALVEOLO = "alveolo";
    private static final String QUERY_PARAM_QT      = "qt";
    private static final String QUERY_PARAM_UN      = "un";
    private static final String QUERY_PARAM_QTALT   = "qtAlt";
    private static final String QUERY_PARAM_UNALT   = "unAlt";
    private static final String QUERY_PARAM_USER    = "user";
    private static final String QUERY_PARAM_NDOS    = "ndos";
    private static final String QUERY_PARAM_NO      = "no";
    private static final String QUERY_PARAM_ENT     = "ent";
    private static final String QUERY_PARAM_DATA    = "data";
    private static final String QUERY_PARAM_EST     = "est";
    private static final String QUERY_PARAM_BOSTAMP = "bostamp";

    private static final String BOSTAMP_CARGA       = "bostampcarga";
    private static final String MYFILE              = "configs";


    private Activity thisActivity = TransferenciaActivity.this;

    AlertDialog dialog;
    Bundle bundle = new Bundle();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView textView_operador, textView_localizacao;
    private EditText editText_lercodigo, input;
    private ToggleButton toggleButton_modo;



    private SharedPreferences sharedpreferences;




    private ListView listView;
    private ArrayList<DataModelBi> lista = new ArrayList<>();

    private DataModelArrayAdapterCarga adapter;

    private String armazem = "", zona = "", alveolo = "";
    private String armazem_pda = "99";
    private String zona_pda = "G",alveolo_pda ="G";

    private String bostampCarga = "";
    private String bostampDescarga = "";



    private ViewsBackStackManager viewsBackStackManager;
    private ArrayAdapterPdaLoads arrayAdapterPdaLoads;
    private MenuItem menuItemOptions = null;
    private String RECOVER_PDA = "Recuperar carga PDA";
    private ConstraintLayout constraintLayoutBotoes, constraintLayout_dados, constraintLayout_codigo;
    private ArrayList<DataModelBo> listPdaLoads = new ArrayList<>();



    private String ref_lida, lote_lido = "";
    private double qtd_lida = 0;
    private boolean FLAG_MODO = false, mCalledOtherActivity = false;
    private String FLAG_MODO_EDITTEXT = "OPERADOR";
    private String mtxterro  ="";
    private EditTextBarCodeReader.OnGetScannedTextListener editTextReadBarCodeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        // procedimento onde definimos a programação


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transferência");
        getSupportActionBar().setSubtitle("Carga");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);
        if(sharedpreferences.contains(BOSTAMP_CARGA)) {
            bostampCarga = sharedpreferences.getString(BOSTAMP_CARGA,"");
        }

        //initNavigationDrawer();

        editText_lercodigo   = (EditText) findViewById(R.id.editText_carga_codigo);
        textView_localizacao = (TextView) findViewById(R.id.textView_carga_localizacao);
        textView_operador    = (TextView) findViewById(R.id.textView_carga_operador);
        listView             = (ListView) findViewById(R.id.listView_carga_artigos);
        toggleButton_modo    = (ToggleButton) findViewById(R.id.toggleButton_carga_modo);


        constraintLayoutBotoes  = findViewById(R.id.constraintLayoutBotoestranf);
        constraintLayout_dados  = findViewById(R.id.constraintLayout_localizacaoTrans);
        constraintLayout_codigo = findViewById(R.id.constraintLayout_codigoTrans);

        // editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));
        // editText_lercodigo.setInputType(InputType.TYPE_NULL);

        defineEditTextReadBarCodeListener();
        editText_lercodigo.setOnClickListener(this);

        EditTextBarCodeReader editTextBarCodeReaderLerCodigo = new EditTextBarCodeReader(editText_lercodigo,this);
        editTextBarCodeReaderLerCodigo.setOnGetScannedTextListener(editTextReadBarCodeListener);
        editTextBarCodeReaderLerCodigo.setOnTouchListenerIsBlocked(true);
        editTextBarCodeReaderLerCodigo.setClearEditTextAfterRead(true);
        //TransferenciaActivity.this.GetInput("Operador","Ler código de operador");

        toggleButton_modo.setChecked(true);

        toggleButton_modo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    FLAG_MODO = false;
                    toolbar.setSubtitle("Carga");
                    System.out.println("MODO CARGA");

                } else {
                    FLAG_MODO = true;
                    // se a lista estiver vazia não deixa passar para o modo de descarga
                    if (lista.isEmpty()) {
                        toggleButton_modo.setChecked(true);
                        return;
                    }

                    toolbar.setSubtitle("Descarga");
                    System.out.println("MODO DESCARGA");

                }

                // FLAG_MODO_EDITTEXT = "OPERADOR";
                // textView_operador.setText(clear);

                textView_localizacao.setText(clear);
                System.out.println("DefineTextHint - setOnCheckedChangeListener");
                defineEditTextHint(true);

            }
        });

        // definir o que fazer quando faz long click
        toggleButton_modo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                if (textView_operador.getText().toString().trim().isEmpty()) {
                    Dialogos.dialogoErro("Dados em falta","Ler operador",2,thisActivity,false);
                    return true;
                }
                processBatchUnload();
                return true;


            }
        });


        menuItemOptions = toolbar.findViewById(R.id.menu_item_list);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onPressedBack();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewsBackStackManager = new ViewsBackStackManager();

        viewsBackStackManager.setActiveView(BackStackViews.TRANFERENCIA);
        viewsBackStackManager.addViewToBackStack(BackStackViews.TRANFERENCIA);
        Log.d("ActiveView", BackStackViews.TRANFERENCIA);
        Log.d("StackViews",viewsBackStackManager.getViewStack()+"");

        startActivity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cab, menu);

        menuItemOptions = menu.findItem(R.id.menu_item_list);

        menuItemOptions.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(TransferenciaActivity.this);
                builder.setTitle("Introduza o operador");

                final EditText input = new EditText(TransferenciaActivity.this);
                builder.setView(input);
                input.setHint("Introduza o operador");

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditTextBarCodeReader editTextBarCodeReader;
                editTextBarCodeReader  = new EditTextBarCodeReader(input,thisActivity);
//                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextBarCodeReader.setReplaceTextOnBarCodeReading(true);
                editTextBarCodeReader.setOnTouchListenerIsBlocked(true);


                editTextBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
                    @Override
                    public void onGetScannedText(final String scannedText, EditText editText)
                    {

                        final DataModelLeituraCB leituraCB = new DataModelLeituraCB(scannedText);

                        if (!  leituraCB.getTipocb().equals(DataModelLeituraCB.TIPOCB_OPERADOR))
                        {
                            showToast(TransferenciaActivity.this,"Operador não valido",2.0,20);
                            return;
                        }
                        GetPdaLoadsWs getPdaLoadsWs = new GetPdaLoadsWs(TransferenciaActivity.this);
                        getPdaLoadsWs.setOnLerAlveoloListener(new GetPdaLoadsWs.OnGetPdaLoadsListener() {
                            @Override
                            public void onSuccess(ArrayList<DataModelBo> pdaLoads)
                            {
                                alertDialog.dismiss();

                                listPdaLoads         = pdaLoads;
                                arrayAdapterPdaLoads = new ArrayAdapterPdaLoads(TransferenciaActivity.this, 0, listPdaLoads);
                                arrayAdapterPdaLoads.notifyDataSetChanged();

                                if(listPdaLoads.size() == 0)
                                {
                                    showToast(thisActivity,"Sem cargas para recuperar do operador "+ scannedText,2.0,20);
                                    return;
                                }
                                else
                                    showPdaLoads(leituraCB.getOperador());

                            }

                            @Override
                            public void onError(String error)
                            {
                                alertDialog.dismiss();
                                showToast(TransferenciaActivity.this,"Erro: "+ error,2.0,20);


                            }
                        });
                        GetPdaLoadsWs.ReqType reqType = new GetPdaLoadsWs.ReqType();

                        getPdaLoadsWs.execute(leituraCB.getOperador(), reqType.REQTYPE_TRANSF);

                    }
                });


                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuItemOptions.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    public void showPdaLoads(final int opLido)
    {
        constraintLayoutBotoes.setVisibility(View.GONE);
        constraintLayout_dados.setVisibility(View.GONE);
        constraintLayout_codigo.setVisibility(View.GONE);
        toolbar.setSubtitle(RECOVER_PDA);

        viewsBackStackManager.setActiveView(BackStackViews.RECUPERAR_CARGA_PDA);
        viewsBackStackManager.addViewToBackStack(BackStackViews.RECUPERAR_CARGA_PDA);
        Log.d("ActiveView", BackStackViews.RECUPERAR_CARGA_PDA);
        Log.d("StackViews",viewsBackStackManager.getViewStack()+"");

        listView.setAdapter(arrayAdapterPdaLoads);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l)
                {
                    if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.RECUPERAR_CARGA_PDA))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TransferenciaActivity.this);
                        builder.setTitle("Deseja recuperar a carga?");

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();

                                setBostampCarga(arrayAdapterPdaLoads.getItem(position).getBostamp().trim());

                                textView_operador.setText(String.valueOf(opLido));
                                System.out.println(viewsBackStackManager.getActiveView());
                                constraintLayoutBotoes.setVisibility(View.VISIBLE);
                                constraintLayout_dados.setVisibility(View.VISIBLE);
                                constraintLayout_codigo.setVisibility(View.VISIBLE);

                                onPressedBack();

                            }
                        });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        });


                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });



    }


    private void processBatchUnload() {


        final DataModelAlv alveoloDestino = new DataModelAlv();;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActivity);
        alertDialog.setTitle("Processar descarga em lote");
        alertDialog.setMessage("Ler localização de descarga");

        final EditText inputPedeLocConsumo = new EditText(thisActivity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputPedeLocConsumo.setLayoutParams(lp);

        final EditTextBarCodeReader barCodeReaderInputPedeLocConsumo;
        barCodeReaderInputPedeLocConsumo  = new EditTextBarCodeReader(inputPedeLocConsumo,thisActivity);
        barCodeReaderInputPedeLocConsumo.setReplaceTextOnBarCodeReading(true);
        barCodeReaderInputPedeLocConsumo.setOnTouchListenerIsBlocked(true);

        barCodeReaderInputPedeLocConsumo.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {


                if (! alveoloDestino.loadAlvFromLocalization(scannedText)) {
                    barCodeReaderInputPedeLocConsumo.setText("");
                    showToast(thisActivity,"A localização lida é inválida",2.0,20);
                    return;
                }

                barCodeReaderInputPedeLocConsumo.setText(alveoloDestino.getFormattedLoc());


                // vamos ver se a localização de descarga é igual a qualquer uma das localizações de carga

                for (DataModelBi carga: lista) {

                    if ( Integer.valueOf(carga.getArmazem())  == alveoloDestino.getArmazem()
                            && carga.getZona1().equals(alveoloDestino.getZona())
                    && carga.getAlveolo1().equals(alveoloDestino.getIdentificacao()) ) {

                        barCodeReaderInputPedeLocConsumo.setText("");
                        Dialogos.dialogoErro("Validação de dados","Existem linhas em que a localização de carga é a mesma da descarga: "+alveoloDestino.getFormattedLoc(),
                                2,thisActivity,false);
                        return;

                    }
                }


                LerAlveoloWs lerAlveoloWs = new LerAlveoloWs(thisActivity);
                lerAlveoloWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
                    @Override
                    public void onSuccess(DataModelAlv dmAlveolo) {

                        if (dmAlveolo == null) {
                            barCodeReaderInputPedeLocConsumo.setText("");
                            Dialogos.dialogoErro("Validação de dados","Não encontrei no sistema a localização "+dmAlveolo.getFormattedLoc(),
                                    2,thisActivity,false);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {


                    }
                });

            }
        });


        alertDialog.setView(inputPedeLocConsumo);


        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (inputPedeLocConsumo.getText().toString().isEmpty()) return;

                        BatchUnloadWs batchUnloadWs = new BatchUnloadWs(thisActivity);
                        batchUnloadWs.setOnBatchUnloadListener(new BatchUnloadWs.OnBatchUnloadListener() {
                            @Override
                            public void onSuccess(DataModelWsResponse wsResponse) {

                                if (wsResponse.getCodigo() == 0) {

                                    if (!lista.isEmpty()) {
                                        lista.clear();
                                        adapter.notifyDataSetChanged();
                                    }

                                    lerLinhasCarga();

                                }
                                else {
                                    Dialogos.dialogoErro("Erro ao processar descarga",wsResponse.getDescricao(),5,thisActivity,false);
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {

                                Dialogos.dialogoErro("Erro ao processar descarga",error.getMessage(),5,thisActivity,false);

                            }
                        });
                        batchUnloadWs.execute(bostampCarga, Integer.parseInt(textView_operador.getText().toString())
                                ,alveoloDestino.getArmazem(),alveoloDestino.getZona(),alveoloDestino.getIdentificacao());

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        final AlertDialog dialog = alertDialog.create();

        dialog.show();

    }


    public void requestFocus(View view) {

    }


    public void onBackPressed() {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'


        if(! bostampCarga.equals("")){


            DeleteBoWs deleteBoWs = new DeleteBoWs(thisActivity);
            deleteBoWs.setOnDeleteBoListener(new DeleteBoWs.OnDeleteBoListener() {
                @Override
                public void onSuccess(DataModelWsResponse wsResponse) {

                    finish();
                }

                @Override
                public void onError(VolleyError error) {
                    finish();
                }
            });

            deleteBoWs.execute(bostampCarga,true);

        }

        else finish();

        // finish();

    }

    @Override
    public boolean onSupportNavigateUp() {


        onBackPressed();
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

        System.out.println("On resume");
        System.out.println("Called from other activity: "+mCalledOtherActivity);

        if (!mCalledOtherActivity)
            return;
        else
            mCalledOtherActivity = false;


        if (!lista.isEmpty()) {
            lista.clear();
            adapter.notifyDataSetChanged();
        }

        lerLinhasCarga();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (!lista.isEmpty()) {

            lista.clear();
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.editText_carga_codigo) {

            if (FLAG_MODO_EDITTEXT.equals("LOTE")) {
                FLAG_MODO_EDITTEXT = "CODIGO";
            }

            if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {
                FLAG_MODO_EDITTEXT = "CODIGO";
            }

            System.out.println("DefineTextHint - onClick");
            defineEditTextHint(true);
            requestFocus(editText_lercodigo);
        }

    }

    private void dialogoErro(String title, String subtitle) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(TransferenciaActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(false);

        builder1.setView(image);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();


        new CountDownTimer(2000, 500) {

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

    private void dialogoInfo(String title, String subtitle) {

        ImageView image_info = new ImageView(getApplication());

        image_info.setImageResource(R.mipmap.ic_info);


        AlertDialog.Builder builderInfo = new AlertDialog.Builder(TransferenciaActivity.this);
        builderInfo.setTitle(title);
        builderInfo.setMessage(subtitle);
        builderInfo.setCancelable(false);

        builderInfo.setView(image_info);

        final AlertDialog alertInfo = builderInfo.create();
        alertInfo.show();

        new CountDownTimer(1000, 500) {

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

/*    private void GetInput(String Titulo, String Subtitulo) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TransferenciaActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(Titulo);

        // Setting Dialog Message
        alertDialog.setMessage(Subtitulo);
        input = new EditText(TransferenciaActivity.this);
        input.generateViewId();


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        input.addTextChangedListener(new MyTextWatcher(input));
        alertDialog.setCancelable(false);
        dialog = alertDialog.create();
        dialog.create();

        dialog.show();

    }*/

    /*private class MyTextWatcher implements TextWatcher {

        private View view;

        private String codigos2[];
        private String codigos1[];
        private String prefixo = "";

        private MyTextWatcher(View view) {

            this.view = view;
        }


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            System.out.println("OnTextChanged");

            switch (view.getId()) {

                case R.id.editText_carga_codigo:

                    System.out.println(FLAG_MODO_EDITTEXT);
                    if (editText_lercodigo.getText().toString().trim().isEmpty()) {
                        System.out.println(" On Text Changed: EditText_Lercodigo está vazio. Vou sair");
                        return;
                    }
                    System.out.println("A editext não esta vazia");
                    // Se o flag estiver OPERADOR pede o operador
                    if (FLAG_MODO_EDITTEXT.equals("OPERADOR")) {

                        if (validaLeituraOperador(editText_lercodigo.getText().toString())) {
                            // cria o dossier de carga se flag_modo false
                            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
                            if (!FLAG_MODO) {
                                if (bostampcarga.isEmpty())
                                    criaBO(6, 137, 0);
                            } else {

                                criaBO(5, 137, 0);
                            }
                        }

                        System.out.println("DefineTextHint - quando modo = operador");
                        defineEditTextHint(true);
                        return;
                    }
                    // se a flag for a localização
                    if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {

                        if (!validaLeituraLocalizacao(editText_lercodigo.getText().toString())) {
                            dialogoErro("Erro", "A localização lida" + editText_lercodigo.getText().toString() + "não é válida");
                            editText_lercodigo.setText(clear);
                        } else {

                            // se esta localização pode ser lida - Valida se é admissivel ler esta localização neste contexto . Tb verifica se ela existe
                            locationCanBeRead();
                        }

                        return;
                    }

                    // Se flag de leitura for o lote
                    if (FLAG_MODO_EDITTEXT.equals("LOTE")) {

                        validaLeituraLote(editText_lercodigo.getText().toString());
                        return;
                    }
                    // Se flag de leitura for a quantidade
                    if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {

                        String txt_qtd_lida = editText_lercodigo.getText().toString();
                        Integer val;
                        try {
                            val = Integer.valueOf(txt_qtd_lida);
                            if (val == null) {
                                dialogoErro("Erro", "A quantidade lida" + editText_lercodigo.getText().toString() + "não é válida");
                                defineEditTextHint(false);
                                return;
                            }

                        } catch (NumberFormatException e) {
                            dialogoErro("Erro", "A quantidade lida" + editText_lercodigo.getText().toString() + "não é válida");
                            defineEditTextHint(false);
                            return;
                        }

                        qtd_lida = val;
                        System.out.println("Quantidade lida: "+qtd_lida);

                        if (!FLAG_MODO) processaLinhaCarga();
                        else processaLinhaDescarga();
                        return;
                    }

                    // Se a flag de leitura for o código então pode ler o código ou a localização

                    if (FLAG_MODO_EDITTEXT.equals("CODIGO"))
                        System.out.println("OnTextChange: FLAG = CODIGO");
                    // se der uma validação de localização então carrega a localização e mantem a flag no código
                    if (validaLeituraLocalizacao(editText_lercodigo.getText().toString())) {

                        // System.out.println("defineTextHint - Leitura localização OK");
                        // defineEditTextHint(true);

                        locationCanBeRead();

                        return;
                    } else {

                        *//* Se estiver em modo "CODIGO" primeiro vamos validar se o que foi lido foi uma string começada por R, ou seja,
                        um código de barras que contem a referencia+lote+quantidade*//*

                        String retval = validaLeituraCodigo(editText_lercodigo.getText().toString());


                        if (retval.equals("OK")) {
                            System.out.println("OnTextChange: FLAG = CODIGO: CODIGO OK");
                            if (!FLAG_MODO) processaLinhaCarga();
                            else processaLinhaDescarga();
                            return;
                        }

                        if (retval.equals("VALIDA_REF")) {
                            System.out.println("OnTextChange: FLAG = CODIGO, Valida a referencia");
                            validaLeituraRef(editText_lercodigo.getText().toString());

                        } else {
                            dialogoErro("Erro", retval);
                            editText_lercodigo.setText(clear);

                        }
                    }

            }
        }


        public void afterTextChanged(Editable editable) {

        }
    }*/


    private boolean validaLeituraOperador(String textoLido) {
            /* Esta função valida a leitura do operador. Se tudo estiver ok então carrega os dados do operador
                Senão, devolve falso
            */
        String opPrefixp = "";
        String opNumero = "";

        if (textoLido.length() < 5) {
            dialogoErro("Erro", "Operador não é válido");
            return false;
        }

        opPrefixp = textoLido.substring(0, 4);

        if (!opPrefixp.equals("(OP)")) {
            dialogoErro("Erro", "Operador não é válido");
            return false;
        } else {
            // Vamos buscar o operador
            opNumero = textoLido.substring(4);
                /* Se a substring operador for numerica e inteira então vamos carregar os dados do operador
                   Senão vamos devolver um erro
                */
            try {
                Integer val = Integer.valueOf(opNumero);
                if (val != null) {
                    textView_operador.setText(opNumero);
                    return true;
                } else
                    return false;
            } catch (NumberFormatException e) {
                dialogoErro("Erro", "Operador não é válido");
                return false;
            }

        }

    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        // System.out.println("Execucao defineEditTextHint");

        // editText_lercodigo.setText(clear);

        // Quando o campo operador estiver vazio obriga a que seja lido o operador
        if (textView_operador.getText().toString().isEmpty()) {

            editText_lercodigo.setHint("Ler operador");
            textView_localizacao.setText("");
            armazem = "";
            zona = "";
            alveolo = "";

            FLAG_MODO_EDITTEXT = "OPERADOR";
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            if (mostraDialogoInfo) dialogoInfo("Acção", "Ler operador");
            return;
        }
        // Quando o campo localização estiver vazio obriga a ler a localização

        if (textView_localizacao.getText().toString().isEmpty()) {

            if (!FLAG_MODO) {
                editText_lercodigo.setHint("Ler localização de CARGA");
                if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de CARGA");
                System.out.println("Mensagem ler carga");
            } else {
                editText_lercodigo.setHint("Ler localização de DESCARGA");
                if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de DESCARGA");
            }

            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            return;
        } else {
            switch (FLAG_MODO_EDITTEXT) {
                case "LOCALIZACAO":

                    if (!FLAG_MODO) {
                        editText_lercodigo.setHint("Ler localização de CARGA");
                        if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de CARGA");
                    } else {
                        editText_lercodigo.setHint("Ler localização de DESCARGA");
                        if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de DESCARGA");
                    }

                    textView_localizacao.setText("");
                    armazem = "";
                    zona = "";
                    alveolo = "";
                    ref_lida = "";
                    lote_lido = "";
                    qtd_lida = 0;
                    break;
                case "OPERADOR":
                    editText_lercodigo.setHint("Ler operador");
                    if (mostraDialogoInfo) dialogoInfo("Acção", "Ler Operador");
                    ref_lida = "";
                    lote_lido = "";
                    qtd_lida = 0;
                    break;
                case "CODIGO":
                    editText_lercodigo.setHint("Ler codigo");
                    if (mostraDialogoInfo) dialogoInfo("Acção", "Ler código");
                    ref_lida = "";
                    lote_lido = "";
                    qtd_lida = 0;
                    break;
                case "LOTE":
                    lote_lido = "";
                    qtd_lida = 0;
                    editText_lercodigo.setHint("Ler lote (Ref:" + ref_lida + ")");
                    if (mostraDialogoInfo) dialogoInfo("Acção", "Ler lote");
                    break;
                case "QUANTIDADE":
                    qtd_lida = 0;
                    if (lote_lido.equals("")) {
                        editText_lercodigo.setHint("Ler Qtd  (" + ref_lida + ") ");
                    }
                    else {
                        editText_lercodigo.setHint("Ler Qtd  (" + ref_lida + ") / (" + lote_lido + ")");
                    }
                    if (mostraDialogoInfo) dialogoInfo("Acção", "Ler quantidade");
                    break;
                default:
                    ref_lida = "";
                    lote_lido = "";
                    qtd_lida = 0;
                    editText_lercodigo.setHint("");
                    break;
            }


        }
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

                textView_localizacao.setText(armazem_lido);
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

    private String validaLeituraCodigo(String textoLido) {

        // Este procedimento tem como objectivo validar se a string recebida é uma string que contem a referencia + lote + quantidade
        // A string deste tipo tem de começar por (R). As tags lote e quantidade têm de estar presentes mas podem ter ordem trocada

        System.out.println("Valida Codigo: TextoLido:" + textoLido);

        String tmp_ref_lida, tmp_lote_lido, tmp_qtd_lida = "";
        Integer casas_dec_qtd = 0;

        Integer posicao_tag_lote, posicao_tag_qtd = 0;
        //Vamos verficar se o texto lido começa por (R)

        if (textoLido.length() < 3){
            return "VALIDA_REF";
        }

        if (textoLido.substring(0, 3).equals("(R)")) {
            System.out.println("Codigo Começa por R");
            posicao_tag_lote = textoLido.indexOf("(S)");
            if (posicao_tag_lote == -1) {
                System.out.println("Erro no código; Não encontrei a tag do lote");
                return "Erro no código; Não encontrei a tag do lote";
            }

            // vamos verificar se existe alguma posicao numerica

            for (int i = 0; i < 10; ++i) {
                if (posicao_tag_qtd == 0) {
                    posicao_tag_qtd = textoLido.indexOf("(Q" + String.valueOf(i) + ")");
                    if (posicao_tag_qtd != 0) casas_dec_qtd = i;
                }
            }

            if (posicao_tag_qtd == -1) {
                System.out.println("Erro no código; Não encontrei a tag da Qtd");
                return "Erro no código; Não encontrei a tag da Qtd";
            }


            if (posicao_tag_qtd < posicao_tag_lote) {

                ref_lida = textoLido.substring(3, posicao_tag_qtd);
                qtd_lida = Double.parseDouble(textoLido.substring(posicao_tag_qtd + 4, posicao_tag_lote));
                lote_lido = textoLido.substring(posicao_tag_lote + 3);
            } else {
                ref_lida = textoLido.substring(3, posicao_tag_lote);
                lote_lido = textoLido.substring(posicao_tag_lote + 3, posicao_tag_qtd);
                qtd_lida = Double.parseDouble(textoLido.substring(posicao_tag_qtd + 4, textoLido.length()));
            }

            System.out.println("Ref lida" + ref_lida);
            System.out.println("Lote Lido" + lote_lido);
            System.out.println("Qtd Lida" + qtd_lida);

            if (qtd_lida <= 0) {
                return "A quantidade lida " + qtd_lida + " não é um valor válido";

            }
            System.out.println("Valida Codigo: return OK");
            return "OK";
        } else {
            // Se o código não começa por (R) vamos assumir que lemos a referencia e verificar se a referencia existe

            return "VALIDA_REF";
        }

        // se o código não começa por R então vamos assumir que lemos a referencia do artigo

    }


    private void validaLeituraLote(String textoLido) {

        lote_lido = textoLido;

        GetSeWs getSeWs = new GetSeWs(thisActivity);
        getSeWs.setOnGetSeListener(new GetSeWs.OnGetSeListener() {
            @Override
            public void onSuccess(DataModelSe se) {

                if (se != null) {

                    qtd_lida = 0;

                    if (!FLAG_MODO) processaLinhaCarga();
                    else processaLinhaDescarga();
                    return;

                }
                else {
                    dialogoErro("Erro", "O lote " + lote_lido + " não existe");
                    defineEditTextHint(false);
                    lote_lido = "";
                }

            }

            @Override
            public void onError(VolleyError error) {
                dialogoErro("Erro", error.getMessage());
                textView_operador.setText("");
                defineEditTextHint(false);
            }
        });
        getSeWs.execute(ref_lida,lote_lido);


    }

    private void validaLeituraRef(final String textoLido) {

        //Valida se a ref existe na base de dados

        GetStWs getStWs = new GetStWs(thisActivity);
        getStWs.setOnLerStListener(new GetStWs.OnLerStListener() {
            @Override
            public void onSuccess(ArrayList<DataModelST> arraySt) {

                if (arraySt.size() == 0) {

                    Dialogos.dialogoErro("Erro","O artigo" + textoLido + "não existe",3,thisActivity,false);
                    // dialogoErro("Erro", "O artigo" + editText_lercodigo.getText().toString() + "não existe");
                    FLAG_MODO_EDITTEXT = "CODIGO";
                    defineEditTextHint(false);

                }
                else {
                    ref_lida = arraySt.get(0).getRef();

                    if(arraySt.get(0).getUsalote())FLAG_MODO_EDITTEXT = "LOTE";
                    else  FLAG_MODO_EDITTEXT = "QUANTIDADE";

                    defineEditTextHint(true);

                }

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
        getStWs.execute(textoLido);


    }

    private void processaLinhaCarga() {


        if (bostampCarga.isEmpty()) {

            DataModelBo newBo = new DataModelBo();

            newBo.setNo(137);
            newBo.setNdos(6);
            newBo.setEstab(0);
            Integer mOperador =  Integer.valueOf(textView_operador.getText().toString());
            newBo.setU_operador(mOperador);

            CreateBoWs createBoWs = new CreateBoWs(thisActivity);
            createBoWs.setOnCreateListener(new CreateBoWs.OnCreateBoListener() {
                @Override
                public void onSuccess(DataModelWsResponse wsResponse) {


                    if(wsResponse.getCodigo() == 0) {

                        setBostampCarga(wsResponse.getDescricao());
                        lerStockProcessaCarga();

                    }
                    else {

                        textView_operador.setText("");
                        textView_localizacao.setText("");
                        defineEditTextHint(false);

                        Dialogos.dialogoErro("Erro na criação de cabeçalho de carga",wsResponse.getDescricao(),5,thisActivity,false);
                    }

                }

                @Override
                public void onError(VolleyError error) {

                }
            });
            createBoWs.execute(newBo);

        }
        else {
            lerStockProcessaCarga();
        }


    }

    private void processaLinhaDescarga() {



        if (bostampDescarga.isEmpty()) {


            DataModelBo newBo = new DataModelBo();

            newBo.setNo(137);
            newBo.setNdos(5);
            newBo.setEstab(0);
            Integer mOperador =  Integer.valueOf(textView_operador.getText().toString());
            newBo.setU_operador(mOperador);

            CreateBoWs createBoWs = new CreateBoWs(thisActivity);
            createBoWs.setOnCreateListener(new CreateBoWs.OnCreateBoListener() {
                @Override
                public void onSuccess(DataModelWsResponse wsResponse) {

                    if(wsResponse.getCodigo() == 0) {
                        bostampDescarga = wsResponse.getDescricao();
                        lerStockProcessaDescarga();
                    }
                    else {
                        textView_operador.setText("");
                        textView_localizacao.setText("");
                        defineEditTextHint(false);
                        Dialogos.dialogoErro("Erro na criação de cabeçalho de carga",wsResponse.getDescricao(),5,thisActivity,false);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                }
            });
            createBoWs.execute(newBo);


        }
        else {

            lerStockProcessaDescarga();

        }
    }


    private void locationCanBeRead() {

        String tmpActivity;

        if (!FLAG_MODO)
            tmpActivity = "transferencia_carga";
        else
            tmpActivity = "transferencia_descarga";

        AsyncRequest processaPedido = new AsyncRequest(thisActivity, 11);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LocationCanBeRead?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .appendQueryParameter("activity",tmpActivity)
                .build();

        processaPedido.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
            @Override
            public void asyncResponse(String response, int op) {


                response = response.substring(1,response.length()-1);

                if ( ! response.equals("OK")) {

                    String txtErro ="Erro não definido ";
                    if (response.substring(0,3).equals("ERR"))
                        txtErro = response.substring(4,response.length());


                    textView_localizacao.setText("");
                    armazem = "";
                    zona = "";
                    alveolo = "";
                    defineEditTextHint(true);
                    Dialogos.dialogoErro("Leitura de localização",txtErro,5,thisActivity,false);

                }

                else {

                    FLAG_MODO_EDITTEXT = "CODIGO";
                    System.out.println("DefineTextHint - quando modo = operador");
                    defineEditTextHint(true);
                }

            }
        });

        processaPedido.execute(builtURI_processaPedido.toString());
    }

    private void aSyncResponseLocationCanBeRead(String response, int op) {

        response = response.substring(1,response.length()-1);

        if ( ! response.equals("OK")) {

            String txtErro ="Erro não definido ";
            if (response.substring(0,3).equals("ERR"))
                txtErro = response.substring(4,response.length());

            textView_localizacao.setText("");
            armazem = "";
            zona = "";
            alveolo = "";
            defineEditTextHint(true);
            Dialogos.dialogoErro("Leitura de localização",txtErro,5,thisActivity,false);

        }

        else {

            FLAG_MODO_EDITTEXT = "CODIGO";
            System.out.println("DefineTextHint - quando modo = operador");
            defineEditTextHint(true);
        }

    }

    private void defineEditTextReadBarCodeListener() {

        editTextReadBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {


                System.out.println(FLAG_MODO_EDITTEXT);

                if (scannedText.isEmpty()) {
                    return;
                }

                // Se o flag estiver OPERADOR pede o operador
                if (FLAG_MODO_EDITTEXT.equals("OPERADOR")) {

                    if (validaLeituraOperador(scannedText)) {
                        // cria o dossier de carga se flag_modo false
                        FLAG_MODO_EDITTEXT = "LOCALIZACAO";
                        defineEditTextHint(true);

                    }

                    return;
                }
                // se a flag for a localização
                if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {

                    if (!validaLeituraLocalizacao(scannedText)) {
                        dialogoErro("Erro", "A localização lida" + scannedText + "não é válida");
                    } else {

                        // se esta localização pode ser lida - Valida se é admissivel ler esta localização neste contexto . Tb verifica se ela existe
                        locationCanBeRead();
                    }

                    return;
                }

                // Se flag de leitura for o lote
                if (FLAG_MODO_EDITTEXT.equals("LOTE")) {

                    validaLeituraLote(scannedText);
                    return;
                }
                // Se flag de leitura for a quantidade
                if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {

                    String txt_qtd_lida = scannedText;
                    Integer val;
                    try {
                        val = Integer.valueOf(txt_qtd_lida);
                        if (val == null) {
                            dialogoErro("Erro", "A quantidade lida" + scannedText + "não é válida");
                            defineEditTextHint(false);
                            return;
                        }

                    } catch (NumberFormatException e) {
                        dialogoErro("Erro", "A quantidade lida" + scannedText + "não é válida");
                        defineEditTextHint(false);
                        return;
                    }

                    qtd_lida = val;
                    System.out.println("Quantidade lida: "+qtd_lida);

                    if (!FLAG_MODO) processaLinhaCarga();
                    else processaLinhaDescarga();
                    return;
                }

                // Se a flag de leitura for o código então pode ler o código ou a localização

                if (FLAG_MODO_EDITTEXT.equals("CODIGO"))
                    System.out.println("OnTextChange: FLAG = CODIGO");
                // se der uma validação de localização então carrega a localização e mantem a flag no código
                if (validaLeituraLocalizacao(scannedText)) {

                    // System.out.println("defineTextHint - Leitura localização OK");
                    // defineEditTextHint(true);

                    locationCanBeRead();

                    return;
                } else {

                        /* Se estiver em modo "CODIGO" primeiro vamos validar se o que foi lido foi uma string começada por R, ou seja,
                        um código de barras que contem a referencia+lote+quantidade*/

                    String retval = validaLeituraCodigo(scannedText);


                    if (retval.equals("OK")) {
                        System.out.println("OnTextChange: FLAG = CODIGO: CODIGO OK");
                        if (!FLAG_MODO) processaLinhaCarga();
                        else processaLinhaDescarga();
                        return;
                    }

                    if (retval.equals("VALIDA_REF")) {
                        System.out.println("OnTextChange: FLAG = CODIGO, Valida a referencia");
                        validaLeituraRef(scannedText);

                    } else {
                        dialogoErro("Erro", retval);

                    }
                }

            }
        };

    }

    private  void startActivity() {

        constraintLayoutBotoes.setVisibility(View.VISIBLE);
        constraintLayout_dados.setVisibility(View.VISIBLE);
        constraintLayout_codigo.setVisibility(View.VISIBLE);

        if(arrayAdapterPdaLoads != null)
        {
            arrayAdapterPdaLoads.clear();
            arrayAdapterPdaLoads.notifyDataSetChanged();
            listView.setAdapter(arrayAdapterPdaLoads);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                if (!FLAG_MODO) {

                    bundle.clear();

                    if (!FLAG_MODO) bundle.putString("trf_mode","carga");
                    else bundle.putString("trf_mode","descarga");
                    bundle.putBoolean("valida_stock",true);

                    bundle.putBoolean("editing_listview",true);
                    bundle.putString("ref", adapter.getItem(position).getRef());
                    bundle.putString("lote", adapter.getItem(position).getLote());
                    bundle.putString("armazem_ori", String.valueOf(adapter.getItem(position).getArmazem()));
                    bundle.putDouble("qtd_lida", adapter.getItem(position).getQtt());
                    bundle.putDouble("qtdAlt_lida", adapter.getItem(position).getUni2qtt());
                    bundle.putString("unidade", adapter.getItem(position).getUnidade());
                    bundle.putString("unidadeAlt", adapter.getItem(position).getUnidad2());
                    bundle.putString("zona_ori", adapter.getItem(position).getZona1());
                    bundle.putString("alveolo_ori", adapter.getItem(position).getAlveolo1());

                    bundle.putString("bistamp_carga", adapter.getItem(position).getBistamp());

                    bundle.putString("atividade","transferencia");


                    System.out.println("Bundle - Qtd lida :"+adapter.getItem(position).getQtt());
                    System.out.println("Bundle - Qtd Alt lida:"+adapter.getItem(position).getUni2qtt());



                    Intent EditIntent = new Intent(TransferenciaActivity.this, EditActivity.class);
                    EditIntent.putExtras(bundle);
                    mCalledOtherActivity = true;
                    startActivity(EditIntent);

                }
                else {

                    if(textView_operador.getText().toString().isEmpty()) {
                        Dialogos.dialogoErro("Dados em falta","Ler operador",2,thisActivity,false);
                        return;
                    }

                    if(textView_localizacao.getText().toString().isEmpty()) {
                        Dialogos.dialogoErro("Dados em falta","Ler localização",2,thisActivity,false);
                        return;
                    }

                    ref_lida = adapter.getItem(position).getRef();
                    lote_lido = adapter.getItem(position).getLote();
                    processaLinhaDescarga();
                }

            }
        });

        textView_localizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_localizacao.setText("");
                defineEditTextHint(true);

            }
        });
        // vamos verificar se o stamp de carga existe

        GetBoWs getBoWs = new GetBoWs(thisActivity);
        getBoWs.setOnGetBoListener(new GetBoWs.OnGetBoListener() {
            @Override
            public void onSuccess(DataModelBo bo) {

                if (bo == null) {
                    setBostampCarga("");
                }
                else {
                    lerLinhasCarga();
                }


                defineEditTextHint(true);
            }

            @Override
            public void onError(VolleyError error) {
                setBostampCarga("");
                defineEditTextHint(true);

            }
        });

        getBoWs.execute(bostampCarga);

    }

    private  void lerStockProcessaCarga() {

        //*******************************************
        AsyncRequest LerStockProcessaCarga = new AsyncRequest(TransferenciaActivity.this, 8);

        Uri builtURI_LerStockProcessaCarga = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerStock?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_REF, ref_lida)
                .appendQueryParameter(QUERY_PARAM_LOTE, lote_lido)
                .appendQueryParameter(QUERY_PARAM_ARMAZEM, armazem)
                .appendQueryParameter(QUERY_PARAM_ZONA, zona)
                .appendQueryParameter(QUERY_PARAM_ALVEOLO, alveolo)
                .build();

        LerStockProcessaCarga.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
            @Override
            public void asyncResponse(String response, int op) {

                if (response == null) {
                    dialogoErro("Erro", "Não foi possivel obter informação da base de dados");
                    // editText_lercodigo.setText(clear);
                    return;
                }

                mtxterro = "Não existe stock disponivel de ["+ref_lida.trim()+"]";
                if (lote_lido.length() > 0){
                    mtxterro = mtxterro+" ["+lote_lido.trim()+"]";
                }
                mtxterro = mtxterro+" na localização ["+armazem+"]";
                if (zona.length() > 0 || alveolo.length()>0){

                    mtxterro = mtxterro+"["+zona.trim()+"]["+alveolo.trim()+"]";
                }

                // Se a resposta for vazia então não existe stock disponivel
                if (response.equals(vazio)) {

                    dialogoErro("Erro", mtxterro);

                    Dialogos.dialogoErro("Erro", mtxterro,5,TransferenciaActivity.this,false);
                    FLAG_MODO_EDITTEXT = "CODIGO";
                    defineEditTextHint(false);
                    return;

                }

                // Se chegamos aqui quer dizer que existe stock na localização seleccionada, mesmo não sendo suficiente em relação à que foi lida

                try {

                    JSONconverter jsonConverter = new JSONconverter();
                    response = jsonConverter.ConvertJSON(response);

                    JSONArray values = new JSONArray(response);


                    for (int i = 0; i < values.length(); i++) {

                        JSONObject c = values.getJSONObject(i);

                        String j_referencia = c.getString("ref");
                        String j_lote = c.getString("lote");
                        String j_design = c.getString("design");
                        String j_stock = c.getString("stock");
                        String j_unidade = c.getString("unidade");
                        String j_fconversao = c.getString("fconversao");
                        String j_unidadeAlt = c.getString("uni2");
                        String j_stockAlt = c.getString("stock2");

                        System.out.println("Stock lido :" + j_stock);
                        System.out.println("Stock Alt lido:" + j_stockAlt);

                        if (Double.valueOf(j_stock) <= 0) {
                            Dialogos.dialogoErro("Erro", mtxterro,5,TransferenciaActivity.this,false);
                            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
                            defineEditTextHint(false);
                            return;
                        }

                        bundle.clear();

                        if (!FLAG_MODO) bundle.putString("trf_mode","carga");
                        else bundle.putString("trf_mode","descarga");;
                        // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
                        bundle.putBoolean("editing_listview",false);
                        bundle.putBoolean("valida_stock",true);

                        bundle.putString("bostamp_carga", bostampCarga);
                        bundle.putString("ref", j_referencia);
                        bundle.putString("lote", j_lote);
                        bundle.putString("design", j_design);
                        bundle.putDouble("stock", Double.parseDouble(j_stock));
                        bundle.putDouble("stockAlt", Double.parseDouble(j_stockAlt));
                        bundle.putDouble("fconversao", Double.parseDouble(j_fconversao));


                        bundle.putDouble("qtd_lida", qtd_lida);
                        bundle.putDouble("qtdAlt_lida", (qtd_lida / Double.parseDouble(j_fconversao)) * 1.000);
                        bundle.putString("unidade", j_unidade);
                        bundle.putString("unidadeAlt", j_unidadeAlt);
                        bundle.putString("user", textView_operador.getText().toString());

                        bundle.putString("armazem_ori", armazem.trim());
                        bundle.putString("zona_ori", zona);
                        bundle.putString("alveolo_ori", alveolo);
                        bundle.putString("bistamp_carga","");

                        bundle.putString("armazem_dest", armazem_pda);
                        bundle.putString("zona_dest", zona_pda);
                        bundle.putString("alveolo_dest", alveolo_pda);


                        bundle.putString("atividade","transferencia");

                    }


                    FLAG_MODO_EDITTEXT = "LOCALIZACAO";


                    Intent EditIntent = new Intent(TransferenciaActivity.this, EditActivity.class);
                    EditIntent.putExtras(bundle);
                    mCalledOtherActivity = true;
                    startActivity(EditIntent);

                } catch (final JSONException e) {

                    Log.e("", "Erro Conversão: " + e.getMessage());

                }

            }
        });
        LerStockProcessaCarga.execute(builtURI_LerStockProcessaCarga.toString());
        //*******************************************

    }

    private void lerStockProcessaDescarga() {


        AsyncRequest lerStockDescarga = new AsyncRequest(TransferenciaActivity.this, 99);

        Uri builtURI_lerStockDescarga = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerStockDescarga?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_BOSTAMP, bostampCarga)
                .appendQueryParameter(QUERY_PARAM_REF, ref_lida)
                .appendQueryParameter(QUERY_PARAM_LOTE, lote_lido)
                .build();

        lerStockDescarga.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
            @Override
            public void asyncResponse(String response, int op) {

                if (response == null) {
                    dialogoErro("Erro", "Não foi possivel obter informação da base de dados");
                    // editText_lercodigo.setText(clear);
                    return;
                }

                if (response.equals(vazio)) {
                    dialogoErro("Erro", "Nao existe stock disponivel para descarga");
                    FLAG_MODO_EDITTEXT = "CODIGO";
                    defineEditTextHint(false);

                } else {

                    try {

                        JSONconverter jsonConverter = new JSONconverter();
                        response = jsonConverter.ConvertJSON(response);

                        JSONArray values = new JSONArray(response);


                        for (int i = 0; i < values.length(); i++) {

                            JSONObject c = values.getJSONObject(i);

                            String j_referencia = c.getString("ref");
                            String j_lote = c.getString("lote");
                            String j_design = c.getString("design");
                            String j_stockCarga = c.getString("stockcarga");
                            String j_unidade = c.getString("unidade");
                            String j_fconversao = c.getString("fconversao");
                            String j_unidadeAlt = c.getString("uni2");
                            String j_stockCargaAlt = c.getString("stockcarga2");

                            bundle.clear();


                            if (!FLAG_MODO) bundle.putString("trf_mode","carga");
                            else bundle.putString("trf_mode","descarga");
                            bundle.putBoolean("valida_stock",true);

                            bundle.putString("bostamp_carga", bostampCarga);
                            bundle.putString("bostamp_descarga", bostampDescarga);
                            bundle.putString("ref", j_referencia);
                            bundle.putString("lote", j_lote);
                            bundle.putString("design", j_design);
                            bundle.putDouble("stock", Double.parseDouble(j_stockCarga));
                            bundle.putDouble("stockAlt", Double.parseDouble(j_stockCargaAlt));
                            bundle.putDouble("fconversao", Double.parseDouble(j_fconversao));


                            bundle.putDouble("qtd_lida", qtd_lida);
                            bundle.putDouble("qtdAlt_lida", (qtd_lida / Double.parseDouble(j_fconversao)) * 1.000);
                            bundle.putString("unidade", j_unidade);
                            bundle.putString("unidadeAlt",j_unidadeAlt);
                            bundle.putString("user", textView_operador.getText().toString());


                            bundle.putString("armazem_ori", "99");
                            bundle.putString("zona_ori",zona_pda);
                            bundle.putString("alveolo_ori", alveolo_pda);

                            bundle.putString("armazem_dest", String.valueOf(armazem));
                            bundle.putString("zona_dest",zona);
                            bundle.putString("alveolo_dest", alveolo);

                            bundle.putString("atividade","transferencia");

                            bundle.putString("bistamp_carga","");

                        }

                        // Limpa localização
                        System.out.println("defineTextHint - Processa linha de descarga");
                        FLAG_MODO_EDITTEXT = "LOCALIZACAO";


                        Intent EditIntent = new Intent(TransferenciaActivity.this, EditActivity.class);
                        EditIntent.putExtras(bundle);
                        mCalledOtherActivity = true;
                        startActivity(EditIntent);


                    } catch (final JSONException e) {

                        Log.e("", "Erro Conversão: " + e.getMessage());

                    }
                }

            }
        });


        lerStockDescarga.execute(builtURI_lerStockDescarga.toString());

    }

    private void lerLinhasCarga() {

        GetBiWs getBiWs = new GetBiWs(thisActivity);
        getBiWs.setOnGetBiListener(new GetBiWs.OnGetBiListener() {
            @Override
            public void onSuccess(ArrayList<DataModelBi> arrayBi) {


                lista = arrayBi;

                adapter = new DataModelArrayAdapterCarga(thisActivity, 0,lista);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                if (lista.size() == 0) {

                    if (FLAG_MODO) {
                        FLAG_MODO = false;
                        toggleButton_modo.setChecked(true);
                        bostampDescarga = "";
                    }

                    textView_operador.setText("");

                    setBostampCarga("");

                }

                textView_localizacao.setText(clear);
                defineEditTextHint(true);

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        getBiWs.execute(bostampCarga,"");
    }

    private void setBostampCarga(String stamp) {

        bostampCarga = stamp;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(BOSTAMP_CARGA, bostampCarga);
        editor.apply();

    }

    public void onPressedBack()
    {
        viewsBackStackManager.removeLastViewOnStack();

        if(viewsBackStackManager.getViewStack().size() == 0)
        {
            finish();
            return;
        }


        if(viewsBackStackManager.getLastViewOnStack().equals(BackStackViews.TRANFERENCIA))
        {
            startActivity();
            return;
        }

        Toast toast = Toast.makeText(getApplicationContext(),"Sem nenhuma activity",Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();

    }

    private class BackStackViews
    {
        private final static String TRANFERENCIA           = "TRANFERENCIA_1"           ;
        private final static String RECUPERAR_CARGA_PDA    = "RECUPERAR_CARGA_PDA_1_1"  ;

    }

    @Override
    public void asyncResponse(String response, int op) {

    }

    public class InputFilterText
    {
        public Boolean checkInput(String text)
        {
//            boolean isFound = Pattern.compile("[^0-9]+").matcher(text).find();
//            return isFound;
//            return Pattern.compile("^[0-9]").matcher(text).matches();

//          if(!Pattern.compile(carateresEspeciais).matcher(text).find())
//            return false;
//
//            if(text.matches("[!#$%&/)=?'.,-_|*-+@£§{}'(´~^ºª]*"))
//                return false;
////            text = text.replaceAll("[^a-zA-Z0-9]", ""); // tudo que não for números e letras são substituidos por ""

           for(int i = 0; i < text.length(); i++)
           {
               if(Character.isLetter(text.charAt(i)))
                 return false;
           }
           return true;
        }
    }
}


