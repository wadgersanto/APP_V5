package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gnotus.inoveplastika.API.Logistica.LerAlveoloWs;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterCargaPicking;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterConsultaStock;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterPickingDocs;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelCargaPicking;
import gnotus.inoveplastika.DataModels.DataModelConsultaStock;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.DataModels.DataModelPickingDocs;
import gnotus.inoveplastika.Logistica.DataModelAlv;


public class PickingActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete, View.OnClickListener {

    private Toolbar toolbar;
    private SharedPreferences sharedpreferences;
    private static final String MYFILE = "configs";
    private static final String WSRESPONSEVAZIO = "\"[]\"";
    private static final String FLAG_LEITURA_LOCALIZACAO = "LOCALIZACAO";
    private static final String FLAG_LEITURA_OPERADOR = "OPERADOR";
    private static final String FLAG_LEITURA_LOTE = "LOTE";
    private static final String FLAG_LEITURA_CODIGO = "CODIGO";
    private static final String FLAG_PICKING_EXP = "EXP"; // we are adding quantity to the transfer zone
    private static final String FLAG_PICKING_DEVEXP = "DEVEXP"; // we are removing quantity from the transfer zone
    private static final String FLAG_PICKING_ABASTPROD = "ABASTPROD"; // we are transfering to the prodution zone

    //private static final String BOSTAMP_PICKING_CARGA = "bostamp_picking_carga";

    // cada tipo de picking vai ter um bostamp de carga diferente de modo a poder fazer uma descarga independente
    private static final String BOSTAMP_CARGA_PK_EXP = "bostamp_carga_pk_exp";
    private static final String BOSTAMP_CARGA_PK_DEVEXP = "bostamp_carga_pk_devexp";
    private static final String BOSTAMP_CARGA_PK_ABASTPROD = "bostamp_carga_pk_abastprod";

    private static final String TIPO_ARMAZEM_60 = "Produção (60)";
    private static final String TIPO_ARMAZEM_61 = "Montagem (61)";
    private static final String TIPO_ARMAZEM_62 = "Montagem (62)";

    CharSequence[] armazensDescarga = {TIPO_ARMAZEM_60, TIPO_ARMAZEM_61,TIPO_ARMAZEM_62};

    private Activity thisActivity = PickingActivity.this;
    private ListView listViewPickingDocs, listViewPickingList, listViewLotes, listViewCarga;

    private ArrayList<DataModelPickingDocs> listaPickingDocs = new ArrayList<>();
    private ArrayList<DataModelBi> listaPickingList = new ArrayList<>();
    private ArrayList<DataModelCargaPicking> listaCarga = new ArrayList<>();
    private ArrayList<DataModelConsultaStock> listaStockRefLote = new ArrayList<>();

    private ArrayAdapterPickingDocs adapterPickingDocs;
    private DataModelArrayAdapterBi adapterPickingList;
    private ArrayAdapterCargaPicking adapterListaCarga;
    private ArrayAdapterConsultaStock adapterStockRefLote;

    AlertDialog alertDialogOpcoes;
    private ProgressDialog progDailog ;

    private EditText editText_leitura;

    private TextView textViewOf, textViewOfLeg, textViewOperador,textViewOperadorLeg,textViewLocalizacao, textViewLocalizacaoLeg,
            textViewInfo1a,textViewInfo1b,textViewInfo2a,textViewInfo2b;

    private MenuItem menuItemRefresh,menuItemListaCarga;

    private Button buttonTerminar;

    private ConstraintLayout constraintLayoutDados, constraintLayoutLeitura;

    private String pickingDocSelBostamp = "",pickingListSelRef ="", pickingListSelBistamp ="", listaCargaSelBistamp = "",
    listaCargaSelZona = "", listaCargaSelAlveolo = "",bostamp_carga = "",
    referencia, ref_lida = "", lote = "", lote_lido = "", zona = "",alveolo, unidade = "", uni2dade = "", stdesign = "",
            spAcBarTitlePkDocs, spAcBarTitlePkList,spAcBarSubtitle;


    private  boolean refUsaLote, mCalledOtherActivity = true;

    private Bundle bundleRec = new Bundle(); // Bundle recebido

    private Bundle bundleSent = new Bundle(); // Bundle a ser enviado para outra actividade

    // xxxx_lida --> guarda as variaveis que estao a ser lidas, depois de validadas estão a ser guardadas em variaveis


