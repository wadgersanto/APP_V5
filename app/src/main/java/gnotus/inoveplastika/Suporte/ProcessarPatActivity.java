package gnotus.inoveplastika.Suporte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.EditTextOps;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.R;
import gnotus.inoveplastika.UserFunc;

import static gnotus.inoveplastika.Globals.MAN_TIPO_PEDIDO_CORRETIVA;
import static gnotus.inoveplastika.Globals.MAN_TIPO_PEDIDO_PREVENTIVA;
import static gnotus.inoveplastika.MainActivity_Lista.CRLF;


public class ProcessarPatActivity extends AppCompatActivity implements OnClickListener {

    private Toolbar toolbar;
    private SharedPreferences sharedpreferences;
    private static final String MYFILE = "configs";
    private static final String WSRESPONSEVAZIO = "\"[]\"";

    // tipos de ações passada no bundle quando é aberta a atividade e que determina os PA's a apresentar  na listView
    // Estes tipos de ação vão determinar como vai ser executado o serviço processaPa(pastamp)
    private static final Integer TIPO_ACCAO_PEDIDOS_POR_RECECIONAR = 1;
    private static final Integer TIPO_ACCAO_PEDIDOS_EM_ABERTO = 2;
    private static final Integer TIPO_ACCAO_VALIDAR_PRODUCAO = 3;
    private static final Integer TIPO_ACCAO_VALIDAR_QUALIDADE = 4;
    // tipos de ações adicionais que não as principais. Vão executar outro tipo de processamento que não o serviço processaPa(pastamp)
    private static final Integer TIPO_ACCAO_DESBLOQUEAR_MAN_CORRETIVA = 11;

    // tipo de equipamento para listagem de PA
    private static final int TIPO_EQUIP_MOLDES = 1;
    private static final int TIPO_EQUIP_OUTROS = 2;

    // texto de opções de tipo de equipamento para listagem de PAs
    private static final String TIPO_EQUIP_MOLDES_TEXT ="MOLDES";
    private static final String TIPO_EQUIP_OUTROS_TEXT ="OUTROS";


    // PPR = Pedidos por rececionar - Tipos de ações possiveis
    private static final String ACTIONS_PPR_RECECIONAR = "Rececionar pedido";
    private static final String ACTIONS_PPR_DESBLOQUEAR_RP = "Desbloquear registos produção";

    private static final String TIPO_PA_MOLDE = "MOLDE";


    private static int VALIDATE_OS_PROD_STEP_VALIDATEOS = 1;
    private static int VALIDATE_OS_PROD_STEP_VALIDQUAL = 2;
    private static int VALIDATE_OS_PROD_STEP_COTAS = 3;

    private static int validateOsProdCurrentStep = 0;

    private Activity thisActivity = ProcessarPatActivity.this;
    private ListView listViewPats;

    private ArrayList<DataModelPa> listaPaUnfiltered = new ArrayList<>();
    ArrayList<DataModelPa> listaPaFiltrada = new ArrayList<>();
    private ArrayList<DataModelSuporteTemas> listaTemas = new ArrayList<>();
    private ArrayList<DataModelCm4> listaTecnicos = new ArrayList<>();
    private DataModelPa selectedPa; // variavel que fica com o registo que foi seleccionado da lista de PAs

    private DataModelCm4 tecnico; // esta variavel fica com o técnico lido quando é realizada a leitura do operador


    // código do técnico e nome do técnico
    private String operadorLido,nomeOperadorLido,oldEditTextInput;
    private String pFilterPaType = "";
    private String filterEquip = "";
    private Integer selTipoEquip = 0; // tipo equipamento seleccionado para a listagem (usado quando fazemos o pedido de PAs por rececionar ou abertos)

    private MenuItem menuItemRefresh,menuItemListaCarga,menuItemSearch;

    private Activity thisActitity = ProcessarPatActivity.this;
    private ArrayAdapterPa adapterPa;
    private ConstraintLayout constraintLayoutSwitchQual,constraintLayoutSwitchCotas,constraintLayoutSpinner1, constraintLayoutSpinner2;

    private EditText editTextInput;
    private TextView textViewDialog1,textViewDialog2, textViewRelatorio,textViewInfoCotas;

    private Button button_submeter;

    private Bundle bundleRec = new Bundle(); // Bundle recebido
    private Bundle bundleSent = new Bundle(); // Bundle a ser enviado para outra actividade

