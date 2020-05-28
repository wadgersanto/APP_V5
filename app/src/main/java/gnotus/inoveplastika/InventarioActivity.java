package gnotus.inoveplastika;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.Logistica.DataModelAlv;


public class InventarioActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete, View.OnClickListener {

    private static final String clear = "";
    private static final String vazio = "\"[]\"";

    private static final String MYFILE = "configs";

    private static final String TIPO_INVENTARIO_REF = "REF";
    private static final String TIPO_INVENTARIO_ALV = "ALV";
    private static final String TIPOINVLOC_LOCCONSUMOOF = "locconsumoof";

    private Activity thisActivity = InventarioActivity.this;

    AlertDialog dialog;
    Bundle bundle = new Bundle();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView textView_operador, textView_localizacao,textView_of_titulo,textView_of,
            textViewInfo1a,textViewInfo1b,textViewInfo2a,textViewInfo2b;
    private EditText editText_leitura, input;
    private Button button_terminar;

    private SharedPreferences sharedpreferences;

    private ListView listView;
    private ArrayList<DataModelBi> lista = new ArrayList<>();

    private DataModelArrayAdapterBi adapter;

    private int armazem = 0;
    private String zona = "", alveolo = "";

    private String bostamp_inventario = "";

    private String ref_lida, lote_lido = "", unidade = "", uni2 = "", stdesign = "";
    private double qtd_lida = 0, fconversao = 1;
    private boolean ref_usalote;

