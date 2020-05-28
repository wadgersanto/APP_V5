package gnotus.inoveplastika;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gnotus.inoveplastika.DataModels.DataModelCarga;


public class InvlocActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete, View.OnClickListener {

    private static final String clear = "";
    private static final String vazio = "\"[]\"";

    private static final String QUERY_PARAM_REF = "ref";
    private static final String QUERY_PARAM_LOTE = "lote";
    private static final String QUERY_PARAM_BOSTAMP = "bostamp";

    private static final String BOSTAMP_CARGA = "bostampcarga";
    private static final String MYFILE = "configs";


    AlertDialog dialog;
    Bundle bundle = new Bundle();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView textView_operador, textView_localizacao;
    private EditText editText_lercodigo, input;
    private Button button_terminar;

    private SharedPreferences sharedpreferences;


    private ListView listView;
    private ArrayList<DataModelCarga> lista = new ArrayList<>();

    private DataModelArrayAdapterInvloc adapter;

    private String armazem = "", zona = "", alveolo = "";

    private String bostampInvloc = "";

    private String ref_lida, lote_lido = "";
    private double qtd_lida = 0;
    private boolean FLAG_MODO = false;
    private String FLAG_MODO_EDITTEXT = "OPERADOR";


    private int armazem_ori = 0,armazem_dest = 0;
    private String zona_generica = "G";
    private String alveolo_generico = "G";
    private String mtxterro ="";