    private ProgressDialog progDailog ;

    // Tipo de acção a executar
    private int tipoAccao;

    private static final String SPINNER_TEMA_VAZIO = "(Escolha o tema)";
    private static final String SPINNER_ENTIDADE_VAZIO = "(Intervenção realizada por)";

    private static final String SPINNER_VALIDACAO_VAZIO = "(Validação)";
    private static final String SPINNER_VALIDACAO_OK = "OK";
    private static final String SPINNER_VALIDACAO_NOK = "NAO OK";

    private Spinner spinnerTema, spinnerEntidade;
    private Switch switchQldToVal,switchMedirCotas;

    private ArrayList<String> aEntidade =new ArrayList<>(), aTema = new ArrayList<>(), aOpValid = new ArrayList<>();

    private ConstraintLayout layoutListaPats, layoutDialogo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setSubtitle("");

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

//        constraintLayoutCod = (ConstraintLayout) (findViewById(R.id.constraintLayout_codigo));
//        constraintLayoutLoc = (ConstraintLayout) (findViewById(R.id.constraintLayout_localizacao));
//
//        constraintLayoutCod.setVisibility(View.GONE);
//        constraintLayoutLoc.setVisibility(View.GONE);
//
//        button_submeter = (Button) (findViewById(R.id.button_consultar));
//        button_submeter.setText("Rececionar");


        textViewDialog1 = (TextView) findViewById(R.id.textViewDialog1);
        textViewDialog2 = (TextView) findViewById(R.id.textViewDialog2);
        textViewDialog2.setTextColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        textViewDialog2.setTextSize(16);
        textViewDialog2.setOnClickListener(this);

        button_submeter= (Button) (findViewById(R.id.button_yes));
        textViewRelatorio = findViewById(R.id.editTextLongo);
        textViewRelatorio.setOnClickListener(this);

        textViewInfoCotas = findViewById(R.id.editText1);
        textViewInfoCotas.setOnClickListener(this);
        textViewInfoCotas.setHint("Info medição cotas");
        textViewInfoCotas.setVisibility(View.GONE);

        listViewPats = (ListView) (findViewById(R.id.listViewMenu));
        listViewPats.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        spinnerTema = (Spinner) (findViewById(R.id.spinner1));
        spinnerTema.setOnItemSelectedListener(new ItemSelectedListenerTema());

        spinnerEntidade = (Spinner) (findViewById(R.id.spinner2));

