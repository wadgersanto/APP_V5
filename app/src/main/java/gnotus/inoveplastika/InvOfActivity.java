package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.LerAlveoloWs;
import gnotus.inoveplastika.API.Phc.DeleteBoWs;
import gnotus.inoveplastika.API.Producao.DevolveLinhasInvOfWs;
import gnotus.inoveplastika.API.Producao.DevolveStampInvOfWs;
import gnotus.inoveplastika.API.Producao.LerInfoOf2Ws;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Producao.InfoOfDataModel;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Logistica.DataModelAlv;


public class InvOfActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete, View.OnClickListener {

    // Atividade para terminar OF

    private static final String clear = "";

    private static final String MYFILE = "configs";

    // AlertDialog dialog;
    Bundle bundle = new Bundle();
    Bundle bundlerecebido = new Bundle();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView textView_operador, textView_localizacao,textView_of, textView_of_titulo,textView_localizacao_titulo,
            textViewInfo1a,textViewInfo1b,textViewInfo2a,textViewInfo2b;
    private EditText editText_leitura;
    private Button button_terminar;

    private ListView listView;
    private ArrayList<DataModelBi> lista = new ArrayList<>();
    // private ArrayList<Integer> listArmsMontagem = new ArrayList<>();
    private ArrayList<InfoOfDataModel> listaInfoOf = new ArrayList<>();

    private DataModelArrayAdapterBi adapter;

    private int armazem = 0;
    private String zona = "", alveolo = "";

    private String bostamp_inv_of = "";

    private String ref_lida, lote_lido = "", unidade = "", uni2 = "", stdesign = "";
    private double qtd_lida = 0, fconversao = 1;
    private boolean ref_usalote,isOfMontagem;

    private String FLAG_MODO_EDITTEXT = "OPERADOR";

    private Activity thisActivity = InvOfActivity.this;

