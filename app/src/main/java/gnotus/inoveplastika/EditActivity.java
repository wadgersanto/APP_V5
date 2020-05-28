package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Producao.PedidoAbastProdActivity;

public class EditActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete {

    private static final String QUERY_PARAM_REF = "ref";
    private static final String QUERY_PARAM_LOTE = "lote";
    private static final String QUERY_PARAM_ARMAZEM = "armazem";
    private static final String QUERY_PARAM_ZONA = "zona";
    private static final String QUERY_PARAM_ALVEOLO = "alveolo";
    private static final String QUERY_PARAM_BISTAMP = "bistamp";
    private static final String QUERY_PARAM_QT = "qt";
    private static final String QUERY_PARAM_QTALT = "qtAlt";

    private static final String QUERY_PARAM_BOSTAMP = "bostamp";
    private static final String QUERY_PARAM_BOSTAMPC = "bostampC";
    private static final String QUERY_PARAM_BOSTAMPD = "bostampD";
    private static final String QUERY_PARAM_UNIDADE = "unidade";
    private static final String QUERY_PARAM_UNIDADEALT = "unidadeAlt";
    private static final String QUERY_PARAM_FCONVERSAO = "fconversao";
    private static final String QUERY_PARAM_ARMAZEMD = "armazemd";
    private static final String QUERY_PARAM_ZONAD = "zonad";
    private static final String QUERY_PARAM_ALVEOLOD = "alveolod";
    private static final String QUERY_PARAM_USER = "user";

    private static final String ATIVIDADE_ORIGEM_INVOF = "invof";
    private static final String ATIVIDADE_ORIGEM_INVENTARIO = "inventario";

    private static final String TIPO_INVENTARIO_REF = "REF";
    private static final String TIPO_INVENTARIO_ALV = "ALV";

    private static final String FLAG_PICKING_ABASTPROD = "ABASTPROD"; // we are transfering to the prodution zone

    private static final String JsonArrayVazio = "\"[]\"";

    private Activity thisActivity = EditActivity.this;

    Bundle bundle = new Bundle();
    private Toolbar toolbar;
    private TextView textView_descricao, textView_referencia, textView_of, textView_stock, textView_qtlida,
            textView_unidade,textView_edit_titulo_qt, textView_leg_qtlida,textView_leg_stock, textView_leg_Ref,textView_leg_descricao,
    textview_localizacao,textView_leg_localizacao, textView_leg_lote, textView_lote;

    private ProgressDialog progDailog ;

    private EditText editText_quantidade;

    private RadioGroup radioGroup_edit;
    private RadioButton radioButton_un, radioButton_unAlt;

    // Flag que controla se estamos a inserir quantidades na unidade alternativa (true) ou principal (false)
    private boolean FLAG_MODO_UNIDADE_ALT = false;
    private double dconversao, dstock, dstockAlt;
    private String ref,design,lote,unidade, unidadeAlt;
    private String armazem = "",zona = "", alveolo = "";
    private String armazem_dest = "",zona_dest = "", alveolo_dest = "";
    private String user, bostamp_carga, bostamp_descarga, bistamp_carga;
    private String atividadeOrigem,tipo_inventario,tipoinvloc;

    // variavel que vai dizer se valida ou não o stock disponivel - no caso de inventários não vamos fazer essa validação. Serve tb para passar para o ws_criabi

    private Boolean validaStock;

    // o campo stock corresponde ao valor que se pretende validar como limite de stock disponivel
    // a quantidade lida é a quantidade que queremos carregar por defeito no ecrã quando o abrimos
    private double stock, stockAlt, qtd_lida,qtdalt_lida;

    // Os campos Qtt e Qtt2 são usados para ter a informação da quantidade inicial e quantidade satisfeita das linhas de um dossier
    private double qtt, qtt2;

    // se a quantidade de carga lida é valida, ou seja, está dentro do stock disponivel
    private boolean FLAG_QTT_IS_VALID = false;

    // se a quantidade de descarga lida é valida, ou seja, está dentro do stock disponivel
    //private boolean FLAG_QT_DESCARGA = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar Quantidade");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        textView_descricao = (TextView) findViewById(R.id.textView_edit_desc);
        textView_leg_descricao = (TextView) findViewById(R.id.textView_edit_leg_desc);
        textView_leg_localizacao = (TextView) findViewById(R.id.textView_leg_localizacao);
        textView_referencia = (TextView) findViewById(R.id.textView_edit_ref);
        textView_leg_Ref = (TextView) findViewById(R.id.textView_edit_leg_ref);
        textView_of = (TextView) findViewById(R.id.textView_edit_lote);
        textView_stock = (TextView) findViewById(R.id.textView_edit_stock);
        textView_leg_stock  = (TextView) findViewById(R.id.textView_edit_leg_stock);
        textView_qtlida = (TextView) findViewById(R.id.textView_edit_quantidade);
        textView_leg_qtlida = (TextView) findViewById(R.id.textView_edit_leg_quantidade);
        textView_unidade = (TextView) findViewById(R.id.textView_edit_unidade);

        textview_localizacao =(TextView) findViewById(R.id.textView_3a);