        aEntidade.add(SPINNER_ENTIDADE_VAZIO);
        aEntidade.add("Inoveplastika");
        aEntidade.add("Entidade externa");

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aEntidade);
        spinnerEntidade.setAdapter(adapter);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aTema);
        spinnerTema.setAdapter(adapter);

        constraintLayoutSpinner1= (ConstraintLayout) (findViewById(R.id.constraintLayoutSpinner1));
        constraintLayoutSpinner2= (ConstraintLayout) (findViewById(R.id.constraintLayoutSpinner2));

        constraintLayoutSwitchQual = (ConstraintLayout) (findViewById(R.id.constraintLayoutSwitch1));
        constraintLayoutSwitchCotas = (ConstraintLayout) (findViewById(R.id.constraintLayoutSwitch2));

        switchQldToVal = findViewById(R.id.switch1);
        switchMedirCotas = findViewById(R.id.switch2);

        switchMedirCotas.setText("Medir cotas");

        layoutListaPats = (ConstraintLayout) (findViewById(R.id.linearLayout));
        layoutDialogo = (ConstraintLayout) (findViewById(R.id.linearLayoutDialog));

        bundleRec = this.getIntent().getExtras();
        tipoAccao = bundleRec.getInt("tipoaccao");

        aOpValid.clear();
        aOpValid.add(SPINNER_VALIDACAO_VAZIO);
        aOpValid.add(SPINNER_VALIDACAO_OK);
        aOpValid.add(SPINNER_VALIDACAO_NOK);

        mostraListaPats();
        // getSupportActionBar().setSubtitle(spAcBarSubtitle);

        // volleyObterListaTecnicos --> volleyObterListaTemas() --> LerOperador()
        volleyObterListaTecnicos();


        listViewPats.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {

                selectedPa = listaPaFiltrada.get(position);

                if (tipoAccao == 1) {
                    showUnreceivedPatOptions(position);
                    // showPatDataToProcess(position);
                }

                // 2018-12-04 - Suspender a obrigatoriedade de rececionar por ordem

                if (1 == 0) {
                    if ((tipoAccao == TIPO_ACCAO_PEDIDOS_POR_RECECIONAR && position == 0) || tipoAccao == TIPO_ACCAO_PEDIDOS_EM_ABERTO) {
                        showPatDataToProcess();
                    }

                    if ((tipoAccao == TIPO_ACCAO_PEDIDOS_POR_RECECIONAR && !(position == 0))) {

                        Toast toast = Toast.makeText(thisActitity, "Tem de rececionar os pedidos por ordem de data", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                return true;
            }
        });


        listViewPats.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {



                if (tipoAccao == 2 || tipoAccao == 3 || tipoAccao == 4 ) {
                    selectedPa = listaPaFiltrada.get(position);
                    readTechnician(tipoAccao);
                }

            }
        });

        switchMedirCotas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) textViewInfoCotas.setVisibility(View.VISIBLE);
                else textViewInfoCotas.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cab, menu);

        menuItemSearch = menu.findItem(R.id.menu_item_search);
        MenuItem menuItemRefresh = menu.findItem(R.id.menu_item_refresh);
        MenuItem menuItemList = menu.findItem(R.id.menu_item_list);

        menuItemRefresh.setVisible(false);
        menuItemList.setVisible(false);
        menuItemSearch.setVisible(true);

        menuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActitity);
                builder.setTitle("Introduza a opção de pesquisa");

                final EditText input = new EditText(thisActitity);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditTextOps.hideKeyboard(thisActitity,input);
                        filterEquip = input.getText().toString();
                        filtraListaPa(filterEquip);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditTextOps.hideKeyboard(thisActitity,input);
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();



                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menuItemRefresh = menu.findItem(R.id.menu_item_refresh);
        menuItemListaCarga = menu.findItem(R.id.menu_item_list);

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_refresh:
                menuItemRefreshClick();
                return true;
            case R.id.menu_item_list:
                menuItemListClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void onBackPressed() {

        if (layoutDialogo.getVisibility() == View.VISIBLE) {
            layoutListaPats.setVisibility(View.VISIBLE);
            layoutDialogo.setVisibility(View.GONE);
            menuItemSearch.setVisible(true);
            return;
        }

     finish();
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.editTextLongo:
                getEditTextReport((TextView) v);
                break;
            case R.id.editText1:
                getEditTextReport((TextView) v);
                break;

            case R.id.textViewDialog2:
                showTextDialog2();
                break;

        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;


        private MyTextWatcher(View view) {

            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {

        }

    }



    private void menuItemRefreshClick() {

    }


    private  void menuItemListClick(){

    }

    private void readTechnician(final int accao) {

        tecnico = null;

        // para usar em modo debug device virtual
        if (1 == 0) {
            operadorLido = "120";
            // volleyObterListaPats(operadorLido,true);
            return;
        }

        String txtmessage = "";

        if (accao == TIPO_ACCAO_PEDIDOS_POR_RECECIONAR) {
            txtmessage = "Rececionar pedido";
        }

        if (accao == TIPO_ACCAO_DESBLOQUEAR_MAN_CORRETIVA) {
            txtmessage = "Desbloquear molde (produção)";
        }


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle("Ler operador");
        alertDialog.setMessage(txtmessage);


        editTextInput = new EditText(thisActitity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editTextInput.setLayoutParams(lp);


        final EditTextBarCodeReader editTextInputBarCodeReader = new EditTextBarCodeReader(editTextInput,thisActitity);
        editTextInputBarCodeReader.setReplaceTextOnBarCodeReading(true);
        editTextInputBarCodeReader.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                if (scannedText.length() < 4) {
                    Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActitity,false);
                    editTextInputBarCodeReader.setText("");
                    return;
                }

                if (scannedText.substring(0,4).equals("(OP)")) {

                    operadorLido = UserFunc.processaLeituraOperador(scannedText);

                    for (DataModelCm4 dmCm4 : listaTecnicos ) {
                        if (UserFunc.isInteger(operadorLido)) {
                            if (dmCm4.getCm() == Integer.valueOf(operadorLido)) {
                                tecnico = dmCm4;
                                break;
                            }
                        }

                    }


                    if (tecnico == null) {
                        Dialogos.dialogoErro("O operador ["+operadorLido+"] não está registado como técnico","",4,thisActitity,false);
                        editTextInputBarCodeReader.setText("");
                        return;
                    }
                    else
                    {
                        editTextInputBarCodeReader.setText(operadorLido);
                    }

                }
                else {

                    editTextInputBarCodeReader.setText("");
                    Dialogos.dialogoErro("Leitura de operador inválida",scannedText,4,thisActitity,false);
                }


            }
        });


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

        final AlertDialog dialog = alertDialog.create();


        //alertDialog.show();

        dialog.setCancelable(false);

        dialog.show();

        // impedir que o dialogo feche quando a leitura - Override ao click do próprio botão
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                if (editTextInput.getText().toString().isEmpty()) {
                    Dialogos.dialogoErro("Operador em falta","",3,thisActitity,false);
                }
                else  {

                    if (technicianCanExecuteAction(tecnico,accao)) {
                        executeActionAfterReadTecnician(accao);
                    }
                    else {

                        Dialogos.dialogoErro("Erro ao processar pedido",
                                "O técnico "+tecnico.getCmdesc()+" não tem permissões para executar esta operação",3,thisActivity,false);
                    }

                    EditTextOps.hideKeyboard(thisActivity,editTextInput);

                    dialog.dismiss();

                }



            }
        });

    }


    private  void showPatDataToProcess() {

        if (tipoAccao == 2) {

            if (!selectedPa.getPtipo().equals(Globals.MAN_TIPO_PEDIDO_PREVENTIVA)) {
                constraintLayoutSpinner1.setVisibility(View.VISIBLE);
                aTema.clear();
                aTema.add(SPINNER_TEMA_VAZIO);
            }

            else constraintLayoutSpinner1.setVisibility(View.GONE);

            getSupportActionBar().setTitle("Finalizar ordem de serviço");

            textViewRelatorio.setVisibility(View.VISIBLE);
            textViewRelatorio.setHint("Relatório Intervenção");
            constraintLayoutSpinner2.setVisibility(View.VISIBLE);

            textViewDialog1.setText(selectedPa.getTipo()+": "+selectedPa.getSerie()+"  ("+selectedPa.getPdata().substring(0,10)+" "+selectedPa.getPhora()+")"
                    +CRLF+selectedPa.getNref()+" - "+selectedPa.getPtipo().toUpperCase());

            String mTextDialog2 = "PROBLEMA: "+selectedPa.getResumo();

            if (selectedPa.getPsobs().length() > 0) {
                mTextDialog2  = mTextDialog2+ CRLF+"STATUS : ["+selectedPa.getStatus()+"]";
                mTextDialog2  = mTextDialog2+ CRLF+"INFO : "+selectedPa.getPsobs();
            }

            textViewDialog2.setText(mTextDialog2);

            // vamos carregar a lista de temas em função do tipo de pedido que estamos a fechar

           /* if (selectedPa.getTipo().equals(TIPO_PA_MOLDE) && ! selectedPa.getPtipo().equals(Globals.MAN_TIPO_PEDIDO_PREVENTIVA)) {
                constraintLayoutSwitchQual.setVisibility(View.VISIBLE);
            } else constraintLayoutSwitchQual.setVisibility(View.GONE);*/

            constraintLayoutSwitchQual.setVisibility(View.GONE);
            constraintLayoutSwitchCotas.setVisibility(View.GONE);

          /*  if (selectedPa.getTipo().equals(TIPO_PA_MOLDE)) {
                switchQldToVal.setVisibility(View.VISIBLE);
            }*/

            for (DataModelSuporteTemas tmpTemas : listaTemas) {

                if (selectedPa.getTipo().equals(TIPO_PA_MOLDE)) {
                    if (tmpTemas.getTipo().equals(TIPO_PA_MOLDE)) {
                        aTema.add(tmpTemas.getTema());
                    }
                }
                else
                {
                    if (! tmpTemas.getTipo().equals(TIPO_PA_MOLDE)) {
                        aTema.add(tmpTemas.getTema());
                    }
                }

            }

            ArrayAdapter<String>adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aTema);
            spinnerTema.setAdapter(adapter);

            layoutListaPats.setVisibility(View.GONE);
            layoutDialogo.setVisibility(View.VISIBLE);
            menuItemSearch.setVisible(false);

            spinnerEntidade.setSelection(0);
            spinnerTema.setSelection(0);


            textViewRelatorio.setText("");

            button_submeter.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {


                    if ( spinnerEntidade.getSelectedItem().equals(SPINNER_ENTIDADE_VAZIO)
                            ||  ( spinnerTema.getSelectedItem().equals(SPINNER_TEMA_VAZIO) && ! selectedPa.getPtipo().equals(Globals.MAN_TIPO_PEDIDO_PREVENTIVA))
                            || textViewRelatorio.getText().toString().isEmpty() ) {
                        Dialogos.dialogoErro("Atenção","Existem variáveis em falta",4,thisActivity,false);
                    }
                    else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActitity);

                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        volleyProcessarPa(selectedPa.getPastamp());

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }

                            }
                        };

                        builder.setMessage("Pretende finalizar a ordem de serviço").setNegativeButton("Não", dialogClickListener)
                                .setPositiveButton("Sim", dialogClickListener).show();

                    }

                }

            });

            return;
        }

        if (tipoAccao == 3 || tipoAccao == 4) {

            // vamos usar o spinner tema para as opções de validação da OF
            if (tipoAccao == TIPO_ACCAO_VALIDAR_PRODUCAO) {

                constraintLayoutSwitchQual.setVisibility(View.VISIBLE);
                constraintLayoutSwitchCotas.setVisibility(View.VISIBLE);

                switchQldToVal.setChecked(false);
                switchMedirCotas.setChecked(false);

            }

            if (tipoAccao == TIPO_ACCAO_VALIDAR_QUALIDADE) {

                constraintLayoutSwitchQual.setVisibility(View.GONE);
                constraintLayoutSwitchCotas.setVisibility(View.GONE);

            }

            spinnerTema.setVisibility(View.VISIBLE);
            constraintLayoutSpinner2.setVisibility(View.GONE);

            textViewRelatorio.setVisibility(View.GONE);
            textViewRelatorio.setHint("Motivo");


            switch (tipoAccao) {
                case 3:
                    getSupportActionBar().setTitle("Produção: Validar OS");
                    break;
                case 4:
                    getSupportActionBar().setTitle("Qualidade: Validar OS");
                    break;

            }



            textViewDialog1.setText(selectedPa.getTipo()+": "+selectedPa.getSerie()+"  ("+selectedPa.getFdata().substring(0,10)+" "+selectedPa.getFhora()+")"
                    +CRLF+selectedPa.getNref()+" - "+selectedPa.getPtipo().toUpperCase());

//            textViewDialog1.setText(listaPa.get(position).getTipo()+":"+listaPa.get(position).getSerie()
//                    +CRLF+"("+listaPa.get(position).getFdata().substring(0,10)+" "+listaPa.get(position).getFhora()+")");

            textViewDialog2.setText("PROBLEMA: "+selectedPa.getResumo()+CRLF
            +"RELATÓRIO: "+selectedPa.getSolucao());

            ArrayAdapter<String>adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aOpValid);
            spinnerTema.setAdapter(adapter);

            layoutListaPats.setVisibility(View.GONE);
            layoutDialogo.setVisibility(View.VISIBLE);
            menuItemSearch.setVisible(false);

            spinnerTema.setSelection(0);

            textViewRelatorio.setText("");

            button_submeter.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {



                    if ( spinnerTema.getSelectedItem().equals(SPINNER_VALIDACAO_VAZIO)
                            || (spinnerTema.getSelectedItem().equals(SPINNER_VALIDACAO_NOK) &&  textViewRelatorio.getText().toString().isEmpty() )
                            || ( switchMedirCotas.isChecked() && textViewInfoCotas.getText().toString().equals(""))  ) {
                        Dialogos.dialogoErro("Atenção","Existem variáveis em falta",4,thisActivity,false);
                    }
                    else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActitity);

                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:

                                        volleyProcessarPa(selectedPa.getPastamp().toString());

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }

                            }
                        };

                        builder.setMessage("Pretende validar a ordem de serviço").setNegativeButton("Não", dialogClickListener)
                                .setPositiveButton("Sim", dialogClickListener).show();

                    }

                }

            });

            return;
        }


    }

    private void volleyUnlockMoldRegisterProd(int tecnico, String pastamp) {


        RequestQueue queue = Volley.newRequestQueue(this);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/UnlockMoldRegisterProd?").buildUpon()
                .appendQueryParameter("tecnico", String.valueOf(tecnico))
                .appendQueryParameter("pastamp", String.valueOf(pastamp) )
                .build();

        progDailog.show();

// Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();


                        Gson gson = new Gson();

                        DataModelWsResponse wsResponse = gson.fromJson(response,new TypeToken<DataModelWsResponse>() {}.getType());

                        System.out.println(gson.toString());

                        System.out.println("WsResponse Código" + wsResponse.getCodigo());

                        if (wsResponse.getCodigo() == 0) {

                            volleyObterListaPats(selTipoEquip,false);
                            Dialogos.dialogoInfo("Ordem de serviço desbloqueada","",2.0,thisActivity,false);

                        }
                        else {

                            Dialogos.dialogoErro("Erro na execução do pedido",wsResponse.getDescricao(),3,thisActivity,false);
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),3,thisActivity,true);
            }
        });

        // Add the request to the RequestQueue.

        System.out.println(uri);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);



    }

    private  void volleyProcessarPa(String pastamp) {

        //
        RequestQueue queue = Volley.newRequestQueue(this);


        String relatorio = "", tema ="", entidade = "", infocotas ="";

        if (tipoAccao == 2 ) {
            relatorio = textViewRelatorio.getText().toString();
            tema = spinnerTema.getSelectedItem().toString();
            entidade = spinnerEntidade.getSelectedItem().toString();
        }

        if (tipoAccao == 3 ) {
            infocotas = textViewInfoCotas.getText().toString();
            relatorio = textViewRelatorio.getText().toString();
            tema = spinnerTema.getSelectedItem().toString();
        }

        if (tipoAccao == 4 ) {
            relatorio = textViewRelatorio.getText().toString();
            tema = spinnerTema.getSelectedItem().toString();
        }

        DataModelProcessarPa processarPa = new DataModelProcessarPa();

        processarPa.setPastamp(pastamp);
        processarPa.setTecnico(Integer.parseInt(operadorLido));
        processarPa.setTipoaccao(tipoAccao);
        processarPa.setRelatorio(relatorio);
        processarPa.setTema(tema);
        processarPa.setEntidade(entidade);
        processarPa.setQldtoval(switchQldToVal.isChecked());
        processarPa.setMedircotas(switchMedirCotas.isChecked());
        processarPa.setInfocotas(infocotas);

        String url = "http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ProcessarPa?";


        progDailog.show();

        System.out.println("");

        Gson gson = new Gson();

        JSONObject jsonProcessaPa;

        try {

            jsonProcessaPa = new JSONObject(gson.toJson(processarPa));

        }
        catch (JSONException e) {
            Dialogos.dialogoErro("Erro na conversão para Json da classe processarPa",e.getMessage(),6,thisActivity,false);
            e.printStackTrace();
            return;
        }

        System.out.println(jsonProcessaPa);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonProcessaPa,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progDailog.dismiss();

                        try {

                            if (response.getInt("Codigo") == 0) {

                                Dialogos.dialogoInfo("Sucesso","Pedido processado",3.0,thisActivity,false);

                                volleyObterListaPats(selTipoEquip,false);
                                if (layoutDialogo.getVisibility() == View.VISIBLE) {
                                    mostraListaPats();
                                }
                            }
                            else
                            {
                                Dialogos.dialogoErro("Erro no processamento do pedido",
                                        response.getString("Descricao"),6,thisActivity,false);
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

                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),10,thisActitity,false);

                progDailog.dismiss();
            }
        });



        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);



    }

    private  void volleyObterListaPats(int tipo_equip, final boolean mostraDialogoSemRegistos) {

        RequestQueue queue = Volley.newRequestQueue(this);


        String filtro = "";

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterListaPats?").buildUpon()
                .appendQueryParameter("tipoaccao", String.valueOf(tipoAccao))
                .appendQueryParameter("tipoequip", String.valueOf(tipo_equip) )
                .appendQueryParameter("filtro", filtro)
                .build();

        progDailog.show();

// Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();
                        listaPaUnfiltered = gson.fromJson(response,new TypeToken<ArrayList<DataModelPa>>() {}.getType());

                        if (listaPaUnfiltered.isEmpty() && mostraDialogoSemRegistos) {
                            Dialogos.dialogoInfo("","Não encontrei registos",3.0,thisActitity, true);
                            return;
                        }

                        filtraListaPa(filterEquip);


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),3,thisActivity,true);
            }
        });