    private ProgressDialog progDailog ;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLeituraBarCodeListener;
    private EditTextBarCodeReader ediTextLeituraBarCodeReader;

// !!!! UTILIZA O LAYOUT DO BI_USER_LOC


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bi_userloc);

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Terminar OF - Injeção");
        getSupportActionBar().setSubtitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initNavigationDrawer();

        textViewInfo1a = findViewById(R.id.textView_Info1a);
        textViewInfo1b = findViewById(R.id.textView_Info1b);
        textViewInfo2a = findViewById(R.id.textView_Info2a);
        textViewInfo2b = findViewById(R.id.textView_Info2b);

        textViewInfo1a.setVisibility(View.GONE);
        textViewInfo1b.setVisibility(View.GONE);
        textViewInfo2a.setVisibility(View.GONE);
        textViewInfo2b.setVisibility(View.GONE);

        editText_leitura = (EditText) findViewById(R.id.editText_leitura);
        textView_localizacao = (TextView) findViewById(R.id.textView_carga_localizacao);
        textView_localizacao_titulo = (TextView) findViewById(R.id.textView_carga_titulo_localizacao);
        textView_localizacao.setOnClickListener(this);

        textView_of = findViewById(R.id.textView_of);
        textView_of_titulo = findViewById(R.id.textView_of_titulo);
        textView_of.setOnClickListener(this);

        textView_operador = (TextView) findViewById(R.id.textView_carga_operador);
        listView = (ListView) findViewById(R.id.listView_linhasbi);

        button_terminar = (Button) findViewById(R.id.button_biuserloc_terminar);
        button_terminar.setText("Lançar inventário");

        // editText_leitura.addTextChangedListener(new MyTextWatcher(editText_leitura));
        // editText_leitura.setInputType(InputType.TYPE_NULL);


        defineEditTextLeituraBarCodeListener();
        ediTextLeituraBarCodeReader = new EditTextBarCodeReader(editText_leitura,thisActivity);
        ediTextLeituraBarCodeReader.setOnGetScannedTextListener(editTextLeituraBarCodeListener);
        editText_leitura.setOnClickListener(this);


        bundlerecebido = this.getIntent().getExtras();

        isOfMontagem = bundlerecebido.getBoolean("ofmontagem");

        if (isOfMontagem) {
            getSupportActionBar().setTitle("Terminar OF Montagem");
            textView_localizacao.setVisibility(View.GONE);
            textView_localizacao_titulo.setVisibility(View.GONE);
            button_terminar.setText("Terminar OF");
            button_terminar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
        else {

            textView_of.setVisibility(View.GONE);
            textView_of_titulo.setVisibility(View.GONE);
        }


        button_terminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ao terminar vamos executar o script

                String txtMessage;

                // validação de dados

                //*************************************************************************
                if (textView_operador.getText().toString().trim().isEmpty()) {
                    Dialogos.dialogoErro("Finalizar OF", "Existem dados em falta: Operador", 3, InvOfActivity.this, false);
                    return;
                }


                if (isOfMontagem) {

                    if (textView_of.getText().toString().trim().isEmpty()) {
                        Dialogos.dialogoErro("Finalizar OF", "Existem dados em falta: OF", 3, InvOfActivity.this, false);
                        return;
                    }

                    terminaOFM();
                    return;

                }
                else {

                    if (textView_localizacao.getText().toString().trim().isEmpty()) {
                        Dialogos.dialogoErro("Finalizar OF", "Existem dados em falta: Localização", 3, InvOfActivity.this, false);
                        return;
                    }

                    // se lista não estiver vazia vamos verificar se todas as linhas foram contadas


                    if (!lista.isEmpty()) {
                        for (int i = 0; i < lista.size(); i++) {
                            if (!lista.get(i).getJacontou() && lista.get(i).getQtt() == 0 ) {
                                Dialogos.dialogoErro("Atenção", "Existem linhas que não foram contadas "+lista.get(i).getReferencia(), 3, InvOfActivity.this, false);
                                return;
                            }
                        }
                    }

                }

                //*************************************************************************



                txtMessage = "Deseja finalizar as OFs?";


                AlertDialog.Builder builder = new AlertDialog.Builder(InvOfActivity.this);
                builder.setMessage(txtMessage);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        String mRetval;
                        // se a lista estiver vazia vamos finalizar a OF sem necessidade de acertos de inventários porque não existem consumos

                        if (lista.isEmpty()) {


                            Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/FinalizarOf?").buildUpon()
                                    .appendQueryParameter("armazem", String.valueOf(armazem))
                                    .appendQueryParameter("zona", zona)
                                    .appendQueryParameter("alveolo", alveolo)
                                    .appendQueryParameter("operador", textView_operador.getText().toString())
                                    .build();

                            AsyncRequest finalizarOf = new AsyncRequest(InvOfActivity.this,99 );


                            finalizarOf.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
                                @Override
                                public void asyncResponse(String response, int op) {

                                    String mRetval;

                                    try {
                                        mRetval = response.substring(1, response.length() - 1);
                                    }
                                    catch (Exception ei) {
                                        mRetval ="Erro na execução do webservice FinalizarOf";
                                    }

                                    if (mRetval.equals("OK")) {

                                        Dialogos.dialogoInfo("Sucesso", "OF finalizada!", 2.0, InvOfActivity.this, false);

                                        if (!lista.isEmpty()) {
                                            lista.clear();
                                            adapter.notifyDataSetChanged();
                                        }
                                        bostamp_inv_of = "";
                                        textView_localizacao.setText("");
                                        textView_operador.setText("");
                                        defineEditTextHint(false);
                                    } else {
                                        mRetval = mRetval.substring(6, mRetval.length());
                                        Dialogos.dialogoErro("Erro ao finalizar OF ", mRetval, 60, InvOfActivity.this, false);
                                        defineEditTextHint(false);
                                    }


                                }
                            });

                            finalizarOf.execute(builtURI_processaPedido.toString());

                        }
                        // se faz inventário

                        else
                        {
                            AsyncRequest procAcertoInvAsynRequest = new AsyncRequest(InvOfActivity.this,3 );

                            Uri builtURI_procAcertoInv = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ProcessaAcertoInventario?").buildUpon()
                                    .appendQueryParameter("bostamp_inventario", bostamp_inv_of)
                                    .appendQueryParameter("tipoinventario", "OF")
                                    .build();

                            procAcertoInvAsynRequest.execute(builtURI_procAcertoInv.toString());

                            // suspenso processo anterior mas que não executa o spinner


                        }


                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog myDialog = builder.create();
                myDialog.show();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                bundle.clear();

                // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
                bundle.putBoolean("editing_listview", true);
                bundle.putString("trf_mode", "");
                bundle.putString("bostamp_carga", bostamp_inv_of);
                // bundle.putString("ref", adapter.getItem(position).getReferencia());
                bundle.putString("ref", adapter.getItem(position).getRef());
                bundle.putString("lote", adapter.getItem(position).getLote());
                bundle.putString("design", adapter.getItem(position).getDesign());
                bundle.putDouble("stock", adapter.getItem(position).getQtt2());

                bundle.putString("armazem_ori", String.valueOf(armazem));
                bundle.putString("zona_ori", zona);
                bundle.putString("alveolo_ori", alveolo);


                // ao chamar a atividade de editar quantidade no inventário por of só queremos usar a unidade principal
                bundle.putDouble("stockAlt", 0);
                bundle.putDouble("fconversao", 1);

                bundle.putDouble("qtd_lida",  adapter.getItem(position).getQtt());
                bundle.putDouble("qtdAlt_lida", 0);
                bundle.putString("unidade", adapter.getItem(position).getUnidade());

                bundle.putBoolean("valida_stock", false);
                bundle.putString("atividade", "invof");

                bundle.putString("bistamp_carga", adapter.getItem(position).getBistamp());

                Intent EditIntent = new Intent(InvOfActivity.this, EditActivity.class);
                EditIntent.putExtras(bundle);
                startActivity(EditIntent);

            }
        });

        defineEditTextHint(true);
    }



    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                Globals.getInstance().defineAcaoMenu(menuItem, InvOfActivity.this, drawerLayout, R.id.navigation_config_ip);

                return true;

            }
        });


        navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(2).setChecked(true);


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

    public void requestFocus(View view) {

    }

    public void onBackPressed() {

        if (! lista.isEmpty()) {
            WsExecute.EliminaDossier(bostamp_inv_of,"BO",false,InvOfActivity.this);
        }

        finish();
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

        //adapter.notifyDataSetChanged();

        if (lista.isEmpty()) {
            textView_localizacao.setText(clear);
            lista.clear();
        }

        if (!bostamp_inv_of.isEmpty()) {
            DevolveLinhasInvOf(bostamp_inv_of, false);
        }

        defineEditTextHint(true);

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


        if (v.getId() == R.id.editText_leitura) {

            if (FLAG_MODO_EDITTEXT.equals("LOTE")) {
                FLAG_MODO_EDITTEXT = "CODIGO";
            }

            if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {
                FLAG_MODO_EDITTEXT = "CODIGO";
            }

            System.out.println("DefineTextHint - onClick");
            defineEditTextHint(true);
            requestFocus(editText_leitura);
        }

        System.out.println("On click");

        if(v.getId() == R.id.textView_carga_localizacao ){


            if (textView_localizacao.getText().toString().equals("")) return;

            if (lista.isEmpty()) {
                textView_localizacao.setText(clear);
                defineEditTextHint(true);
                return;
            }

            String alertDialogMessage;

            alertDialogMessage = "Quer limpar a localizaçao? Nota: vai perder o inventario realizado";

            AlertDialog.Builder builder = new AlertDialog.Builder(InvOfActivity.this);
            builder.setMessage(alertDialogMessage);

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {


                    DeleteBoWs deleteBoWs = new DeleteBoWs(thisActivity);
                    deleteBoWs.setOnDeleteBoListener(new DeleteBoWs.OnDeleteBoListener() {
                        @Override
                        public void onSuccess(DataModelWsResponse wsResponse) {

                            if (!lista.isEmpty()) {
                                lista.clear();
                                adapter.notifyDataSetChanged();
                            }
                            bostamp_inv_of ="";
                            textView_localizacao.setText(clear);
                            defineEditTextHint(true);

                            if (!(wsResponse.getCodigo() == 0)) {
                                Dialogos.dialogoErro("Erro na execução do pedido",wsResponse.getDescricao(),5,thisActivity,false);
                            }

                        }

                        @Override
                        public void onError(VolleyError error) {

                        }
                    });
                    deleteBoWs.execute(bostamp_inv_of,false);

                    // WsExecute.EliminaDossier(bostamp_inv_of,"BO",false,InvOfActivity.this);

                   /* AsyncRequest request = new AsyncRequest(thisActivity,99);

                    Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                            .appendQueryParameter("bostamp",bostamp_inv_of)
                            .appendQueryParameter("tabela","BO")
                            .appendQueryParameter("onlyifnolines",String.valueOf(false))
                            .build();

                    request.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
                        @Override
                        public void asyncResponse(String response, int op) {

                            if (!lista.isEmpty()) {

                                lista.clear();
                                adapter.notifyDataSetChanged();

                            }
                            bostamp_inv_of ="";
                            textView_localizacao.setText(clear);
                            defineEditTextHint(true);

                        }
                    });

                    request.execute(uri.toString());
*/
/*                    String mResultado = "";


                    if (!lista.isEmpty()) {

                        lista.clear();
                        adapter.notifyDataSetChanged();

                    }
                    bostamp_inv_of ="";
                    textView_localizacao.setText(clear);
                    defineEditTextHint(true);*/


                }
            });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            AlertDialog myDialog = builder.create();
            myDialog.show();

        }

        if(v.getId() == R.id.textView_carga_operador ){
            FLAG_MODO_EDITTEXT = "OPERADOR";
            textView_operador.setText("");
            defineEditTextHint(true);
        }

        if(v.getId() == R.id.textView_of ){
            textView_of.setText("");
            defineEditTextHint(true);
        }

    }

    @Override
    public void asyncResponse(String response, int op) {

        ImageView image = new ImageView(InvOfActivity.this);
        image.setImageResource(R.mipmap.ic_notok);

        if (response == null) {
            Dialogos.dialogoErro("Erro","Ocorreu um erro no processamento do pedido ao serviço",5,InvOfActivity.this, false);
            editText_leitura.setText(clear);
            return;
        }

        String tmpResposta;
        tmpResposta = response.substring(1, response.length() - 1);

        // op == 0  Consulta a bd para devolve o stamp do inventário de produção desta localização, caso exista


        //*************************************************************************
        // Criação de cabeçalho do inventário de produção
        if (op == 1) {

            if (tmpResposta.equals("")) {
                Dialogos.dialogoErro("Erro","Erro na criação do cabeçalho do documento",5,InvOfActivity.this, false);
                textView_localizacao.setText("");
                defineEditTextHint(false);
                return;
            } else {
                bostamp_inv_of = tmpResposta;
                DevolveLinhasInvOf(bostamp_inv_of, true);
            }

            // vamos agora carregar a informação nas linhas do ecrã

        }


        //*************************************************************************
        if (op == 3) {

            if (tmpResposta.equals("OK")) {

                Dialogos.dialogoInfo("Sucesso", "Inventário lançado!", 2.0, InvOfActivity.this, false);

                if (!lista.isEmpty()) {
                    lista.clear();
                    adapter.notifyDataSetChanged();
                }
                bostamp_inv_of = "";
                textView_localizacao.setText("");
                textView_operador.setText("");
                defineEditTextHint(false);
            } else {
                Dialogos.dialogoErro("Erro no lançamento de inventário", tmpResposta, 60, InvOfActivity.this, false);
            }
        }

        //*** Processa fecho de OFM

        if (op == 4) {

            if (tmpResposta.equals("OK")) {

                Dialogos.dialogoInfo("Sucesso", "OF finalizada!", 2.0, InvOfActivity.this, false);

                textView_operador.setText("");
                textView_of.setText("");
                defineEditTextHint(false);

            } else {
                Dialogos.dialogoErro("Erro no fecho da OF :", tmpResposta, 60, InvOfActivity.this, false);
            }
        }

        // obter array com os dados da OF
        if (op == 5) {
            // processaLeituraArrayOFM(response);
        }

        //*********************************************************************************************************************
        // Processa a leitura da informação da referencia via webservice
        if (op == 6) {

            JSONconverter jsonConverter = new JSONconverter();
            response = jsonConverter.ConvertJSON(response);

            // se os dados vierem vazios
            if (response.equals("[]")) {
                Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + ref_lida, 10, thisActivity, false);
                ref_lida = "";
                defineEditTextHint(false);
                return;
            }

            try {

                JSONArray arrayST = new JSONArray(response);

                if (arrayST.length() == 0) {
                    Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + ref_lida, 10, thisActivity, false);
                    ref_lida = "";
                    return;
                }
                else {

                    JSONObject camposST = arrayST.getJSONObject(0);
                    ref_lida = camposST.getString("ref").trim();
                    unidade = camposST.getString("unidade");
                    uni2 = camposST.getString("uni2");
                    fconversao = camposST.getDouble("fconversao");
                    stdesign = camposST.getString("design");
                    ref_usalote = camposST.getBoolean("usalote");

                    if (ref_usalote) {
                        FLAG_MODO_EDITTEXT = "LOTE";
                        defineEditTextHint(true);
                        return;
                    }
                    else {
                        lote_lido = "";
                        RefConsomeNaLoc(ref_lida,lote_lido,8);
                        return;
                    }

                }

            }
                 catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
            return;
            }
        }
        //*********************************************************************************************************************

        //*********************************************************************************************************************
        // Processa a leitura da informação do lote

        if (op == 7) {

            JSONconverter jsonConverter = new JSONconverter();
            response = jsonConverter.ConvertJSON(response);

            // se os dados vierem vazios
            if (response.equals("[]")) {
                Dialogos.dialogoErro("Validação do lote", "Não encontrei no sistema o lote " + lote_lido+ " para " +
                        "a referencia "+ref_lida, 10, thisActivity, false);
                ref_lida = "";
                lote_lido = "";
                FLAG_MODO_EDITTEXT = "CODIGO";
                defineEditTextHint(false);
                return;
            }

            try {

                JSONArray arraySE = new JSONArray(response);

                if (arraySE.length() == 0) {
                    Dialogos.dialogoErro("Validação do lote", "Não encontrei no sistema o lote " + lote_lido+ " para " +
                            "a referencia "+ref_lida, 10, thisActivity, false);
                    ref_lida = "";
                    lote_lido = "";
                    FLAG_MODO_EDITTEXT = "CODIGO";
                    defineEditTextHint(false);
                    return;
                }
                else {

                    JSONObject camposSE = arraySE.getJSONObject(0);
                    lote_lido = camposSE.getString("lote").trim();
                    unidade = camposSE.getString("unidade");
                    uni2 = camposSE.getString("uni2");
                    stdesign = camposSE.getString("design");
                    fconversao = camposSE.getDouble("fconversao");

                    System.out.println("YYYYRef lida: "+ref_lida);
                    System.out.println("YYYYLote lido: "+lote_lido);
                    // tendo o lote sido lido com sucesso vamos verificar se o mesmo pode ser consumido na localização
                    RefConsomeNaLoc(ref_lida,lote_lido,8);

                }

            }
            catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro na validaçaõ do lote", e.getMessage(), 10, thisActivity, false);
                return;
            }
        }


        // processou o pedido para verificar se a referencia + lote consomem na localização
        // se a referencia ou a ref+lote não existirem acabam por não consumir na localização

        if (op == 8) {

            if (! Boolean.valueOf(response)) {
                String mtxtResponse = "A referência "+ref_lida+" não consome na localização ou o lote "+lote_lido+" não foi consumido ou não existe em stock";
                Dialogos.dialogoErro("Validação de dados", mtxtResponse, 10, thisActivity, false);
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0.0;
                FLAG_MODO_EDITTEXT = "CODIGO";
                defineEditTextHint(false);
            }
            else {

                processaLeituraRefInv();
                FLAG_MODO_EDITTEXT = "CODIGO";
            }

        }

    }



    private boolean validaLeituraOperador(String textoLido) {
            /* Esta função valida a leitura do operador. Se tudo estiver ok então carrega os dados do operador
                Senão, devolve falso
            */
        String opPrefixp = "";
        String opNumero = "";

        if (textoLido.length() < 5) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",3,InvOfActivity.this,false);
            return false;
        }

        opPrefixp = textoLido.substring(0, 4);

        if (!opPrefixp.equals("(OP)")) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",3,InvOfActivity.this,false);

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
                Dialogos.dialogoErro("Erro", "Operador não é válido",3,InvOfActivity.this,false);
                return false;
            }

        }

    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        editText_leitura.setText(clear);

        // Quando a OF é de montagem então no define hint temos sempre de ocultar o texto

        // se o operador estiver vazio obriga a que seja lido o operador
        if (textView_operador.getText().toString().isEmpty()) {

            editText_leitura.setHint("Ler operador");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler operador",1.0,InvOfActivity.this,false);
            FLAG_MODO_EDITTEXT = "OPERADOR";
            textView_localizacao.setText(clear);
            textView_of.setText(clear);

            if (isOfMontagem) button_terminar.setVisibility(View.INVISIBLE);

            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            armazem = 0;
            zona = "";
            alveolo = "";
            return;
        }

        // se a localização estiver vazia obriga a que seja lida a localização


        if (textView_localizacao.getText().toString().isEmpty() && ! isOfMontagem) {
            editText_leitura.setHint("Ler localização de Inventário");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler localização de Inventário",2.0,InvOfActivity.this,false);

            System.out.println("Mensagem ler localização inventário");

            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            armazem = 0;
            zona = "";
            alveolo = "";
            return;
        }

        if(textView_of.getText().toString().isEmpty() && isOfMontagem){

            editText_leitura.setHint("Ler OF");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler OF",2.0,InvOfActivity.this,false);

            FLAG_MODO_EDITTEXT = "OF";
            button_terminar.setVisibility(View.INVISIBLE);

            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            armazem = 0;
            zona = "";
            alveolo = "";
            return;
        }

        // se neste ponto a of for de montagem então vamos informar que pode finalizar
        if(isOfMontagem) {
            editText_leitura.setHint("Finalizar OF");
            return;
        }

        System.out.println("Is of montagem: "+isOfMontagem);


        switch (FLAG_MODO_EDITTEXT) {
            case "CODIGO":
                editText_leitura.setHint("Ler codigo");
                // No caso do código não vamos mostrar a mensagem, fica muito repetitivo
                //if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler código",2,InvOfActivity.this,false);
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;
                break;
            case "LOTE":
                lote_lido = "";
                qtd_lida = 0;
                editText_leitura.setHint("Ler lote (Ref:" + ref_lida + ")");
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler lote",2.0,InvOfActivity.this,false);
                break;
            case "QUANTIDADE":
                qtd_lida = 0;
                if (lote_lido.equals("")) {
                    editText_leitura.setHint("Ler Qtd  (" + ref_lida + ") ");
                } else {
                    editText_leitura.setHint("Ler Qtd  (" + ref_lida + ") / (" + lote_lido + ")");
                }
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler quantidade",2.0,InvOfActivity.this,false);
                break;
            default:
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;
                editText_leitura.setHint("");
                break;
        }

    }

    private boolean validaLeituraLocalizacao(String textoLido) {

        String zona_lida, alveolo_lido = "";
        Integer armazem_lido = 0;

        System.out.println("Localização lida " + textoLido);

        // A localização tem de começar por ALV

        String prefixo = textoLido.substring(0, 5);

        if (!prefixo.equals("(ALV)")) {
            return false;
        }

        switch (prefixo) {
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

                armazem_lido = Integer.parseInt(textoLido.substring(5, pos_tag1));
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

        if (textoLido.substring(0, 3).equals("(R)")) {

            System.out.println("Codigo Começa por R ");

            // calcular a posição do lote e da quantidade

            posicao_tag_lote = textoLido.indexOf("(S)");

            System.out.println(posicao_tag_lote);

            // vamos verificar se existe alguma posicao numerica

            for (int i = 0; i < 10; ++i) {
                if (posicao_tag_qtd == 0) {
                    posicao_tag_qtd = textoLido.indexOf("(Q" + String.valueOf(i) + ")");
                    if (posicao_tag_qtd != 0) casas_dec_qtd = i;
                }
            }

            System.out.println(posicao_tag_qtd);

            if (posicao_tag_lote == -1){
                lote_lido = "";
                if (posicao_tag_qtd == -1) {
                    qtd_lida = 0;
                    ref_lida = textoLido.substring(3,textoLido.length());
                }
                else{
                    ref_lida = textoLido.substring(3,posicao_tag_qtd);
                }
            }

            if (posicao_tag_qtd == -1) {
                // Aqui sabemos que a posição do lote não é -1
                ref_lida = textoLido.substring(3,posicao_tag_lote);
                lote_lido = textoLido.substring(posicao_tag_lote + 3);

            }
            // sabemos então que tanto o lote como a quantidade têm posição
            else
            {
                if (posicao_tag_lote > posicao_tag_qtd){
                    ref_lida = textoLido.substring(3,posicao_tag_qtd);
                    qtd_lida = Double.parseDouble(textoLido.substring(posicao_tag_qtd + 4, posicao_tag_lote));
                    lote_lido = textoLido.substring(posicao_tag_lote + 3);
                }
                else{
                    ref_lida = textoLido.substring(3, posicao_tag_lote).trim();
                    lote_lido = textoLido.substring(posicao_tag_lote + 3, posicao_tag_qtd);
                    qtd_lida = Double.parseDouble(textoLido.substring(posicao_tag_qtd + 4, textoLido.length()));
                }
            }

            qtd_lida = qtd_lida/Math.pow(10,casas_dec_qtd);

            // Como estamos no inventario OF vou colocar a quantidade lida a 0
            System.out.println("Ref lida" + ref_lida);
            System.out.println("Lote Lido" + lote_lido);
            System.out.println("Qtd Lida" + qtd_lida);


            return "OK";

        } else {
            // Se o código não começa por (R) vamos assumir que lemos a referencia e verificar se a referencia existe
            return "VALIDA_REF";
        }

        // se o código não começa por R então vamos assumir que lemos a referencia do artigo

    }

    private void criaBO(String ndos, String no, String estab, int pArmazem, String pZona, String pAlveolo) {


        AsyncRequest criaBO = new AsyncRequest(InvOfActivity.this, 1);

        Uri builtURI_criaBO = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/CriaDossierInventario?").buildUpon()
                .appendQueryParameter("ndos", ndos)
                .appendQueryParameter("no", no)
                .appendQueryParameter("data", "")
                .appendQueryParameter("estab", estab)
                .appendQueryParameter("user", textView_operador.getText().toString())
                .appendQueryParameter("armazem", Integer.toString(pArmazem))
                .appendQueryParameter("zona1", pZona)
                .appendQueryParameter("alveolo1", pAlveolo)
                .appendQueryParameter("tipoinventario","OF")
                .appendQueryParameter("tipoinvloc", "")
                .build();

        criaBO.execute(builtURI_criaBO.toString());
    }

    private void devolveStampInvOf(int pArmazem, String pZona, String pAlveolo) {


        DevolveStampInvOfWs devolveStampInvOfWs = new DevolveStampInvOfWs(this);

        devolveStampInvOfWs.setmOnDevolveStampInvOfListener(new DevolveStampInvOfWs.OnDevolveStampInvOfListener() {
            @Override
            public void onSuccess(DataModelWsResponse wsResponse) {


                if (wsResponse.getCodigo() == 0) {

                    // se a resposta for vazia (stamp vazio) vamos criar um novo dossier

                    if (wsResponse.getDescricao().trim().equals("")) {
                        criaBO("32", "137", "0", armazem, zona, alveolo);
                    }

                    else {

                        bostamp_inv_of = wsResponse.getDescricao();
                        DevolveLinhasInvOf(bostamp_inv_of, true);
                        return;
                    }
                }
                // se der um erro no pedido
                else {

                    Dialogos.dialogoErro("Erro", wsResponse.getDescricao(), 5, InvOfActivity.this, false);
                    textView_localizacao.setText(clear);
                    defineEditTextHint(false);
                    return;

                }

            }

            @Override
            public void onError(String error) {

            }
        });

        devolveStampInvOfWs.execute(pArmazem,pZona,pAlveolo);


        /*AsyncRequest devolveStampInvOf = new AsyncRequest(InvOfActivity.this, 0);

        Uri builtURI_devolveStampInvOf = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/DevolveStampInvOf?").buildUpon()
                .appendQueryParameter("armazem", Integer.toString(pArmazem))
                .appendQueryParameter("zona", pZona)
                .appendQueryParameter("alveolo", pAlveolo)
                .build();

        devolveStampInvOf.execute(builtURI_devolveStampInvOf.toString());*/

    }


    private void DevolveLinhasInvOf(String pBostamp, boolean pRecalcLinhas) {


        // caso a lista esteja preenchida vamos limpar (para depois carregar novamente

        if (!lista.isEmpty()) {
            lista.clear();
            adapter.notifyDataSetChanged();
        }

        DevolveLinhasInvOfWs devolveLinhasInvOfWs = new DevolveLinhasInvOfWs(this);

        devolveLinhasInvOfWs.setmOnDevolveLinhasInvOfListener(new DevolveLinhasInvOfWs.OnDevolveLinhasInvOfListener() {
            @Override
            public void onSuccess(ArrayList<DataModelBi> linhasInvOf) {

                lista = linhasInvOf;

                if (lista.size() == 0) {
                    Dialogos.dialogoInfo("Terminar OF", "Pode finalizar as OFs nesta localização", 3.0, InvOfActivity.this, false);
                    defineEditTextHint(false);
                    return;
                }
                else {

                    Bundle myBundle = new Bundle();
                    adapter = new DataModelArrayAdapterBi(thisActivity, 0, lista, myBundle);
                    listView.setAdapter(adapter);

                    defineEditTextHint(true);

                }


            }

            @Override
            public void onError(String error) {

            }
        });

        devolveLinhasInvOfWs.execute(pBostamp,pRecalcLinhas);


    }

    private void processaLeituraRefInv() {

        // este procedimento vai chamar o ecrã edit activity carregando os dados necessários para processar a informação

        Double stock = 0.0;
        Double qttContada = 0.00;


        for (int n = 0; n < lista.size(); n++) {
            if (lista.get(n).getReferencia().trim().equals(ref_lida) &&
                    lista.get(n).getLote().trim().equals(lote_lido)) {
                qttContada = qttContada + lista.get(n).getQtt();
                stock = stock + lista.get(n).getQtt2();
            }
        }

        bundle.clear();

        // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
        bundle.putBoolean("editing_listview", false);
        bundle.putString("trf_mode", "");
        bundle.putString("bostamp_carga", bostamp_inv_of);
        bundle.putString("ref", ref_lida);
        bundle.putString("lote", lote_lido);
        bundle.putString("design", stdesign);
        bundle.putDouble("stock", stock);
        bundle.putDouble("stockAlt", 0);
        bundle.putDouble("fconversao", 1);


        bundle.putDouble("qtd_lida", qttContada);
        bundle.putDouble("qtdAlt_lida", 0);
        bundle.putString("unidade", unidade);

        bundle.putString("armazem_ori",String.valueOf(armazem));
        bundle.putString("zona_ori",zona);
        bundle.putString("alveolo_ori",alveolo);

        bundle.putBoolean("valida_stock", false);
        bundle.putString("atividade", "invof");

        Intent EditIntent = new Intent(InvOfActivity.this, EditActivity.class);
        EditIntent.putExtras(bundle);
        startActivity(EditIntent);

    }

    private void leituraOperador() {

        if (validaLeituraOperador(editText_leitura.getText().toString())) {
            // cria o dossier de carga se flag_modo false
            if (isOfMontagem) FLAG_MODO_EDITTEXT = "OF";
            else FLAG_MODO_EDITTEXT = "LOCALIZACAO";

            defineEditTextHint(true);
        }
        else
            defineEditTextHint(false);

        return;

    }

    private void leituraLocalizacao(String textolido) {


        // se a localização lida tem uma estrutura válida de localização então vamos carregar a informação de algum inventário
        // já existente nessa localização. Se não exitir nenhum inventário então vamos criar um novo


        if (!lista.isEmpty()) {
            lista.clear();
            adapter.notifyDataSetChanged();
        }

        if (!validaLeituraLocalizacao(textolido)) {
            Dialogos.dialogoErro("Erro","A localização lida" + textolido + "não é válida",3,InvOfActivity.this,false);
            editText_leitura.setText(clear);
        } else {


            // vamos ler a localização onde estamos a querer encerrar as OFs

            LerAlveoloWs lerAlveoloWs = new LerAlveoloWs(this);

            lerAlveoloWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
                @Override
                public void onSuccess(DataModelAlv pAlveolo) {


                    // se a localização não existe ou se a localização não faz inventário quando a OF termina

                    String txtLocalizacao = "["+armazem+"]["+zona+"]["+alveolo+"]";

                    if (pAlveolo == null ) {

                        Dialogos.dialogoErro("Terminar OF","A localização "+txtLocalizacao+" não existe",5,thisActivity,false);
                        bostamp_inv_of = "";
                        FLAG_MODO_EDITTEXT = "LOCALIZACAO";
                        editText_leitura.setHint("");
                        editText_leitura.setText("");
                        textView_localizacao.setText(clear);
                        defineEditTextHint(false);
                    }

                    if (pAlveolo.getU_noinvfof()) {

                        Dialogos.dialogoErro("Terminar OF","A localização "+txtLocalizacao+" não permite terminar OFs de injeção",5,thisActivity,false);
                        bostamp_inv_of = "";
                        FLAG_MODO_EDITTEXT = "LOCALIZACAO";
                        editText_leitura.setHint("");
                        editText_leitura.setText("");
                        textView_localizacao.setText(clear);
                        defineEditTextHint(false);

                    }
                    else {
                        button_terminar.setText("Lançar inventário");
                        devolveStampInvOf(armazem, zona, alveolo);
                        FLAG_MODO_EDITTEXT = "CODIGO";
                        return;
                    }

                }

                @Override
                public void onError(VolleyError error) {

                }
            });

            lerAlveoloWs.execute(armazem, zona, alveolo);

        }

    }

    private void leituraCodigo(String textolido) {

        // vamos verificar se foi lida novamente a localização

        if (textolido.substring(0, 5).equals("(ALV)")) {

            if (!validaLeituraLocalizacao(textolido)) {
                Dialogos.dialogoErro("Erro","A localização lida" + textolido + "não é válida",3,InvOfActivity.this,false);
                editText_leitura.setText(clear);
            } else {
                devolveStampInvOf(armazem, zona, alveolo);
                FLAG_MODO_EDITTEXT = "CODIGO";
            }
            return;
        }

        String retval = validaLeituraCodigo(textolido);

        // se o codigo começa com R e tem a estrutura correcta então retorna OK
        // caso não começe por R ou não tenha a estrutura correcta assumimos que lemos a referencia e então o lote fica vazio

        if (retval.equals("OK")) {
            Integer operacao = 7;
            LerSeWs(ref_lida,lote_lido,operacao);
            return;
        }


        if (! retval.equals("OK")) {
            Integer operacao = 6;
            lote_lido = "";
            LerStWs(textolido,operacao);
        }

    }

    private void readOf(final String numof) {


        LerInfoOf2Ws lerInfoOf2Ws = new LerInfoOf2Ws(this);

        lerInfoOf2Ws.setOnLerInfoOf2Listener(new LerInfoOf2Ws.OnLerInfoOf2Listener() {
            @Override
            public void onSuccess(InfoOfDataModel infoOf) {

                if (infoOf == null) {
                    Dialogos.dialogoErro("Leitura de OF", "Não encontrei no sistema a OF " + numof, 5, thisActivity, false);
                    textView_of.setText("");
                    defineEditTextHint(false);
                    return;
                }

                // if (! camposOF.getBoolean("iniciada")) {
                if (! infoOf.getIniciada()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+numof+" não foi iniciada", 4, InvOfActivity.this, false);
                    textView_of.setText("");
                    defineEditTextHint(false);
                    return;
                }

                // if (camposOF.getBoolean("finalizada")) {
                if (infoOf.getFinalizada()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+numof+" já está finalizada", 4, InvOfActivity.this, false);
                    textView_of.setText("");
                    defineEditTextHint(false);
                    return;
                }

                //if (!(camposOF.getInt("ndos") == 41)) {

                // se a OF foi aberta numa zona que não está configurada para não fazer inventários no fecho da OF
                // , ou seja, se está numa Zona em que é necessário fazer inventários no fecho da OF, não podemos permitir
                // que possa ser fechada pela opção de OF de Montagem (onde basta ler a OF e fechar)


                if (! infoOf.getU_noinvfof()) {
                    Dialogos.dialogoErro("Leitura de OF", "A OF "+numof+" está numa zona que não permite finalizar OFs de montagem.", 8, InvOfActivity.this, false);
                    textView_of.setText("");
                    defineEditTextHint(false);
                    return;
                }


                textView_of.setText(numof);
                button_terminar.setVisibility(View.VISIBLE);
                Dialogos.dialogoInfo("Terminar OF", "Pode terminar a OF"+numof, 1.0, InvOfActivity.this, false);
                defineEditTextHint(false);


            }
        });

        lerInfoOf2Ws.execute(numof);



    }

    private void leituraQuantidade(String textolido) {

        String txt_qtd_lida = editText_leitura.getText().toString();
        Integer val;
        try {
            val = Integer.valueOf(txt_qtd_lida);
            if (val == null) {
                Dialogos.dialogoErro("Erro", "A quantidade lida" + editText_leitura.getText().toString() + "não é válida",10,InvOfActivity.this,false);
                defineEditTextHint(false);
                return;
            }

        } catch (NumberFormatException e) {
            Dialogos.dialogoErro("Erro", "A quantidade lida" + editText_leitura.getText().toString() + "não é válida",10,InvOfActivity.this,false);
            defineEditTextHint(false);
            return;
        }

        qtd_lida = val;
        FLAG_MODO_EDITTEXT = "CODIGO";

        processaLeituraRefInv();

    }

    private void terminaOFM (){

        AlertDialog.Builder builder = new AlertDialog.Builder(InvOfActivity.this);
        builder.setMessage("Quer terminar a OF");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                AsyncRequest asyncRequestFinalizaOfm = new AsyncRequest(InvOfActivity.this, 4);

                Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/FinalizarOfM?").buildUpon()
                        .appendQueryParameter("numof", textView_of.getText().toString().trim())
                        .appendQueryParameter("operador", textView_operador.getText().toString())
                        .build();

                asyncRequestFinalizaOfm.execute(builtURI_processaPedido.toString());

            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog myDialog = builder.create();
        myDialog.show();

    }


    private void LerStWs(String pReferencia, Integer pOperacao) {

        String filtro = "";

        AsyncRequest asyncRequest = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerST?").buildUpon()
                .appendQueryParameter("referencia", pReferencia)
                .appendQueryParameter("filtro", filtro)
                .build();

        asyncRequest.execute(builtURI_processaPedido.toString());


    }

    private void LerSeWs(String pReferencia, String pLote, Integer pOperacao) {

        String filtro = "";
        lote_lido = pLote;

        AsyncRequest asyncRequest = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                .appendQueryParameter("referencia", pReferencia)
                .appendQueryParameter("lote", pLote)
                .appendQueryParameter("filtro", filtro)
                .build();

        asyncRequest.execute(builtURI_processaPedido.toString());

    }

    private void RefConsomeNaLoc(String pReferencia, String pLote, Integer pOperacao) {


        AsyncRequest asyncRequest = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RefConsomeOfNaLoc?").buildUpon()
                .appendQueryParameter("referencia", pReferencia)
                .appendQueryParameter("lote", pLote)
                .appendQueryParameter("armazem", String.valueOf(armazem))
                .appendQueryParameter("zona", zona)
                .appendQueryParameter("alveolo", alveolo)
                .appendQueryParameter("tipovalidacao","inv_of")
                .build();

        asyncRequest.execute(builtURI_processaPedido.toString());

    }


    private void defineEditTextLeituraBarCodeListener() {

        editTextLeituraBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                System.out.println(FLAG_MODO_EDITTEXT);

                if (scannedText.equals("")) {
                    System.out.println(" On Text Changed: editText_leitura está vazio. Vou sair");
                    return;
                }

                // Se o flag estiver OPERADOR pede o operador
                if (FLAG_MODO_EDITTEXT.equals("OPERADOR")) {
                    leituraOperador();
                    return;
                }

                // se a flag for a localização
                if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {
                    leituraLocalizacao(scannedText);
                    return;
                }

                // se a flag for a OF
                if (FLAG_MODO_EDITTEXT.equals("OF")) {
                    readOf(scannedText);
                    return;
                }

                // se a flag for o código pode sempre ler a flag da localização e substituir
                if (FLAG_MODO_EDITTEXT.equals("CODIGO")) {
                    leituraCodigo(scannedText);
                    return;
                }

                // Se flag de leitura for o lote
                if (FLAG_MODO_EDITTEXT.equals("LOTE")) {
                    LerSeWs(ref_lida,scannedText,7);
                    return;
                }

                // Se flag de leitura for a quantidade
                if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {
                    leituraQuantidade(scannedText);
                    return;
                }


            }
        };
    }



}