        textView_leg_lote =(TextView) findViewById(R.id.textView_edit_leg_lote);
        textView_lote =(TextView) findViewById(R.id.textView_edit_leg_stock);


        editText_quantidade = (EditText) findViewById(R.id.editText_edit_quantidades);
        textView_edit_titulo_qt = (TextView) findViewById(R.id.textView_edit_titulo_qt);


        editText_quantidade.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {

                    EditTextOps.hideKeyboard(thisActivity,editText_quantidade);

                    System.out.println("Carregou no OK");

                    // SE A ATIVIDADE DE ORIGEM FOR O INVENTARIO
                    System.out.println("Valor da String:"+String.valueOf(qtd_lida));


                    if (editText_quantidade.getText().toString().isEmpty()){
                        Dialogos.dialogoErro("Dados inválidos","O campo quantidade não pode estar vazio",2,EditActivity.this, false);
                        return false;
                    }

                    if (editText_quantidade.getText().toString().equals(".")){
                        Dialogos.dialogoErro("Dados inválidos","O campo quantidade é inválido",2,EditActivity.this, false);
                        editText_quantidade.requestFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                        return false;
                    }

                    if (atividadeOrigem.equals(PedidoAbastProdActivity.class.getSimpleName())){

                        registaPedidoAbastProd();
                        return true;

                    }

                    if (atividadeOrigem.equals(ATIVIDADE_ORIGEM_INVOF)|| atividadeOrigem.equals(ATIVIDADE_ORIGEM_INVENTARIO)){

                        Boolean pAdiciona;

                        if (bundle.getBoolean("editing_listview")) pAdiciona = false;
                        else pAdiciona = true   ;

                        AsyncRequest criaDossierInv = new AsyncRequest(EditActivity.this, 4);

                        Uri builtURI_criaDossierInv = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RegistaLeituraInvOf?").buildUpon()
                                .appendQueryParameter("bostamp", bostamp_carga)
                                .appendQueryParameter("referencia", ref)
                                .appendQueryParameter("lote",lote)
                                .appendQueryParameter("qtt",String.valueOf(qtd_lida))
                                .appendQueryParameter("armazem",String.valueOf(armazem))
                                .appendQueryParameter("zona",zona)
                                .appendQueryParameter("alveolo",alveolo)
                                .appendQueryParameter("adicionaqtt",String.valueOf(pAdiciona))
                                .build();

                        criaDossierInv.execute(builtURI_criaDossierInv.toString());

                        return true;

                    }

                    // ATIVIDADE = "PICKING ACTIVITY"

                    if (atividadeOrigem.equals(PickingActivity.class.getSimpleName())){



                        // se estivermos a registar uma devolução de expedição vamos directamente transferir
                        // para a zona de devolução de expedição, não fazemos carga PDA

                        if (bundle.getString("pickingType").equals("DEVEXP") ) {

                            AsyncRequest mAsynRequest = new AsyncRequest(thisActivity, 6);

                            Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() +
                                    "/TerminalPHC_INOVEPLASTIKA/Service1.svc/registaDevExp?").buildUpon()
                                    .appendQueryParameter("referencia", ref.trim())
                                    .appendQueryParameter("lote", lote.trim())
                                    .appendQueryParameter("pkbistamp", bundle.getString("obistamp"))
                                    .appendQueryParameter("qtt", String.valueOf(qtd_lida))
                                    .appendQueryParameter("operador", user)

                                    .build();

                            mAsynRequest.execute(builtURI_processaPedido.toString());

                            return true;
                        }



                        // se não estiver a editar quer dizer que estamos a introduzir

                        if (! bundle.getBoolean("editing_listview") ) {

                            // se a atividade for a picking com a acção de expedição, vamos ler a quantidade ainda em falta no picking.
                            // se a quantidade em falta for inferior à quantidade seleccionada vamos perguntar ao utilizador se confirma


                            if (bundle.getString("pickingType").equals("EXP") || bundle.getString("pickingType").equals(FLAG_PICKING_ABASTPROD) ) {

                                double tmpQttPkEmFalta = bundle.getDouble("QttPkExpFalta");

                                if (tmpQttPkEmFalta < qtd_lida ) {

                                    String  txtMessage = "A quantidade lida "+UserFunc.retiraZerosDireita(qtd_lida)
                                            +" é superior à quantidade em falta no picking "+UserFunc.retiraZerosDireita(tmpQttPkEmFalta)+". Deseja continuar?";

                                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                                    builder.setMessage(txtMessage);

                                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            criaPickingBi(bostamp_carga,String.valueOf(qtd_lida),ref.trim(),lote.trim(),armazem,zona,alveolo,user,
                                                    Objects.toString(bundle.getString("obistamp"),""),Objects.toString(bundle.getString("oobistamp"),""));

                                        }
                                    });

                                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                       return;

                                        }
                                    });

                                    AlertDialog myDialog = builder.create();
                                    myDialog.show();


                                }

                                else {

                                    criaPickingBi(bostamp_carga,String.valueOf(qtd_lida),ref.trim(),lote.trim(),armazem,zona,alveolo,user,
                                            Objects.toString(bundle.getString("obistamp"),""),Objects.toString(bundle.getString("oobistamp"),""));

                                }


                            }

                            else {

                                criaPickingBi(bostamp_carga,String.valueOf(qtd_lida),ref.trim(),lote.trim(),armazem,zona,alveolo,user,
                                        Objects.toString(bundle.getString("obistamp"),""),Objects.toString(bundle.getString("oobistamp"),""));

                            }

                        }
                        else {

                            // SE A QUANTIDADE FOR VALIDA
                            if (FLAG_QTT_IS_VALID) {

                                AsyncRequest atualizaQts = new AsyncRequest(EditActivity.this, 1);

                                Uri builtURI_atualizaQts = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/atualizaQts?").buildUpon()
                                        .appendQueryParameter(QUERY_PARAM_BISTAMP, bistamp_carga.trim())
                                        .appendQueryParameter(QUERY_PARAM_QT, String.valueOf(qtd_lida))
                                        .appendQueryParameter(QUERY_PARAM_QTALT, String.valueOf(qtdalt_lida))
                                        .build();
                                atualizaQts.execute(builtURI_atualizaQts.toString());


                            } else {
                                finishActivity();
                            }
                        }

                        return true;

                    }


                    //  CHEGADOS AQUI ASSUMIMOS QUE ESTAMOS NA TRANSFERENCIA ENTRE ARMAZENS

                    // SE ESTAMOS EM MODO CARGA


                    if(bundle.getString("trf_mode").equals("carga")) {

                        // SE ESTAMOS EM MODO DE EDIÇÃO, OU SEJA, SE CHAMAMOS O ECRÃ PELA EDIÇÃO DA LINHA
                        if(bundle.getBoolean("editing_listview") ) {

                            // SE A QUANTIDADE FOR VALIDA
                            if (FLAG_QTT_IS_VALID) {

                                AsyncRequest atualizaQts = new AsyncRequest(EditActivity.this, 1);

                                Uri builtURI_atualizaQts = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/atualizaQts?").buildUpon()
                                        .appendQueryParameter(QUERY_PARAM_BISTAMP, bistamp_carga)
                                        .appendQueryParameter(QUERY_PARAM_QT, String.valueOf(qtd_lida))
                                        .appendQueryParameter(QUERY_PARAM_QTALT, String.valueOf(qtdalt_lida))
                                        .build();
                                atualizaQts.execute(builtURI_atualizaQts.toString());


                            } else {
                                finishActivity();
                            }
                        }

                        // se a actividade foi chamada pelo modo de carga no momento da leitura (inserir) então vamos chamar o serviço de criar linha
                        if(!bundle.getBoolean("editing_listview")) {


                            AsyncRequest criaBI = new AsyncRequest(EditActivity.this, 3);

                            Uri builtURI_criaBI = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/criaBI?").buildUpon()


                                    .appendQueryParameter("bostamp", bostamp_carga)
                                    .appendQueryParameter("qt", String.valueOf(qtd_lida))
                                    .appendQueryParameter("ref", ref.trim())
                                    .appendQueryParameter("lote", lote.trim())

                                    .appendQueryParameter("armazem_ori", armazem)
                                    .appendQueryParameter("zona_ori", zona)
                                    .appendQueryParameter("alveolo_ori", alveolo)

                                    .appendQueryParameter("armazem_dest", armazem_dest)
                                    .appendQueryParameter("zona_dest", zona_dest)
                                    .appendQueryParameter("alveolo_dest", alveolo_dest)

                                    .appendQueryParameter("user", user)
                                    .appendQueryParameter("valida_stock", validaStock.toString())

                                    .build();

                            criaBI.execute(builtURI_criaBI.toString());

                        }

                    } else {


                        if (FLAG_QTT_IS_VALID) {

                            AsyncRequest processaDescarga = new AsyncRequest(EditActivity.this, 2);

                            Uri builtURI_processaDescarga = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/processaDescarga?").buildUpon()
                                    .appendQueryParameter(QUERY_PARAM_BOSTAMPC, bostamp_carga)
                                    .appendQueryParameter(QUERY_PARAM_BOSTAMPD, bostamp_descarga)
                                    .appendQueryParameter(QUERY_PARAM_REF, ref)
                                    .appendQueryParameter(QUERY_PARAM_LOTE, lote)
                                    .appendQueryParameter(QUERY_PARAM_QT, String.valueOf(qtd_lida))
                                    .appendQueryParameter(QUERY_PARAM_UNIDADE,unidade)
                                    .appendQueryParameter(QUERY_PARAM_QTALT,String.valueOf(qtdalt_lida))
                                    .appendQueryParameter(QUERY_PARAM_UNIDADEALT,unidadeAlt)
                                    .appendQueryParameter(QUERY_PARAM_FCONVERSAO,String.valueOf(dconversao))
                                    .appendQueryParameter(QUERY_PARAM_ARMAZEMD,armazem_dest)
                                    .appendQueryParameter(QUERY_PARAM_ZONAD,zona_dest)
                                    .appendQueryParameter(QUERY_PARAM_ALVEOLOD,alveolo_dest)
                                    .appendQueryParameter(QUERY_PARAM_USER,bundle.getString("user"))
                                    .build();

                            processaDescarga.execute(builtURI_processaDescarga.toString());


                        } else {

                            finishActivity();

                        }

                    }

                    return true;
                }
                return false;
            }
        });



        radioGroup_edit = (RadioGroup) findViewById(R.id.radioGroup_edit);
        radioButton_un = (RadioButton) findViewById(R.id.radioButton_edit_un);
        radioButton_unAlt = (RadioButton) findViewById(R.id.radioButton_edit_unAlt);

        bundle = this.getIntent().getExtras();

        // carrega a informação do bundle nas variáveis da actividade
        carregaInfoBundle();


        // se a atividade de chamada for a atividade de transferencia

        if (bundle.getString("atividade").equals("transferencia")) {
            // qual o modo de transferencia carga/descarga

            if (bundle.getString("trf_mode").equals("carga")) {

                getSupportActionBar().setSubtitle("Modo: Carga");

                if (bundle.getBoolean("editing_listview")) {

                    AsyncRequest lerStock = new AsyncRequest(EditActivity.this, 0);

                    Uri builtURI_lerStock = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerStock?").buildUpon()
                            .appendQueryParameter(QUERY_PARAM_REF, ref.trim())
                            .appendQueryParameter(QUERY_PARAM_LOTE, lote.trim())
                            .appendQueryParameter(QUERY_PARAM_ARMAZEM, armazem.trim() )
                            .appendQueryParameter(QUERY_PARAM_ZONA, zona.trim())
                            .appendQueryParameter(QUERY_PARAM_ALVEOLO, alveolo.trim())
                            .build();

                    lerStock.execute(builtURI_lerStock.toString());
                }
            }

            if (bundle.getString("trf_mode").equals("descarga")) getSupportActionBar().setSubtitle("Modo: Descarga");
        }

        // preencher o SubTitulo
        switch (atividadeOrigem) {

            case "invloc":
                getSupportActionBar().setSubtitle("");

            case "invof":
                getSupportActionBar().setSubtitle("");
        }

        editText_quantidade.addTextChangedListener(new MyTextWatcher(editText_quantidade));
        editText_quantidade.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        carregaInformacaoEcra();

        editText_quantidade.selectAll();

        // editText_quantidade.setSelectAllOnFocus(true);



        // This overrides the radiogroup onCheckListener
        radioGroup_edit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButton_edit_un) {

                    if (bundle.getString("trf_mode").equals("carga")) textView_unidade.setText(unidade);

                    else textView_unidade.setText(bundle.getString("unidade"));

                    FLAG_MODO_UNIDADE_ALT = false;

                    if (! editText_quantidade.getText().toString().isEmpty()) editText_quantidade.setText(String.valueOf(qtd_lida));

                }

                if (checkedId == R.id.radioButton_edit_unAlt) {

                    if (bundle.getString("trf_mode").equals("carga")) textView_unidade.setText(unidadeAlt);

                    else textView_unidade.setText(bundle.getString("unidadeAlt"));

                    FLAG_MODO_UNIDADE_ALT = true;

                    if (! editText_quantidade.getText().toString().isEmpty())  editText_quantidade.setText(String.valueOf(qtdalt_lida));

                }

                editText_quantidade.selectAll();
            }
        });

    }


    public void onBackPressed() {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'

        finishActivity();
    }

    @Override
    public boolean onSupportNavigateUp() {


        onBackPressed();
        return true;
    }

    private void dialogoErro() {


        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(EditActivity.this);
        builder1.setTitle("Erro");
        builder1.setMessage("Quantidade superior ao stock");
        builder1.setCancelable(false);

        builder1.setView(image);

        final AlertDialog alert11 = builder1.create();
        alert11.show();

        new CountDownTimer(1000, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                alert11.dismiss();
            }
        }.start();

    }




    @Override
    public void asyncResponse(String response, int op) {

        ImageView image = new ImageView(EditActivity.this);
        image.setImageResource(R.mipmap.ic_notok);

        ImageView imageOK = new ImageView(EditActivity.this);
        imageOK.setImageResource(R.mipmap.ic_ok);

        if (response == null) {
            dialogoErro("Erro","Erro no processamento da informação",true);
            return;
        }

            // Pedido para ler stock disponivel em modo de carga - quando estamos a editar uma linha
        if (op == 0) {

            // como quando estamos a editar uma carga não carregamos no bundle a variavel stock (vai ser lida pelo asyncRequest),
            // e no OnCreate no evento carregaInformacaoEcra carregamos informação da qtd_lida na editext_quantidade,
            // isso vai despoletar o evento OnTextChanged e dentro desse evento caso (stock < qtd_lida)
            // é colocada qtd_lida a zero, então temos de ler novamente a qtd_lida a partir do bundle


            qtd_lida = bundle.getDouble("qtd_lida");
            qtdalt_lida = bundle.getDouble("qtdAlt_lida");

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);


                for (int i = 0; i < values.length(); i++) {

                    JSONObject c = values.getJSONObject(i);

                    ref         = c.getString("ref");
                    lote        = c.getString("lote");
                    design      = c.getString("design");
                    unidade     = c.getString("unidade");
                    dconversao  = Double.parseDouble(c.getString("fconversao")) ;
                    unidadeAlt  = c.getString("uni2");
                    stockAlt    = Double.parseDouble(c.getString("stock2")) ;
                    stock       = Double.parseDouble(c.getString("stock")) ;

                    System.out.println("Editar: Ler Stock localização: qtd_lida :"+qtd_lida );

                    // depois de lermos o stock disponivel que
                    stock = stock+qtd_lida;
                    stockAlt = stockAlt +qtdalt_lida;

                    // agora vamos calcular o valor arredondado a 4 casas decimais

                    stockAlt = Math.round(stockAlt*1000.0) / 1000.0;

                    carregaInformacaoEcra();
                }


            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());

            }

        }

        if (op == 1) {

            if (response.equals("true")) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditActivity.this);
                builder1.setTitle("Sucesso");
                builder1.setMessage("Quantidade atualizada");
                builder1.setCancelable(true);

                builder1.setView(imageOK);

                final AlertDialog alert11 = builder1.create();
                alert11.show();

                new CountDownTimer(1000, 500) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub

                        alert11.dismiss();
                        finishActivity();
                    }
                }.start();




            } else {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditActivity.this);
                builder1.setTitle("Erro");
                builder1.setMessage("Na atualização das quantidades de carga");
                builder1.setCancelable(true);

                builder1.setView(image);

                final AlertDialog alert11 = builder1.create();
                alert11.show();

                new CountDownTimer(1000, 500) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub

                        alert11.dismiss();
                    }
                }.start();


                System.out.println("Erro");

            }


        }

        // Processa descarga de um item

        if(op == 2) {

            String mv_resultado = response.substring(1,response.length()-1);
            if(mv_resultado.equals("OK")) {

                dialogoOK("Sucesso","Item transferido",true);

            } else {
                dialogoErro("Erro",mv_resultado,true);
            }

        }

        // Criar linha de carga a partir da introducao/leitura do código de barras

        if(op == 3) {

            String mv_resultado = response.substring(1,response.length()-1);

            if(mv_resultado.equals("0")) dialogoOK("Sucesso","Item transferido", true);
            else Dialogos.dialogoErro("Erro",mv_resultado,6,EditActivity.this,true);
                // dialogoErro("Erro",mv_resultado,true);

        }

        // invof regista leitura
        if(op == 4) {

            String mv_resultado = response.substring(1,response.length()-1);

            if(mv_resultado.equals("OK")) dialogoOK("Sucesso","Contagem registada", true);
            else dialogoErro("Erro",mv_resultado,true);

        }

        if (op == 5) {

            asyncResponseCriaPickingBi(response,op);
        }

        if (op == 6) {

            asyncResponseRegistaDevExp(response,op);
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

            //fconcversao = Globals.getInstance().getArtigos().get(bundle.getInt("index")).getFconversao();

            switch (view.getId()) {

                case R.id.editText_edit_quantidades:

                    // se o campo quantidade ficar vazio a quantidade lida fica a zero
                    if (editText_quantidade.getText().toString().isEmpty())
                    {
                        qtd_lida = 0.0;
                        qtdalt_lida = 0.0;
                        return;
                    }

                    // se o campo quantidade ficar com ponto a quantidade lida fica a zero

                    if (editText_quantidade.getText().toString().equals(".")) {
                        qtd_lida = 0.0;
                        qtdalt_lida = 0.0;
                        return;
                    }


                    Double stock_lido;

                    // se está em modo de quantidade principal
                    if (!FLAG_MODO_UNIDADE_ALT)
                    {

                        System.out.println("FLAG MODO UNIDADE ALT"+FLAG_MODO_UNIDADE_ALT);

                        qtd_lida    = Double.parseDouble(editText_quantidade.getText().toString());
                        qtdalt_lida = (1 / dconversao) * Double.parseDouble(editText_quantidade.getText().toString());
                        System.out.println("Valida Stock:"+validaStock);
                        System.out.println("Stock:"+stock);
                        System.out.println("Qtd lida:"+qtd_lida);

                        // se valida stock e o stock principal é inferior à quantidade lida
                        if (validaStock && stock < qtd_lida) {

                            FLAG_QTT_IS_VALID = false;

                            editText_quantidade.setText("");
                            dialogoErro();
                        }
                        else FLAG_QTT_IS_VALID = true  ;
                    }

                    else {

                        System.out.println("FLAG MODO UNIDADE ALT: "+FLAG_MODO_UNIDADE_ALT);


                        qtd_lida = dconversao * Double.parseDouble(editText_quantidade.getText().toString());
                        qtdalt_lida = Double.parseDouble(editText_quantidade.getText().toString());

                        System.out.println("Valida Stock:"+validaStock);
                        System.out.println("Stock ALt:"+stockAlt);
                        System.out.println("Qtd Alt lida:"+qtdalt_lida);

                        // se valida stock e o stock alternativo é inferior à quantidade lida
                        if (validaStock && stockAlt < qtdalt_lida) {

                            FLAG_QTT_IS_VALID = false;

                            editText_quantidade.setText("");
                            dialogoErro();
                        }
                        else FLAG_QTT_IS_VALID = true;
                    }


                    break;

            }

        }

        public void afterTextChanged(Editable editable) {


        }

    }

    private void dialogoErro(String title, String subtitle, final boolean terminar) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(EditActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(false);

        builder1.setView(image);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();


        new CountDownTimer(1500, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                alertErro.dismiss();
                if (terminar){
                    finish();
                }
            }
        }.start();

    }

    private void dialogoInfo(String title, String subtitle) {

        ImageView image_info = new ImageView(getApplication());

        image_info.setImageResource(R.mipmap.ic_info);


        AlertDialog.Builder builderInfo = new AlertDialog.Builder(EditActivity.this);
        builderInfo.setTitle(title);
        builderInfo.setMessage(subtitle);
        builderInfo.setCancelable(false);

        builderInfo.setView(image_info);

        final AlertDialog alertInfo = builderInfo.create();
        alertInfo.show();

        new CountDownTimer(1500, 500) {

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

    private void dialogoOK(String title, String subtitle, final boolean terminar) {



        ImageView imageOK = new ImageView(EditActivity.this);
        imageOK.setImageResource(R.mipmap.ic_ok);


        AlertDialog.Builder builderOK = new AlertDialog.Builder(EditActivity.this);
        builderOK.setTitle(title);
        builderOK.setMessage(subtitle);
        builderOK.setCancelable(false);

        builderOK.setView(imageOK);

        final AlertDialog alertInfo = builderOK.create();
        alertInfo.show();

        new CountDownTimer(1500, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                alertInfo.dismiss();
                if (terminar){
                    finishActivity();
                }
            }
        }.start();


    }

    private void carregaInformacaoEcra() {
        // Carrega a informação lida e coloca nos objectos do ecrã

        Log.e("carregaInformacaoEcra","inicio");

        textView_leg_Ref.setVisibility(View.GONE);
        textView_referencia.setVisibility(View.GONE);
        textView_leg_descricao.setVisibility(View.GONE);

        String mTxtLocalizacao  ="["+armazem+"]";

        System.out.println("Zona:"+zona);
        System.out.println("Alveolo:"+zona);

        if (!(zona.equals("") &&  alveolo.equals(""))) {
            mTxtLocalizacao = mTxtLocalizacao+"["+zona.trim()+"]["+alveolo.trim()+"]";
        }

        textview_localizacao.setText(mTxtLocalizacao);


        if (dconversao == 1.0) {

            textView_stock.setText(stock + " " + unidade);
            textView_unidade.setText(unidade);
            textView_qtlida.setText(qtd_lida + " " + unidade);
            radioButton_un.setChecked(true);
            FLAG_MODO_UNIDADE_ALT = false;
            radioGroup_edit.setVisibility(View.GONE);

            if (validaStock) {
                if (stock < qtd_lida) {
                    editText_quantidade.setText("");
                    FLAG_QTT_IS_VALID = false;
                } else {
                    editText_quantidade.setText(String.valueOf(qtd_lida));
                    FLAG_QTT_IS_VALID = true;
                }
            }

            // vamos colocar por defeito sempre a quantidade a zero 2020-03-05

            editText_quantidade.setText("");
            FLAG_QTT_IS_VALID = false;


        } else {


            textView_stock.setText(stock + " " + unidade + " / " + stockAlt  + " " + unidadeAlt);
            textView_unidade.setText(unidadeAlt);
            textView_qtlida.setText(qtd_lida + " " + unidade + " / " + qtdalt_lida  + " " + unidadeAlt);
            radioGroup_edit.setVisibility(View.VISIBLE);
            radioButton_unAlt.setChecked(true);
            FLAG_MODO_UNIDADE_ALT = true    ;

            if (validaStock) {
                if (stock < qtd_lida) {
                    editText_quantidade.setText("");
                    FLAG_QTT_IS_VALID = false;
                } else {
                    editText_quantidade.setText(String.valueOf(qtdalt_lida));
                    editText_quantidade.selectAll();
                    FLAG_QTT_IS_VALID = true;
                }
            }

            // vamos colocar por defeito sempre a quantidade a zero 2020-03-05
            editText_quantidade.setText("");
            FLAG_QTT_IS_VALID = false;
        }

        textView_of.setText(lote);

        textView_descricao.setText(ref.trim()+" - "+design.trim());

        radioButton_un.setText(unidade);
        radioButton_unAlt.setText(unidadeAlt);



        if (atividadeOrigem.equals("invof")) {

            getSupportActionBar().setTitle("Inventário OF");
            getSupportActionBar().setSubtitle("");

            if (bundle.getBoolean("editing_listview")) {
                textView_edit_titulo_qt.setText("Alterar contagem");
            }
            else {
                textView_edit_titulo_qt.setText("Adicionar contagem");
            }

            // textView_leg_stock.setText("Stock actual:");
            textView_leg_qtlida.setText("Qtd contada");

            textView_descricao.setText(ref.trim()+" - "+design.trim());

            // textView_referencia.setText(ref);

            radioGroup_edit.setVisibility(View.GONE);

            textView_qtlida.setText(qtd_lida + " " + unidade);
            textView_unidade.setText(unidade);

            FLAG_MODO_UNIDADE_ALT = false;

            editText_quantidade.selectAll();

        }


        if (atividadeOrigem.equals("inventario")){

            if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)) {
                getSupportActionBar().setTitle("Inventário de Localização");

                // se o inventario de localização for localização de consumo de of então vamos ativar o botao de unidade principal
                if (tipoinvloc.equals("locconsumoof")) {
                    radioButton_un.setChecked(true);
                    textView_unidade.setText(bundle.getString("unidade"));
                    FLAG_MODO_UNIDADE_ALT = false;
                }
            }
            if (tipo_inventario.equals(TIPO_INVENTARIO_REF)) {
                getSupportActionBar().setTitle("Inventário por referência");
            }

            getSupportActionBar().setSubtitle("");

            textView_leg_qtlida.setText("Qtd contada:");

            if (!bundle.getBoolean("editing_listview"))
                textView_edit_titulo_qt.setText("Adicionar contagem");
            else
                textView_edit_titulo_qt.setText("Alterar contagem");

        }

        // Definir a localização por defeito para todas as situações


        // define localização no caso de ser modo descarga numa transferencia

        if (atividadeOrigem.equals("transferencia") && bundle.getString("trf_mode").equals("descarga")) {
            textview_localizacao.setText("TERMINAL MOVEL");
        }


        if (atividadeOrigem.equals("invloc")) {

            mTxtLocalizacao  ="["+armazem.toString()+"]";

            if (!(zona_dest.equals("") && alveolo_dest.equals(""))) {
                mTxtLocalizacao = mTxtLocalizacao+"["+zona_dest.trim()+"]["+alveolo_dest.trim()+"]";
            }
            textview_localizacao.setText(mTxtLocalizacao);
            textView_leg_stock.setText("Stock contado:");
            textView_stock.setText(qtd_lida + " " + unidade + " / " + qtdalt_lida  + " " + unidadeAlt);

            textView_leg_qtlida.setVisibility(View.GONE);
            textView_qtlida.setVisibility(View.GONE);

            if (!bundle.getBoolean("editing_listview"))
                textView_edit_titulo_qt.setText("Adicionar contagem");
            else
                textView_edit_titulo_qt.setText("Alterar contagem");

        }


        if (atividadeOrigem.equals(PedidoAbastProdActivity.class.getSimpleName())) {

            textView_leg_stock.setText("Qtd pendente:");
            textView_leg_localizacao.setVisibility(View.GONE);
            textview_localizacao.setVisibility(View.GONE);
            textView_leg_lote.setVisibility(View.GONE);
            textView_lote.setVisibility(View.GONE);

        }

        Log.e("Carrega informação ecrã","fim");

    }

    private void carregaInfoBundle() {
        // Carrega a informação do bundle nas variáveis a usar neste ecrã

        Log.e("carregaInfoBundle","inicio");

        user = bundle.getString("user");
        // stamp do documento de carga
        bostamp_carga = bundle.getString("bostamp_carga");
        bostamp_descarga = bundle.getString("bostamp_descarga");
        bistamp_carga = bundle.getString("bistamp_carga");
        System.out.println("bistamp_carga:"+bistamp_carga);


        ref = bundle.getString("ref");
        design = bundle.getString("design");
        if (TextUtils.isEmpty(ref))  ref = "";
        if (TextUtils.isEmpty(design))  design = "";

        lote = bundle.getString("lote");

        armazem = bundle.getString("armazem_ori");
        zona = bundle.getString("zona_ori");
        alveolo = bundle.getString("alveolo_ori");

        armazem_dest =bundle.getString("armazem_dest");
        zona_dest = bundle.getString("zona_dest");
        alveolo_dest = bundle.getString("alveolo_dest");

        if (TextUtils.isEmpty(zona)) zona = "";
        if (TextUtils.isEmpty(alveolo)) alveolo = "";

        if (TextUtils.isEmpty(zona_dest)) zona_dest = "";
        if (TextUtils.isEmpty(alveolo_dest)) alveolo_dest = "";


        System.out.println("Armazem :"+armazem );
        System.out.println("Zona :"+zona );
        System.out.println("Alveolo:"+alveolo);

        dconversao = bundle.getDouble("fconversao");
        System.out.println("Fconversao :"+dconversao );

        qtd_lida = bundle.getDouble("qtd_lida");
        qtdalt_lida = bundle.getDouble("qtdAlt_lida");

        System.out.println("Carrega info bundle: qtd_lida :"+qtd_lida );

        // agora vamos forçar a quantidade alternativa pois pode ter ficado erradamente registada na linha
        // arredondando a 4 casas

        if ( dconversao != 0){
            qtdalt_lida = Math.round(qtd_lida*1000.0/dconversao)/1000.0;
        }

        stock = bundle.getDouble("stock");
        stockAlt= bundle.getDouble("stockAlt");

        System.out.println("stock :"+stock );

        // se não estivermos em modo de editar quer dizer que passamos
        // a quantidade de stock disponivel, pelo que vamos arredondar a 4 casas

        if (!bundle.getBoolean("editing_listview")) {
            stockAlt = Math.round(stockAlt*1000.0)/1000.0;
        }

        unidade = bundle.getString("unidade");
        unidadeAlt = bundle.getString("unidadeAlt");
        validaStock =bundle.getBoolean("valida_stock");
        atividadeOrigem = bundle.getString("atividade");

        tipo_inventario = bundle.getString("tipo_inventario");
        tipoinvloc   = bundle.getString("tipoinvloc");

        qtt = bundle.getDouble("qtt");
        qtt2 = bundle.getDouble("qtt2");

        System.out.println("Valida Stock :"+validaStock );
        System.out.println("Editing list view: "+bundle.getBoolean("editing_listview"));

        Log.e("carregaInfoBundle","FIM");


    }

    private void lerStockInvLoc(){
        // PARA JÁ ESTÁ INATIVO!!!!!
        // AINDA NÃO UTILIZADO

        // lê o stock no armazem de destino de modo a dar indicação do que já foi inventariado
        // esta atividade só deve ser executada quando estamos a editar as linhas, depois de receber a quantidade lida

        JSONArray jsonArrayLerStock = WsExecute.lerStock(ref,lote, Integer.parseInt(armazem_dest),zona_dest,alveolo_dest,EditActivity.this);

        if (jsonArrayLerStock.equals(JsonArrayVazio)) {
            Dialogos.dialogoErro("Erro", "Nao existe no sistema a referencia/lote seleccionados",6,EditActivity.this,true);
            return;
        }

        try {

            int i = 0;

            JSONObject c = jsonArrayLerStock.getJSONObject(i);

            ref = c.getString("ref");
            lote = c.getString("lote");
            design = c.getString("design");
            unidade = c.getString("unidade");
            dconversao = Double.parseDouble(c.getString("fconversao")) ;
            unidadeAlt = c.getString("uni2");
            stockAlt = Double.parseDouble(c.getString("stock2")) ;
            stock = Double.parseDouble(c.getString("stock")) ;

            // depois de lermos o stock disponivel que
            stock = stock+qtd_lida;
            stockAlt = stockAlt +qtdalt_lida;

            // agora vamos calcular o valor arredondado a 4 casas decimais

            stockAlt = Math.round(stockAlt*1000.0) / 1000.0;


        } catch (final JSONException e) {

            Log.e("", "Erro Conversão: " + e.getMessage());

        }
    }

    private void asyncResponseCriaPickingBi(String response, int op) {

        String mv_resultado = response.substring(1,response.length()-1);

        if(mv_resultado.equals("OK")) dialogoOK("Sucesso","Item transferido", true);
        else Dialogos.dialogoErro("Erro",mv_resultado,6,EditActivity.this,true);
        // dialogoErro("Erro",mv_resultado,true);
    }

    private void asyncResponseRegistaDevExp(String response, int op) {

        String mv_resultado = response.substring(1,response.length()-1);

        if(mv_resultado.equals("OK")) dialogoOK("Sucesso","Item transferido", true);
        else Dialogos.dialogoErro("Erro",mv_resultado,6,EditActivity.this,true);
        // dialogoErro("Erro",mv_resultado,true);
    }

    private void criaPickingBi(String bostamp,String qtt,String ref, String lote, String armazem, String zona, String alveolo, String user,
                               String obistamp, String oobistamp) {

        AsyncRequest mAsynRequest = new AsyncRequest(thisActivity, 3);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() +
                "/TerminalPHC_INOVEPLASTIKA/Service1.svc/criaPickingBI?").buildUpon()

                .appendQueryParameter("bostamp", bostamp_carga)
                .appendQueryParameter("qt", String.valueOf(qtd_lida))
                .appendQueryParameter("ref", ref.trim())
                .appendQueryParameter("lote", lote.trim())

                .appendQueryParameter("armazem_ori", armazem)
                .appendQueryParameter("zona_ori", zona)
                .appendQueryParameter("alveolo_ori", alveolo)

                .appendQueryParameter("user", user)
                .appendQueryParameter("obistamp", Objects.toString(bundle.getString("obistamp"),""))
                .appendQueryParameter("oobistamp", Objects.toString(bundle.getString("oobistamp"),"") )

                .build();

        mAsynRequest.execute(builtURI_processaPedido.toString());
    }

    private void registaPedidoAbastProd() {

      Double qttAdicionar = qtd_lida - (qtt-qtt2);

        DataModelBi bi = new DataModelBi();

        bi.setBistamp(bistamp_carga);
        bi.setQtt(qttAdicionar);
        bi.setOperador(Integer.parseInt(user));
        bi.setArmazem(Integer.parseInt(armazem));

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
                                Dialogos.dialogoInfo("Sucesso","Alteração registada!",1.0,thisActivity,true);
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


       /* Double NewQtt = qtd_lida + (qtt-qtt2);

        RequestQueue queue = Volley.newRequestQueue(thisActivity);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/atualizaQts?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_BISTAMP, bistamp_carga)
                .appendQueryParameter(QUERY_PARAM_QT, String.valueOf(NewQtt))
                .appendQueryParameter(QUERY_PARAM_QTALT, "0")
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onSuccess(String response) {

                        if (response.equals("true")) {

                            Dialogos.dialogoInfo("Sucesso","Quantidade atualizada",2.0,thisActivity,true);

                        }
                        else {
                            Dialogos.dialogoErro("Erro","Erro na atualização da quantidade",2,thisActivity,false);
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

        queue.add(stringRequest);*/

    }

    private void finishActivity() {

        EditTextOps.hideKeyboard(thisActivity,editText_quantidade);
        finish();
    }
}
