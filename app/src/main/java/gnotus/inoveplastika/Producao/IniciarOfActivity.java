package gnotus.inoveplastika.Producao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.regex.Pattern;

import gnotus.inoveplastika.API.Logistica.LerAlveoloWs;
import gnotus.inoveplastika.API.Phc.GetStWs;
import gnotus.inoveplastika.API.Producao.ActLocalConsumoOfWs;
import gnotus.inoveplastika.API.Producao.GetInfoOfWs;
import gnotus.inoveplastika.API.Producao.GetLinhasLocConsumoWs;
import gnotus.inoveplastika.API.Producao.IniciarOfWs;
import gnotus.inoveplastika.API.Producao.LocPodeIniciarOfWs;
import gnotus.inoveplastika.DataModels.DataModelBi;
import gnotus.inoveplastika.DataModels.DataModelLeituraCB;
import gnotus.inoveplastika.DataModels.DataModelST;
import gnotus.inoveplastika.DataModels.DataModelWsResponse;
import gnotus.inoveplastika.Dialogos;
import gnotus.inoveplastika.EditTextBarCodeReader;
import gnotus.inoveplastika.Globals;
import gnotus.inoveplastika.Logistica.DataModelAlv;
import gnotus.inoveplastika.R;

public class IniciarOfActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private ProgressDialog myProgDialog;

    private EditText inputPedeLocConsumo;

    private int FLAG_LER_ALVEOLO_LOC_CONSUMO = 1;

    private static final String json_vazio = "\"[]\"";

    private EditText editText_lercodigo,editTextLeCbPeca;
    private TextView textView_referencia, textView_of, textView_operador, textView_localizacao;
    private Button button_submeter;

    private ListView listViewLocConsumo;

    private EditText input;

    private static final String clear = "";

    private String oldDialogInput;

    private String ref = "";
    private String lote = "";
    private String armazem,zona,alveolo,cbpadrao = "";
    String armazem_lido, zona_lida, alveolo_lido = "";

    private boolean ofPosconsumo, obrigaLocConsumo;

    private String FLAG_TIPO_LEITURA = "REF";

    private ArrayList<DataModelBi> listaLocs = new ArrayList<>();

    private LocConsumosOfAdapter adapterLocs;
    private Activity thisActitity = IniciarOfActivity.this;

    private ProgressDialog progDailog ;

    private String textLocConsumo;

    private EditTextBarCodeReader.OnGetScannedTextListener editTextReadBarCodeListener;
    private EditTextBarCodeReader barCodeReaderLerCodigo; // ler codigo de barras na atividade
    private EditTextBarCodeReader barCodeReaderInputPedeLocConsumo; // ler código de barras no dialog que pede localização do consumo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_of);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Iniciar OF");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        progDailog = new ProgressDialog(this);
        progDailog.setMessage("Aguarde");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);

        //initNavigationDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView_referencia = (TextView) findViewById(R.id.textView_ref);
        textView_of = (TextView) findViewById(R.id.textView_of);
        textView_operador = (TextView) findViewById(R.id.textView_iniof_operador);
        textView_localizacao = (TextView) findViewById(R.id.textView_iniof_local);

        textView_referencia.setOnClickListener(this);
        textView_of.setOnClickListener(this);
        textView_operador.setOnClickListener(this);
        textView_localizacao.setOnClickListener(this);

        button_submeter = findViewById(R.id.button_iniof_iniciar);
        button_submeter.setText("INICIAR");
        button_submeter.setEnabled(true);

        editText_lercodigo = (EditText) findViewById(R.id.editText_iniof_lercodigo);
        // editText_lercodigo.setInputType(InputType.TYPE_NULL);
        // editText_lercodigo.addTextChangedListener(new MyTextWatcher(editText_lercodigo));

        defineEditTextReadBarCodeListener();
        barCodeReaderLerCodigo = new EditTextBarCodeReader(editText_lercodigo,this);
        barCodeReaderLerCodigo.setOnGetScannedTextListener(editTextReadBarCodeListener);
        editText_lercodigo.setOnClickListener(this);

        listViewLocConsumo = (ListView) findViewById(R.id.listView_locaisconsumo);

        button_submeter.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                ImageView image = new ImageView(getApplication());
                image.setImageResource(R.mipmap.ic_notok);

                String mUser = textView_operador.getText().toString().trim();

                if (ref.equals("") || lote.equals("") || (armazem.equals(0)) || zona.equals("") || alveolo.equals("") || mUser.equals("") ){
                    Dialogos.dialogoErro("Atenção","Existem variáveis em falta", 5,IniciarOfActivity.this,false);
                    return;
                }

                if (!listaLocs.isEmpty()) {
                    for (int i = 0; i < listaLocs.size(); i++) {
                        if (listaLocs.get(i).getZona1().equals("") || listaLocs.get(i).getIdentificacao1().equals("")
                                || listaLocs.get(i).getArmazem() == 0) {
                            Dialogos.dialogoErro("Atenção", "Existem linhas sem localição de consumo definida", 3, IniciarOfActivity.this, false);
                            return;
                        }
                    }
                }

              /*  // desativada a opção em 2020-02-21 (novas versões de Gaia)
                if (ofPosconsumo && listaLocs.isEmpty() && armazem.equals("60") ) {
                    String msgErro = "A OF é pos-consumo, deve existir pelo menos uma linhas para atribuir localização de consumo";
                    Dialogos.dialogoErro("Erro ao iniciar OF", msgErro, 3, IniciarOfActivity.this, false);
                    return;
                }*/


                if ( ! cbpadrao.isEmpty()) {
                    lerCodBarrasPadrao();
                }
                else {

                    iniciarOf();

                }



            }


        });

        listViewLocConsumo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long arg3) {


                String stamplinha = adapterLocs.getItem(position).getBistamp();
                textLocConsumo = "";

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(IniciarOfActivity.this);
                alertDialog.setTitle("REF: "+adapterLocs.getItem(position).getReferencia());
                alertDialog.setMessage("Ler localização de consumo");


                inputPedeLocConsumo = new EditText(IniciarOfActivity.this);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                inputPedeLocConsumo.setLayoutParams(lp);


                barCodeReaderInputPedeLocConsumo  = new EditTextBarCodeReader(inputPedeLocConsumo,thisActitity);
                barCodeReaderInputPedeLocConsumo.setReplaceTextOnBarCodeReading(true);
                barCodeReaderInputPedeLocConsumo.setOnTouchListenerIsBlocked(true);

                barCodeReaderInputPedeLocConsumo.setOnGetScannedTextListener(new EditTextBarCodeReader.OnGetScannedTextListener() {
                    @Override
                    public void onGetScannedText(String scannedText, EditText editText) {

                        Log.e("OnScannedTextListener",scannedText);

                        if (! validaLeituraLocalizacao(scannedText,false)) {

                            Log.e("Não validou leitura loc",scannedText);

                            barCodeReaderInputPedeLocConsumo.setText("");

                            Dialogos.dialogoErro("Erro na leitura da localização",
                                    "A localização "+scannedText+" é inválida :",3, IniciarOfActivity.this,false);

                        }
                        else
                        {

                            lerAlveolo(Integer.valueOf(armazem_lido) ,zona_lida,alveolo_lido);
                            // vamos ler a informação da localização e verificar se é uma localização igual à da maquina ou é uma localização de consumo of
                        }




                    }
                });


                alertDialog.setView(inputPedeLocConsumo);
                //alertDialog.setIcon(R.drawable.key);


                alertDialog.setPositiveButton("Submeter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                if (! inputPedeLocConsumo.getText().toString().isEmpty()) {

                                    dialog.cancel();

                                    ActLocalConsumoOfWs actLocalConsumoOfWs = new ActLocalConsumoOfWs(thisActitity);

                                    actLocalConsumoOfWs.setmOnActLocalConsumoOfListener(new ActLocalConsumoOfWs.OnActLocalConsumoOfListener() {
                                        @Override
                                        public void onSuccess(DataModelWsResponse wsResponse) {

                                            if (wsResponse.getCodigo() == 0) {
                                                carregaLinhasLocConsumo(false);
                                                return;
                                            }
                                            else {
                                                Dialogos.dialogoErro("Erro ao processar as linhas das localizações de consumo",wsResponse.getDescricao(),10,IniciarOfActivity.this,false);
                                            }

                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });

                                    actLocalConsumoOfWs.execute(adapterLocs.getItem(position).getBistamp(),armazem_lido,zona_lida,alveolo_lido);

                                }
                                else
                                {
                                    Dialogos.dialogoErro("Falta leitura da localização de consumo","",3,thisActitity,false);
                                }

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
                //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                dialog.show();



            }
        });


        defineEditTextHint(true);

    }

    @Override
    public void onClick(View v) {


        switch(v.getId()) {

            case R.id.editText_iniof_lercodigo:
                if (!editText_lercodigo.getText().toString().equals("")) {
                    defineEditTextHint(false);
                }
                break;

            case R.id.textView_ref:
                if (!textView_referencia.getText().toString().equals("")) {
                    textView_referencia.setText(clear);
                    defineEditTextHint(false);
                }
                break;

            case R.id.textView_of:
                if (!textView_of.getText().toString().equals("")) {
                    textView_of.setText(clear);
                    defineEditTextHint(false);
                }
                break;

            case R.id.textView_iniof_local:
                if (!textView_localizacao.getText().toString().equals("")) {
                    textView_localizacao.setText(clear);
                    defineEditTextHint(false);
                }
                break;

            case R.id.textView_iniof_operador:
                if (!textView_operador.getText().toString().equals("")) {
                    textView_operador.setText(clear);
                    defineEditTextHint(false);
                }
                break;
        }

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
            requestFocus(editText_lercodigo);
        }


        System.out.println("OF: " + loteF);

        return loteF;
    }

    private void dialogoErro(String title, String subtitle, int tempo) {

        ImageView image_ok = new ImageView(getApplication());
        ImageView image = new ImageView(getApplication());

        image.setImageResource(R.mipmap.ic_notok);
        image_ok.setImageResource(R.mipmap.ic_ok);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(IniciarOfActivity.this);


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

        AlertDialog.Builder builderInfo = new AlertDialog.Builder(IniciarOfActivity.this);
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

        requestFocus(editText_lercodigo);

        if (textView_referencia.getText().equals("")) {
            textView_of.setText(clear);
            textView_localizacao.setText(clear);
            textView_operador.setText(clear);
            editText_lercodigo.setHint("Ler referência");
            FLAG_TIPO_LEITURA = "REF";
            ref = "";
            lote = "";
            armazem = "";
            zona = "";
            alveolo = "";

            limpaListaLocConsumo();

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);

            return;
        }

        if (textView_of.getText().equals("")) {

            textView_localizacao.setText(clear);
            textView_operador.setText(clear);
            editText_lercodigo.setHint("Ler OF");
            FLAG_TIPO_LEITURA = "OF";
            lote = "";
            armazem = "";
            zona = "";
            alveolo = "";

            limpaListaLocConsumo();

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);

            return;
        }

        if (textView_localizacao.getText().equals("")) {

            textView_operador.setText(clear);
            editText_lercodigo.setHint("Ler localização");
            FLAG_TIPO_LEITURA = "LOCALIZACAO";
            armazem = "";
            zona = "";
            alveolo = "";

            limpaListaLocConsumo();

            if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);
            return;
        }

        if (textView_operador.getText().equals("")) {

            editText_lercodigo.setHint("Ler Operador");
            FLAG_TIPO_LEITURA = "OPERADOR";
            // if (mostraDialogoInfo) dialogoInfo("Acção", editText_lercodigo.getHint().toString(),1);
            return;
        }
        else
        {

            if (!listaLocs.isEmpty()) {
                for (int i = 0; i < listaLocs.size(); i++) {
                    if (listaLocs.get(i).getZona1().toString().equals("") || listaLocs.get(i).getAlveolo1().toString().equals("")
                            || listaLocs.get(i).getArmazem() == 0) {
                        return;
                    }
                }
                if (mostraDialogoInfo) dialogoInfo("Acção","Iniciar OF",1);
            }
            else
            if (mostraDialogoInfo) dialogoInfo("Acção","Iniciar OF",1);

            editText_lercodigo.setHint("Iniciar OF");
            FLAG_TIPO_LEITURA = "INICIAROF";

            return;

        }


        // chegados aqui temos todos os dados preenchidos



        //if (mostraDialogoInfo) dialogoInfo("Acção","Iniciar OF",1);

    }

    private void carregaLinhasLocConsumo( boolean resetLocal) {


        if (!ofPosconsumo) {
            defineEditTextHint(false);
            return;
        }


        GetLinhasLocConsumoWs linhasLocConsumoWs = new GetLinhasLocConsumoWs(thisActitity);
        linhasLocConsumoWs.setOnGetLinhasLocConsumoListener(new GetLinhasLocConsumoWs.OnGetLinhasLocConsumoListener() {
            @Override
            public void onSuccess(ArrayList<DataModelBi> linhasConsumosOF) {

                listaLocs = linhasConsumosOF;

                adapterLocs = new LocConsumosOfAdapter(thisActitity, 0, listaLocs);
                listViewLocConsumo.setAdapter(adapterLocs);
                adapterLocs.notifyDataSetChanged();

                listViewLocConsumo.setVisibility(View.VISIBLE);

                defineEditTextHint(true);
            }

            @Override
            public void onError(String error) {
                textView_localizacao.setText("");
                defineEditTextHint(false);
            }

        });
        linhasLocConsumoWs.execute(ref,lote,Integer.valueOf(armazem) ,zona,alveolo,resetLocal);


    }

    private void limpaListaLocConsumo(){

        if (!listaLocs.isEmpty()) {
            listaLocs.clear();
            adapterLocs.notifyDataSetChanged();
        }

    }

    private boolean validaLeituraLocalizacao(String textoLido, Boolean registaLocLida) {


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

                if (registaLocLida) {
                    armazem = armazem_lido;
                    zona = "";
                    alveolo = "";
                    textView_localizacao.setText(armazem_lido);
                }


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
                System.out.println("Pos tag_1:" + pos_tag1);
                System.out.println("Pos tag_2:" + pos_tag2);

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



                if (registaLocLida) {
                    textView_localizacao.setText("[" + armazem_lido + "] [" + zona_lida + "] [" + alveolo_lido + "]");
                    armazem = armazem_lido;
                    zona = zona_lida;
                    alveolo = alveolo_lido;
                }

                return true;

            default: {
                return false;
            }

        }

    }




    private void validaLeituraOF(String ref, String lote) {

        GetInfoOfWs getInfoOfWs = new GetInfoOfWs(thisActitity);
        getInfoOfWs.setOnGetInfoOfListener(new GetInfoOfWs.OnGetInfoOfListener() {
            @Override
            public void onSuccess(InfoOfDataModel mInfoOf) {


                if (mInfoOf == null) {
                    Dialogos.dialogoErro("Erro na leitura da OF","A OF "+mInfoOf.getNumof()+"não existe para a referencia "+mInfoOf.getRef(),5,IniciarOfActivity.this, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }


                if (mInfoOf.getFechada()) {
                    Dialogos.dialogoErro("Informação","A OF "+mInfoOf.getNumof()+" já está fechada",5,IniciarOfActivity.this, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }


                // se foi iniciada mas não finalizada
                if (mInfoOf.getIniciada() && !mInfoOf.getFinalizada() ) {
                    Dialogos.dialogoErro("Informação","A OF "+mInfoOf.getNumof()+" já foi iniciada",5,IniciarOfActivity.this, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }

                if (mInfoOf.getOs_corretivas_open() > 0) {
                    String txtSubTitMess = "O molde associado à OF "+mInfoOf.getNumof()+" tem ordens de manutençao corretivas em aberto. Não pode iniciar a OF";
                    Dialogos.dialogoErro("Informação",txtSubTitMess,5,IniciarOfActivity.this, false);
                    textView_referencia.setText("");
                    defineEditTextHint(false);
                    return;
                }


                ofPosconsumo = mInfoOf.getPosconsumo();
                cbpadrao = mInfoOf.getU_cbpadrao();

                defineEditTextHint(true);


            }

            @Override
            public void onError(VolleyError error) {

                textView_referencia.setText("");
                defineEditTextHint(false);

            }
        });
        getInfoOfWs.execute(ref,lote);


    }

    private  void lerAlveolo(final Integer pArmazem, final String pZona, final String pAlveolo)
    {

        LerAlveoloWs lerAlveoloWs = new LerAlveoloWs(this);

        lerAlveoloWs.setOnLerAlveoloListener(new LerAlveoloWs.OnLerAlveoloListener() {
            @Override
            public void onSuccess(DataModelAlv dmAlveolo) {

                if (dmAlveolo == null) {
                    barCodeReaderInputPedeLocConsumo.setText("");
                    Dialogos.dialogoErro("", "Não encontrei no sistema a localização ["+pArmazem+"]["+pZona+"]["+pAlveolo+"]", 3, thisActitity, false);
                    defineEditTextHint(false);
                }
                else {

                    // localização de consumo de of

                    System.out.println("armazem: "+armazem_lido.equals(armazem) );
                    System.out.println("zona: "+zona_lida.equals(zona) );
                    System.out.println("alveolo: "+alveolo_lido.equals(alveolo) );

                    boolean locConsumoIgualLocOF;
                    locConsumoIgualLocOF = armazem_lido.equals(armazem) && zona_lida.equals(zona) && alveolo_lido.equals(alveolo);

                    // se a localização lida for igual à localização onde se vai iniciar a of então é válido
                    if (! locConsumoIgualLocOF) {

                        // se a localização lida não for uma localização de consumos de OFs
                        if (! dmAlveolo.getU_loccof()) {

                            // inputPedeLocConsumo.setText(""); // desativada 2019-12-01
                            barCodeReaderInputPedeLocConsumo.setText("");

                            Dialogos.dialogoErro("Dados inválidos", "A localização ["+armazem_lido+"]["+zona_lida+"]["+alveolo_lido+"] " +
                                    "não é válida como localização de consumos de OF", 3, thisActitity, false);

                        }
                        else  {
                            textLocConsumo = "["+armazem_lido+"] ["+zona_lida+"] ["+alveolo_lido+"]" ;
                            // inputPedeLocConsumo.setText(textLocConsumo); desativada 2019-12-01
                            barCodeReaderInputPedeLocConsumo.setText(textLocConsumo);
                        }
                    }
                    else
                    {
                        textLocConsumo = "["+armazem_lido+"] ["+zona_lida+"] ["+alveolo_lido+"]" ;
                        // inputPedeLocConsumo.setText(textLocConsumo); // desativada 2019-12-01
                        barCodeReaderInputPedeLocConsumo.setText(textLocConsumo);
                    }


                }

            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        lerAlveoloWs.execute(pArmazem,pZona,pAlveolo);


    }

    private  void LocPodeIniciarOf(String numof,String pArmazem, String pZona, String pAlveolo, int pOperacao) {


        LocPodeIniciarOfWs locPodeIniciarOfWs = new LocPodeIniciarOfWs(this);

        locPodeIniciarOfWs.setOnRegisterSupplierReceptionListener(new LocPodeIniciarOfWs.OnLocPodeIniciarOfListener() {
            @Override
            public void onSuccess(DataModelWsResponse wsResponse) {

                if (wsResponse.getCodigo() == 0) {
                    carregaLinhasLocConsumo(true);
                }

                else {

                    Dialogos.dialogoErro("Iniciar OF",wsResponse.getDescricao(),10,thisActitity,false);
                    textView_localizacao.setText("");
                    defineEditTextHint(false);
                }

            }

            @Override
            public void onError(VolleyError error) {

                textView_localizacao.setText("");

            }
        });

        locPodeIniciarOfWs.execute(numof,pArmazem,pZona,pAlveolo);

    }

    private void lerCodBarrasPadrao() {

        // variavel que guarda a informação que estava no AlertDialog antes da nova leitura de codigo de barras

        oldDialogInput = "";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisActitity);
        alertDialog.setTitle("Validação de dados");
        alertDialog.setMessage("Ler código de barras da peça padrão");
        alertDialog.setCancelable(false);

        editTextLeCbPeca = new EditText(thisActitity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // editTextLeCbPeca.setInputType(InputType.TYPE_NULL);
        editTextLeCbPeca.setLayoutParams(lp);

        alertDialog.setView(editTextLeCbPeca);

        final EditTextBarCodeReader barCodeReaderLerCbPadrao = new EditTextBarCodeReader(editTextLeCbPeca,thisActitity);
        barCodeReaderLerCbPadrao.setReplaceTextOnBarCodeReading(true);
        barCodeReaderLerCbPadrao.setOnTouchListenerIsBlocked(true);


        alertDialog.setPositiveButton("Submeter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

        final AlertDialog dialog = alertDialog.create();
        //alertDialog.create();

        //alertDialog.show();
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setFocusable(false);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setFocusable(false);


        // Override ao click do próprio botão
        // impedir que o dialogo feche sempre que se clica no botão

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String mCbPeca = editTextLeCbPeca.getText().toString();

                // se o código de barras lido não for igual à peça
                System.out.println("Indice de leitura: "+mCbPeca.indexOf(cbpadrao));


                if (! mCbPeca.equals(cbpadrao)) {
                    barCodeReaderLerCbPadrao.setText("");
                    Dialogos.dialogoErro("Leitura inválida","O código de barras lido não corresponde ao codigo de barras da peça",5,thisActitity, false);
                }
                else
                {
                    dialog.dismiss();
                    iniciarOf();

                }

            }
        });

    }

    private  void iniciarOf() {

        IniciarOfWs iniciarOfWs =  new IniciarOfWs(this);

        iniciarOfWs.setmOnIniciarOfListener(new IniciarOfWs.OnIniciarOfListener() {
            @Override
            public void onSuccess(DataModelWsResponse wsResponse) {

                if (wsResponse.getCodigo() == 0) {
                    dialogoInfo("Sucesso","OF iniciada",2);
                    textView_referencia.setText(clear);
                    defineEditTextHint(false);
                    return;
                }
                else
                    Dialogos.dialogoErro("Erro ao iniciar OF",wsResponse.getDescricao(),60,IniciarOfActivity.this,false);

            }
        });

        iniciarOfWs.execute(ref,lote,armazem,zona,alveolo,textView_operador.getText().toString());

    }

    private void defineEditTextReadBarCodeListener() {

        editTextReadBarCodeListener = new EditTextBarCodeReader.OnGetScannedTextListener() {
            @Override
            public void onGetScannedText(String scannedText, EditText editText) {

                View view;
                boolean FLAG_CODIGO = false;
                String codigos1[];
                String codigos2[];

                String valorlido = editText_lercodigo.getText().toString();

                if (!valorlido.equals("")) {
                    editText_lercodigo.setText("");
                }
                else
                    return;


                // conforme o tipo de leitura vamos proceder à validação da informação carregada

                // se o comprimento for >= 3 e se for um código começado por R então vamos ler a referencia e a OF

                if (valorlido.length() >= 3) {

                    // vamos verificar se o valor lido é um código de barras
                    DataModelLeituraCB lerCb = new DataModelLeituraCB(valorlido);

                    if (lerCb.getTipocb().equals(DataModelLeituraCB.TIPOCB_REF)) {

                        ref = lerCb.getReferencia();
                        lote = lerCb.getLote();

                        // se a referencia e lote não estiverem vazias
                        if (! ref.isEmpty() && ! lote.isEmpty()) {

                            textView_referencia.setText(ref);
                            textView_of.setText(lote);
                            validaLeituraOF(ref, lote);
                            return;

                        }


                    }

                }



                switch (FLAG_TIPO_LEITURA) {

                    case "REF":

                        if (valorlido.length() < 3){
                            Dialogos.dialogoErro("Erro","Leitura inválida: "+valorlido,3,IniciarOfActivity.this,false);
                            defineEditTextHint(false    );
                            return;
                        }

                        final String mReflida = valorlido.trim();

                        GetStWs mGetStWs = new GetStWs(thisActitity);

                        mGetStWs.setOnLerStListener(new GetStWs.OnLerStListener() {
                            @Override
                            public void onSuccess(ArrayList<DataModelST> arraySt) {

                                if (arraySt.size() == 0) {
                                    Dialogos.dialogoErro("Validação do artigo", "Não encontrei no sistema o artigo " + mReflida, 10, IniciarOfActivity.this, false);
                                    textView_referencia.setText("");
                                    defineEditTextHint(false);
                                    return;
                                }
                                else {

                                    ref = arraySt.get(0).getRef();
                                    textView_referencia.setText(ref);
                                    defineEditTextHint(true);
                                    return;
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                textView_referencia.setText("");
                                defineEditTextHint(false);
                            }
                        });


                        mGetStWs.execute(mReflida);

                        break;

                    case "LOCALIZACAO":

                        if (! validaLeituraLocalizacao(valorlido,true))
                        {
                            dialogoErro("Erro","Leitura de localização inválida",3);
                            editText_lercodigo.setText(clear);
                            defineEditTextHint(false);
                        }

                        else {

                            // vamos verificar se a localização pode iniciar a of
                            LocPodeIniciarOf(lote,armazem,zona,alveolo,7);
                        }

                        break;

                    case "OF":

                        lote = processaLote(valorlido);
                        textView_of.setText(lote);
                        validaLeituraOF(ref, lote);

                        break;

                    case "OPERADOR":
                        // se o que foi lido começa por (OP)

                        if (valorlido.substring(0,4).equals("(OP)")) {

                            String mNrOperador = valorlido.substring(4,valorlido.length()) ;

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