    private int armazem = 0, listaCargaSelArmazem = 0, lvwPkDocsSelItem, lvwPkListSelItem;
    private double qtd_lida = 0, fconversao = 1, listaCargaQtdLida;
    private String pickingType = "";
    private String FLAG_TIPO_LEITURA = "REF";

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLeituraBarCodeListener;
    private EditTextBarCodeReader ediTextLeituraBarCodeReader;

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
        toolbar.setOnClickListener(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setSubtitle("");

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

       /* if(sharedpreferences.contains(BOSTAMP_PICKING_CARGA)) {
            bostamp_carga = sharedpreferences.getString(BOSTAMP_PICKING_CARGA,"");
        }*/

        bundleRec = this.getIntent().getExtras();

        pickingType = bundleRec.getString("pickingType");

        if (pickingType.equals(FLAG_PICKING_EXP)){
            spAcBarTitlePkDocs = "Ordens Picking";
            spAcBarTitlePkList = "Lista Picking";
            spAcBarSubtitle = "(Expedir)";

            if(sharedpreferences.contains(BOSTAMP_CARGA_PK_EXP)) {
                bostamp_carga = sharedpreferences.getString(BOSTAMP_CARGA_PK_EXP,"");
            }

        }

        if (pickingType.equals(FLAG_PICKING_DEVEXP)){
            spAcBarTitlePkDocs = "Ordens Picking";
            spAcBarTitlePkList = "Stock em expedição";
            spAcBarSubtitle = "(Devolver)";

            if(sharedpreferences.contains(BOSTAMP_CARGA_PK_DEVEXP)) {
                bostamp_carga = sharedpreferences.getString(BOSTAMP_CARGA_PK_DEVEXP,"");
            }

        }

        if (pickingType.equals(FLAG_PICKING_ABASTPROD)){
            spAcBarTitlePkDocs = "Lista pedidos";
            spAcBarTitlePkList = "Pedido";
            spAcBarSubtitle = "Abast. Produção";

            if(sharedpreferences.contains(BOSTAMP_CARGA_PK_ABASTPROD)) {
                bostamp_carga = sharedpreferences.getString(BOSTAMP_CARGA_PK_ABASTPROD,"");
            }
        }


        getSupportActionBar().setTitle(spAcBarTitlePkDocs);
        getSupportActionBar().setSubtitle(spAcBarSubtitle);


        editText_leitura = (EditText) findViewById(R.id.editText_leitura);

        /*editText_leitura.addTextChangedListener(new PickingActivity.MyTextWatcher(editText_leitura));
        editText_leitura.setInputType(0);
        */



        defineEditTextReadBarCodeListener();
        ediTextLeituraBarCodeReader = new EditTextBarCodeReader(editText_leitura,thisActivity);
        ediTextLeituraBarCodeReader.setOnGetScannedTextListener(editTextLeituraBarCodeListener);
        editText_leitura.setOnClickListener(this);




        constraintLayoutDados = (ConstraintLayout) findViewById(R.id.constraintLayout_localizacao);
        constraintLayoutDados.setVisibility(View.GONE);

        constraintLayoutLeitura = (ConstraintLayout) findViewById(R.id.constraintLayout_codigo);
        constraintLayoutLeitura.setVisibility(View.GONE);

        buttonTerminar = (Button) findViewById(R.id.button_biuserloc_terminar);
        buttonTerminar.setOnClickListener(this);
        buttonTerminar.setVisibility(View.GONE);
        buttonTerminar.setText("Descarregar");

        listViewPickingDocs = (ListView) findViewById(R.id.listView_linhasbi);
        listViewPickingList = (ListView) findViewById(R.id.listView2);
        listViewLotes = (ListView) findViewById(R.id.listView3);
        listViewCarga = (ListView) findViewById(R.id.listView4);

        textViewOf = (TextView) findViewById(R.id.textView_of);
        textViewOfLeg = (TextView) findViewById(R.id.textView_of_titulo);
        textViewOf.setVisibility(View.GONE);
        textViewOfLeg.setVisibility(View.GONE);

        textViewLocalizacao = (TextView) findViewById(R.id.textView_carga_localizacao);
        textViewLocalizacao.setOnClickListener(this);
        textViewLocalizacaoLeg = (TextView) findViewById(R.id.textView_carga_titulo_localizacao);

        textViewOperador = (TextView) findViewById(R.id.textView_carga_operador);
        textViewOperadorLeg = (TextView) findViewById(R.id.textView_carga_titulo_operador);

        textViewInfo1a = (TextView) findViewById(R.id.textView_Info1a);
        textViewInfo1b = (TextView) findViewById(R.id.textView_Info1b);
        textViewInfo2a = (TextView) findViewById(R.id.textView_Info2a);
        textViewInfo2b = (TextView) findViewById(R.id.textView_Info2b);

        textViewInfo1a.setVisibility(View.GONE);
        textViewInfo1b.setVisibility(View.GONE);
        textViewInfo2a.setVisibility(View.GONE);
        textViewInfo2b.setVisibility(View.GONE);

        textViewInfo1a.setTextSize(15);
        textViewInfo1b.setTextSize(15);


        carregaPickingDocs();


        listViewPickingDocs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                lvwPkDocsSelItem = position;

                pickingDocSelBostamp = adapterPickingDocs.getItem(position).getBostamp();

                // limpa sempre operador
                textViewOperador.setText("");

                refreshPickingList(pickingDocSelBostamp);

                mostraListViewPickingList();


            }
        });

        listViewPickingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                Double dQtdEmFalta = 0.0;
                Double dFconversao = 0.0;

                lvwPkListSelItem = position;

                if (pickingType.equals(FLAG_PICKING_EXP) || pickingType.equals(FLAG_PICKING_ABASTPROD) ) {

                    pickingListSelRef = adapterPickingList.getItem(position).getReferencia();
                    pickingListSelBistamp = adapterPickingList.getItem(position).getBistamp();

                    textViewInfo2a.setText(adapterPickingList.getItem(position).getReferencia());

                    String textQtd = "";

                    if (pickingType.equals(FLAG_PICKING_EXP)) {

                        dQtdEmFalta = adapterPickingList.getItem(position).getQtt()
                                -  adapterPickingList.getItem(position).getBinum1() ;

                        textQtd = "Qtd falta: " + UserFunc.retiraZerosDireita(adapterPickingList.getItem(position).getQtt()
                                -  adapterPickingList.getItem(position).getBinum1())+" " +
                                ""+adapterPickingList.getItem(position).getUnidade();

                        dFconversao = adapterPickingList.getItem(position).getFconversao();
                        // vamos colocar a unidade alternativa
                        if (dFconversao != 1)
                            textQtd = textQtd+"/"+ UserFunc.retiraZerosDireita(UserFunc.arredonda(dQtdEmFalta/dFconversao,2))+
                                    " "+ adapterPickingList.getItem(position).getUnidad2();

                        textViewInfo2b.setText(textQtd);

                        carregaLotes(pickingListSelRef);
                    }


                    if (pickingType.equals(FLAG_PICKING_ABASTPROD)) {

                        dQtdEmFalta = adapterPickingList.getItem(position).getQtt() -  adapterPickingList.getItem(position).getQtt2();

                        textQtd = "Qtd falta: " + UserFunc.retiraZerosDireita(dQtdEmFalta)+" " +
                                ""+adapterPickingList.getItem(position).getUnidade();

                        dFconversao = adapterPickingList.getItem(position).getFconversao();

                        if (dFconversao != 1) {
                            textQtd = textQtd + "/"+UserFunc.retiraZerosDireita(UserFunc.arredonda(dQtdEmFalta/dFconversao,2))+
                            " " +   adapterPickingList.getItem(position).getUnidad2();

                        }

                        textViewInfo2b.setText(textQtd);

                        carregaLotes(pickingListSelRef);
                    }

                    mostraListViewLotes();
                }


                if (pickingType.equals(FLAG_PICKING_DEVEXP)) {

                    pickingListSelRef = adapterPickingList.getItem(position).getReferencia();
                    pickingListSelBistamp = adapterPickingList.getItem(position).getBistamp();

                    textViewInfo2a.setText(adapterPickingList.getItem(position).getReferencia());
                    textViewInfo2b.setText("");

                    armazem = 2;
                    zona = "EXP";
                    alveolo ="EXP";
                    textViewLocalizacao.setText("[2][EXP][EXP]");

                    listaStockDevExpedicao(pickingListSelRef,pickingListSelBistamp);

                    mostraListViewLotes();

                }


            }
        });


        listViewCarga.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                // primeiro vamos verificar se existe algum item de carga

                if (listaCarga.isEmpty())
                    return;

                // vamos carregar a editActivity com o tipo Edit e com as mesmas caracteristicas
                listaCargaSelBistamp = adapterListaCarga.getItem(position).getBistamp();

                String refLida, loteLido, zonaLida,alvLido;
                Integer armLido;

                refLida = adapterListaCarga.getItem(position).getReferencia();
                loteLido = adapterListaCarga.getItem(position).getLote();
                listaCargaSelZona = adapterListaCarga.getItem(position).getZona1();
                listaCargaSelAlveolo = adapterListaCarga.getItem(position).getAlveolo1();
                listaCargaSelArmazem = adapterListaCarga.getItem(position).getArmazem();
                listaCargaQtdLida = adapterListaCarga.getItem(position).getQtt();

                lerStockLoteLoc(refLida,loteLido,listaCargaSelArmazem,listaCargaSelZona,listaCargaSelAlveolo,10);

            }
        });

        buttonTerminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ao terminar vamos executar o script

                if (listViewCarga.getVisibility() == View.VISIBLE) {

                    constraintLayoutLeitura.setVisibility(View.VISIBLE);
                    editText_leitura.requestFocus();

                    FLAG_TIPO_LEITURA = "DESCARGAOPERADOR";
                    defineEditTextHint(true );

                    return;

                }

                if (listViewPickingList.getVisibility() == View.VISIBLE) {

                    constraintLayoutLeitura.setVisibility(View.VISIBLE);
                    editText_leitura.requestFocus();

                    FLAG_TIPO_LEITURA = "ENCERRAR_OP_OPERADOR";
                    defineEditTextHint(true );

                    return;

                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cab, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menuItemRefresh = menu.findItem(R.id.menu_item_refresh);
        menuItemListaCarga = menu.findItem(R.id.menu_item_list);

        if (pickingType.equals(FLAG_PICKING_DEVEXP)) menuItemListaCarga.setVisible(false);


        //if (menuItemListaCarga != null) menuItemListaCarga.setVisible(false);


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

        //
        if (!mCalledOtherActivity)
            return;
        else
            mCalledOtherActivity = false;

        if (listViewCarga != null && listViewCarga.getVisibility() == View.VISIBLE) {
            lerLinhasCarga(bostamp_carga,9);
            return;
        }

        // se estamos a ver a lista de lotes e esta não está null
        if (listViewLotes != null && listViewLotes.getVisibility() == View.VISIBLE)
        {

            // vamos limpar a localização para que seja pedida novamente

            textViewLocalizacao.setText("");

            if (pickingType.equals(FLAG_PICKING_DEVEXP))

                menuItemRefreshClick();

            if (pickingType.equals(FLAG_PICKING_EXP) || pickingType.equals(FLAG_PICKING_ABASTPROD)) {

                // pedimos aqui para refrescar a pickinglist para recalcular a quantidade satsfeita e
                // assim atualizar o campo qtt em falta neste ecrã.

                if (pickingDocSelBostamp != null)
                    refreshPickingList(pickingDocSelBostamp);
                else
                    refreshPickingList("");

                defineEditTextHint(true);
                return;
            }

        }


    }

    public void onBackPressed() {

        if (listViewCarga.getVisibility() == View.VISIBLE) {

            if (pickingType.equals(FLAG_PICKING_EXP)) {
                menuItemListaCarga.setVisible(true);
            }


            if (pickingType.equals(FLAG_PICKING_ABASTPROD)) {
                menuItemListaCarga.setVisible(true);
            }



            if (pickingType.equals(FLAG_PICKING_DEVEXP)){
                menuItemListaCarga.setVisible(false);
            }


            listViewCarga.setVisibility(View.GONE);
            buttonTerminar.setVisibility(View.GONE);

            // se a lista que estiver visivel for a lista de picking então vamos mostrar
            if(listViewPickingList.getVisibility() == View.VISIBLE) {
                constraintLayoutDados.setVisibility(View.VISIBLE);
                constraintLayoutLeitura.setVisibility(View.GONE);
                getSupportActionBar().setTitle(spAcBarTitlePkDocs);
                refreshPickingList(pickingDocSelBostamp);

                // Como estamos a retornar da lista de carga e poderemos ter apagado o stamp do bo da carga,
                // vamos validar se o bostamp existe. Se não existir vamos limpar o operador de modo a que
                // peçamos novamente para ler o operador e criar um novo stamp

                // operação 70 = onBackPressed
                verificaExisteBostampCarga(bostamp_carga, 50);

                return;

            }

            if(listViewLotes.getVisibility() == View.VISIBLE) {
                constraintLayoutDados.setVisibility(View.VISIBLE);
                constraintLayoutLeitura.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(spAcBarTitlePkList);
                editText_leitura.requestFocus();
                FLAG_TIPO_LEITURA = "";
                defineEditTextHint(false);
                refreshPickingList(pickingDocSelBostamp);
                return;
            }

            if(listViewPickingDocs.getVisibility() == View.VISIBLE) {
                getSupportActionBar().setTitle(spAcBarTitlePkDocs);
                constraintLayoutLeitura.setVisibility(View.GONE);
                return;
            }

            return;
        }

        if (listViewPickingList.getVisibility() == View.VISIBLE) {
            carregaPickingDocs();
            mostraListViewPickingDocs();
            return;
        }

        if (listViewPickingDocs.getVisibility() == View.VISIBLE) {

            // ao sair vamos eliminar o dossier de carga

            if(! bostamp_carga.equals("")){


                AsyncRequest processaPedido = new AsyncRequest(thisActivity, 90);

                Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                        .appendQueryParameter("bostamp",String.valueOf (bostamp_carga))
                        .appendQueryParameter("tabela","BO")
                        .appendQueryParameter("onlyifnolines",String.valueOf(true))
                        .build();

                processaPedido.execute(builtURI_processaPedido.toString());

            }
            else finish();

            return;
        }

        if (listViewLotes.getVisibility() == View.VISIBLE) {

            // limpar a leitura da localização

            textViewLocalizacao.setText("");

            mostraListViewPickingList();
            refreshPickingList(pickingDocSelBostamp);

            return;

        }


    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.toolbar:
                // carregaPickingDocs();
                break;
            case R.id.editText_leitura:
                if (! editText_leitura.getText().toString().equals(""))
                {
                    editText_leitura.setText("");
                    defineEditTextHint(true);
                }
                break;
            case R.id.textView_carga_localizacao:
                textViewLocalizacao.setText("");
                defineEditTextHint(true);
                break;


        }
    }


    @Override
    public void asyncResponse(String response, int op) {

        if (response != null) {

        }

        // obtem picking docs
        if (op == 0) {
            aSyncResponseLerPickingDocs(response,op);
            return;
        }

        // obtem picking list de um determinado picking doc
        if (op == 1) {
            aSyncResponseRefreshPickingList(response,op);
        }

        if (op == 2) {
            // lista o stock de uma determinada referencia por lote e localização
            aSyncResponseListarStockPorLoteLoc(response,op);
        }

        if (op == 3) {
            //Resposta da Leitura de lote
            aSyncResponseLerSE(response,op);
        }

        if (op == 4) {
            //Resposta da Leitura de lote
            aSyncResponseLerST(response,op);
        }

        if (op == 5) {
            //Consulta o stock de um determinado lote numa localização
            aSyncResponseLerStockLoteLoc(response,op);
        }

        if (op == 7 || op == 50 ) {
            //Consulta se existe o bostmap da carga
            aSyncResponseExisteBostampCarga(response,op);
        }

        if (op == 8) {
            //Consulta o stock de um determinado lote numa localização
            aSyncResponseCriaDossierCarga(response,op);
        }

        if (op == 9) {
            //Consulta o stock de um determinado lote numa localização
            aSyncResponseLerLinhasCarga(response,op);
        }

        if (op == 10) {
            //Consulta o stock de um determinado lote numa localização
            // quando estamos editar a linha de carga
            aSyncResponseLerStockLoteLoc(response,op);
        }

        if (op == 11) {
            //Consulta o stock de um determinado lote numa localização
            // quando estamos editar a linha de carga
            aSyncResponseProcessaDescarga(response,op);
        }

        if (op == 12) {
         // conclui a ordem de picking
            onBackPressed();
            Dialogos.dialogoInfo("","Ordem picking concluída",1.0,thisActivity,false);
        }


        // elimina dossier quando sai da aplicação
        if (op == 90){
            finish();
        }

    }


    private void carregaPickingDocs(){

        if (!listaPickingDocs.isEmpty()) {
            listaPickingDocs.clear();
            adapterPickingDocs.notifyDataSetChanged();
        }

        if (adapterPickingDocs != null) {
            adapterPickingDocs.notifyDataSetChanged();
        }


        AsyncRequest mAsynRequest = new AsyncRequest(thisActivity,0 );

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/PickingDocs?").buildUpon()
                .appendQueryParameter("tipo",pickingType)
                .build();
        mAsynRequest.execute(builtURI_processaPedido.toString());

    }

    private void refreshPickingList(String pBostamp){


        if (!listaPickingList.isEmpty()) {
            listaPickingList.clear();
            adapterPickingList.notifyDataSetChanged();
        }

        if (adapterPickingList != null) {
            adapterPickingList.notifyDataSetChanged();
        }

        AsyncRequest mAsynRequest = new AsyncRequest(thisActivity,1 );

        String pTipoConsulta = "";

        if (pickingType.equals(FLAG_PICKING_ABASTPROD))
            pTipoConsulta = "picking_abast";

        if (pickingType.equals(FLAG_PICKING_EXP))
            pTipoConsulta = "picking_exp";

        if (pickingType.equals(FLAG_PICKING_DEVEXP))
            pTipoConsulta = "picking_devexp";


        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/DevolveLinhasBi?").buildUpon()
                .appendQueryParameter("bostamp",pBostamp)
                .appendQueryParameter("tipoconsulta",pTipoConsulta)
                .build();
        mAsynRequest.execute(builtURI_processaPedido.toString());
    }


    private void aSyncResponseLerPickingDocs(String response, int op) {


        if (response.equals(WSRESPONSEVAZIO)) {

            return;

        } else {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);


                for (int i = 0; i < values.length(); i++) {

                    JSONObject c = values.getJSONObject(i);

                    listaPickingDocs.add(new DataModelPickingDocs(c));

                }

                Bundle pBundle = new Bundle();
                pBundle.putString("pickingType",pickingType);

                adapterPickingDocs = new ArrayAdapterPickingDocs(this, 0, listaPickingDocs,pBundle);
                listViewPickingDocs.setAdapter(adapterPickingDocs);

                adapterPickingDocs.notifyDataSetChanged();

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro na conversão jSon",e.getMessage(),5,thisActivity,false);

            }

        }

    }

    private void aSyncResponseProcessaDescarga(String response, int op) {

        String mv_resultado = response.substring(1,response.length()-1);

        if(mv_resultado.equals("OK")) {
            Dialogos.dialogoInfo("Sucesso","Descarga processada",3.0,thisActivity,false);
            FLAG_TIPO_LEITURA = "";
            onBackPressed();
            defineEditTextHint(false);
        } else {
            Dialogos.dialogoErro("Erro no processamento da descarga",mv_resultado,10,thisActivity,false);
        }

    }
    private void aSyncResponseRefreshPickingList(String response, int op) {


        if (! response.equals(WSRESPONSEVAZIO)) {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);

                for (int i = 0; i < values.length(); i++) {

                    JSONObject c = values.getJSONObject(i);


                    String j_bistamp = c.getString("bistamp");
                    String j_referencia = c.getString("ref");
                    String j_lote = c.getString("lote");
                    String j_descricao = c.getString("design");
                    Double j_qtt = Double.parseDouble(c.getString("qtt"));
                    Double j_uni2qtt = Double.parseDouble(c.getString("uni2qtt"));
                    Double j_qtt2 = Double.parseDouble(c.getString("qtt2"));
                    String j_unidade = c.getString("unidade");
                    String j_unidad2 = c.getString("unidad2");
                    int j_armazem = c.getInt("armazem");
                    int j_ar2mazem = c.getInt("ar2mazem");
                    String j_zona1 = c.getString("zona1");
                    String j_alveolo1 = c.getString("alveolo1");
                    String j_zona2 = c.getString("zona2");
                    String j_alveolo2 = c.getString("alveolo2");
                    Boolean j_jacontou = c.getBoolean("u_jacontou");
                    int j_binum1 = c.getInt("binum1");
                    Double j_fconversao = c.getDouble("fconversao");

                    listaPickingList.add(new DataModelBi(j_bistamp, j_referencia, j_lote, j_descricao, j_qtt, j_uni2qtt, j_qtt2, j_binum1, j_unidade, j_unidad2, j_armazem, j_ar2mazem,
                            j_zona1, j_alveolo1, j_zona2, j_alveolo2, j_fconversao, j_jacontou));

                }


                Bundle pBundle = new Bundle();
                pBundle.putString("listview","listViewPickingList");
                pBundle.putString("pickingType",pickingType);

                adapterPickingList = new DataModelArrayAdapterBi(this, 0, listaPickingList, pBundle);

                listViewPickingList.setAdapter(adapterPickingList);

                adapterPickingList.notifyDataSetChanged();

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro","Erro na conversão Json :" +e.getMessage(),6,thisActivity, false);

            }

        }

        // se no final do carregamento das linhas de picking

        if (listViewLotes.getVisibility() == View.VISIBLE && pickingListSelRef != null) {

            Double dFconversao = 1.0;

            if (pickingType.equals(FLAG_PICKING_EXP) || pickingType.equals(FLAG_PICKING_ABASTPROD)) {
                Double qttemfalta = getQttEmFalta(pickingListSelRef);
                String unidade = getUnidadeRef(pickingListSelRef);


                if (qttemfalta != 0.0) {

                    String textQtd = "Qtd em falta: " + UserFunc.retiraZerosDireita(qttemfalta) + " " + unidade;

                    dFconversao = adapterPickingList.getItem(lvwPkListSelItem).getFconversao();
                    // vamos colocar a unidade alternativa
                    if (dFconversao != 1)
                        textQtd = textQtd+"/"+ UserFunc.retiraZerosDireita(UserFunc.arredonda(qttemfalta/dFconversao,2))+
                        " "+ adapterPickingList.getItem(lvwPkListSelItem).getUnidad2();

                    textViewInfo2b.setText(textQtd);

                        carregaLotes(pickingListSelRef);

                } else {
                    mostraListViewPickingList();
                    //desativado porque está a ser executada dentro do procedimento refreshpickinglist -
                    // se não der mais problemas limpar este código
                    // refreshPickingList(pickingDocSelBostamp);
                }
            }

        }

    }

    private void aSyncResponseLerLinhasCarga(String response, int op) {


        if (! response.equals(WSRESPONSEVAZIO)) {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);

                for (int i = 0; i < values.length(); i++) {

                    JSONObject c = values.getJSONObject(i);


                    String j_bistamp = c.getString("bistamp");
                    String j_referencia = c.getString("ref");
                    String j_lote = c.getString("lote");
                    String j_descricao = c.getString("design");
                    Double j_qtt = Double.parseDouble(c.getString("qtt"));
                    Double j_uni2qtt = Double.parseDouble(c.getString("uni2qtt"));
                    Double j_qtt2 = Double.parseDouble(c.getString("qtt2"));
                    String j_unidade = c.getString("unidade");
                    String j_unidad2 = c.getString("unidad2");
                    int j_armazem = c.getInt("armazem");
                    int j_ar2mazem = c.getInt("ar2mazem");
                    String j_zona1 = c.getString("zona1");
                    String j_alveolo1 = c.getString("alveolo1");
                    String j_zona2 = c.getString("zona2");
                    String j_alveolo2 = c.getString("alveolo2");
                    String j_clientepicking = c.getString("clientepicking");
                    Double j_fconversao = c.getDouble("fconversao");
                    String j_obranome = c.getString("obranome");

                    listaCarga.add(new DataModelCargaPicking(j_bistamp, j_referencia, j_lote, j_descricao, j_qtt, j_uni2qtt, j_qtt2, j_unidade, j_unidad2, j_armazem, j_ar2mazem,
                            j_zona1, j_alveolo1, j_zona2, j_alveolo2, j_fconversao,j_clientepicking,j_obranome));

                }

                Bundle pBundle = new Bundle();
                pBundle.putString("listview","listViewCarga");
                pBundle.putString("pickingType",pickingType);

                adapterListaCarga = new ArrayAdapterCargaPicking(this, 0, listaCarga, pBundle);
                listViewCarga.setAdapter(adapterListaCarga);

                adapterListaCarga.notifyDataSetChanged();
                listViewCarga.setVisibility(View.VISIBLE);


            } catch (final JSONException e) {

                Dialogos.dialogoErro("Erro na conversão Json: ",e.getMessage(),4,thisActivity,false);
                Log.e("", "Erro Conversão: " + e.getMessage());
            }

        }

    }

    private void aSyncResponseLerAlveolo(String response, int op){


        JSONconverter jsonConverter = new JSONconverter();
        response = jsonConverter.ConvertJSON(response);


        try {


            JSONArray values = new JSONArray(response);

            String infolocalizacao  = "["+armazem+"]["+zona+"]["+alveolo+"]";

            if (response.equals("[]") || values.length() == 0 ) {

                Dialogos.dialogoErro("", "Não encontrei no sistema a localização "+infolocalizacao, 3, thisActivity, false);
                defineEditTextHint(false);
            }
            else
            {
                // vamos ler os dados


                JSONObject c = values.getJSONObject(0);
                // localização de consumo de of
                Boolean j_loccof = c.getBoolean("szzloccof");


                textViewLocalizacao.setText(infolocalizacao);
                FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;

                defineEditTextHint(true);


            }

        } catch (final JSONException e) {

            Log.e("", "Erro Conversão: " + e.getMessage());
            Dialogos.dialogoErro("Erro",e.getMessage(),60,thisActivity, false);
            defineEditTextHint(false);
        }


    }

    private void aSyncResponseListarStockPorLoteLoc(String response, int op) {

        if (response.equals(WSRESPONSEVAZIO)) {

            return;

        } else {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);

                for (int i = 0; i < values.length(); i++) {

                    JSONObject c = values.getJSONObject(i);

                    Double j_stock = Double.parseDouble(c.getString("stock"));
                    int j_armazem = c.getInt("armazem");
                    String j_zona = c.getString("zona");
                    String j_alveolo = c.getString("alveolo");

                    /*
                    String j_referencia = c.getString("ref");
                    String j_descricao = c.getString("design");
                    String j_lote = c.getString("lote");

                    Double j_stock2 = Double.parseDouble(c.getString("stock2"));
                    String j_unidade = c.getString("unidade");
                    String j_uni2 = c.getString("uni2");

                    */

                    // só carrega lotes com quantidade positiva



                    if (j_stock > 0) {

                        if (pickingType.equals(FLAG_PICKING_ABASTPROD))
                            listaStockRefLote.add(new DataModelConsultaStock(c));

                        if ( pickingType.equals(FLAG_PICKING_EXP))
                             {
                                 // só carrega lotes do armazem 2 que não estão na zona de expedição
                                 if (!(j_armazem == 2 && j_zona.equals("EXP") && j_alveolo.equals("EXP")) )
                                    listaStockRefLote.add(new DataModelConsultaStock(c));
                            }

                        if ( pickingType.equals(FLAG_PICKING_DEVEXP) ) {
                            listaStockRefLote.add(new DataModelConsultaStock(c));
                        }

                    }

                }

                Bundle pBundle = new Bundle();

                adapterStockRefLote = new ArrayAdapterConsultaStock(this, 0, listaStockRefLote,pBundle);
                listViewLotes.setAdapter(adapterStockRefLote);

                adapterStockRefLote.notifyDataSetChanged();

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());

            }

        }

        // no final do carregamento dos lotes vamos verificar se o stamp de carga existe e se não existir vamos limpar o operador
        // e limpar o bostamp

        verificaExisteBostampCarga(bostamp_carga,50);

    }

    private void aSyncResponseLerSE(String response, int op) {


        if (response.equals(WSRESPONSEVAZIO)) {

            Dialogos.dialogoErro("Leitura de lote","Não encontrei no sistema o lote "+lote_lido,4,thisActivity,false  );
            lote = "";
            return;

        } else {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                JSONArray values = new JSONArray(response);

                if (values.length() == 0) {
                    Dialogos.dialogoErro("Leitura de lote","Não encontrei no sistema o lote "+lote_lido,4,thisActivity,false  );
                    lote = "";
                    return;

                }

                JSONObject camposSE = values.getJSONObject(0);
                referencia = camposSE.getString("ref").trim();
                lote = camposSE.getString("lote").trim();
                unidade = camposSE.getString("unidade");
                uni2dade = camposSE.getString("uni2");
                fconversao = camposSE.getDouble("fconversao");

                // vamos


                lerStockLoteLoc(referencia,lote,armazem,zona,alveolo,5);


            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());

            }

        }

    }

    private void aSyncResponseLerST(String response, int op) {

        JSONconverter jsonConverter = new JSONconverter();
        response = jsonConverter.ConvertJSON(response);

        // se os dados vierem vazios
        if (response.equals(WSRESPONSEVAZIO)) {
            Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + ref_lida, 10, thisActivity, false);
            ref_lida = "";
            referencia = "";
            defineEditTextHint(false);
            return;
        }

        try {

            JSONArray arrayST = new JSONArray(response);

            if (arrayST.length() == 0) {
                Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + ref_lida, 10, thisActivity, false);
                ref_lida = "";
                referencia = "";
                return;
            }
            else {

                JSONObject camposST = arrayST.getJSONObject(0);
                referencia = camposST.getString("ref").trim();
                ref_lida = camposST.getString("ref").trim();
                unidade = camposST.getString("unidade");
                uni2dade = camposST.getString("uni2");
                fconversao = camposST.getDouble("fconversao");
                stdesign = camposST.getString("design");
                refUsaLote = camposST.getBoolean("usalote");

                if (refUsaLote) {
                    FLAG_TIPO_LEITURA = "LOTE";
                    defineEditTextHint(true);
                    return;
                }

            }

        }
        catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
            return;
        }
    }

    private void aSyncResponseLerStockLoteLoc(String response, int op) {

        // Neste momento já lemos a localização, referencia e lote
        // fazemos então a leitura do stock na localização de modo a podermos validar a quantidade maxima a requisitar dessa localização
        // caso exista stock vamos abrir a actividade

        String mInfo = "";

        // se os dados vierem vazios

        JSONconverter jsonConverter = new JSONconverter();
        response = jsonConverter.ConvertJSON(response);

        if (response.equals(WSRESPONSEVAZIO)) {

            mInfo = "Não encontrei stock do artigo: " + referencia +" lote: "+lote + " na localização "+textViewLocalizacao.getText().toString();
            Dialogos.dialogoErro("Validação de leitura", mInfo, 6, thisActivity, false);

            if (pickingType.equals(FLAG_PICKING_EXP))
                textViewLocalizacao.setText("");

            defineEditTextHint(false);
            return;
        }


        try {

            JSONArray arrayStock = new JSONArray(response);

            if (arrayStock.length() == 0 && op == 5) {

                mInfo = "Não encontrei stock do artigo: " + referencia +" lote: "+lote + " na localização "+textViewLocalizacao.getText().toString();
                Dialogos.dialogoErro("Validação de leitura", mInfo, 6, thisActivity, false);

                if (pickingType.equals(FLAG_PICKING_EXP))
                textViewLocalizacao.setText("");

                defineEditTextHint(false);
                return;

            }


            JSONObject c = arrayStock.getJSONObject(0);

            String j_referencia = c.getString("ref").trim();
            String j_lote = c.getString("lote").trim();
            String j_design = c.getString("design").trim();
            String j_stock = c.getString("stock");
            String j_unidade = c.getString("unidade").trim();
            String j_fconversao = c.getString("fconversao");
            String j_unidadeAlt = c.getString("uni2").trim();
            String j_stockAlt = c.getString("stock2");

            System.out.println("Stock lido :" + j_stock);
            System.out.println("Stock Alt lido:" + j_stockAlt);


            // Limpamos aqui a localização porque qualquer ação posterior a esta implica ler novamente a localização
            if (pickingType.equals(FLAG_PICKING_EXP))
            textViewLocalizacao.setText("");

            if (Double.valueOf(j_stock) <= 0 && op == 5) {

                mInfo = "Não encontrei stock do artigo: " + referencia +" lote: "+lote + " na localização "+textViewLocalizacao.getText().toString();
                Dialogos.dialogoErro("Validação de leitura", mInfo, 6, thisActivity, false);
                defineEditTextHint(false);
                return;
            }


            bundleSent.clear();

            bundleSent.putString("trf_mode","");

            bundleSent.putBoolean("valida_stock",true);

            bundleSent.putString("ref", j_referencia);
            bundleSent.putString("lote", j_lote);
            bundleSent.putString("design", j_design);

            // se a quantidade em falta para satisfação da encomenda for superior ao stock que existe do artigo, então
            // é essa a quantidade que passa como stock maximo


            bundleSent.putDouble("fconversao", Double.parseDouble(j_fconversao));

            bundleSent.putDouble("qtd_lida", qtd_lida);
            bundleSent.putDouble("qtdAlt_lida", (qtd_lida / Double.parseDouble(j_fconversao)) * 1.000);

            bundleSent.putString("unidade", j_unidade);
            bundleSent.putString("unidadeAlt", j_unidadeAlt);
            bundleSent.putString("user", textViewOperador.getText().toString());


            bundleSent.putString("bistamp_carga","");
            bundleSent.putString("bostamp_carga",bostamp_carga);
            bundleSent.putString("atividade",thisActivity.getClass().getSimpleName());

            // primeiro vamos colocar os campo obistamp e oobistamp vazios
            // que depois serão preenchidos conforme o tipo de pickingActivity

            bundleSent.putString("oobistamp","");
            bundleSent.putString("obistamp","");

            if (pickingType.equals(FLAG_PICKING_ABASTPROD)) {
                bundleSent.putString("obistamp",pickingListSelBistamp);
                bundleSent.putString("oobistamp",pickingListSelBistamp);
            }


            if (pickingType.equals(FLAG_PICKING_EXP))
                bundleSent.putString("oobistamp",pickingListSelBistamp);


            bundleSent.putString("pickingType",pickingType);


            // se o pedido for originado quando estamos a ler o artigo através do código de barras
            if (op == 5) {

                bundleSent.putString("armazem_ori", String.valueOf(armazem));
                bundleSent.putString("zona_ori", zona);
                bundleSent.putString("alveolo_ori", alveolo);

                Double tmpQttEmFalta = getQttEmFalta(pickingListSelRef);
                String tmpUnidade =  getUnidadeRef(pickingListSelRef);

                if (pickingType.equals(FLAG_PICKING_DEVEXP)) {

                    boolean foundRefLote = false;

                    if (!listaStockRefLote.isEmpty()) {
                        String tmpRef;
                        String tmpLote;
                        for (int i = 0; i < listaStockRefLote.size(); i++) {

                            tmpRef = listaStockRefLote.get(i).getReferencia();
                            tmpLote = listaStockRefLote.get(i).getLote();

                            if (tmpRef.equals(j_referencia) && tmpLote.equals(j_lote) )
                                foundRefLote = true;
                                tmpQttEmFalta  = listaStockRefLote.get(i).getStock();
                        }
                    }

                    if (! foundRefLote) {
                        Dialogos.dialogoErro("Leitura inválida","Não existe stock nesta localização da ref:"+j_referencia+" / lote:"+j_lote,
                                5,thisActivity, false);
                        return;
                    }
                    bundleSent.putDouble("stock", tmpQttEmFalta);
                    bundleSent.putDouble("stockAlt", (tmpQttEmFalta / Double.parseDouble(j_fconversao)) * 1.000);

                }

                if (pickingType.equals(FLAG_PICKING_EXP)) {

                    // Estavamos inicialmente a validar a quantidade máxima permitida na picagem pelo menor dos dois valores:
                    //          stock disponivel do lote;
                    //          quantidade do picking por satisfazer
                    //  para permitir poder picar mais do que o pedido vamos só validar pelo stock disponível


                    /*if (tmpQttEmFalta < Double.parseDouble(j_stock)) {
                        bundleSent.putDouble("stock", tmpQttEmFalta);
                        bundleSent.putDouble("stockAlt", (tmpQttEmFalta / Double.parseDouble(j_fconversao)) * 1.000);
                    }
                    else {
                        bundleSent.putDouble("stock", Double.parseDouble(j_stock));
                        bundleSent.putDouble("stockAlt", Double.parseDouble(j_stockAlt));
                    }*/

                    bundleSent.putDouble("stock", Double.parseDouble(j_stock));
                    bundleSent.putDouble("stockAlt", Double.parseDouble(j_stockAlt));

                    // vamos passar para o bundle a quantidade em falta na picagem, para que se pergunte confirmação ao utilizador caso seleccione uma quantidade superior
                    // à quantidade em falta

                    bundleSent.putDouble("QttPkExpFalta",tmpQttEmFalta);

                }

                if (pickingType.equals(FLAG_PICKING_ABASTPROD)) {

                    bundleSent.putDouble("stock", Double.parseDouble(j_stock));
                    bundleSent.putDouble("stockAlt", Double.parseDouble(j_stockAlt));

                    bundleSent.putDouble("QttPkExpFalta",tmpQttEmFalta);

                }


                bundleSent.putBoolean("editing_listview",false);
            }

            // se o pedido foi originado quando estamos a editar a linha de carga
            if (op == 10) {

                bundleSent.putString("bistamp_carga",listaCargaSelBistamp);
                bundleSent.putBoolean("editing_listview",true);

                bundleSent.putString("armazem_ori", String.valueOf(listaCargaSelArmazem) );
                bundleSent.putString("zona_ori", listaCargaSelZona);
                bundleSent.putString("alveolo_ori", listaCargaSelAlveolo);

                bundleSent.putDouble("stock", listaCargaQtdLida);
                bundleSent.putDouble("stockAlt", (listaCargaQtdLida / Double.parseDouble(j_fconversao)) * 1.000);

            }


            Intent EditIntent = new Intent(thisActivity, EditActivity.class);
            EditIntent.putExtras(bundleSent);
            mCalledOtherActivity = true;
            startActivity(EditIntent);


        }
        catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
            return;
        }

    }

    private void aSyncResponseExisteBostampCarga(String response, int op){

        if (response.equals("false")) {

            // se o pedido foi só para ve
            if (op == 50) {
                bostamp_carga ="";
                textViewOperador.setText("");
                defineEditTextHint(false);
            }
            else
                criaDossierCarga(8);

        }
        else {
            // vamos devolver as linhas do listviewcarga

        }
    }

    private void aSyncResponseCriaDossierCarga(String response, int op){


        if (response.equals(WSRESPONSEVAZIO) || response.substring(1,5).equals("erro")) {

            Dialogos.dialogoErro("Erro na criação de dossier carga","Não foi possivel criar o dossier de carga",5,thisActivity,false);
            textViewOperador.setText("");
            defineEditTextHint(false);

        }
        else {

            String tmpBostamp = response.substring(1,response.length()-1);

            SharedPreferences.Editor editor = sharedpreferences.edit();

            switch (pickingType) {

                case FLAG_PICKING_EXP:
                        editor.putString(BOSTAMP_CARGA_PK_EXP, tmpBostamp);
                    break;

                case FLAG_PICKING_DEVEXP:
                    editor.putString(BOSTAMP_CARGA_PK_DEVEXP, tmpBostamp);
                    break;

                case FLAG_PICKING_ABASTPROD:
                    editor.putString(BOSTAMP_CARGA_PK_ABASTPROD, tmpBostamp);
                    break;

            }

            bostamp_carga = tmpBostamp;
            editor.apply();
            lerLinhasCarga(bostamp_carga,9);


        }

    }



    private void mostraListViewPickingDocs() {

        listViewPickingList.setVisibility(View.INVISIBLE);
        listViewPickingDocs.setVisibility(View.VISIBLE);
        listViewLotes.setVisibility(View.INVISIBLE);

        constraintLayoutLeitura.setVisibility(View.GONE);
        constraintLayoutDados.setVisibility(View.GONE);

        textViewOperador.setVisibility(View.GONE);
        textViewOperadorLeg.setVisibility(View.GONE);

        textViewLocalizacao.setVisibility(View.GONE);

        buttonTerminar.setVisibility(View.GONE);
        buttonTerminar.setText("");

        getSupportActionBar().setTitle(spAcBarTitlePkDocs);



    }

    private void mostraListViewPickingList() {

        // coloca visivel a lista de picking - Referencias de uma determinada picking order

        listViewPickingList.setVisibility(View.VISIBLE);
        listViewPickingDocs.setVisibility(View.INVISIBLE);
        listViewLotes.setVisibility(View.INVISIBLE);

        constraintLayoutLeitura.setVisibility(View.GONE);
        constraintLayoutDados.setVisibility(View.VISIBLE);

        textViewInfo1a.setVisibility(View.VISIBLE);
        textViewInfo1b.setVisibility(View.VISIBLE);


        if (pickingType.equals(FLAG_PICKING_EXP))
        {

            textViewInfo1a.setTypeface(null, Typeface.BOLD);
            textViewInfo1b.setTypeface(null, Typeface.NORMAL);

            textViewInfo2a.setVisibility(View.INVISIBLE);
            textViewInfo2b.setVisibility(View.INVISIBLE);

            textViewInfo1a.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getNome());
            textViewInfo1b.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getData());

            buttonTerminar.setVisibility(View.VISIBLE);
            buttonTerminar.setText("Dar ordem como concluída");

        }



        if (pickingType.equals(FLAG_PICKING_ABASTPROD))
        {
            textViewInfo1a.setTypeface(null, Typeface.BOLD);
            textViewInfo1b.setTypeface(null, Typeface.BOLD);

            textViewInfo2a.setTypeface(null, Typeface.NORMAL);
            textViewInfo2a.setTypeface(null, Typeface.NORMAL);

            textViewInfo2a.setVisibility(View.VISIBLE);
            textViewInfo2b.setVisibility(View.VISIBLE);


            // Ao mostrar a lista de picking carrega a informação do nome do cliente neste campo

            textViewInfo1a.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getNome());
            textViewInfo1b.setText("Armazém: "+ adapterPickingDocs.getItem(lvwPkDocsSelItem).getArmazem());

            textViewInfo2a.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getData());
            textViewInfo2b.setText("");

            buttonTerminar.setVisibility(View.VISIBLE);
            buttonTerminar.setText("Dar ordem como concluída");


        }



        if (pickingType.equals(FLAG_PICKING_DEVEXP)) {

            textViewInfo1a.setTypeface(null, Typeface.BOLD);
            textViewInfo1b.setTypeface(null, Typeface.NORMAL);

            textViewInfo2a.setVisibility(View.INVISIBLE);
            textViewInfo2b.setVisibility(View.INVISIBLE);

            textViewInfo1a.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getNome());
            textViewInfo1b.setText(adapterPickingDocs.getItem(lvwPkDocsSelItem).getData());

            buttonTerminar.setVisibility(View.GONE);
        }



        textViewOperador.setVisibility(View.GONE);
        textViewOperadorLeg.setVisibility(View.GONE);

        textViewLocalizacao.setVisibility(View.GONE);
        textViewLocalizacaoLeg.setVisibility(View.GONE);


        getSupportActionBar().setTitle(spAcBarTitlePkList);


    }

    private void mostraListViewLotes() {


        listViewPickingList.setVisibility(View.INVISIBLE);
        listViewPickingDocs.setVisibility(View.INVISIBLE);
        listViewLotes.setVisibility(View.VISIBLE);

        constraintLayoutLeitura.setVisibility(View.VISIBLE);
        constraintLayoutDados.setVisibility(View.VISIBLE);

        textViewInfo1a.setVisibility(View.VISIBLE);
        textViewInfo1b.setVisibility(View.VISIBLE);

        textViewInfo2a.setVisibility(View.VISIBLE);
        textViewInfo2b.setVisibility(View.VISIBLE);
        textViewInfo2a.setTextSize(16);
        textViewInfo2b.setTextSize(16);
        textViewInfo2a.setTypeface(Typeface.DEFAULT_BOLD);


        textViewOperador.setVisibility(View.VISIBLE);
        textViewOperadorLeg.setVisibility(View.VISIBLE);

        textViewLocalizacao.setVisibility(View.VISIBLE);
        textViewLocalizacaoLeg.setVisibility(View.VISIBLE);

        buttonTerminar.setVisibility(View.GONE);
        buttonTerminar.setText("");

        if (menuItemListaCarga != null)
        {
            if (pickingType.equals(FLAG_PICKING_EXP) || pickingType.equals(FLAG_PICKING_ABASTPROD))
                menuItemListaCarga.setVisible(true);

            if (pickingType.equals(FLAG_PICKING_DEVEXP))
                menuItemListaCarga.setVisible(false);
        }


        editText_leitura.requestFocus();

        getSupportActionBar().setTitle("Picking");

        FLAG_TIPO_LEITURA = FLAG_LEITURA_LOCALIZACAO;
        defineEditTextHint(false);

    }

    private void carregaLotes(String pRef)
    {

        if (!listaStockRefLote.isEmpty()) {
            listaStockRefLote.clear();
            adapterStockRefLote.notifyDataSetChanged();
        }

        if (adapterStockRefLote != null) {
            adapterStockRefLote.notifyDataSetChanged();
        }

        AsyncRequest ListarInventario = new AsyncRequest(thisActivity, 2);

        Uri builtURI_ListarInventario = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ListarLotesDispPicking?").buildUpon()
                .appendQueryParameter("referencia", pRef)
                .appendQueryParameter("pickingType", pickingType)
                .build();

        ListarInventario.execute(builtURI_ListarInventario.toString());

    }

    private void listaStockDevExpedicao(String pRef, String pPkbistamp)
    {

        if (!listaStockRefLote.isEmpty()) {
            listaStockRefLote.clear();
            adapterStockRefLote.notifyDataSetChanged();
        }

        if (adapterStockRefLote != null) {
            adapterStockRefLote.notifyDataSetChanged();
        }

        AsyncRequest ListarInventario = new AsyncRequest(thisActivity, 2);

        Uri builtURI_ListarInventario = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ListarStockDevExped?").buildUpon()
                .appendQueryParameter("referencia", pRef)
                .appendQueryParameter("lote", "%")
                .appendQueryParameter("pkbistamp", pPkbistamp)
                .build();

        ListarInventario.execute(builtURI_ListarInventario.toString());

    }

    private void processaLeituraOperador(String strOperador) {

        // na leitura de operador cria o dossier

        String opLido;
        opLido = validaLeituraOperador(strOperador);



        if (opLido.equals("")) {
            textViewOperador.setText("");
            defineEditTextHint(false);
        }
        else{

            textViewOperador.setText(opLido);

            if (FLAG_TIPO_LEITURA.equals("DESCARGAOPERADOR")) {

                if (pickingType.equals(FLAG_PICKING_EXP)) {
                    processaDescarga("2","EXP","EXP");
                }

                if (pickingType.equals(FLAG_PICKING_ABASTPROD)) {
                    escolheArmDescargaAbastProd();
                }

                return;

            }

            if (FLAG_TIPO_LEITURA.equals("ENCERRAR_OP_OPERADOR")) {
                encerrarOrdemPicking();
                return;
            }



            defineEditTextHint(false);
            // Vai verificar se existe o bostamp de carga. Se não existir vai criar, se existir vai carregar as linhas

            if(pickingType.equals(FLAG_PICKING_DEVEXP)) {
                armazem = 2;
                zona = "EXP";
                alveolo ="EXP";
                textViewLocalizacao.setText("[2][EXP][EXP]");
                defineEditTextHint(true);
            }

            if(pickingType.equals(FLAG_PICKING_EXP)) {
                verificaExisteBostampCarga(bostamp_carga, 7);
                defineEditTextHint(true);
            }

            if(pickingType.equals(FLAG_PICKING_ABASTPROD)) {
                verificaExisteBostampCarga(bostamp_carga, 7);
                defineEditTextHint(true);
            }

        }

    }

    private void processaLeituraCodigo(String textolido) {


        DataModelLeituraCB leituraCB = new DataModelLeituraCB(textolido);

        if (leituraCB.getTipocb().equals("R")) {
            // se o tipo de leitura for R então assumimos que lemos um codigo de barras com ref+lote+qtt
            qtd_lida = leituraCB.getQtt();
            ref_lida = leituraCB.getReferencia();
            lote_lido = leituraCB.getLote();

            if (! ref_lida.equals(pickingListSelRef)) {
                Dialogos.dialogoErro("Dados inválidos","A referencia lida não é a referência seleccionada para picking",5,thisActivity,false);
                armazem = 0;
                zona = "";
                alveolo = "";
                textViewLocalizacao.setText("");
                defineEditTextHint(false);
            }
            else
                processaLeituraLote(ref_lida,lote_lido);

            return;
        }

        // se o tipo de código de barras não for R então vamos assumir que lemos a referencia, pelo que
        // vamos validar se a referencia existe

        if (!textolido.equals(pickingListSelRef)) {
            Dialogos.dialogoErro("Dados inválidos","A referencia lida não é a referência seleccionada para picking",5,thisActivity,false);
            defineEditTextHint(false);
            return;
        }
        else
            processaLeituraRef(textolido);

    }

    private void processaLeituraLoc(String textolido) {

        // se a localização lida tem uma estrutura válida de localização então vamos carregar a informação de algum inventário
        // já existente nessa localização. Se não exitir nenhum inventário então vamos criar um novo

        if (listaStockRefLote.isEmpty()) {
            Dialogos.dialogoInfo("Leitura inválida","Não existem lotes disponiveis para efectuar o picking",3.0, thisActivity,false);
            return;
        }


        DataModelLeituraCB dmCB = new DataModelLeituraCB(textolido);

        if (dmCB.getTipocb().equals("ALV")) {

            armazem = dmCB.getArmazem();
            zona = dmCB.getZona();
            alveolo = dmCB.getAlveolo();

            // vamos validar se existe algum registo de stock na Lista de Lotes desta localização

            Boolean existeStockDestaLoc = false;

            if (!listaStockRefLote.isEmpty()) {
                for (int i = 0; i < listaStockRefLote.size() && ! existeStockDestaLoc; i++) {

                    Integer tmpArmazem = listaStockRefLote.get(i).getArmazem();
                    String tmpZona = listaStockRefLote.get(i).getZona();
                    String tmpAlveolo = listaStockRefLote.get(i).getAlveolo();

                    if (tmpArmazem == armazem && tmpZona.equals(zona) && tmpAlveolo.equals(alveolo) )
                    {
                        existeStockDestaLoc = true;
                    }

                    }
                }

        if (!existeStockDestaLoc) {
            Dialogos.dialogoInfo("Leitura inválida","Não existem lotes disponiveis da localização seleccionada para picking",3.0, thisActivity,false);
            return;
        }

            lerAlveolo(armazem,zona,alveolo);

        }

        else {

            Dialogos.dialogoErro("Erro","Leitura de localização inválida",4,thisActivity,false);
            armazem = 0;
            zona = "";
            alveolo = "";
            textViewLocalizacao.setText("");

        }

    }

    private void processaLeituraLote(final String pRef, final String pLote) {


        // vamos verificar se o lote lido corresponde ao FIFO

        // se a lista de stocks por lote não tiver elementos, então saímos do procedimento
        if (listaStockRefLote.size() == 0)
                return;

        // vamos obter a informação do lote lido

        RequestQueue queue = Volley.newRequestQueue(this);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                .appendQueryParameter("referencia", pRef)
                .appendQueryParameter("lote", pLote)
                .appendQueryParameter("filtro", "")
                .build();

        progDailog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, builtURI_processaPedido.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONconverter jsonConverter = new JSONconverter();
                            response = jsonConverter.ConvertJSON(response);

                            JSONArray values = new JSONArray(response);

                            if (values.length() == 0) {
                                Dialogos.dialogoErro("Leitura de lote","Não encontrei no sistema o lote "+pLote,4,thisActivity,false  );
                                lote = "";
                                progDailog.dismiss();
                                return;

                            }


                            JSONObject camposSE = values.getJSONObject(0);
                            referencia = camposSE.getString("ref").trim();
                            lote = camposSE.getString("lote").trim();
                            unidade = camposSE.getString("unidade");
                            uni2dade = camposSE.getString("uni2");
                            fconversao = camposSE.getDouble("fconversao");


                            // vamos verificar se o lote lido corresponde à primeira posição
                            if (! listaStockRefLote.get(0).getLote().trim().equals(lote.trim()))
                            {

                                String  txtMessage = "O lote "+lote+" não cumpre o FIFO, deseja continuar?";

                                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                                builder.setMessage(txtMessage);

                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // caso não seja o lote que corresponde ao FIFO e o utilizador confirmou que quer exexcutar o procedimento
                                        //***************************************************************************************************************
                                        ref_lida = pRef;
                                        lote_lido = lote;
                                        lerStockLoteLoc(referencia,lote,armazem,zona,alveolo,5);

                                        //***************************************************************************************************************

                                    }
                                });

                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        ref_lida = "";
                                        lote_lido = "";
                                        FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;
                                        defineEditTextHint(true);

                                    }
                                });

                                AlertDialog myDialog = builder.create();
                                myDialog.show();

                            }

                            // se o lote corresponder ao FIFO vamos executar

                            else  {

                                //***************************************************************************************************************
                                ref_lida = pRef;
                                lote_lido = lote;
                                lerStockLoteLoc(referencia,lote,armazem,zona,alveolo,5);
                                //***************************************************************************************************************


                            }



                        } catch (final JSONException e) {

                            Log.e("", "Erro Conversão: " + e.getMessage());
                            Dialogos.dialogoErro("Erro",e.getMessage(),60,thisActivity, false);

                        }
                        progDailog.dismiss();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
            }
        });