    private EditTextBarCodeReader.OnGetScannedTextListener editTextReadBarCodeListener;
    private EditTextBarCodeReader editTextBarCodeReaderLerCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invloc);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inventário inicial");
        getSupportActionBar().setSubtitle("");

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(MYFILE, Context.MODE_PRIVATE);

        //initNavigationDrawer();

        editText_lercodigo = (EditText) findViewById(R.id.editText_carga_codigo);
        textView_localizacao = (TextView) findViewById(R.id.textView_carga_localizacao);
        textView_operador = (TextView) findViewById(R.id.textView_carga_operador);
        listView = (ListView) findViewById(R.id.listView_carga_artigos);
        button_terminar = (Button) findViewById(R.id.button_invloc_terminar);

        // editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));


        defineEditTextReadBarCodeListener();
        editTextBarCodeReaderLerCodigo = new EditTextBarCodeReader(editText_lercodigo,this);
        editTextBarCodeReaderLerCodigo.setOnGetScannedTextListener(editTextReadBarCodeListener);
        editText_lercodigo.setOnClickListener(this);


        defineEditTextHint(true);

        button_terminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if (!lista.isEmpty()) {

                    lista.clear();
                    adapter.notifyDataSetChanged();
                }

                textView_operador.setText(clear);
                defineEditTextHint(true);
                bostampInvloc = "";

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {


                if (!FLAG_MODO) {


                    // vamos ler a informação do factor de conversao

                    String tmpLote = adapter.getItem(position).getLote();
                    String tmpRef = adapter.getItem(position).getReferencia();
                    Double j_fconversao = 1.00;

                    System.out.println("Linha Lote: "+tmpLote);

                    JSONArray jsonArray = new JSONArray();

                    if (!TextUtils.isEmpty(tmpLote)){
                        jsonArray = WsExecute.lerSE(tmpRef,tmpLote,"",InvlocActivity.this);
                    } else {
                        jsonArray = WsExecute.lerST(tmpRef,InvlocActivity.this);
                    }

                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject c = jsonArray.getJSONObject(i);
                            j_fconversao = c.getDouble("fconversao");
                        }
                    } catch (final JSONException e) {

                        Log.e("", "Erro Conversão: " + e.getMessage());

                    }

                    System.out.println("Fconversao "+j_fconversao);

                    bundle.clear();

                    bundle.putBoolean("valida_stock",false);

                    bundle.putString("trf_mode","carga");
                    bundle.putBoolean("editing_listview",true);

                    bundle.putString("ref", adapter.getItem(position).getReferencia());
                    bundle.putString("design", adapter.getItem(position).getDescricao());
                    bundle.putString("lote", adapter.getItem(position).getLote());
                    bundle.putString("armazem_ori", adapter.getItem(position).getArmazem());
                    bundle.putString("zona_ori", adapter.getItem(position).getZona1());
                    bundle.putString("alveolo_ori", adapter.getItem(position).getAlveolo1());

                    bundle.putString("armazem_dest", adapter.getItem(position).getAr2mazem());
                    bundle.putString("zona_dest", adapter.getItem(position).getZona2());
                    bundle.putString("alveolo_dest", adapter.getItem(position).getAlveolo2());


                    bundle.putDouble("qtd_lida", adapter.getItem(position).getQt());
                    bundle.putDouble("qtdAlt_lida", adapter.getItem(position).getQtAlt());

                    bundle.putDouble("stock", adapter.getItem(position).getQt());
                    bundle.putDouble("stockAlt", adapter.getItem(position).getQtAlt());

                    bundle.putString("unidade", adapter.getItem(position).getUnidade());
                    bundle.putString("unidadeAlt", adapter.getItem(position).getUnidadeAlt());
                    bundle.putDouble("fconversao", j_fconversao);

                    bundle.putString("bistamp_carga", adapter.getItem(position).getBistamp());
                    bundle.putString("atividade", "invloc");

                    System.out.println("Bundle - Qtd lida :"+adapter.getItem(position).getQt());
                    System.out.println("Bundle - Qtd Alt lida:"+adapter.getItem(position).getQtAlt());

                    Intent EditIntent = new Intent(InvlocActivity.this, EditActivity.class);
                    EditIntent.putExtras(bundle);
                    startActivity(EditIntent);
                }
            }
        });


    }


    public void requestFocus(View view) {

    }


    public void onBackPressed() {

        if (lista.isEmpty() && ! bostampInvloc.equals(""))
        {

            AsyncRequest processaPedido = new AsyncRequest(InvlocActivity.this, 0);

            Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                    .appendQueryParameter("bostamp",String.valueOf (bostampInvloc))
                    .appendQueryParameter("tabela","BO")
                    .appendQueryParameter("onlyifnolines",String.valueOf(false))
                    .build();

            processaPedido.setOnAsyncRequestComplete(new AsyncRequest.OnAsyncRequestComplete() {
                @Override
                public void asyncResponse(String response, int op) {
                    finish();
                }
            });

            processaPedido.execute(builtURI_processaPedido.toString());

            // WsExecute.aSyncEliminaDossier(bostampInvloc,"BO",false,InvlocActivity.this  );
        }
        else  finish();


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

        if (!lista.isEmpty()) {

            lista.clear();
            adapter.notifyDataSetChanged();
        }


        if (bostampInvloc.equals("")) return;

        AsyncRequest lerInformacao = new AsyncRequest(InvlocActivity.this, 3);


        Uri builtURI_lerInformacao = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerInformacao?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_BOSTAMP, bostampInvloc)
                .appendQueryParameter("atividade","invloc")
                .build();

        lerInformacao.execute(builtURI_lerInformacao.toString());

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

    @Override
    public void asyncResponse(String response, int op) {

        ImageView image = new ImageView(InvlocActivity.this);
        image.setImageResource(R.mipmap.ic_notok);

        if (response == null) {
            dialogoErro("Erro", "Não foi possivel obter informação da base de dados");
            editText_lercodigo.setText(clear);
            return;
        }

        // processa a linha de carga

        if (op == 2) {

            // Linha processada com sucesso
            if (response.equals("0")) {

                // Actualiza a informação das linhas de carga
                AsyncRequest lerInformacao = new AsyncRequest(InvlocActivity.this, 3);

                Uri builtURI_lerInformacao = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerInformacao?").buildUpon()
                        .appendQueryParameter(QUERY_PARAM_BOSTAMP,bostampInvloc)
                        .build();

                lerInformacao.execute(builtURI_lerInformacao.toString());
                // Limpa a localização e obriga a que o operador faça a leitura da localização
                FLAG_MODO_EDITTEXT = "CODIGO";
                ref_lida = "";
                lote_lido = "";
                qtd_lida = 0;

            } else {
                dialogoErro("Erro", response);
            }

        }

        // Lê os dados do dossier em função do bostamp em memoria
        if (op == 3) {

            if (!lista.isEmpty()) {

                lista.clear();
                adapter.notifyDataSetChanged();
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // se a lista da carga estiver vazia entao reinicia a operação e coloca em modo de carga

            if (response.equals(vazio)) {

                if (FLAG_MODO) {
                    FLAG_MODO_EDITTEXT = "OPERADOR";
                    FLAG_MODO = false;

                    Globals.getInstance().setTrfCargaBostamp("");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(BOSTAMP_CARGA, "");
                    editor.apply();

                }

            } else {

                try {

                    JSONconverter jsonConverter = new JSONconverter();
                    response = jsonConverter.ConvertJSON(response);

                    JSONArray values = new JSONArray(response);


                    for (int i = 0; i < values.length(); i++) {

                        JSONObject c = values.getJSONObject(i);

                        String j_bistamp = c.getString("bistamp");
                        String j_ref = c.getString("ref");
                        String j_lote = c.getString("lote");
                        String j_descricao = c.getString("design");
                        String j_qt = c.getString("qtt");
                        String j_qtAlt = c.getString("uni2qtt");
                        String j_unidade = c.getString("unidade");
                        String j_unidadeAlt = c.getString("unidad2");
                        String j_armazem = c.getString("armazem");
                        String j_ar2mazem = c.getString("ar2mazem");
                        String j_zona1 = c.getString("zona1");
                        String j_alveolo1 = c.getString("alveolo1");
                        String j_zona2 = c.getString("zona2");
                        String j_alveolo2 = c.getString("alveolo2");

                        lista.add(new DataModelCarga(j_bistamp, j_ref, j_lote, j_descricao, Double.parseDouble(j_qt), Double.parseDouble(j_qtAlt), j_unidade, j_unidadeAlt,
                                j_armazem,j_ar2mazem,j_zona1,j_alveolo1,j_zona2,j_alveolo2));

                    }


                    adapter = new DataModelArrayAdapterInvloc(this, 0, lista);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (final JSONException e) {

                    Log.e("", "Erro Conversão: " + e.getMessage());

                }

            }
            System.out.println("Fim da leitura operação 3");

            defineEditTextHint(true);
        }


        // Pedido foi a validação da ref
        if (op == 6) {
            System.out.println(response);

            // SE A RESPOSTA É 2 ENTÃO EXISTE A REF E USA LOTE
            if (Integer.parseInt(response) == 2) {
                System.out.println("A resposta é 2");
                // ref_lida = editText_lercodigo.getText().toString(); desativado 2019-11-30
                ref_lida = editTextBarCodeReaderLerCodigo.getScannedText();
                FLAG_MODO_EDITTEXT = "LOTE";
                defineEditTextHint(true);
            }
            // SE A RESPOSTA É 1 ENTÃO EXITE A REF MAS NÃO USA LOTE
            if (Integer.parseInt(response) == 1) {
                // ref_lida = editText_lercodigo.getText().toString(); // desativado 2019-11-30
                ref_lida = editTextBarCodeReaderLerCodigo.getScannedText();

                FLAG_MODO_EDITTEXT = "QUANTIDADE";
                defineEditTextHint(true);
            }

            // SE A RESPOSTA É "0" ENTÃO A REFERENCIA NÃO EXISTE
            if (Integer.parseInt(response) == 0) {
                dialogoErro("Erro", "O artigo" + editText_lercodigo.getText().toString() + "não existe");
                FLAG_MODO_EDITTEXT = "CODIGO";
                defineEditTextHint(false);
                return;
            }
        }
        // Pedido de validação de lote
        if (op == 7) {
            // System.out.println(response);

            if (response.equals("true")) {
                //lote_lido = editText_lercodigo.getText().toString();
                FLAG_MODO_EDITTEXT = "QUANTIDADE";
                defineEditTextHint(true);
            } else {
                dialogoErro("Erro", "O lote " + lote_lido + " não existe");
                defineEditTextHint(false);
                lote_lido = "";
            }
            System.out.println("DefineTextHint - Validacao lote");

        }

        // Pedido para ler info stock e processar a transferencia

        if (op == 8) {

        }


    }

    private void dialogoErro(String title, String subtitle) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(InvlocActivity.this);
        builder1.setTitle(title);
        builder1.setMessage(subtitle);
        builder1.setCancelable(false);

        builder1.setView(image);

        final AlertDialog alertErro = builder1.create();
        alertErro.show();

        new CountDownTimer(2500, 500) {

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


        AlertDialog.Builder builderInfo = new AlertDialog.Builder(InvlocActivity.this);
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

        private class MyTextWatcher implements TextWatcher {

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

           /* System.out.println("OnTextChanged");
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

                            // se o stamp estiver vazio vamos criar o cabeçalho

                            if (bostampInvloc.equals("")){
                                if (!criaCabecalhoInventario()) return;
                            }
                            else {
                                // vamos verificar se o stamp existe
                                String mExInvStamp;
                                mExInvStamp = WsExecute.existeBostamp(bostampInvloc, InvlocActivity.this);

                                if (mExInvStamp.equals("false")){
                                    if (!criaCabecalhoInventario()) return;
                                }

                                System.out.println(mExInvStamp);
                            }

                            // cria o dossier de carga se flag_modo false
                            FLAG_MODO_EDITTEXT = "LOCALIZACAO";


                        }

                        System.out.println("DefineTextHint - quando modo = operador");
                        defineEditTextHint(true);
                        return;
                    }
                    // se a flag for a localização
                    if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {

                        if (!validaLeituraLocalizacao(editText_lercodigo.getText().toString())) {
                            Dialogos.dialogoErro("A localização lida não é válida",editText_lercodigo.getText().toString(),5,InvlocActivity.this,false);
                            editText_lercodigo.setText(clear);
                        } else {

                            // vamos primeiro verificar se a referencia  já processou lote
                            String jaFezInvLoc = WsExecute.refJaFezInvLoc("","",Integer.parseInt(armazem),zona,alveolo,bostampInvloc,InvlocActivity.this);

                            if (jaFezInvLoc.equals("true")){

                                mtxterro = "Esta localização já tem movimentos!!! Não pode fazer inventario inicial";

                                Dialogos.dialogoInfo("Informação",mtxterro,4.0,InvlocActivity.this, false);

                                defineEditTextHint(false);
                                return;
                            }

                            FLAG_MODO_EDITTEXT = "CODIGO";
                            System.out.println("DefineTextHint - quando modo = operador");
                            defineEditTextHint(true);
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

                        if (!FLAG_MODO) processaLinhaCarga();

                        return;
                    }

                    // Se a flag de leitura for o código então pode ler o código ou a localização

                    if (FLAG_MODO_EDITTEXT.equals("CODIGO"))
                        System.out.println("OnTextChange: FLAG = CODIGO");
                    // se der uma validação de localização então carrega a localização e mantem a flag no código
                    if (validaLeituraLocalizacao(editText_lercodigo.getText().toString())) {
                        System.out.println("defineTextHint - Leitura localização OK");
                        defineEditTextHint(true);
                        return;
                    } else {

                        *//* Se estiver em modo "CODIGO" primeiro vamos validar se o que foi lido foi uma string começada por R, ou seja,
                        um código de barras que contem a referencia+lote+quantidade*//*

                        String retval = validaLeituraCodigo(editText_lercodigo.getText().toString());


                        if (retval.equals("OK")) {
                            System.out.println("OnTextChange: FLAG = CODIGO: CODIGO OK");
                            if (!FLAG_MODO) processaLinhaCarga();
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

            }*/
        }


        public void afterTextChanged(Editable editable) {

        }
    }

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


        System.out.println("Execucao defineEditTextHint");
        editText_lercodigo.setText(clear);

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
                editText_lercodigo.setHint("Ler localização de Contagem");
                if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de Contagem");
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
                        editText_lercodigo.setHint("Ler localização de Contagem");
                        if (mostraDialogoInfo) dialogoInfo("Acção", "Ler localização de Contagem");
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

        // A localização tem de começar por ALV


        if (textoLido.length()<5){
                return false;
        }

        String prefixo = textoLido.substring(0, 5);

        if (! prefixo.equals("(ALV)")) {
            return false;
        }

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

                textView_localizacao.setText( "["+armazem_lido + "] [" + zona_lida + "] [" + alveolo_lido+"]");
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

        if ( textoLido.indexOf("(R)")== 1 ) {
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

        //Valida se o lote lido existe na base de dados

        lote_lido = textoLido;

        AsyncRequest validaLote = new AsyncRequest(InvlocActivity.this, 7);

        System.out.println(ref_lida + " lote:" + lote_lido);

        Uri builtURI_validaLote = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/verificaLote?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_REF, ref_lida)
                .appendQueryParameter(QUERY_PARAM_LOTE, lote_lido)
                .build();

        validaLote.execute(builtURI_validaLote.toString());
        //AsyncTask<String,Integer,String> at =  validaLote.execute(builtURI_validaLote.toString());

    }

    private void validaLeituraRef(String textoLido) {

        //Valida se a ref existe na base de dados

        AsyncRequest validaRef = new AsyncRequest(InvlocActivity.this, 6);

        Uri builtURI_validaRef = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/verificaRef?").buildUpon()
                .appendQueryParameter(QUERY_PARAM_REF, textoLido)
                // .appendQueryParameter(QUERY_PARAM_LOTE, textoLido)
                .build();

        validaRef.execute(builtURI_validaRef.toString());
        //AsyncTask<String,Integer,String> at =  validaLote.execute(builtURI_validaLote.toString());

    }

    private void processaLinhaCarga() {


        if (bostampInvloc.isEmpty()) {
            dialogoErro("Não foi possivel processar o pedido", "O stamp da carga está vazio");
            defineEditTextHint(false);
            return;
        }

        // Este pedido vai verificar se existe
        // ValidaLeituraInvLoc
        // Este pedido vai verificar se esta referência
        // Este pedido vai ler o stock disponivel na localização em questão e abrir a actividade para seleccionar a quantidade e processar a carga

        Double qttContada;
        qttContada =0.0;

        for (int n = 0; n < lista.size(); n++) {

            System.out.println("Ref Linha :"+lista.get(n).getReferencia().trim()+" ref:"+ref_lida);
            System.out.println("Lote Linha :"+lista.get(n).getLote()+" lote:"+lote_lido);
            System.out.println("Armazem Linha :"+lista.get(n).getArmazem()+" arm:"+armazem);
            System.out.println("Zona Linha :"+lista.get(n).getZona2()+" zona:"+zona);
            System.out.println("Alv Linha :"+lista.get(n).getAlveolo2()+" alveolo:"+alveolo);


            System.out.println("Qtd Linha :"+lista.get(n).getQt());

            System.out.println("ref igual :"+lista.get(n).getReferencia().trim().equals(ref_lida.trim()));
            System.out.println("lote igual :"+lista.get(n).getLote().trim().equals(lote_lido.trim()));
            System.out.println("armazem igual :"+String.valueOf(lista.get(n).getArmazem()).equals(armazem));
            System.out.println("Zona igual :"+lista.get(n).getZona2().trim().equals(zona.trim()));
            System.out.println("alveolo igual :"+lista.get(n).getAlveolo2().trim().equals(alveolo.trim()));



            if (lista.get(n).getReferencia().trim().equals(ref_lida.trim()) &&
                    lista.get(n).getLote().trim().equals(lote_lido.trim()) &&
                    String.valueOf(lista.get(n).getArmazem()).equals(armazem)&&
                    lista.get(n).getZona2().trim().equals(zona.trim()) &&
                    lista.get(n).getAlveolo2().trim().equals(alveolo.trim()))
            {
                System.out.println("Somei");
                qttContada = lista.get(n).getQt();
            }
        }

        System.out.println("qttContada:"+qttContada);


        JSONArray mRespJSONArray = WsExecute.lerStock (ref_lida,lote_lido, Integer.parseInt(armazem),zona,alveolo,InvlocActivity.this);


        if (mRespJSONArray == null) return;

        // Se a resposta for vazia então não existe a combinacao ref+lote no sistema
        // se a resposta for vazia então quer dizer que não é permitida a leitura
        if (mRespJSONArray.equals(vazio)) {
            dialogoErro("Erro", "Nao existe no sistema a referencia/lote seleccionados");
            FLAG_MODO_EDITTEXT = "CODIGO";
            defineEditTextHint(false);
            return;
        }

        try {

            int i = 0;

            JSONObject c = mRespJSONArray.getJSONObject(i);

            String j_referencia = c.getString("ref");
            String j_lote = c.getString("lote");
            String j_design = c.getString("design");
            Double j_stock = c.getDouble("stock");
            String j_unidade = c.getString("unidade");
            Double j_fconversao = c.getDouble("fconversao");
            String j_unidadeAlt = c.getString("uni2");
            Double j_stockAlt = c.getDouble("stock2");


            bundle.clear();

            // se no caso do modo de carga estamos a inserir uma linha nova ou a alterar uma já carregada
            bundle.putBoolean("editing_listview",false);
            bundle.putString("trf_mode","carga");
            bundle.putString("bostamp_carga", bostampInvloc);
            bundle.putString("ref", j_referencia.trim());
            bundle.putString("lote", j_lote.trim());
            bundle.putString("design", j_design.trim());
            bundle.putDouble("stock", qttContada);
            bundle.putDouble("stockAlt", (qttContada / j_fconversao) * 1.000);
            bundle.putDouble("fconversao", j_fconversao);

            bundle.putDouble("qtd_lida", qttContada);
            bundle.putDouble("qtdAlt_lida", (qttContada / j_fconversao) * 1.000);
            bundle.putString("unidade", j_unidade);
            bundle.putString("unidadeAlt", j_unidadeAlt);
            bundle.putString("user", textView_operador.getText().toString());

            bundle.putString("armazem_ori", armazem);
            bundle.putString("zona_ori", zona_generica);
            bundle.putString("alveolo_ori", alveolo_generico);

            bundle.putString("armazem_dest", armazem);
            bundle.putString("zona_dest", zona);
            bundle.putString("alveolo_dest", alveolo);

            bundle.putBoolean("valida_stock", false);
            bundle.putString("atividade", "invloc");




            // Limpa localização
            //textView_localizacao.setText("");
            //System.out.println("defineTextHint - Processa linha de descarga");
            //defineEditTextHint(true);

            Intent EditIntent = new Intent(InvlocActivity.this, EditActivity.class);
            EditIntent.putExtras(bundle);
            startActivity(EditIntent);

        } catch (final JSONException e) {

            Log.e("", "Erro Conversão: " + e.getMessage());

        }

    }

    private boolean criaCabecalhoInventario() {

        Integer mUser = Integer.parseInt(textView_operador.getText().toString());

        String mRetval = WsExecute.criaBo(33,137,"0","",0,mUser,
                0,"","",InvlocActivity.this);

        if (mRetval.equals("erro")){

            dialogoErro("Erro", "Na criação do cabeçalho de carga");
            return false;
        }
        else {
            bostampInvloc = mRetval;
            if (textView_localizacao.getText().toString().isEmpty())
                FLAG_MODO_EDITTEXT = "LOCALIZACAO";
            else FLAG_MODO_EDITTEXT = "CODIGO";
            return true;
        }
    }

    private void defineEditTextReadBarCodeListener() {

        editTextReadBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {


                editText_lercodigo.setText("");

                System.out.println(FLAG_MODO_EDITTEXT);

                if (scannedText.equals("")) {
                    return;
                }


                // Se o flag estiver OPERADOR pede o operador
                if (FLAG_MODO_EDITTEXT.equals("OPERADOR")) {

                    if (validaLeituraOperador(scannedText)) {

                        // se o stamp estiver vazio vamos criar o cabeçalho

                        if (bostampInvloc.equals("")){
                            if (!criaCabecalhoInventario()) return;
                        }
                        else {
                            // vamos verificar se o stamp existe
                            String mExInvStamp;
                            mExInvStamp = WsExecute.existeBostamp(bostampInvloc, InvlocActivity.this);

                            if (mExInvStamp.equals("false")){
                                if (!criaCabecalhoInventario()) return;
                            }

                            System.out.println(mExInvStamp);
                        }

                        // cria o dossier de carga se flag_modo false
                        FLAG_MODO_EDITTEXT = "LOCALIZACAO";

                        defineEditTextHint(true);

                    }


                    /*System.out.println("DefineTextHint - quando modo = operador");
                    ;*/
                    return;

                }
                // se a flag for a localização
                if (FLAG_MODO_EDITTEXT.equals("LOCALIZACAO")) {

                    if (!validaLeituraLocalizacao(scannedText)) {
                        Dialogos.dialogoErro("A localização lida não é válida",scannedText,5,InvlocActivity.this,false);
                        // editText_lercodigo.setText(clear);
                    } else {

                        // vamos primeiro verificar se a referencia  já processou lote
                        String jaFezInvLoc = WsExecute.refJaFezInvLoc("","",Integer.parseInt(armazem),zona,alveolo,bostampInvloc,InvlocActivity.this);

                        if (jaFezInvLoc.equals("true")){

                            mtxterro = "A localização ["+armazem +"]["+zona+"]["+alveolo+"] já tem movimentos!!! Não pode fazer inventario inicial";

                            Dialogos.dialogoInfo("Informação",mtxterro,4.0,InvlocActivity.this, false);

                            defineEditTextHint(false);
                            return;
                        }

                        FLAG_MODO_EDITTEXT = "CODIGO";
                        System.out.println("DefineTextHint - quando modo = operador");
                        defineEditTextHint(true);
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

                    if (!FLAG_MODO) processaLinhaCarga();

                    return;
                }

                // Se a flag de leitura for o código então pode ler o código ou a localização

                if (FLAG_MODO_EDITTEXT.equals("CODIGO"))
                    System.out.println("OnTextChange: FLAG = CODIGO");
                // se der uma validação de localização então carrega a localização e mantem a flag no código
                if (validaLeituraLocalizacao(scannedText)) {
                    System.out.println("defineTextHint - Leitura localização OK");
                    defineEditTextHint(true);
                    return;
                } else {

                        /* Se estiver em modo "CODIGO" primeiro vamos validar se o que foi lido foi uma string começada por R, ou seja,
                        um código de barras que contem a referencia+lote+quantidade*/

                    String retval = validaLeituraCodigo(scannedText);


                    if (retval.equals("OK")) {
                        System.out.println("OnTextChange: FLAG = CODIGO: CODIGO OK");
                        if (!FLAG_MODO) processaLinhaCarga();
                        return;
                    }

                    if (retval.equals("VALIDA_REF")) {
                        System.out.println("OnTextChange: FLAG = CODIGO, Valida a referencia");
                        validaLeituraRef(scannedText);

                    } else {
                        dialogoErro("Erro", retval);
                        //editText_lercodigo.setText(clear);

                    }
                }

            }
        };


    }
}


