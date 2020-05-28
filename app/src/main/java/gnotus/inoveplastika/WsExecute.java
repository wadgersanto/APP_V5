package gnotus.inoveplastika;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.health.SystemHealthManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;

/**
 * Created by Alexandre on 09/12/2017.
 */

public class WsExecute {


    public static void aSyncEliminaDossier(String bostamp, String tabela, boolean onlyifnolines,Activity activity){

        AsyncRequest processaPedido = new AsyncRequest(activity, 4);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                .appendQueryParameter("bostamp",String.valueOf (bostamp))
                .appendQueryParameter("tabela","BO")
                .appendQueryParameter("onlyifnolines",String.valueOf(false))
                .build();

        processaPedido.execute(builtURI_processaPedido.toString());
    }

    public static String criaBo(int ndos, int no, String ent, String data, int estab, int user, int armazem,
                                String zona1, String alveolo1,Activity activity){


        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/criaBO?").buildUpon()
                .appendQueryParameter("ndos",String.valueOf(ndos))
                .appendQueryParameter("no", String.valueOf(no))
                .appendQueryParameter("ent", "CL")
                .appendQueryParameter("data", "")
                .appendQueryParameter("est", String.valueOf(estab))
                .appendQueryParameter("user", String.valueOf(user))
                .appendQueryParameter("armazem", String.valueOf(armazem))
                .appendQueryParameter("zona1", zona1)
                .appendQueryParameter("alveolo1", alveolo1)
                .build();

        String mResultado = "";


        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro";
        }
        return   mResultado;

    }

    public static String converteLote(String referencia, String lote,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ConverteLote?").buildUpon()
                .appendQueryParameter("referencia",referencia)
                .appendQueryParameter("lote",lote)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice converteLote";
        }

        return   mResultado;
    }


    public static String processaAcertoInvOf(String bostamp_inventario, Activity a) {
        String mResultado = "";

        System.out.println("Processa pedido: processaAcertoInvOf ");

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/ProcessaAcertosInvOf?").buildUpon()
                .appendQueryParameter("bostamp_inventario", bostamp_inventario)
                .build();

        //processaPedido.execute(builtURI_processaPedido.toString());

        try {
            mResultado = new AsyncRequest(a,12).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice processaAcertoInvOf";
        }
        return mResultado;
    }

    public static String registaLeituraInvOf(String bostamp_inventario, String ref, String lote, Double qtt, Boolean adicionaqtt, Activity activity ){

        System.out.println("WsExecute: Processa pedido invof_RegistaLeitura ");

        //AsyncRequest processaPedido = new AsyncRequest(activity, 99);

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RegistaLeituraInvOf?").buildUpon()
                .appendQueryParameter("bostamp", bostamp_inventario)
                .appendQueryParameter("referencia", ref)
                .appendQueryParameter("lote",lote)
                .appendQueryParameter("qtt",qtt.toString())
                .appendQueryParameter("adicionaqtt",adicionaqtt.toString())
                .build();

        //processaPedido.execute(builtURI_processaPedido.toString());

        String mResultado = "";
        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice RegistaLeituraInvOf";
        }

        return   mResultado;
    }



    public static JSONArray lerStock(String referencia, String lote, int armazem, String zona, String alveolo, Activity activity){


        JSONconverter jsonConverter = new JSONconverter();
        JSONArray values;


        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/lerStock?").buildUpon()
                .appendQueryParameter("ref", referencia.trim())
                .appendQueryParameter("lote",lote.trim())
                .appendQueryParameter("armazem", String.valueOf (armazem))
                .appendQueryParameter("zona",zona.trim())
                .appendQueryParameter("alveolo",alveolo.trim())
                .build();

        String mResultado = "";


        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = jsonConverter.ConvertJSON(mResultado);
            values = new JSONArray(mResultado);
            return values;
        }

        catch (Exception ei) {
            Dialogos.dialogoErro("Erro na leitura de stock", ei.getMessage(),60,activity,false);
            return null;
        }

    }

    public static String finalizarOf(int armazem, String zona, String alveolo, String operador, Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/FinalizarOf?").buildUpon()
                .appendQueryParameter("armazem", String.valueOf(armazem))
                .appendQueryParameter("zona", zona)
                .appendQueryParameter("alveolo", alveolo)
                .appendQueryParameter("operador", operador)
                .build();

        String mResultado = "";


        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice FinalizarOf";
        }
        return   mResultado;

    }


    public static JSONArray lerSE(String referencia, String lote, String filtro, Activity activity){

        JSONconverter jsonConverter = new JSONconverter();
        JSONArray myJsonArray;

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerSE?").buildUpon()
                .appendQueryParameter("referencia", referencia)
                .appendQueryParameter("lote", lote)
                .appendQueryParameter("filtro", filtro)
                .build();

        String mResultado;

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = jsonConverter.ConvertJSON(mResultado);
            myJsonArray = new JSONArray(mResultado);
            return myJsonArray;
        }

        catch (Exception ei) {
            Dialogos.dialogoErro("Erro na leitura do WS LerSE", ei.getMessage(),60,activity,false);
            return null;
        }

    }

    public static JSONArray lerST(String referencia, Activity activity){

        JSONconverter jsonConverter = new JSONconverter();
        JSONArray myJsonArray;

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/LerST?").buildUpon()
                .appendQueryParameter("referencia", referencia.trim())
                .build();

        String mResultado;

        System.out.println(builtURI_processaPedido);

        try {

            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            System.out.println(mResultado);

            mResultado = jsonConverter.ConvertJSON(mResultado);
            myJsonArray = new JSONArray(mResultado);
            return myJsonArray;
        }

        catch (Exception ei) {
            Dialogos.dialogoErro("Erro na leitura do WS LerST", ei.getMessage(),60,activity,false);
            return null;
        }

    }
    public static String existeBostamp(String bostamp,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/existeBostamp?").buildUpon()
                .appendQueryParameter("bostamp",bostamp)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice existeBostamp";
        }
        System.out.println("WsExecute LocProcessaInv resultado: "+mResultado);

        return   mResultado;

    }

    public static String refJaFezInvLoc(String referencia, String lote,int armazem, String zona, String alveolo,String bostamp,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RefJaFezInvLoc?").buildUpon()
                .appendQueryParameter("referencia",referencia)
                .appendQueryParameter("lote",lote)
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .appendQueryParameter("bostamp",bostamp)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice refJaFezInvLoc";
        }
        System.out.println("WsExecute refJaFezInvLoc resultado: "+mResultado);

        return   mResultado;
    }

    public static String verificaAlveolo(int armazem, String zona, String alveolo,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/VerificaAlveolo?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice verificaAlveolo";

        }

        return   mResultado;
    }

    public static String verificaZona(int armazem, String zona,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/VerificaZona?").buildUpon()
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
            mResultado = mResultado.substring(1,mResultado.length()-1);
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice verificaZona";
        }

        return   mResultado;
    }

    public static String EliminaDossier(String bostamp, String tabela, boolean onlyifnolines,Activity activity){

        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/EliminaDossier?").buildUpon()
                .appendQueryParameter("bostamp",String.valueOf (bostamp))
                .appendQueryParameter("tabela",tabela)
                .appendQueryParameter("onlyifnolines",String.valueOf(onlyifnolines))
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice EliminaDossier";
        }

        return   mResultado;
    }

    public static Boolean refConsumeOfNaLoc(String referencia,int armazem, String zona, String alveolo, String tipovalidacao, Activity activity){


        Uri builtURI_processaPedido = Uri.parse("http://" + Globals.getInstance().getCaminho() + "/TerminalPHC_INOVEPLASTIKA/Service1.svc/RefConsomeOfNaLoc?").buildUpon()
                .appendQueryParameter("referencia",referencia)
                .appendQueryParameter("armazem",String.valueOf (armazem))
                .appendQueryParameter("zona",zona)
                .appendQueryParameter("alveolo",alveolo)
                .appendQueryParameter("tipovalidacao",tipovalidacao)
                .build();
        String mResultado = "";

        try {
            mResultado = new AsyncRequest(activity,99).execute(builtURI_processaPedido.toString()).get();
        }

        catch (Exception ei) {
            mResultado ="Erro na execução do webservice verificaAlveolo";
        }

        System.out.println("E o resultado é: "+mResultado);

        return   Boolean.valueOf(mResultado);

    }


}
