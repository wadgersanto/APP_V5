package gnotus.inoveplastika;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;

public class UserFunc {

    public static Double arredonda(Double valor, int casasdecimais)
    {

        Double divisor;
        divisor = Math.pow(10,casasdecimais);

        return Math.round(valor*divisor)/divisor;

    }


    public static String retiraZerosDireita(Double numero){

        String strnumero;

        strnumero = String.format("%.3f",numero).replace(",",".");

        boolean processa = true;

        while (strnumero.indexOf(".") >= 0 && ( strnumero.substring(strnumero.length()-1).equals("0") || strnumero.substring(strnumero.length()-1).equals(".") )) {
            strnumero  = strnumero.substring(0,strnumero.length()-1);
        }

        return strnumero;

    }

    public static String processaLeituraOperador(String textoLido) {


        /* Esta função valida a leitura do operador. Se estiver OK returna o número do operador, senão retorna vazio

         */
        String opPrefixp = "";
        String opNumero = "";

        if (textoLido.length() < 5) {
            return "";
        }

        opPrefixp = textoLido.substring(0, 4);

        if (!opPrefixp.equals("(OP)")) {
            return "";
        } else {
            // Vamos buscar o operador
            opNumero = textoLido.substring(4).trim();
                /* Se a substring operador for numerica e inteira então vamos carregar os dados do operador
                   Senão vamos devolver um erro
                */
            try {
                Integer val = Integer.valueOf(opNumero);
                if (val != null) {
                    return opNumero.toString();
                } else
                    return "";
            } catch (NumberFormatException e) {
                return "";
            }

        }

    }

    public static double arredonda(double numero, double precisao) {

        return Math.round(numero*Math.pow(10,precisao))/Math.pow(10,precisao);

    }

    public static double datediff(String tipo, Date datainicial, Date datafinal) {


        tipo = tipo.toUpperCase();
        long divisor = 1000*60*60;

        if (tipo.equals("S")) divisor = 1000;
        if (tipo.equals("SS")) divisor = 1000;

        if (tipo.equals("M")) divisor = 1000*60;
        if (tipo.equals("MM")) divisor = 1000*60;

        if (tipo.equals("H")) divisor = 1000*60*60;
        if (tipo.equals("HH")) divisor = 1000*60*60;

        if (tipo.equals("D")) divisor = 1000*60*60*24;
        if (tipo.equals("DD")) divisor = 1000*60*60*24;



        double diff = datafinal.getTime()-datainicial.getTime();

        return (diff /divisor);


    }

    public static Calendar strDateToTime(String strdata) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(0,0,0,0,0,0);


        Integer year = 0;
        Integer month = 0;
        Integer day = 0;
        Integer hour = 0;
        Integer minute = 0;
        Integer second = 0;

        long retVal = 0;

        if (strdata.length() >= 4) {
            year =  Integer.parseInt(strdata.substring(0,4));
            System.out.println(year);
        }

        if (strdata.length() >= 7) {
            month =  Integer.parseInt(strdata.substring(5,7));
            System.out.println(month);
        }

        if (strdata.length() >= 10) {
            day =  Integer.parseInt(strdata.substring(8,10));
            System.out.println(day);
        }

        if (strdata.length() >= 13) {
            hour =  Integer.parseInt(strdata.substring(11,13));
            System.out.println(hour);
        }

        if (strdata.length() >= 16) {
            minute =  Integer.parseInt(strdata.substring(14,16));
            System.out.println(minute);
        }

        if (strdata.length() >= 16) {
            second =  Integer.parseInt(strdata.substring(17,19));
            System.out.println(second);
        }

        calendar.set(year,month-1,day,hour,minute,second);

        return calendar;

    }

    public static boolean isInteger(String text) {

        try {
            int num = Integer.parseInt(text);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }

    }
}
