package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import gnotus.inoveplastika.ArrayAdapters.ArrayAdapterMenuList;
import gnotus.inoveplastika.ComponentesNOK.ComponentesNOKActivity;
import gnotus.inoveplastika.DataModels.DataModelMenuList;
import gnotus.inoveplastika.PickingBoxes.PickingBoxesActivity;
import gnotus.inoveplastika.Producao.EntradaProducaoActivity;
import gnotus.inoveplastika.Producao.IniciarOfActivity;
import gnotus.inoveplastika.Producao.IniciarTurnoActivity;
import gnotus.inoveplastika.Producao.PedidoAbastProdActivity;
import gnotus.inoveplastika.QualityControl.QualityActivity;
import gnotus.inoveplastika.RececaoEncomenda.ReceptionActivity;
import gnotus.inoveplastika.Suporte.InserirPatActivity;
import gnotus.inoveplastika.Suporte.ProcessarPatActivity;

public class SMenuLista extends AppCompatActivity  implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private Activity thisActivity = SMenuLista.this;

    // Bundle para enviar intents
    Bundle bundleSent = new Bundle();
    // Bundle recebido da atividade que chamou esta
    Bundle bundleRecebido = new Bundle();

    SharedPreferences sharedpreferences;

    private ArrayList<DataModelMenuList> sMenuConsultas      = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuInventarios    = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuPicking        = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuProducao       = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuManutencao     = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuOutrasOpcoes   = new ArrayList<>();
    private ArrayList<DataModelMenuList> sMenuQualidade      = new ArrayList<>();

    private ArrayAdapterMenuList adapterMenuList;

    private ListView listViewMenuList;

    // Qual é o id do menu que foi seleccionado no MainActivity

    private String mainActivityMenuId = "", mainActivityMenuOpcao;

    AlertDialog alertDialogOpcoes;

    CharSequence[] OpcoesPecasNok = {"Em produção","Já declaradas"};
    CharSequence[] OpcoesTipoOf = {"Injeção","Montagem"};


    private ConstraintLayout layoutLista, layoutAccao;

    ProgressDialog bar;

    CharSequence[] OpcoesExpedicao = {"Picking","Devolução Picking"};

    @Override
    public void onClick(View view)

    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        layoutLista = (ConstraintLayout) (findViewById(R.id.linearLayout));
        layoutAccao = (ConstraintLayout) (findViewById(R.id.linearLayoutDialog));

        layoutLista.setVisibility(View.VISIBLE);
        layoutAccao.setVisibility(View.GONE);

        bundleRecebido = this.getIntent().getExtras();
        // Verifica qual o item do menu que foi
        mainActivityMenuId = bundleRecebido.getString("menuid");
        mainActivityMenuOpcao= bundleRecebido.getString("menuopcao");

        sMenuInventarios.add(new DataModelMenuList("INVENTARIO INICIAL LOCALIZAÇÃO","2.01"));
        sMenuInventarios.add(new DataModelMenuList("INVENTARIO POR LOCALIZAÇÃO","2.02"));
        sMenuInventarios.add(new DataModelMenuList("INVENTARIO POR REF","2.03"));

        sMenuProducao.add(new DataModelMenuList("REGISTAR PRODUÇÃO","3.01"));
        sMenuProducao.add(new DataModelMenuList("INICIAR OF","3.02"));
        sMenuProducao.add(new DataModelMenuList("TERMINAR OF","3.03"));
        sMenuProducao.add(new DataModelMenuList("REGISTAR PEÇAS NOK","3.04"));
        sMenuProducao.add(new DataModelMenuList("INVENTARIAR LOCALIZAÇÃO CONSUMOS OF","3.05"));
        sMenuProducao.add(new DataModelMenuList("CONSULTA ESTADO OF","3.06"));
        sMenuProducao.add(new DataModelMenuList("PEDIDO ABAST. PRODUÇÃO","3.07"));
        sMenuProducao.add(new DataModelMenuList("COMPONENTES NOK","3.08"));

        sMenuConsultas.add(new DataModelMenuList("STOCK NA LOCALIZAÇÃO","4.01"));
        sMenuConsultas.add(new DataModelMenuList("STOCK POR ARTIGO","4.02"));

        sMenuPicking.add(new DataModelMenuList("EXPEDIÇÃO","5.01"));
        sMenuPicking.add(new DataModelMenuList("DEVOLUÇÃO EXPEDIÇÃO","5.02"));
        sMenuPicking.add(new DataModelMenuList("ABASTECIMENTO PRODUÇÃO","5.03"));
        sMenuPicking.add(new DataModelMenuList("ATRIBUIR CAIXA PICKING","5.04"));
        sMenuPicking.add(new DataModelMenuList("RECEÇÃO ENCOMENDAS","5.05"));

        sMenuManutencao.add(new DataModelMenuList("REGISTAR PEDIDO MANUTENÇÃO","6.01"));
        sMenuManutencao.add(new DataModelMenuList("PEDIDOS POR RECECIONAR","6.02"));
        sMenuManutencao.add(new DataModelMenuList("ORDENS SERVIÇO ABERTAS","6.03"));
        sMenuManutencao.add(new DataModelMenuList("PRODUÇÃO: VALIDAR ORDENS SERVIÇO","6.04"));
        sMenuManutencao.add(new DataModelMenuList("QUALIDADE: VALIDAR ORDENS SERVIÇO","6.05"));


        sMenuQualidade.add(new DataModelMenuList("CONTROLO DE QUALIDADE","7.01"));


        sMenuOutrasOpcoes.add(new DataModelMenuList("OPERADOR: INICIAR TURNO","8.01"));


        listViewMenuList= (ListView) findViewById(R.id.listViewMenu);
        listViewMenuList.setFocusable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mainActivityMenuOpcao);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor(Globals.getInstance().getDefaultToolbarColour()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        switch (mainActivityMenuId) {

            case "2":

                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuInventarios);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

            case "3":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuProducao);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

            case "4":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuConsultas);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

            case "5":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuPicking);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

            case "6":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuManutencao);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

            case "7":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuQualidade);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;

                case "8":
                adapterMenuList = new ArrayAdapterMenuList(thisActivity, 0, sMenuOutrasOpcoes);
                adapterMenuList.notifyDataSetChanged();
                listViewMenuList.setAdapter(adapterMenuList);
                break;
        }

        listViewMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            //handle multiple view click events
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                String selMenuId = adapterMenuList.getItem(position).getMenuid();

                Intent myIntent;

                switch (selMenuId) {

                    // INVENTÁRIOS
                    case "2.01":

                        myIntent = new Intent(thisActivity,InvlocActivity.class);
                        startActivity(myIntent);
                        break;

                    case "2.02":

                        if (1 == 1) {

                            Dialogos.dialogoInfo("Informação","Opção desativada",3.0,thisActivity,false);
                            return;

                        }

                        bundleSent.clear();
                        bundleSent.putString("tipo_inv","ALV");
                        bundleSent.putString("tipoinvloc","");

                        Intent myIntent_inventario = new Intent(thisActivity,InventarioActivity.class);
                        myIntent_inventario.putExtras(bundleSent);
                        startActivity(myIntent_inventario);
                        break;

                    case "2.03":

                        if (1 == 1) {

                            Dialogos.dialogoInfo("Informação","Opção desativada",3.0,thisActivity,false);
                            return;

                        }


                        bundleSent.clear();
                        bundleSent.putString("tipo_inv","REF");
                        bundleSent.putString("tipoinvloc","");
                        Intent myIntent_invref = new Intent(thisActivity,InventarioActivity.class);
                        myIntent_invref.putExtras(bundleSent);
                        startActivity(myIntent_invref);
                        break;



                    // PRODUÇÃO
                    case "3.01":
                        // REGISTAR PRODUÇÃO
                        Intent myIntent_regProd = new Intent(thisActivity, EntradaProducaoActivity.class);
                        startActivity(myIntent_regProd);
                        break;
                    case "3.02":
                        // INICIAR OF
                        Intent myIntent_IniOf = new Intent(thisActivity, IniciarOfActivity.class);
                        startActivity(myIntent_IniOf);
                        break;
                    case "3.03":
                        // Terminar OF
                        CreateAlertDialogTerminarOf();
                        break;
                    case "3.04":
                        // REGISTAR PEÇAS NOK
                        CreateAlertDialogPecasNok() ;
                        break;
                    case "3.05":
                        // INVENTARIAR LOCALIZAÇÃO CONSUMOS OF
                        bundleSent.clear();
                        bundleSent.putString("tipo_inv","ALV");
                        bundleSent.putString("tipoinvloc","locconsumoof");

                        myIntent = new Intent(thisActivity,InventarioActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "3.06":
                        // INFORMAÇÃO ESTADO OF
                        bundleSent.clear();
                        bundleSent.putString("tipoConsultaStock", "INFOOF");
                        executaConsultaStocks(bundleSent);
                        break;


                    case "3.07":
                        // PEDIDO ABASTECIMENTO PRODUÇÃO
                        myIntent = new Intent(thisActivity,PedidoAbastProdActivity.class);
                        startActivity(myIntent);
                        break;


                    case "3.08":
                        // COMPONENTES NOK
                        myIntent = new Intent(thisActivity, ComponentesNOKActivity.class);
                        startActivity(myIntent);
                        break;


                    // CONSULTAS
                    case "4.01":
                        // STOCK NA LOCALIZAÇÃO
                        bundleSent.clear();
                        bundleSent.putString("tipoConsultaStock", "localizacao");
                        executaConsultaStocks(bundleSent);
                        break;

                    case "4.02":
                        // STOCK POR ARTIGO
                        bundleSent.clear();
                        bundleSent.putString("tipoConsultaStock", "referencia");
                        executaConsultaStocks(bundleSent);
                        break;



                        // PICKING
                    case "5.01":
                        // EXPEDIÇÃO
                        bundleSent.clear();
                        bundleSent.putString("pickingType","EXP");

                        myIntent = new Intent(thisActivity,PickingActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "5.02":
                        // DEVOLUÇÃO EXPEDIÇÃO
                        bundleSent.clear();
                        bundleSent.putString("pickingType","DEVEXP");

                        myIntent = new Intent(thisActivity,PickingActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);

                        break;

                    case "5.03":
                        // ABASTECIMENTO PRODUÇÃO

                        bundleSent.clear();
                        bundleSent.putString("pickingType","ABASTPROD");

                        myIntent = new Intent(thisActivity,PickingActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "5.04":
                        // ATRIBUIR CAIXA PICKING
                        bundleSent.clear();
                        myIntent = new Intent(thisActivity, PickingBoxesActivity.class);
                        startActivity(myIntent);

                        break;

                    case "5.05":
                        // RECEÇÃO ENCOMENDAS
                        bundleSent.clear();
                        myIntent = new Intent(thisActivity, ReceptionActivity.class);
                        startActivity(myIntent);

                        break;

                    // MANUTENÇÃO
                    case "6.01":
                        // registar pedido
                        bundleSent.clear();

                        myIntent = new Intent(thisActivity,InserirPatActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "6.02":
                        // Rececionar pedido
                        bundleSent.clear();
                        bundleSent.putInt("tipoaccao",1);
                        myIntent = new Intent(thisActivity,ProcessarPatActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "6.03":
                        // Rececionar pedido
                        bundleSent.clear();
                        bundleSent.putInt("tipoaccao",2);

                        myIntent = new Intent(thisActivity,ProcessarPatActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "6.04":
                        // Produção - Validar ordens serviço
                        bundleSent.clear();
                        bundleSent.putInt("tipoaccao",3);

                        myIntent = new Intent(thisActivity,ProcessarPatActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    case "6.05":
                        // QualidadeValidar ordens serviço
                        bundleSent.clear();
                        bundleSent.putInt("tipoaccao",4);

                        myIntent = new Intent(thisActivity,ProcessarPatActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    // QUALIDADE
                    case "7.01":
//                        // registar qualidade
                        bundleSent.clear();
//
                        myIntent = new Intent(thisActivity, QualityActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;

                    // OUTRAS OPÇÕES
                    case "8.01":
                        // registar pedido
                        bundleSent.clear();

                        myIntent = new Intent(thisActivity,IniciarTurnoActivity.class);
                        myIntent.putExtras(bundleSent);
                        startActivity(myIntent);
                        break;


                }

            }
        });
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(0).setChecked(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.drawer_open, R.string.drawer_close) {

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

        //this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
        //finishAffinity();
        finish();

    }
    @Override
    public boolean onSupportNavigateUp() {


        onBackPressed();
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

    }
    private void executaConsultaStocks(Bundle bundle){

        Intent myIntent = new Intent(this,ConsultaStocksActivity.class);
        myIntent.putExtras(bundle);
        startActivity(myIntent);

    }

    public void CreateAlertDialogPecasNok(){


        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        bundleSent.clear();

        builder.setTitle("Escolha o tipo de registo");

        builder.setItems(OpcoesPecasNok,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent myIntent_finalizarOf;
                final Integer tiporegisto;

                switch(item)
                {
                    case 0:

                        bundleSent.putInt("pnok_tiporegisto",0);
                        break;
                    case 1:

                        bundleSent.putInt("pnok_tiporegisto",1);
                        break;
                }

                myIntent_finalizarOf = new Intent(thisActivity    ,PecasNokActivity.class);
                myIntent_finalizarOf.putExtras(bundleSent);
                startActivity(myIntent_finalizarOf);

                alertDialogOpcoes.dismiss();
            }
        });

        alertDialogOpcoes = builder.create();
        alertDialogOpcoes.show();

    }

    public void CreateAlertDialogTerminarOf(){


        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

        bundleSent.clear();

        builder.setTitle("Escolha o tipo de OF");

        builder.setItems(OpcoesTipoOf,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent myIntent_finalizarOf;

                switch(item)
                {
                    case 0:

                        bundleSent.putBoolean("ofmontagem",false);
                        break;
                    case 1:

                        bundleSent.putBoolean("ofmontagem",true);
                        break;
                }

                myIntent_finalizarOf = new Intent(thisActivity    ,InvOfActivity.class);
                myIntent_finalizarOf.putExtras(bundleSent);
                startActivity(myIntent_finalizarOf);

                alertDialogOpcoes.dismiss();
            }
        });

        alertDialogOpcoes = builder.create();
        alertDialogOpcoes.show();

    }

}