    private String FLAG_MODO_EDITTEXT = "OPERADOR", tipoinvloc = "";
    private String tipo_inventario = "";
    private Boolean recalculouInventario = false;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextReadBarCodeListener;

// !!!! UTILIZA O LAYOUT DO BI_USER_LOC


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bi_userloc);

        bundle = this.getIntent().getExtras();
        carregaInfoBundle();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (tipo_inventario.equals(TIPO_INVENTARIO_ALV) && tipoinvloc.equals(""))
            getSupportActionBar().setTitle("Inventário de Localização ");

        if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)&& tipoinvloc.equals("locconsumoof"))
            getSupportActionBar().setTitle("Invent. loc. consumos OF");

        if (tipo_inventario.equals(TIPO_INVENTARIO_REF))
            getSupportActionBar().setTitle("Inventário por referência");

        getSupportActionBar().setSubtitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        //initNavigationDrawer();

        textViewInfo1a = findViewById(R.id.textView_Info1a);
        textViewInfo1b = findViewById(R.id.textView_Info1b);
        textViewInfo2a = findViewById(R.id.textView_Info2a);
        textViewInfo2b = findViewById(R.id.textView_Info2b);


        textViewInfo1a.setVisibility(View.GONE);
        textViewInfo1b.setVisibility(View.GONE);
        textViewInfo2a.setVisibility(View.GONE);
        textViewInfo2b.setVisibility(View.GONE);

        editText_leitura = findViewById(R.id.editText_leitura);
        textView_localizacao = findViewById(R.id.textView_carga_localizacao);
        textView_localizacao.setOnClickListener(this);

        textView_of = findViewById(R.id.textView_of);
        textView_of_titulo = findViewById(R.id.textView_of_titulo);

        textView_of.setVisibility(View.GONE);
        textView_of_titulo.setVisibility(View.GONE);

        textView_operador = (TextView) findViewById(R.id.textView_carga_operador);
        textView_operador.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listView_linhasbi);

        button_terminar = (Button) findViewById(R.id.button_biuserloc_terminar);
        button_terminar.setText("Lançar inventário");

        /* Anulados quando implementamos
        editText_leitura.addTextChangedListener(new MyTextWatcher(editText_leitura));
        editText_leitura.setInputType(InputType.TYPE_NULL);
        */

        defineEditTextReadBarCodeListener();
        EditTextBarCodeReader editTextBarCodeReaderLerCodigo = new EditTextBarCodeReader(editText_leitura,this);
        editTextBarCodeReaderLerCodigo.setOnGetScannedTextListener(editTextReadBarCodeListener);


        bostamp_inventario ="";

        // Vamos ler a informação do bundle


        button_terminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ao terminar vamos executar o script


                if (textView_operador.getText().toString().isEmpty()) {
                    Dialogos.dialogoErro("Variaveis em falta","Falta leitura de operador",3,InventarioActivity.this, false);
                    return;

                }

                if (lista.isEmpty()) {
                    return;
                }


                if (tipo_inventario.equals("ALV")) {
                    if (!lista.isEmpty()) {
                        for (int i = 0; i < lista.size(); i++) {
                            if (!lista.get(i).getJacontou()) {
                                Dialogos.dialogoErro("Atenção", "Existem linhas do inventário que não foram contadas", 3,
                                        InventarioActivity.this, false);
                                return;
                            }
                        }
                    }
                }

                String txtMessage = "Deseja lançar o inventário?";

                AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this);
                builder.setMessage(txtMessage);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        String param_tipoinventario = "";
                        if (tipoinvloc.equals("")) param_tipoinventario = "LOCALIZACAO";
                        // se o tipo de inventário de localização for localizaçaõ de consumo de of entao
                        if (tipoinvloc.equals(TIPOINVLOC_LOCCONSUMOOF))
                            param_tipoinventario = "LOCCONSUMOOF";

                        AsyncRequest procAcertoInvAsynRequest = new AsyncRequest(InventarioActivity.this, 1);

                        Uri builtURI_procAcertoInv = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ProcessaAcertoInventario?").buildUpon()
                                .appendQueryParameter("bostamp_inventario", bostamp_inventario)
                                .appendQueryParameter("operador", textView_operador.getText().toString())
                                .appendQueryParameter("tipoinventario", param_tipoinventario)
                                .build();

                        procAcertoInvAsynRequest.execute(builtURI_procAcertoInv.toString());

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

                // stock no Sistema
                Double fconversao = adapter.getItem(position).getFconversao();
                Double stock = adapter.getItem(position).getQtt2();
                Double stockAlt = UserFunc.arredonda(adapter.getItem(position).getQtt2()/fconversao,4);

                System.out.println("Arrendonda 1.23456: "+UserFunc.arredonda(1.23456,4));

                Double qttContagem = adapter.getItem(position).getQtt();
                Double qttAltContagem = adapter.getItem(position).getQtt()/fconversao;

                // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
                bundle.putBoolean("editing_listview", true);
                bundle.putString("trf_mode", "");
                bundle.putString("bostamp_carga", bostamp_inventario);
                bundle.putString("ref", adapter.getItem(position).getReferencia());
                bundle.putString("lote", adapter.getItem(position).getLote());
                bundle.putString("design", adapter.getItem(position).getDescricao());
                bundle.putDouble("stock", stock);
                bundle.putDouble("stockAlt", stockAlt);

                bundle.putString("armazem_ori",String.valueOf(adapter.getItem(position).getArmazem()));
                bundle.putString("zona_ori", adapter.getItem(position).getZona1());
                bundle.putString("alveolo_ori", adapter.getItem(position).getAlveolo1());

                // ao chamar a atividade de editar quantidade no inventário por of só queremos usar a unidade principal

                bundle.putDouble("fconversao",fconversao);

                bundle.putDouble("qtd_lida",qttContagem );
                bundle.putDouble("qtdAlt_lida", qttAltContagem);
                bundle.putString("unidade", adapter.getItem(position).getUnidade());
                bundle.putString("unidadeAlt", adapter.getItem(position).getUnidad2());

                bundle.putBoolean("valida_stock", false);
                bundle.putString("atividade", "inventario");

                bundle.putString("bistamp_carga", adapter.getItem(position).getBistamp());

                bundle.putString("tipo_inventario",tipo_inventario);
                bundle.putString("tipoinvloc",tipoinvloc);


                Intent EditIntent = new Intent(InventarioActivity.this, EditActivity.class);
                EditIntent.putExtras(bundle);
                startActivity(EditIntent);

            }
        });

        defineEditTextHint(true);
    }



    public void requestFocus(View view) {

    }

    public void onBackPressed() {

        if(! bostamp_inventario.equals("")){


            AsyncRequest processaPedido = new AsyncRequest(InventarioActivity.this, 4);

            Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                    .appendQueryParameter("bostamp",String.valueOf (bostamp_inventario))
                    .appendQueryParameter("tabela","BO")
                    .appendQueryParameter("onlyifnolines",String.valueOf(false))
                    .build();

            processaPedido.execute(builtURI_processaPedido.toString());

        }

        else finish();
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

        if (tipo_inventario.equals(TIPO_INVENTARIO_REF)) {
            defineEditTextHint(true);
        }

        if (! bostamp_inventario.equals("")) {
            devolveLinhasInventario(bostamp_inventario, "0", 0, "", "", false);
        }


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

        if (v.getId() == R.id.textView_carga_operador) {
            textView_operador.setText(clear);
            defineEditTextHint(true);
            requestFocus(editText_leitura);
            return;
        }


        if (v.getId() == R.id.textView_carga_localizacao) {


            if (textView_localizacao.getText().toString().equals("")) return;

            if (tipo_inventario.equals(TIPO_INVENTARIO_REF)){
                textView_localizacao.setText(clear);
                armazem = 0;
                zona = "";
                alveolo = "";
                defineEditTextHint(true);
            }

            if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)){

                String alertDialogMessage = "";

                if (lista.isEmpty())
                        alertDialogMessage = "Quer limpar a localizaçao?";
                else
                    alertDialogMessage = "Quer limpar a localizaçao?  (Nota: vai perder o inventario realizado)";


                AlertDialog.Builder builder = new AlertDialog.Builder(InventarioActivity.this);
                builder.setMessage(alertDialogMessage);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // vamos eliminar as linhas do dossier

                        AsyncRequest eliminaDossier = new AsyncRequest(InventarioActivity.this, 3);

                        Uri builtURI_processapedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                                .appendQueryParameter("bostamp", bostamp_inventario)
                                .appendQueryParameter("tabela", "BI")
                                .appendQueryParameter("onlyifnolines",String.valueOf(true))
                                .build();

                        eliminaDossier.execute(builtURI_processapedido.toString());

                        textView_localizacao.setText(clear);
                        armazem = 0;
                        zona = "";
                        alveolo = "";
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog myDialog = builder.create();
                myDialog.show();

                }


        }


    }

    @Override
    public void asyncResponse(String response, int op) {

        ImageView image = new ImageView(InventarioActivity.this);
        image.setImageResource(R.mipmap.ic_notok);

        if (response == null) {
            Dialogos.dialogoErro("Erro","Ocorreu um erro no processamento do pedido ao serviço",5,InventarioActivity.this, false);
            editText_leitura.setText(clear);
            return;
        }

        String tmpResposta = "";

        // Cria o stamp de inventário

        if (op == 0) {

            // se a resposta com o bostamp
            tmpResposta = response.substring(1, response.length() - 1);

            System.out.println("resposta op = 0 :" + tmpResposta);

            if (tmpResposta.equals("erro")) {
                Dialogos.dialogoErro("Erro", "Erro na criaçao do dossier de inventario", 5, InventarioActivity.this, false);

                // O stamp de inventário é criado no momento da leitura da localização,
                // pelo que se falhar a criação vamos limpar a localização
                textView_localizacao.setText(clear);

                defineEditTextHint(false);
                return;
            }
            else
            {
                bostamp_inventario = tmpResposta;
                String operador = textView_operador.getText().toString();


                if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)){
                    devolveLinhasInventario(bostamp_inventario,operador,armazem,zona,alveolo,true);
                }

                else
                {
                    defineEditTextHint(true);
                }



            }

        }

        //*************************************************************************
        // PROCESSA ACERTO DE INVENTARIO

        if (op == 1) {
            tmpResposta = response.substring(1, response.length() - 1);
            if (tmpResposta.equals("OK")) {

                Dialogos.dialogoInfo("Sucesso", "Inventário lançado!", 2.0, InventarioActivity.this, false);

                if (!lista.isEmpty()) {
                    lista.clear();
                    adapter.notifyDataSetChanged();
                }
                bostamp_inventario = "";
                textView_localizacao.setText("");
                textView_operador.setText("");
                defineEditTextHint(false);
            } else {
                Dialogos.dialogoErro("Erro no lançamento de inventário", tmpResposta, 60, InventarioActivity.this, false);
            }
        }


        //*************************************************************************
        // processamento devolveLinhasInventario
        if (op == 2) {


            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                // se os dados vierem vazios
                if (response.equals("[]")) {

                    if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)){

                        if (recalculouInventario) {

                            Dialogos.dialogoInfo("", "No sistema não existe stock a inventariar na localização", 3.0, InventarioActivity.this, false);
                            recalculouInventario = false;
                        }

                        defineEditTextHint(false);
                    }
                    else {
                        defineEditTextHint(true);
                    }
                    return;
                }


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
                    Double j_fconversao = c.getDouble("fconversao");
                    Boolean j_jacontou = c.getBoolean("u_jacontou");


                    lista.add(new DataModelBi(j_bistamp, j_referencia, j_lote, j_descricao, j_qtt, j_uni2qtt, j_qtt2,0, j_unidade, j_unidad2, j_armazem, j_ar2mazem,
                            j_zona1, j_alveolo1, j_zona2, j_alveolo2,j_fconversao, j_jacontou));

                }

                Bundle myBundle = new Bundle();
                myBundle.putString("tipoinventario",tipo_inventario);
                myBundle.putString("tipoinvloc",tipoinvloc);

                adapter = new DataModelArrayAdapterBi(this, 0, lista,myBundle);
                listView.setAdapter(adapter);

                defineEditTextHint(true);

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro",e.getMessage(),60,InventarioActivity.this, false);
                textView_localizacao.setText(clear);
                defineEditTextHint(false);
            }

            System.out.println("Fim da leitura operação 2");

        }

        // elimina dados do dossier

        if (op == 3){

            if (!lista.isEmpty()) {
                lista.clear();
                adapter.notifyDataSetChanged();
            }

            if (tipo_inventario.equals(TIPO_INVENTARIO_ALV))
                defineEditTextHint(true);

        }

        // elimina dossier quando sai da aplicação
        if (op == 4){

            finish();

        }


        // valida se a referencia pode ser inserida no inventario por localização quando estamos a fazer um inventário de localizações de consumos of

        if (op == 5 || op == 6){

            // op = 5 - Pedido feito quando foi lida a referencia começada por R
            // op = 6 - Pedido feito quando foi lida a referencia isoladamente


            if (! Boolean.valueOf(response)) {
                String tmpInfo = "A referencia "+ref_lida+" não tem consumos por acertar nesta localização pelo que não pode ser considerada para inventário";

                Dialogos.dialogoErro("Leitura inválida",tmpInfo,6,InventarioActivity.this,false);

            }
            else {

                if (op == 5) processaLeituraRefInv();
                if (op == 6) validaRef(ref_lida);

            }
       }


       // Leitura do ws dos dados da localização lida pelo utilizador

       if (op == 8) {

            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);

                if (response.equals("[]")) {

                    Dialogos.dialogoErro("", "Não encontrei no sistema a localização ["+armazem+"]["+zona+"]["+alveolo+"]", 3, InventarioActivity.this, false);
                    defineEditTextHint(false);
                }
                else
                {
                    // vamos ler os dados
                    JSONArray values = new JSONArray(response);

                    JSONObject c = values.getJSONObject(0);

                    // localização de consumo de of
                    Boolean j_loccof = c.getBoolean("szzloccof");


                    if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)) {

                        // se o tipo de inventario de localização for de consumo of
                        if (tipoinvloc.equals(TIPOINVLOC_LOCCONSUMOOF)) {
                            // Se a localização nao for de consumos de of
                            if (! j_loccof ) {

                                Dialogos.dialogoErro("", "A localização [" + armazem + "][" + zona + "][" + alveolo + "] não é uma localização de consumos de OF", 5, InventarioActivity.this, false);
                                textView_localizacao.setText(clear);
                                defineEditTextHint(false);
                                return;
                            }
                            else finalizaLeituraLocalizacao();

                        }

                        // se o tipo de inventário for de localização e não for um inventário de localização de consumo então será necessário validaar
                        // se existe a localização de inventário deste armazem e desta zona
                        else {
                            // lerAlveolo(armazem,"INV","INV",9);
                        }
                    }

                    else finalizaLeituraLocalizacao();

                }

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro",e.getMessage(),60,InventarioActivity.this, false);
                textView_localizacao.setText(clear);
                defineEditTextHint(false);
            }

        }

        // resposta quando lê os dados da consulta da localização de inventário INV INV
        if (op == 9) {


            try {

                JSONconverter jsonConverter = new JSONconverter();
                response = jsonConverter.ConvertJSON(response);
                JSONArray values = new JSONArray(response);

                if (response.equals("[]")) {

                    Dialogos.dialogoErro("", "Não encontrei no sistema a localização para inventário : ["+armazem+"]["+zona+"][INV]", 5, InventarioActivity.this, false);
                    defineEditTextHint(false);
                }
                else
                {
                    finalizaLeituraLocalizacao();

                }

            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro",e.getMessage(),60,InventarioActivity.this, false);
                textView_localizacao.setText(clear);
                defineEditTextHint(false);
            }

        }

        if (op == 10) {

            aSyncResponseLocationCanBeRead(response,op);
        }


    }


    private boolean validaLeituraOperador(String textoLido) {
            /* Esta função valida a leitura do operador. Se tudo estiver ok então carrega os dados do operador
                Senão, devolve falso
            */
        String opPrefixp = "";
        String opNumero = "";

        if (textoLido.length() < 5) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",3,InventarioActivity.this,false);
            return false;
        }

        opPrefixp = textoLido.substring(0, 4);

        if (!opPrefixp.equals("(OP)")) {
            Dialogos.dialogoErro("Erro", "Operador não é válido",3,InventarioActivity.this,false);

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
                Dialogos.dialogoErro("Erro", "Operador não é válido",3,InventarioActivity.this,false);
                return false;
            }

        }

    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        // editText_leitura.setText(clear);

        String txtMessage = "";
        Double tempoDialogo = 0.0;

        // se o operador estiver vazio obriga a que seja lido o operador
        if (textView_operador.getText().toString().isEmpty()) {
            editText_leitura.setHint("Ler operador");
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler operador",1.0, InventarioActivity.this,false);
            FLAG_MODO_EDITTEXT = "OPERADOR";
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            return;
        }

        // se a localização estiver vazia obriga a que seja lida a localização

        if (textView_localizacao.getText().toString().equals("")) {


            if (tipo_inventario.equals(TIPO_INVENTARIO_REF)){
                txtMessage = "Ler localização";
                tempoDialogo = 1.0;
            }

            if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)){
                txtMessage = "Ler localização do Inventário";
                tempoDialogo = 1.0;
            }


            editText_leitura.setHint(txtMessage);
            if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", txtMessage,tempoDialogo,InventarioActivity.this,false);


            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            ref_lida = "";
            lote_lido = "";
            qtd_lida = 0;
            armazem = 0;
            zona = "";
            alveolo = "";
            return;
        }
        else  {
            if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) FLAG_MODO_EDITTEXT = "CODIGO";
        }

        switch (FLAG_MODO_EDITTEXT) {
            case "CODIGO":
                editText_leitura.setHint("Ler codigo");

                // No caso do código só vamos mostrar a mensagem quando o inventario for de referencia

                if (tipo_inventario.equals(TIPO_INVENTARIO_REF)) {

                    if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler código", 2.0, InventarioActivity.this, false);
                }

                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;
                break;
            case "LOTE":
                lote_lido = "";
                qtd_lida = 0;
                editText_leitura.setHint("Ler lote (Ref:" + ref_lida + ")");
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler lote",1.0,InventarioActivity.this,false);
                break;
            case "QUANTIDADE":
                qtd_lida = 0;
                if (lote_lido.equals("")) {
                    editText_leitura.setHint("Ler Qtd  (" + ref_lida + ") ");
                } else {
                    editText_leitura.setHint("Ler Qtd  (" + ref_lida + ") / (" + lote_lido + ")");
                }
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Acção", "Ler quantidade",2.0,InventarioActivity.this,false);
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

        if (textoLido.length()< 3) return "VALIDA_REF";

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

    private void criaDossierInventario(String ndos, String no, String estab, int pArmazem, String pZona, String pAlveolo,String tipoinventario, String tipoinvloc) {

        AsyncRequest criaDossierInv = new AsyncRequest(InventarioActivity.this, 0);

        Uri builtURI_criaDossierInv = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/CriaDossierInventario?").buildUpon()
                .appendQueryParameter("ndos", ndos)
                .appendQueryParameter("no", no)
                .appendQueryParameter("data", "")
                .appendQueryParameter("estab", estab)
                .appendQueryParameter("user", textView_operador.getText().toString())
                .appendQueryParameter("armazem", String.valueOf(pArmazem))
                .appendQueryParameter("zona1", pZona)
                .appendQueryParameter("alveolo1", pAlveolo)
                .appendQueryParameter("tipoinventario", tipoinventario)
                .appendQueryParameter("tipoinvloc", tipoinvloc)
                .build();

        criaDossierInv.execute(builtURI_criaDossierInv.toString());
    }

    private boolean validaLeituraRef(String pReferencia) {

        // Lê a referencia na base de dados e verifica se ela existe, carregando algumas caracteristicas

        JSONArray arrayST;
        // le lote e carrega o array
        arrayST = WsExecute.lerST(pReferencia,  InventarioActivity.this);

        // se for nulo quer dizer que tivemos um erro no webservice
        // e que a mensagem de erro foi mostrada no processamento do webservice
        if (arrayST.equals(null)) return false;

        if (arrayST.length() == 0) {
            Dialogos.dialogoErro("Validação do´artigo", "Não encontrei no sistema o artigo " + pReferencia, 10, InventarioActivity.this, false);
            ref_lida = "";
            return false;
        } else {
            try {
                JSONObject camposST = arrayST.getJSONObject(0);
                ref_lida = camposST.getString("ref").trim();
                unidade = camposST.getString("unidade");
                uni2 = camposST.getString("uni2");
                fconversao = camposST.getDouble("fconversao");;
                stdesign = camposST.getString("design");
                ref_usalote = camposST.getBoolean("usalote");
                return true;
            } catch (final JSONException e) {
                Log.e("", "Erro Conversão: " + e.getMessage());
                return false;
            }
        }

    }

    private void processaLeituraRefInv() {

        // este procedimento vai chamar o ecrã edit activity carregando os dados necessários para processar a informação

        Double stock = 0.0, stockAlt = 0.0;
        Double qttContada = 0.00;
        Double fconversao = 1.0;
        String unidad2 = "", unidade = "";
        JSONArray jsonLerStock;


        for (int n = 0; n < lista.size(); n++) {
            if (lista.get(n).getReferencia().trim().equals(ref_lida) &&
                    lista.get(n).getLote().trim().equals(lote_lido) &&
                    lista.get(n).getArmazem() == armazem  &&
                    lista.get(n).getZona1().trim().equals(zona) &&
                    lista.get(n).getAlveolo1().trim().equals(alveolo))
            {
                qttContada = qttContada + lista.get(n).getQtt();
            }
        }


        // Vamos ler o stock da localização que foi lido

        jsonLerStock = WsExecute.lerStock(ref_lida,lote_lido,armazem,zona,alveolo,InventarioActivity.this);


            try {
                for (int i = 0; i < jsonLerStock.length(); i++) {

                    JSONObject c = jsonLerStock.getJSONObject(i);
                    fconversao = c.getDouble("fconversao");
                    unidade = c.getString("unidade");
                    unidad2 = c.getString("uni2");
                    stock =c.getDouble("stock");
                    stockAlt=c.getDouble("stock2");
                    stockAlt =  UserFunc.arredonda(stockAlt,4);
                }
            } catch (final JSONException e) {

                Log.e("", "Erro Conversão: " + e.getMessage());
                Dialogos.dialogoErro("Erro na leitura de dados",e.getMessage(),5,InventarioActivity.this,false);
                return;
            }



        Double qttAltContada =qttContada/fconversao;


        bundle.clear();

        // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
        bundle.putBoolean("editing_listview", false);
        bundle.putString("trf_mode", "");
        bundle.putString("bostamp_carga", bostamp_inventario);
        bundle.putString("ref", ref_lida);
        bundle.putString("lote", lote_lido);
        bundle.putString("design", stdesign);
        bundle.putDouble("stock", stock);
        bundle.putDouble("stockAlt", stockAlt);
        bundle.putDouble("fconversao", fconversao);



        bundle.putDouble("qtd_lida", qttContada);
        bundle.putDouble("qtdAlt_lida", qttAltContada);
        bundle.putString("unidade", unidade);
        bundle.putString("unidadeAlt", unidad2);

        bundle.putString("armazem_ori",String.valueOf(armazem));
        bundle.putString("zona_ori", zona);
        bundle.putString("alveolo_ori", alveolo);

        bundle.putBoolean("valida_stock", false);
        bundle.putString("atividade", "inventario");

        bundle.putString("tipo_inventario",tipo_inventario);

        // se é inventario por localizacao, qual o tipo ( vaziou ou locconsumoof-Localização de consumos de of)
        bundle.putString("tipoinvloc",tipoinvloc);

        Intent EditIntent = new Intent(InventarioActivity.this, EditActivity.class);
        EditIntent.putExtras(bundle);
        startActivity(EditIntent);

        if (tipo_inventario.equals(TIPO_INVENTARIO_REF)) {
            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            textView_localizacao.setText("");
        }
        else {
            FLAG_MODO_EDITTEXT = "CODIGO";
        }

    }

    private void leituraOperador(String strOperador) {

        // na leitura de operador cria o dossier
        if (validaLeituraOperador(strOperador)) {
            // cria o dossier de carga se flag_modo false
            FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            defineEditTextHint(true);
        }

    }

    private void processaLeituraLoc(String textolido) {

        // se a localização lida tem uma estrutura válida de localização então vamos carregar a informação de algum inventário
        // já existente nessa localização. Se não exitir nenhum inventário então vamos criar um novo

        if (!validaLeituraLocalizacao(textolido)) {

            Dialogos.dialogoErro("Erro","A localização lida" + textolido + "não é válida",2,InventarioActivity.this,false);
            textView_localizacao.setText(clear);
            defineEditTextHint(false);

        } else {


            // Vamos ler o alveolo Lido -- Caso
            // op = 8 vamos ler a zona e de seguida vamos validar se existe a localização de inventário no caso de ser inventário de localização
            locationCanBeRead(armazem,zona,alveolo);

        }

    }

    private void processaLeituraCodigo(String textolido) {

        String retval = validaLeituraCodigo(textolido);

        if (retval.equals("OK")) {

            if (validaLeituraRef(ref_lida)) {

                if (ref_usalote) {
                    if (lote_lido.equals("")){
                        // não leu lote, vamos mandar ler
                        FLAG_MODO_EDITTEXT = "LOTE";
                        defineEditTextHint(true);
                        return;
                    } else{
                        if (!validaLeituraLote(lote_lido)){
                            FLAG_MODO_EDITTEXT = "LOTE";
                            defineEditTextHint(false);
                            return;
                        }
                    }
                    // se não valida lote
                } else {
                    if (!lote_lido.trim().equals("")) {
                        Dialogos.dialogoInfo("Informação", "O artigo " + ref_lida + " não está configurado para usar " +
                                "lotes", 3.0, InventarioActivity.this, false);
                        defineEditTextHint(false);
                        return;
                    }
                }

                if (qtd_lida >0){

                    if (tipoinvloc.equals(TIPOINVLOC_LOCCONSUMOOF))
                    {
                        // sem estamos num inventário de localização de consumos de of então vamos verificar se a referencia pode ser lida no inventário
                        // so pode ser lida quando existam consumos dessa referencia por incluir em inventáarios

                        AsyncRequest processaPedido = new AsyncRequest(InventarioActivity.this, 5);

                        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RefConsomeOfNaLoc?").buildUpon()
                                .appendQueryParameter("referencia",ref_lida)
                                .appendQueryParameter("armazem",String.valueOf (armazem))
                                .appendQueryParameter("zona",zona)
                                .appendQueryParameter("alveolo",alveolo)
                                .appendQueryParameter("tipovalidacao","inv_loc_cof")
                                .build();

                        processaPedido.execute(builtURI_processaPedido.toString());

                    }
                    else
                        processaLeituraRefInv();

                } else {
                    FLAG_MODO_EDITTEXT = "QUANTIDADE";
                    defineEditTextHint(true);
                }
            }


            return;

            }
        else
            {
                ref_lida = "";
                lote_lido ="";
                qtd_lida = 0;
                FLAG_MODO_EDITTEXT = "CODIGO";
                defineEditTextHint(true);
            }

        // se chegamos aqui quer dizer que

        if (retval.equals("VALIDA_REF")) {

            ref_lida = textolido;

            if (tipoinvloc.equals(TIPOINVLOC_LOCCONSUMOOF))
            {
                // sem estamos num inventário de localização de consumos de of então vamos verificar se a referencia pode ser lida no inventário
                // so pode ser lida quando existam consumos dessa referencia por incluir em inventáarios

                AsyncRequest processaPedido = new AsyncRequest(InventarioActivity.this, 6);

                Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RefConsomeOfNaLoc?").buildUpon()
                        .appendQueryParameter("referencia",ref_lida)
                        .appendQueryParameter("armazem",String.valueOf (armazem))
                        .appendQueryParameter("zona",zona)
                        .appendQueryParameter("alveolo",alveolo)
                        .appendQueryParameter("tipovalidacao","inv_loc_cof")
                        .build();

                processaPedido.execute(builtURI_processaPedido.toString());

            }
            else {

                validaRef(ref_lida);
            }

        } else {
            Dialogos.dialogoErro("Erro",retval,20,InventarioActivity.this,false);
            editText_leitura.setText(clear);
            defineEditTextHint(false);
        }

    }

    private boolean validaLeituraLote(String textolido) {

        // Lê o lote
        JSONArray arraySE;
        // le lote e carrega o array
        arraySE = WsExecute.lerSE(ref_lida, textolido, "", InventarioActivity.this);

        // se for nulo quer dizer que tivemos um erro no webservice
        // e que a mensagem de erro foi mostrada no processamento do webservice
        if (arraySE.equals(null)) return false;

        if (arraySE.length() == 0) {
            Dialogos.dialogoErro("Validação do lote", "Não encontrei no sistema o lote " + textolido, 10, InventarioActivity.this, false);
            lote_lido = "";
            return false;
        } else {
            try {
                JSONObject camposSE = arraySE.getJSONObject(0);
                lote_lido = camposSE.getString("lote").trim();
                unidade = camposSE.getString("unidade");
                uni2 = camposSE.getString("uni2");
                fconversao = camposSE.getDouble("fconversao");
                return true;
            } catch (final JSONException e) {
                Log.e("", "Erro Conversão: " + e.getMessage());
                return false;
            }
        }
    }

    private void leituraLote(String textolido) {

        Boolean mValidaLote;
        mValidaLote = validaLeituraLote(textolido);

        if (mValidaLote.equals(true)) {
            FLAG_MODO_EDITTEXT = "CODIGO";
            processaLeituraRefInv();

        } else {

            FLAG_MODO_EDITTEXT = "LOTE";
        }

    }


    private void devolveLinhasInventario(String bostamp,String operador,int armazem,String zona, String alveolo, boolean recalcula) {

        // primeiro vamos apagar as linhas

        if (!lista.isEmpty()) {
            lista.clear();
            adapter.notifyDataSetChanged();
        }

        if (recalcula) recalculouInventario = true;

        AsyncRequest linhasInventario = new AsyncRequest(InventarioActivity.this, 2);


        Uri builtURI_linhasInventario = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/DevolveLinhasinventario?").buildUpon()
                .appendQueryParameter("bostamp", bostamp)
                .appendQueryParameter("operador", operador)
                .appendQueryParameter("armazem", String.valueOf(armazem))
                .appendQueryParameter("zona", zona)
                .appendQueryParameter("alveolo", alveolo)
                .appendQueryParameter("tipoinventario", "LOCALIZACAO")
                .appendQueryParameter("recalcLinhas", Boolean.toString(recalcula))
                .build();

        linhasInventario.execute(builtURI_linhasInventario.toString());

        // este procedimento vai calcular as linhas de inventário

    }
    private void carregaInfoBundle(){

        tipo_inventario = bundle.getString("tipo_inv");
        tipoinvloc = bundle.getString("tipoinvloc");

    }

    private void validaRef(String txtRef) {
        // procedimento que recebe uma referencia e verifica se ela existe na base de dados e se usa lote


        System.out.println("OnTextChange: FLAG = CODIGO, Valida a referencia");
        if (validaLeituraRef(txtRef)) {
            if(ref_usalote){
                lote_lido = "";
                FLAG_MODO_EDITTEXT = "LOTE";
                defineEditTextHint(true);
            }
            else {
                qtd_lida = 0;
                FLAG_MODO_EDITTEXT = "QUANTIDADE";
                defineEditTextHint(true);
            }
        };

    }


    private void finalizaLeituraLocalizacao () {

        // processo que corre depois de ler o WebService com os dados do Alveolo

        FLAG_MODO_EDITTEXT = "CODIGO";

        button_terminar.setText("Lançar inventário");

        if (bostamp_inventario.equals("")) {
            criaDossierInventario("32","137","0",armazem,zona,alveolo,"LOCALIZACAO",tipoinvloc);
        }
        else
        {

            String mExBostamp = WsExecute.existeBostamp(bostamp_inventario,InventarioActivity.this);

            if (mExBostamp.equals("true")){
                String operador = textView_operador.getText().toString();

                if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)) {

                    devolveLinhasInventario(bostamp_inventario,operador,armazem,zona,alveolo,false);
                }

                if (tipo_inventario.equals(TIPO_INVENTARIO_REF)) {
                    defineEditTextHint(true);
                }

            }
            else {
                criaDossierInventario("32","137","0",armazem,zona,alveolo,"LOCALIZACAO",tipoinvloc);
            }

        }

    }

    private void locationCanBeRead(int pArmazem, String pZona, String pAlveolo) {

        AsyncRequest processaPedido = new AsyncRequest(thisActivity, 10);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LocationCanBeRead?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (pArmazem))
                .appendQueryParameter("zona",pZona)
                .appendQueryParameter("alveolo",pAlveolo)
                .appendQueryParameter("activity","inventario")
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());
    }

    private void aSyncResponseLocationCanBeRead(String response, int op) {


        response = response.substring(1,response.length()-1);

        if ( ! response.equals("OK")) {

            String txtErro ="Erro não definido ";
            if (response.substring(0,3).equals("ERR"))
                txtErro = response.substring(4,response.length());

            armazem = 0;
            zona = "";
            alveolo = "";
            textView_localizacao.setText(clear);
            defineEditTextHint(false);
            Dialogos.dialogoErro("Leitura de localização",txtErro,5,thisActivity,false);

        }

        else {

            LerAlveoloWs lerAlveoloWs = new LerAlveoloWs(this);

            lerAlveoloWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
                @Override
                public void onSuccess(DataModelAlv dmAlveolo) {

                    if (dmAlveolo == null) {
                        Dialogos.dialogoErro("", "Não encontrei no sistema a localização ["+armazem+"]["+zona+"]["+alveolo+"]", 3, InventarioActivity.this, false);
                        defineEditTextHint(false);
                    }
                    else
                    {


                        if (tipo_inventario.equals(TIPO_INVENTARIO_ALV)) {

                            // se o tipo de inventario de localização for de consumo of
                            if (tipoinvloc.equals(TIPOINVLOC_LOCCONSUMOOF)) {
                                // Se a localização nao for de consumos de of
                                if (! dmAlveolo.getU_loccof() ) {

                                    Dialogos.dialogoErro("", "A localização [" + armazem + "][" + zona + "][" + alveolo + "] não é uma localização de consumos de OF", 5, InventarioActivity.this, false);
                                    textView_localizacao.setText(clear);
                                    defineEditTextHint(false);
                                    return;
                                }
                                else finalizaLeituraLocalizacao();

                            }

                            // se o tipo de inventário for de localização e não for um inventário de localização de consumo então será necessário validaar
                            // se existe a localização de inventário deste armazem e desta zona
                            else {

                                /// **************************************************************

                                // lerAlveolo(armazem,"INV","INV",9);

                                LerAlveoloWs lerAlveoloInvWs = new LerAlveoloWs(thisActivity);
                                lerAlveoloInvWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
                                    @Override
                                    public void onSuccess(DataModelAlv dmAlveolo) {


                                        if (dmAlveolo == null) {
                                            Dialogos.dialogoErro("", "Não encontrei no sistema a localização para inventário : ["+armazem+"]["+zona+"][INV]", 5, InventarioActivity.this, false);
                                            defineEditTextHint(false);
                                        }
                                        else finalizaLeituraLocalizacao();

                                    }

                                    @Override
                                    public void onError(VolleyError error) {

                                        Dialogos.dialogoErro("Erro",error.getMessage(),60,InventarioActivity.this, false);
                                        textView_localizacao.setText(clear);
                                        defineEditTextHint(false);

                                    }
                                });
                                lerAlveoloInvWs.execute(armazem,"INV","INV");
                                /// **************************************************************


                            }
                        }

                        else finalizaLeituraLocalizacao();

                    }

                }

                @Override
                public void onError(VolleyError error) {
                    Log.e("", "Erro Conversão: " + error.getMessage());
                    Dialogos.dialogoErro("Erro",error.getMessage(),60,InventarioActivity.this, false);
                    textView_localizacao.setText(clear);
                    defineEditTextHint(false);
                }
            });

            lerAlveoloWs.execute(armazem,zona,alveolo);

        }

    }

    private void defineEditTextReadBarCodeListener() {

        editTextReadBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                editText_leitura.setText(clear);

                String textolido = scannedText;

                System.out.println(FLAG_MODO_EDITTEXT);

                // Se o flag estiver OPERADOR pede o operador
                if (FLAG_MODO_EDITTEXT.equals("OPERADOR")) {
                    leituraOperador(textolido);
                    return;
                }

                // se a flag for a localização
                if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {
                    processaLeituraLoc(textolido);
                    return;
                }
                // se a flag for o código pode sempre ler a flag da localização e substituir
                if (FLAG_MODO_EDITTEXT.equals("CODIGO")) {
                    processaLeituraCodigo(textolido);
                    return;
                }

                // Se flag de leitura for o lote
                if (FLAG_MODO_EDITTEXT.equals("LOTE")) {
                    leituraLote(textolido);
                    return;
                }

                // Se flag de leitura for a quantidade
                if (FLAG_MODO_EDITTEXT.equals("QUANTIDADE")) {
                    //leituraQuantidade(textolido);
                    return;
                }


            }
        };


    }

}

