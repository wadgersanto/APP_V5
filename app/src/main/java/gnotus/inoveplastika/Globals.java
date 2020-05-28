package gnotus.inoveplastika;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import java.util.ArrayList;

import gnotus.inoveplastika.DataModels.DataModelCarga;
import gnotus.inoveplastika.Producao.EntradaProducaoActivity;

public class Globals {

    private static Globals instance;


    //Variáveis globais

    private String caminho;
    private String user;
    private String userid;
    private String trfCargaBostamp = "";
    private String defaultToolbarColour = "#FF669900";
    private Integer mVolleyTimeOut = 60000;
    private String selectedFactory;

    public static final String MAN_TIPO_PEDIDO_CORRETIVA = "CORRETIVA";
    public static final String MAN_TIPO_PEDIDO_PREVENTIVA = "PREVENTIVA";

    public static final String MAN_TIPO_TECNICO_MOLDES = "MOLDES";
    public static final String MAN_TIPO_TECNICO_EQUIPAMENTOS = "EQUIPAMENTOS";

    public static final String FACTORY_GAIA = "GAIA";
    public static final String FACTORY_BARCELOS = "BARCELOS";

    public static Boolean PARAM_OBRIGA_TURNO_EP = true;
    public static Double PARAM_TURNOS_MAX_HEXTRA = 6.0;

    public static String SHAREDPREFS_PARAM_NAME_FABRICA = "fabrica";
    public static String WSRESPONSEVAZIO = "\"[]\"";

    private ArrayList<DataModelCarga> artigos;

    private Globals() {
        artigos = new ArrayList<DataModelCarga>();
    }

    public static synchronized Globals getInstance() {

        if (instance == null) {
            instance = new Globals();
        }

        return instance;
    }

    public String getCaminho() {
        return this.caminho;
    }

    public String  getDefaultToolbarColour() {
        return this.defaultToolbarColour;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {

        this.user = user;
    }

    public void setTrfCargaBostamp(String bostamp) {

        this.trfCargaBostamp = bostamp;
    }

    public String getSelectedFactory() {
        return selectedFactory;
    }

    public void setSelectedFactory(String selectedFactory) {
        this.selectedFactory = selectedFactory;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {

        this.userid = userid;
    }

    public ArrayList<DataModelCarga> getArtigos() {

        return this.artigos;
    }

    public void setArtigos(DataModelCarga artigo) {

        artigos.add(artigo);
    }

    public int getSize() {

        return artigos.size();
    }

    public void defineAcaoMenu(MenuItem mMenuItem, Activity mAtividade, DrawerLayout mDrwawer,int mCurrMenuItem){
        int id = mMenuItem.getItemId();

        Intent myIntent;

        System.out.println("Invloc");

        System.out.println(id);

        if(mCurrMenuItem == id) {
            mDrwawer.closeDrawers();
            return;
        }


        switch (id) {
            case R.id.navigation_inicio:
                break;

            case R.id.navigation_config_ip:
                myIntent = new Intent(mAtividade, ConfigsActivity.class);
                mAtividade.startActivity(myIntent);
                break;

            case R.id.navigation_option3:
                myIntent = new Intent(mAtividade, EntradaProducaoActivity.class);
                mAtividade.startActivity(myIntent);
                break;

            case R.id.navigation_logout:
                break;
        }


        mAtividade.finish();
        mDrwawer.closeDrawers();

    }

    public Integer getmVolleyTimeOut() {
        return mVolleyTimeOut;
    }
}