// Add the request to the RequestQueue.

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);




        // CODIGO ANTIGO. SE O ANTERIOR ESTIVER OK PODE-SE APAGAR

  /*      if (! listaStockRefLote.get(0).getLote().trim().equals(pLote.trim()))
        {

            String  txtMessage = "O lote "+pLote+" não cumpre o FIFO, deseja continuar?";

            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            builder.setMessage(txtMessage);

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // caso não seja o lote que corresponde ao FIFO e o utilizador confirmou que quer exexcutar o procedimento
                    //***************************************************************************************************************
                    ref_lida = pRef;
                    lote_lido = pLote;

                    AsyncRequest mAsynRequest = new AsyncRequest(thisActivity,3 );

                    Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                            .appendQueryParameter("referencia", pRef)
                            .appendQueryParameter("lote", pLote)
                            .appendQueryParameter("filtro", "")
                            .build();

                    mAsynRequest.execute(builtURI_processaPedido.toString());
                    //***************************************************************************************************************

                }
            });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    ref_lida = "";
                    lote_lido = "";
                    FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;
                    defineEditTextHint(true);

                }
            });

            AlertDialog myDialog = builder.create();
            myDialog.show();

        }

        // se o lote corresponder ao FIFO vamos executar

        else  {


            //***************************************************************************************************************
            ref_lida = pRef;
            lote_lido = pLote;

            AsyncRequest mAsynRequest = new AsyncRequest(thisActivity,3 );

            Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                    .appendQueryParameter("referencia", pRef)
                    .appendQueryParameter("lote", pLote)
                    .appendQueryParameter("filtro", "")
                    .build();

            mAsynRequest.execute(builtURI_processaPedido.toString());
            //***************************************************************************************************************


        }*/






    }

    private void processaLeituraRef(String pRef) {

        ref_lida = pRef;

        AsyncRequest mAsynRequest = new AsyncRequest(thisActivity,4 );

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerST?").buildUpon()
                .appendQueryParameter("referencia", pRef)
                .appendQueryParameter("filtro", "")
                .build();

        mAsynRequest.execute(builtURI_processaPedido.toString());

    }


    private String validaLeituraOperador(String textoLido) {
            /* Esta função valida a leitura do operador. Se tudo estiver ok então carrega os dados do operador
                Senão, devolve falso
            */
        String opPrefixp = "";
        String opNumero = "";

        if (textoLido.length() < 5) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",2,thisActivity,false);
            return "";
        }

        opPrefixp = textoLido.substring(0, 4);

        if (!opPrefixp.equals("(OP)")) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",2,thisActivity,false);

            return "";
        } else {
            // Vamos buscar o operador
            opNumero = textoLido.substring(4);
                /* Se a substring operador for numerica e inteira então vamos carregar os dados do operador
                   Senão vamos devolver um erro
                */
            try {
                Integer val = Integer.valueOf(opNumero);
                if (val != null) {
                    return opNumero;
                } else
                    return "";
            } catch (NumberFormatException e) {
                Dialogos.dialogoErro("Erro", "Operador não é válido",3,thisActivity,false);
                return "";
            }

        }

    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        // editText_leitura.setText(clear);

        if (FLAG_TIPO_LEITURA.equals("DESCARGAOPERADOR")) {

            if (listViewCarga != null || !(listViewCarga.getVisibility() == View.VISIBLE)) {
                editText_leitura.setHint("Processar descarga: Ler operador");
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Processar descarga", "Ler operador",2.0,thisActivity,false);
                return;
            }
        }

        if (FLAG_TIPO_LEITURA.equals("ENCERRAR_OP_OPERADOR")) {
            editText_leitura.setHint("Encerrar ordem Picking: Ler operador");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Encerrar Ordem Picking", "Ler operador",2.0,thisActivity,false);
            return;
        }

        if (listViewLotes == null || !(listViewLotes.getVisibility() == View.VISIBLE)) {
            return;
        }

        String txtMessage = "";
        Double tempoDialogo = 2.0;

        // se o operador estiver vazio obriga a que seja lido o operador
        if (textViewOperador.getText().toString().isEmpty()) {
            editText_leitura.setHint("Ler operador");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler operador"
                    ,1.0, thisActivity,false);
            FLAG_TIPO_LEITURA = FLAG_LEITURA_OPERADOR;
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            return;
        }

        // se a localização estiver vazia obriga a que seja lida a localização

        if (textViewLocalizacao.getText().toString().equals("")) {

            if (pickingType.equals(FLAG_PICKING_DEVEXP)) {
                armazem = 2;
                zona = "EXP";
                alveolo ="EXP";
                textViewLocalizacao.setText("[2][EXP][EXP]");
                FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;
                defineEditTextHint(mostraDialogoInfo);
                return;
            }

            txtMessage = "Ler localização";
            tempoDialogo = 0.5;
            editText_leitura.setHint(txtMessage);
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", txtMessage,tempoDialogo,thisActivity,false);

            FLAG_TIPO_LEITURA = FLAG_LEITURA_LOCALIZACAO;
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            armazem = 0;
            zona = "";
            alveolo = "";
            return;
        }

        // se chegados aqui e o hint estiver vazio então quer dizer que
        if (editText_leitura.getHint().toString().isEmpty()) {
            FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;
        }


        switch (FLAG_TIPO_LEITURA) {
            case FLAG_LEITURA_CODIGO:
                editText_leitura.setHint("Ler codigo");
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;

                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", editText_leitura.getHint().toString(),0.5,thisActivity,false);

                break;
            case FLAG_LEITURA_LOTE:
                lote_lido = "";
                qtd_lida = 0;
                editText_leitura.setHint("Ler lote (Ref:" + ref_lida + ")");
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler lote",0.5,thisActivity,false);
                break;
            default:
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;
                editText_leitura.setHint("");
                break;
        }

    }

    private  void lerAlveolo(final Integer pArmazem, final String pZona, final String pAlveolo)
    {


        LerAlveoloWs lerAlveoloWs = new LerAlveoloWs(this);

        lerAlveoloWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
            @Override
            public void onSuccess(DataModelAlv dmAlveolo) {


                String infolocalizacao  = "["+pArmazem+"]["+pZona+"]["+pAlveolo+"]";

                if (dmAlveolo == null) {

                    Dialogos.dialogoErro("", "Não encontrei no sistema a localização "+infolocalizacao, 3, thisActivity, false);
                    defineEditTextHint(false);

                }
                else {

                    textViewLocalizacao.setText(infolocalizacao);
                    FLAG_TIPO_LEITURA = FLAG_LEITURA_CODIGO;

                    defineEditTextHint(true);

                }

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        lerAlveoloWs.execute(pArmazem,pZona,pAlveolo);

    }

    private void lerStockLoteLoc(String pRef, String pLote, Integer pArmazem, String pZona, String pAlveolo, Integer pOperacao) {


        AsyncRequest processaPedido = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerStock?").buildUpon()
                .appendQueryParameter("ref",String.valueOf (pRef))
                .appendQueryParameter("lote",String.valueOf (pLote))
                .appendQueryParameter("armazem",String.valueOf (pArmazem))
                .appendQueryParameter("zona",pZona)
                .appendQueryParameter("alveolo",pAlveolo)
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());


    }

    private  void verificaExisteBostampCarga(String pBostamp,int pOperacao)
    {

        AsyncRequest processaPedido = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ExisteBostamp?").buildUpon()
                .appendQueryParameter("bostamp",pBostamp)
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());

    }

    private  void criaDossierCarga(int pOperacao)
    {

        AsyncRequest processaPedido = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/criaBO?").buildUpon()
                .appendQueryParameter("ndos",String.valueOf(6))
                .appendQueryParameter("no", String.valueOf(137))
                .appendQueryParameter("ent", "CL")
                .appendQueryParameter("data", "")
                .appendQueryParameter("est", String.valueOf(0))
                .appendQueryParameter("user", String.valueOf(textViewOperador.getText().toString()))
                .appendQueryParameter("armazem", "0")
                .appendQueryParameter("zona1", "")
                .appendQueryParameter("alveolo1", "")
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());
    }

    private  void lerLinhasCarga(String pBostamp, Integer pOperacao)
    {

        if (!listaCarga.isEmpty()) {
            listaCarga.clear();
            adapterListaCarga.notifyDataSetChanged();
        }

        if (adapterListaCarga != null) {
            adapterListaCarga.notifyDataSetChanged();
        }

        // atividade que vai passar para a função ler linhas de carga
        String pAtividade = "";

        if(pickingType.equals(FLAG_PICKING_ABASTPROD))
            pAtividade = "picking_abastprod";

        AsyncRequest processaPedido = new AsyncRequest(thisActivity, pOperacao);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerLinhasCarga?").buildUpon()
                .appendQueryParameter("bostamp",pBostamp)
                .appendQueryParameter("atividade",pAtividade)
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());
    }

    private void menuItemRefreshClick() {

        if (listViewCarga != null && listViewCarga.getVisibility() == View.VISIBLE) {
            lerLinhasCarga(bostamp_carga,9);
            return;
        }

        // se a listview de lotes estiver visivel o que faz o botão refrescar
        if (listViewLotes.getVisibility() == View.VISIBLE) {
            if (pickingType.equals(FLAG_PICKING_EXP) || pickingType.equals(FLAG_PICKING_ABASTPROD))
                refreshPickingList(pickingDocSelBostamp);

            if (pickingType.equals(FLAG_PICKING_DEVEXP)){
                if (listaStockRefLote.isEmpty()) {
                    onBackPressed();
                }
                else {
                    listaStockDevExpedicao(pickingListSelRef,pickingListSelBistamp);
                }
            }
            return;
        }

        if (listViewPickingList.getVisibility() == View.VISIBLE) {
            refreshPickingList(pickingDocSelBostamp);
            return;
        }

        // se a listview de lotes estiver visivel o que faz o botão refrescar
        if (listViewPickingDocs.getVisibility() == View.VISIBLE) {
            carregaPickingDocs();
            return;
        }



    }

    private  double getQttEmFalta(String pRef) {
        Double qttFalta = 0.0;

        if (!listaPickingList.isEmpty()) {
            for (int i = 0; i < listaPickingList.size(); i++) {

                if (listaPickingList.get(i).getReferencia().equals(pRef))
                {
                    if (pickingType.equals(FLAG_PICKING_EXP))
                        qttFalta = qttFalta+listaPickingList.get(i).getQtt() - listaPickingList.get(i).getBinum1();

                    if (pickingType.equals(FLAG_PICKING_ABASTPROD))
                        qttFalta = qttFalta+listaPickingList.get(i).getQtt() - listaPickingList.get(i).getQtt2();

                }
            }
        }

        return qttFalta;
    }

    private String getUnidadeRef(String pRef){
        String retUnidade = "";
        // obtem a unidade da referencia que está no picking list
        if (!listaPickingList.isEmpty()) {
            for (int i = 0; i < listaPickingList.size(); i++) {

                if (listaPickingList.get(i).getReferencia().equals(pRef))
                {
                    retUnidade = listaPickingList.get(i).getUnidade();
                    return retUnidade;
                }
            }
        }

        return retUnidade;
    }

    private  void menuItemListClick(){

        if (listViewCarga.getVisibility() != View.VISIBLE) {

            listViewCarga.setVisibility(View.VISIBLE);
            buttonTerminar.setVisibility(View.VISIBLE);
            constraintLayoutDados.setVisibility(View.GONE);
            constraintLayoutLeitura.setVisibility(View.GONE);
            menuItemListaCarga.setVisible(false);
            buttonTerminar.setText("Descarregar");


            getSupportActionBar().setTitle("Lista de carga");
            lerLinhasCarga(bostamp_carga,9);

        }

    }

    private void processaDescarga(final String pArmazem, final String pZona, final String pAlveolo) {


        String localdescarga = "["+pArmazem+"]["+pZona+"]["+pAlveolo+"]";

        String txtMessage = "Deseja descarregar a lista de picking na localização "+localdescarga+" ?";


        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage(txtMessage);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                AsyncRequest processaPedido = new AsyncRequest(thisActivity, 11);

                Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/processaPickingDescarga?").buildUpon()
                        .appendQueryParameter("bostamp",bostamp_carga)
                        .appendQueryParameter("operador",textViewOperador.getText().toString())
                        .appendQueryParameter("armazem",pArmazem)
                        .appendQueryParameter("zona",pZona)
                        .appendQueryParameter("alveolo",pAlveolo)
                        .build();

                processaPedido.execute(builtURI_processaPedido.toString());

            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog myDialog = builder.create();
        myDialog.show();

    }

    private void encerrarOrdemPicking() {


        String txtMessage = "";

        if (pickingType.equals(FLAG_PICKING_ABASTPROD))
            txtMessage = "Deseja dar como concluída a ordem de picking para a OF"+ textViewInfo1a.getText()+" ?";

        if (pickingType.equals(FLAG_PICKING_EXP))
            txtMessage = "Deseja dar como concluída a ordem de picking para o cliente "+ textViewInfo1a.getText()+" ?";

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage(txtMessage);

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                AsyncRequest processaPedido = new AsyncRequest(thisActivity, 12);

                Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ConcluirOrdemPicking?").buildUpon()
                        .appendQueryParameter("bostamp",pickingDocSelBostamp)
                        .build();

                processaPedido.execute(builtURI_processaPedido.toString());


            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mostraListViewPickingList();

            }
        });

        AlertDialog myDialog = builder.create();
        myDialog.show();

    }

    private void escolheArmDescargaAbastProd(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Escolha local descarga");

        builder.setItems(armazensDescarga,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {


                Context context = getApplicationContext();
                CharSequence text = armazensDescarga[item].toString();

                switch (text.toString()){
                    case (TIPO_ARMAZEM_60):
                        processaDescarga("60","MP","MP");
                        break;
                    case (TIPO_ARMAZEM_61):
                        processaDescarga("61","G","G");
                        break;
                    case (TIPO_ARMAZEM_62):
                        processaDescarga("62","G","G");
                        break;
                }


                alertDialogOpcoes.dismiss();

            }
        });

        alertDialogOpcoes = builder.create();
        alertDialogOpcoes.setCancelable(false);
        alertDialogOpcoes.show();

    }

    private void defineEditTextReadBarCodeListener() {


        editTextLeituraBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                editText_leitura.setText("");

                if (FLAG_TIPO_LEITURA.equals("DESCARGAOPERADOR") && !scannedText.equals("")) {
                    if (listViewCarga != null || !(listViewCarga.getVisibility() == View.VISIBLE)) {
                        processaLeituraOperador(scannedText);
                        editText_leitura.setText("");
                    }
                }

                if (FLAG_TIPO_LEITURA.equals("ENCERRAR_OP_OPERADOR") && !scannedText.equals("")) {
                    processaLeituraOperador(scannedText);
                    editText_leitura.setText("");
                    return;
                }

                // só aceitamos leituras caso estejamos com a lista de lotes visiveis
                if (listViewLotes == null ||  !(listViewLotes.getVisibility() == View.VISIBLE)) {
                    return;
                }

                if (scannedText.equals(""))
                {
                    return;
                }
                else editText_leitura.setText("");

                // Se o flag estiver OPERADOR pede o operador
                if (FLAG_TIPO_LEITURA.equals(FLAG_LEITURA_OPERADOR)) {
                    processaLeituraOperador(scannedText);
                    return;
                }

                // se a flag for a localização
                if (FLAG_TIPO_LEITURA.equals(FLAG_LEITURA_LOCALIZACAO)) {
                    processaLeituraLoc(scannedText);
                    return;
                }
                // se a flag for o código pode sempre ler a flag da localização e substituir
                if (FLAG_TIPO_LEITURA.equals(FLAG_LEITURA_CODIGO)) {
                    processaLeituraCodigo(scannedText);
                    return;
                }

                // Se flag de leitura for o lote
                if (FLAG_TIPO_LEITURA.equals(FLAG_LEITURA_LOTE)) {
                    processaLeituraLote(referencia,scannedText);
                    return;
                }

                // Se flag de leitura for a quantidade
                if (FLAG_TIPO_LEITURA.equals("QUANTIDADE")) {
                    //leituraQuantidade(textolido);
                    return;
                }

            }
        };

    }

}

