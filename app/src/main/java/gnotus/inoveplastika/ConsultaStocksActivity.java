package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;

import gnotus.inoveplastika.API.Producao.LerInfoOf2Ws;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterConsultaStock;
import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterDadosTexto;
import gnotus.inoveplastika.DataModels.DataModelConsultaStock;
import gnotus.inoveplastika.DataModels.DataModelDadosTexto;
import gnotus.inoveplastika.Producao.InfoOfDataModel;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.Producao.DataModelMpsConsumoOf;


public class ConsultaStocksActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String clear = "";
    private static final String vazio = "\"[]\"";
    private static final String TIPO_CONSULTA_REFERENCIA = "referencia";
    private static final String TIPO_CONSULTA_LOCALIZACAO = "localizacao";
    private static final String TIPO_CONSULTA_INFOOF = "INFOOF";

    private static final String MYFILE = "configs";

    private boolean FLAG_MODO = false;

    AlertDialog dialog;
    Bundle bundle = new Bundle();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView textView_rotulo1,textView_rotulo2,textView_rotulo3,textView_rotulo4;
    private TextView textView_valor1,textView_valor2,textView_valor3,textView_valor4;
    private ImageView ImageView_1,ImageView_2,ImageView_3,ImageView_4;

    private ConstraintLayout myClBotoes, myClLista, myCLFiltros;

    private EditText editText_leitura, input;
    private Button button_consultar;

    private SharedPreferences sharedpreferences;

    private Activity thisActivity = ConsultaStocksActivity.this;

    private ProgressDialog progDailog ;

    private ListView listView;

    private ArrayList<InfoOfDataModel> listaInfoOf = new ArrayList<>();

    private ArrayList<DataModelConsultaStock> myArrayListConsultaStock = new ArrayList<>();
    private ArrayList<DataModelDadosTexto> myArrayListTexto = new ArrayList<>();

    private ArrayAdapterConsultaStock myAdapterConsultaStock;
    private ArrayAdapterDadosTexto myAdapterDadosTexto;

    private String armazem_lido = "", zona_lida = "", alveolo_lido = "";
    private double qtd_lida = 0;

    private String ref_lida, lote_lido = "";

    private String FLAG_TIPO_LEITURA = "CODIGO";

    private static String tipoConsultaStock;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextLeituraBarCodeListener;
    private EditTextBarCodeReader ediTextLeituraBarCodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_consultas);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);


        //initNavigationDrawer();

        editText_leitura = (EditText) findViewById(R.id.editText_leitura);
        editText_leitura.requestFocus();

        textView_rotulo1 = (TextView) findViewById(R.id.textView_titulo_1);
        textView_rotulo2 = (TextView) findViewById(R.id.textView_titulo_2);
        textView_rotulo3 = (TextView) findViewById(R.id.textView_titulo_3);
        textView_rotulo4 = (TextView) findViewById(R.id.textView_titulo_4);

        textView_valor1 = (TextView) findViewById(R.id.textView_valor_1);
        textView_valor2 = (TextView) findViewById(R.id.textView_valor_2);
        textView_valor3 = (TextView) findViewById(R.id.textView_valor_3);
        textView_valor4 = (TextView) findViewById(R.id.textView_valor_4);

        textView_valor1.setOnClickListener(this);
        textView_valor2.setOnClickListener(this);
        textView_valor3.setOnClickListener(this);
        textView_valor4.setOnClickListener(this);

        ImageView_1 = (ImageView) findViewById(R.id.imageView_1);
        ImageView_2 = (ImageView) findViewById(R.id.imageView_2);
        ImageView_3 = (ImageView) findViewById(R.id.imageView_3);
        ImageView_4 = (ImageView) findViewById(R.id.imageView_4);

        ImageView_1.setOnClickListener(this);
        ImageView_2.setOnClickListener(this);
        ImageView_3.setOnClickListener(this);
        ImageView_4.setOnClickListener(this);

        textView_rotulo1.setVisibility(View.GONE);
        textView_rotulo2.setVisibility(View.GONE);
        textView_rotulo3.setVisibility(View.GONE);
        textView_rotulo4.setVisibility(View.GONE);

        textView_rotulo1.setOnClickListener(this);
        textView_rotulo2.setOnClickListener(this);
        textView_rotulo3.setOnClickListener(this);
        textView_rotulo4.setOnClickListener(this);

        textView_valor1.setVisibility(View.GONE);
        textView_valor2.setVisibility(View.GONE);
        textView_valor3.setVisibility(View.GONE);
        textView_valor4.setVisibility(View.GONE);

        ImageView_1.setVisibility(View.GONE);
        ImageView_2.setVisibility(View.GONE);
        ImageView_3.setVisibility(View.GONE);
        ImageView_4.setVisibility(View.GONE);

        myClBotoes = (ConstraintLayout) findViewById(R.id.constraintLayoutBotoes);
        myClLista = (ConstraintLayout) findViewById(R.id.constraintLayout_carga);
        myCLFiltros = (ConstraintLayout) findViewById(R.id.constraintLayout_localizacao);

        listView = (ListView) findViewById(R.id.listView_listagem);

        bundle = this.getIntent().getExtras();

        carregaInfoBundle();
        float factor = getResources().getDisplayMetrics().density;



        ViewGroup.LayoutParams params = listView.getLayoutParams();

        if (tipoConsultaStock.equals("localizacao")){

            getSupportActionBar().setTitle("Stock na localização");
            getSupportActionBar().setSubtitle("");

            textView_rotulo1.setText("Localização:");
            textView_rotulo1.setVisibility(View.VISIBLE);
            textView_valor1.setVisibility(View.VISIBLE);

            myClBotoes.setVisibility(View.GONE);

            //params.height =  (int) (420 * factor) ;
            //listView.setLayoutParams(params);
            //listView.requestLayout();

            editText_leitura.setHint("Ler localização");

        }

        if (tipoConsultaStock.equals("referencia")){

            editText_leitura.setHint("Ler código");

            FLAG_TIPO_LEITURA = "CODIGO";

            getSupportActionBar().setTitle("Stock por artigo");
            getSupportActionBar().setSubtitle("");

            textView_rotulo1.setText("Código:");
            textView_rotulo1.setVisibility(View.VISIBLE);
            textView_valor1.setVisibility(View.VISIBLE);
            ImageView_1.setVisibility(View.VISIBLE);

            textView_rotulo2.setText("Lote:");
            textView_rotulo2.setVisibility(View.VISIBLE);
            textView_valor2.setVisibility(View.VISIBLE);
            ImageView_2.setVisibility(View.VISIBLE);

            textView_rotulo3.setText("Armazém:");
            textView_rotulo3.setVisibility(View.VISIBLE);
            textView_valor3.setVisibility(View.VISIBLE);
            ImageView_3.setVisibility(View.VISIBLE);


            editText_leitura.setHint("Ler código artigo");

        }

        if (tipoConsultaStock.equals(TIPO_CONSULTA_INFOOF)){

            editText_leitura.setHint("Ler OF");

            getSupportActionBar().setTitle("Consulta estado OF");
            getSupportActionBar().setSubtitle("");

            myClBotoes.setVisibility(View.GONE);
            myCLFiltros.setVisibility(View.GONE);

        }

        button_consultar = (Button) findViewById(R.id.button_consultar);





        // editText_leitura.addTextChangedListener(new MyTextWatcher(editText_leitura));
        // editText_leitura.setInputType(InputType.TYPE_NULL);

        defineEditTextReadBarCodeListener();
        ediTextLeituraBarCodeReader = new EditTextBarCodeReader(editText_leitura,thisActivity);
        ediTextLeituraBarCodeReader.setOnGetScannedTextListener(editTextLeituraBarCodeListener);
        editText_leitura.setOnClickListener(this);

        button_consultar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if (!myArrayListConsultaStock.isEmpty()) {

                    myArrayListConsultaStock.clear();
                    myAdapterConsultaStock.notifyDataSetChanged();
                }

                executaConsultaStock();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
            }
        });


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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (!myArrayListConsultaStock.isEmpty()) {

            myArrayListConsultaStock.clear();
            myAdapterConsultaStock.notifyDataSetChanged();

        }

    }

    @Override
    public void onClick(View v) {


        System.out.println("On Click");

        if (v.getId() == R.id.imageView_1){
            switch (tipoConsultaStock){
                case TIPO_CONSULTA_REFERENCIA:
                    pedeInput("Ler código","",v);
            }
        }
        if (v.getId() == R.id.imageView_2){
            switch (tipoConsultaStock){
                case TIPO_CONSULTA_REFERENCIA:
                    pedeInput("Ler lote","",v);
            }
        }
        if (v.getId() == R.id.imageView_3){
            switch (tipoConsultaStock){
                case TIPO_CONSULTA_REFERENCIA:
                    pedeInput("Ler armazém","",v);
            }
        }
        if (v.getId() == R.id.imageView_4){

        }

        if (v.getId() == R.id.textView_valor_1){
            if (tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA) && ! textView_valor1.getText().toString().isEmpty()) {
                textView_valor1.setText("");
                defineEditTextHint(true);
            }
        }

        if (v.getId() == R.id.textView_valor_2){
            if (tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA) && ! textView_valor2.getText().toString().isEmpty()) {
                textView_valor2.setText("");
                defineEditTextHint(true);
            }
        }

        if (v.getId() == R.id.textView_valor_3){
            if (tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA) && ! textView_valor3.getText().toString().isEmpty()) {
                textView_valor3.setText("");
                defineEditTextHint(false);
            }

        }

    }

    private void carregaInfoBundle() {
        // Carrega a informação do bundle nas variáveis a usar neste ecrã

        tipoConsultaStock = bundle.getString("tipoConsultaStock");

        System.out.println(tipoConsultaStock);
        System.out.println("Fim carregaInfoBundle");

    }

    private boolean validaLeituraLocalizacao(String textoLido) {

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

                zona_lida = "";
                alveolo_lido     ="";

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


                // vamos aqui criar um procedimento para validar a localização
                // para já suspenso

                return true;

            default: {
                return false;
            }

        }

    }
    public static String getTipoConsultaStock(){
        return tipoConsultaStock;
    }

    private void executaConsultaStock(){

        String ref_param, lote_param,zona_param,alveolo_param,filtro_param,armazem_param;
        Boolean porlote,poralveolo;

        // vai buscar os dados do ecrã e carrega nas variaveis
        carregaVariaveisConsulta();

        if (armazem_lido.equals("")) armazem_param = "0";
        else armazem_param = armazem_lido;

        ref_param = ref_lida;
        lote_param = lote_lido;

        alveolo_param = alveolo_lido;
        zona_param = zona_lida;

        porlote = true;
        poralveolo = true;

        System.out.println("TipoConsultaStock "+tipoConsultaStock);

        if(tipoConsultaStock.equals("localizacao")){

            ref_param = "%";
            lote_param = "%";

        }

        if(tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA)){

            if (lote_lido.equals("")){
                lote_param = "%";
            }

            zona_param = "%";
            alveolo_param = "%";

        }


        // caso a lista esteja preenchida vamos limpar (para depois carregar novamente
        if (!myArrayListConsultaStock.isEmpty()) {

            myArrayListConsultaStock.clear();
            myAdapterConsultaStock.notifyDataSetChanged();
        }


        // vamos executar a consulta de stock

        RequestQueue queue = Volley.newRequestQueue(this);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ListarInventario?").buildUpon()
                .appendQueryParameter("referencia", ref_param)
                .appendQueryParameter("lote", lote_param)
                .appendQueryParameter("armazem", armazem_param)
                .appendQueryParameter("zona", zona_param)
                .appendQueryParameter("alveolo",alveolo_param)
                .appendQueryParameter("porlote",porlote.toString())
                .appendQueryParameter("poralveolo",poralveolo.toString())
                .build();

        progDailog.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progDailog.dismiss();

                        Gson gson = new Gson();
                        myArrayListConsultaStock = gson.fromJson(response,new TypeToken<ArrayList<DataModelConsultaStock>>() {}.getType());

                        if (myArrayListConsultaStock.size() == 0) {
                            Dialogos.dialogoInfo("", "Não existem stocks nas condições definidas", 3.0, ConsultaStocksActivity.this, false);
                            return;
                        }

                        Bundle pBundle = new Bundle();
                        myAdapterConsultaStock = new ArrayAdapterConsultaStock(thisActivity, 0, myArrayListConsultaStock,pBundle);
                        listView.setAdapter(myAdapterConsultaStock);
                        myAdapterConsultaStock.notifyDataSetChanged();



                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),3,thisActivity,true);
            }
        });


        System.out.println(uri.toString());

        Integer mTimeoutMs = 10000;

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(mTimeoutMs,
                1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private void pedeInput(String titulo, String mensagem, final View v) {

        // ao chamar a edição, vamos limpar a lista

        if (!myArrayListConsultaStock.isEmpty()) {
            myArrayListConsultaStock.clear();
            myAdapterConsultaStock.notifyDataSetChanged();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsultaStocksActivity.this);

        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);

        input = new EditText(ConsultaStocksActivity.this);

        switch (v.getId()){
            case R.id.imageView_1 :
                input.setText(textView_valor1.getText().toString());
                break;
            case R.id.imageView_2 :
                input.setText(textView_valor2.getText().toString());
                break;
            case R.id.imageView_3 :
                input.setText(textView_valor3.getText().toString());

                // se o tipo de consulta for referencia então estamos a ler o armazém

                if(tipoConsultaStock.equals("referencia")) {
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
            case R.id.imageView_4 :
                input.setText(textView_valor4.getText().toString());
                break;
        }
        input.setSelectAllOnFocus(true);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Inserir",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (v.getId()){
                            case R.id.imageView_1 :
                                textView_valor1.setText(input.getText().toString());
                                break;
                            case R.id.imageView_2 :
                                textView_valor2.setText(input.getText().toString());
                                break;
                            case R.id.imageView_3 :
                                textView_valor3.setText(input.getText().toString());
                                break;
                            case R.id.imageView_4 :
                                textView_valor4.setText(input.getText().toString());
                                break;
                        }
                        input.setText(clear);
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
    private boolean validaRef(String referencia){

        System.out.println("Valida Ref :"+referencia);

        JSONArray myStArray = WsExecute.lerST(referencia,ConsultaStocksActivity.this);

        System.out.println("JsonArray LerSt: "+myStArray);

        // array null erro na consulta da referencia
        if (myStArray == null){
            return false;
        }

        if (myStArray.length() == 0) {
            Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + referencia, 10,
                    ConsultaStocksActivity.this, false);
            return false;
        }

        return true;
    }

    private void carregaVariaveisConsulta(){

        if (tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA)){
            ref_lida = textView_valor1.getText().toString();
            lote_lido = textView_valor2.getText().toString();
            armazem_lido = textView_valor3.getText().toString();
        }

    }

    private void consultaEstadoOF(final String of) {

        String oflida = of;

        DataModelLeituraCB leituraCB = new DataModelLeituraCB(of);

        if (leituraCB.getTipocb().equals("R")) {
            // se o tipo de leitura for R então assumimos que lemos um codigo de barras com ref+lote+qtt
            oflida = leituraCB.getLote();
        }

        LerInfoOf2Ws mLerInfoOf2Ws = new LerInfoOf2Ws(this);

        mLerInfoOf2Ws.setOnLerInfoOf2Listener(new LerInfoOf2Ws.OnLerInfoOf2Listener() {
            @Override
            public void onSuccess(InfoOfDataModel mInfoOf) {

                if (mInfoOf == null) {

                    Dialogos.dialogoErro("Não encontrei no sistema a OF: "+of,"",5,thisActivity,false);

                    if(! myArrayListTexto.isEmpty()) {
                        myArrayListTexto.clear();
                        myAdapterDadosTexto.notifyDataSetChanged();
                    }

                    return;

                }

                myArrayListTexto.add(new DataModelDadosTexto("OF",mInfoOf.getNumof().trim()));
                myArrayListTexto.add(new DataModelDadosTexto("Código Peça",mInfoOf.getRef().trim()));
                if (mInfoOf.getFinalizada() || mInfoOf.getFechada() )
                    myArrayListTexto.add(new DataModelDadosTexto("Estado","Finalizada"));

                else {
                    if (mInfoOf.getIniciada())
                        myArrayListTexto.add(new DataModelDadosTexto("Estado","Em produção"));
                    else
                        myArrayListTexto.add(new DataModelDadosTexto("Estado","Por iniciar"));
                }

                DecimalFormat formatter = new DecimalFormat("#,###,###");

                // myArrayListTexto.add(new DataModelDadosTexto("Qtd Prevista",String.valueOf( UserFunc.retiraZerosDireita(mInfoOf.getQttprevista()) )) );
                // myArrayListTexto.add(new DataModelDadosTexto("Qtd Produzida",String.valueOf( UserFunc.retiraZerosDireita(mInfoOf.getQttprod()) )) );
                // myArrayListTexto.add(new DataModelDadosTexto("Qtd falta",String.valueOf(UserFunc.retiraZerosDireita(mInfoOf.getQttprevista()-mInfoOf.getQttprod()))) );

                myArrayListTexto.add(new DataModelDadosTexto("Qtd Prevista", formatter.format(mInfoOf.getQttprevista()).replace(","," ")));
                myArrayListTexto.add(new DataModelDadosTexto("Qtd Produzida",formatter.format(mInfoOf.getQttprod()).replace(","," ") ));
                myArrayListTexto.add(new DataModelDadosTexto("Qtd falta",formatter.format(mInfoOf.getQttprevista()-mInfoOf.getQttprod()).replace(","," ")));
                myArrayListTexto.add(new DataModelDadosTexto("Primeira entrada",mInfoOf.getPrimeira_entrada()));
                myArrayListTexto.add(new DataModelDadosTexto("Ultima entrada",mInfoOf.getUltima_entrada()));
                myArrayListTexto.add(new DataModelDadosTexto("Ultima caixa", String.valueOf(mInfoOf.getUltima_cx()) ));
                myArrayListTexto.add(new DataModelDadosTexto("Maquina",mInfoOf.getAlveolo()));

                if (! mInfoOf.getFechada())
                    myArrayListTexto.add(new DataModelDadosTexto("Tempo em dias", String.valueOf(mInfoOf.getDiasemprod()) ));

                myArrayListTexto.add(new DataModelDadosTexto("Peças por caixa", formatter.format(mInfoOf.getQttcx()).replace(","," ")));

                Bundle pBundle = new Bundle();

                myAdapterDadosTexto = new ArrayAdapterDadosTexto(thisActivity, 0, myArrayListTexto,pBundle);
                listView.setAdapter(myAdapterDadosTexto);
                myAdapterDadosTexto.notifyDataSetChanged();

                carregaMpsEmConsumoOf(of);

            }
        });

        mLerInfoOf2Ws.execute(of);


    }

    private void defineEditTextHint(boolean mostraDialogoInfo) {

        if (!myArrayListConsultaStock.isEmpty()) {

            myArrayListConsultaStock.clear();
            myAdapterConsultaStock.notifyDataSetChanged();
        }

        requestFocus(editText_leitura);

        if (tipoConsultaStock.equals(TIPO_CONSULTA_REFERENCIA)) {

            if(textView_valor1.getText().toString().isEmpty()) {
                textView_valor2.setText("");
                editText_leitura.setHint("Ler código");
                FLAG_TIPO_LEITURA = "CODIGO";
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Leitura", "Ler código", 1.0, ConsultaStocksActivity.this, false);
                return;
            }

            if(textView_valor2.getText().toString().isEmpty()) {
                editText_leitura.setHint("Ler lote");
                FLAG_TIPO_LEITURA = "LOTE";
                if (mostraDialogoInfo) Dialogos.dialogoInfo("Leitura", "Ler lote", 1.0, ConsultaStocksActivity.this, false);
                return;
            }

        }

        editText_leitura.setHint("Ler código");
        FLAG_TIPO_LEITURA = "CODIGO";

    }

    private void carregaMpsEmConsumoOf(String pOf) {

        RequestQueue queue = Volley.newRequestQueue(this);

        Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ObterMpsEmConsumoOf?").buildUpon()
                .appendQueryParameter("of", pOf)
                .build();

        progDailog.show();

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                        Gson gson = new Gson();

                        ArrayList<DataModelMpsConsumoOf> listaMpsConsumoOf;

                        listaMpsConsumoOf = gson.fromJson(response,new TypeToken<ArrayList<DataModelMpsConsumoOf>>() {}.getType());


                        if (listaMpsConsumoOf.size() == 0) {
                            progDailog.dismiss();
                            return;

                        }
                        else
                        {

                            myArrayListTexto.add(new DataModelDadosTexto("      MPs em consumo:",""));

                            for (int i = 0; i < listaMpsConsumoOf.size(); i++) {
                                myArrayListTexto.add(new DataModelDadosTexto(listaMpsConsumoOf.get(i).getRef(),listaMpsConsumoOf.get(i).getLocalizacao()));
                            }

                        }

                        Bundle pBundle = new Bundle();

                        myAdapterDadosTexto = new ArrayAdapterDadosTexto(thisActivity, 0, myArrayListTexto,pBundle);
                        listView.setAdapter(myAdapterDadosTexto);
                        myAdapterDadosTexto.notifyDataSetChanged();

                        progDailog.dismiss();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDailog.dismiss();
                Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),3,thisActivity,true);
            }
        });


        System.out.println(uri.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void defineEditTextReadBarCodeListener() {


        editTextLeituraBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(final String scannedText, EditText editText) {

                editText_leitura.setText(clear);

                if (scannedText.equals("")) return;

                switch  (tipoConsultaStock) {
                    case TIPO_CONSULTA_LOCALIZACAO:

                        if (validaLeituraLocalizacao(scannedText)){
                            textView_valor1.setText("[" + armazem_lido + "] [" + zona_lida + "] [" + alveolo_lido + "]");
                            executaConsultaStock();
                        }
                        break;

                    case TIPO_CONSULTA_REFERENCIA:

                        if (FLAG_TIPO_LEITURA.equals("CODIGO")) {

                            textView_valor2.setText("");

                            if(validaLeituraCodigo(scannedText).equals("OK")){
                                textView_valor1.setText(ref_lida);
                                textView_valor2.setText(lote_lido);
                            }
                            else
                            {

                                // vamos ler a referencia e verificar se ela existe

                                RequestQueue queue = Volley.newRequestQueue(thisActivity);

                                Uri uri = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerST?").buildUpon()
                                        .appendQueryParameter("referencia", scannedText.trim())
                                        .appendQueryParameter("filtro", "")
                                        .build();

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                progDailog.dismiss();

                                                textView_valor1.setText("");

                                                String mResultado;
                                                mResultado = response;

                                                JSONconverter jsonConverter = new JSONconverter();
                                                JSONArray myJsonArray;

                                                Boolean hintShowDialog;
                                                hintShowDialog = true;

                                                try {

                                                    mResultado = jsonConverter.ConvertJSON(mResultado);
                                                    myJsonArray = new JSONArray(mResultado);

                                                    if (myJsonArray.equals(R.string.ws_response_empty) || myJsonArray.length() == 0) {

                                                        Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + scannedText, 10,
                                                                ConsultaStocksActivity.this, false);
                                                        hintShowDialog = false;
                                                    }
                                                    else
                                                        textView_valor1.setText(scannedText);

                                                }
                                                catch (Exception e) {
                                                    Dialogos.dialogoErro("Erro na leitura da referência", e.getMessage(),60,thisActivity,false);
                                                }

                                                defineEditTextHint(hintShowDialog);

                                            }

                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        progDailog.dismiss();
                                        Dialogos.dialogoErro("Erro no processamento do pedido",error.getMessage(),4,thisActivity,false);
                                    }
                                });

                                progDailog.show();

                                System.out.println(uri.toString());

                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(Globals.getInstance().getmVolleyTimeOut(),
                                        0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                stringRequest.setShouldCache(false);
                                queue.getCache().clear();

                                queue.add(stringRequest);

                            }

                        }

                        if (FLAG_TIPO_LEITURA.equals("LOTE")) {
                            textView_valor2.setText(scannedText);
                            defineEditTextHint(true);
                        }



                        break;

                    case TIPO_CONSULTA_INFOOF:

                        if(! myArrayListTexto.isEmpty()) {

                            myArrayListTexto.clear();
                            myAdapterDadosTexto.notifyDataSetChanged();
                        }


                        consultaEstadoOF(scannedText);

                        break;

                }



            }
        };

    }

}