// Add the request to the RequestQueue.
        System.out.println(uri);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

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

                        volleyObterListaTemas();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        System.out.println(builtURI_asyncRequest);

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private  void volleyObterListaTemas() {

        RequestQueue queue = Volley.newRequestQueue(this);

        Uri builtURI_asyncRequest = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterListaTemas?").buildUpon()
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, builtURI_asyncRequest.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();

                        listaTemas = gson.fromJson(response,new TypeToken<ArrayList<DataModelSuporteTemas>>() {}.getType());


                        if (tipoAccao == TIPO_ACCAO_PEDIDOS_POR_RECECIONAR || tipoAccao == TIPO_ACCAO_PEDIDOS_EM_ABERTO ) {
                            escolheTipoEquip();
                        }

                        if (tipoAccao == TIPO_ACCAO_VALIDAR_PRODUCAO || tipoAccao == TIPO_ACCAO_VALIDAR_QUALIDADE ) {
                            volleyObterListaPats(0,true);
                        }

                        // readTechnician();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido ",error.getMessage(),4,thisActivity,false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private  void mostraListaPats() {


        layoutListaPats.setVisibility(View.VISIBLE);
        layoutDialogo.setVisibility(View.GONE);

        if (tipoAccao == 1) getSupportActionBar().setTitle("Pedidos por rececionar");
        if (tipoAccao == 2) getSupportActionBar().setTitle("Intervenções em aberto");
        if (tipoAccao == 3) getSupportActionBar().setTitle("Produção: Validar OS");
        if (tipoAccao == 4) getSupportActionBar().setTitle("Qualidade: Validar OS");

        if (menuItemSearch != null) {
            menuItemSearch.setVisible(true);
        }


    }

    public class ItemSelectedListenerTema implements AdapterView.OnItemSelectedListener {

        //get strings of first item

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            System.out.println(spinnerTema.getSelectedItem());

            if (tipoAccao == 3 || tipoAccao == 4 )

            {
                if (spinnerTema.getSelectedItem().equals(SPINNER_VALIDACAO_NOK)) {

                    textViewRelatorio.setVisibility(View.VISIBLE);
                }
                else textViewRelatorio.setVisibility(View.GONE);

            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }

    private void filtraListaPa(String filtro) {

        listaPaFiltrada.clear();

        for (DataModelPa item:listaPaUnfiltered) {

            if (item.getPtipo().equals(pFilterPaType) || pFilterPaType.equals("") ) {
                if (item.getSerie().toUpperCase().contains(filtro.toUpperCase()) || filtro.equals("") ) {
                    listaPaFiltrada.add(item);
                }
            }
        }

        adapterPa = new ArrayAdapterPa(thisActitity, 0, listaPaFiltrada,tipoAccao);

        listViewPats.setAdapter(adapterPa);
        adapterPa.notifyDataSetChanged();

    }

    private  void escolheTipoPa() {

        AlertDialog alertDialogOpcoes;

        final CharSequence[] tiposPa = {MAN_TIPO_PEDIDO_PREVENTIVA,MAN_TIPO_PEDIDO_CORRETIVA};

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        bundleSent.clear();

        builder.setTitle("Escolha o tipo de OS");

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                thisActitity.finish();
            }
        });

        builder.setItems(tiposPa,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                pFilterPaType = tiposPa[item].toString();

                volleyObterListaPats(selTipoEquip,true);

            }
        });

        alertDialogOpcoes = builder.create();
        alertDialogOpcoes.show();

    }

    private void getEditTextReport(final TextView textView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Relatório intervenção");


        final EditText input = new EditText(this);
        input.setText(textView.getText());
        input.setLines(2);
        input.setMaxLines(10);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        builder.setView(input);
        builder.setCancelable(false);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditTextOps.hideKeyboard(thisActitity,input);
                textView.setText(input.getText().toString());
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

    private void showTextDialog2() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Relatório intervenção");


        final TextView input = new TextView(this);
        input.setText( textViewDialog2.getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(20);
        input.setTextColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));
        input.setTextSize(18);

        input.setGravity(Gravity.LEFT | Gravity.TOP);


        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });


        builder.show();
    }

    private void escolheTipoEquip() {



        final CharSequence[] opcoesListagemPa = {TIPO_EQUIP_MOLDES_TEXT, TIPO_EQUIP_OUTROS_TEXT};

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        bundleSent.clear();

        builder.setTitle("Escolha o tipo de equipamento");

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                thisActitity.finish();
            }
        });


        builder.setItems(opcoesListagemPa,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (opcoesListagemPa[item].equals(TIPO_EQUIP_MOLDES_TEXT)) selTipoEquip = TIPO_EQUIP_MOLDES;

                if (opcoesListagemPa[item].equals(TIPO_EQUIP_OUTROS_TEXT)) selTipoEquip = TIPO_EQUIP_OUTROS;

                escolheTipoPa();

            }

        }).show();

    }


    private void showUnreceivedPatOptions(int position) {

        // mostra as oções que podemos realizar sobre um PA por rececionar

        final List<String> listItems = new ArrayList<String>();

        listItems.add(ACTIONS_PPR_RECECIONAR);

        if (selTipoEquip == TIPO_EQUIP_MOLDES && pFilterPaType.equals(MAN_TIPO_PEDIDO_CORRETIVA) && ! selectedPa.isU_dblqrp() ) {
            listItems.add(ACTIONS_PPR_DESBLOQUEAR_RP);
        }

        final CharSequence[] opcoesListagemPa = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        bundleSent.clear();




        builder.setTitle("Acção a executar");

        builder.setItems(opcoesListagemPa,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (listItems.get(item).equals(ACTIONS_PPR_RECECIONAR)) readTechnician(TIPO_ACCAO_PEDIDOS_POR_RECECIONAR);

                if (listItems.get(item).equals(ACTIONS_PPR_DESBLOQUEAR_RP)) readTechnician(TIPO_ACCAO_DESBLOQUEAR_MAN_CORRETIVA);

            }

        }).show();


    }


    private void executeActionAfterReadTecnician(Integer tipoaccao) {

        // Executar uma ação sobre um registo de PA
        // No momento em que processamos esta ação a variavel selectedPa do tipo <DataModelPa> já tem o PA seleccionado
        // e a variavel tecnico (DataModelCm4) já contém o técnico que fez leitura do cartão


        // se o tipo de ação for para desbloquear então vamos
        if (tipoaccao.equals(TIPO_ACCAO_DESBLOQUEAR_MAN_CORRETIVA)) {
            volleyUnlockMoldRegisterProd(tecnico.getCm(),selectedPa.getPastamp());
            return;
        }


        if (tipoaccao.equals(TIPO_ACCAO_PEDIDOS_POR_RECECIONAR)){
            volleyProcessarPa(selectedPa.getPastamp());
            return;
        }


        if (tipoaccao.equals(TIPO_ACCAO_PEDIDOS_EM_ABERTO) || tipoaccao.equals(TIPO_ACCAO_VALIDAR_PRODUCAO) || tipoaccao.equals(TIPO_ACCAO_VALIDAR_QUALIDADE) ){
                showPatDataToProcess();
                return;
        }

    }


    private boolean technicianCanExecuteAction(DataModelCm4 mTecnico, Integer tipoAccao) {

        // procedimento que vai verificar se o tecnico pode executar a definida sobre o PA


        if (tipoAccao.equals(TIPO_ACCAO_DESBLOQUEAR_MAN_CORRETIVA)) {
            if (mTecnico.isU_dblqrpmc()) return true;
            else return false;
        }

        if (tipoAccao.equals(TIPO_ACCAO_PEDIDOS_POR_RECECIONAR)|| tipoAccao.equals(TIPO_ACCAO_PEDIDOS_EM_ABERTO)){

            if ( (selectedPa.getTipo().equals("MOLDE") && mTecnico.isV_tecmoldes()) || ( ! selectedPa.getTipo().equals("MOLDE") && mTecnico.isV_tecequip()) ) {
                return true;
            }
            else return false;
        }
        // este procedimento diz-nos se o operador pode executar

        if (tipoAccao.equals(TIPO_ACCAO_VALIDAR_PRODUCAO)) {
            if(mTecnico.isU_vldosprd()) return true;
            else return false;
        }

        if (tipoAccao.equals(TIPO_ACCAO_VALIDAR_QUALIDADE)) {
            if(mTecnico.isV_validate_os_qual()) return true;
            else return false;
        }

        return false;

    }

}

