package gnotus.inoveplastika.Suporte;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Objects;

public class DataModelPa {

    private String pastamp;
    private String ptipo;
    private String status;
    private String serie;
    private String problema;
    private String solucao;
    private String tipo;
    private String tecnnm;
    private String fdata,datat;
    private String fhora,horat;
    private String pdata,phora;
    private String timeopen;
    private String fquem;
    private String resumo;
    private String nref;
    private String psobs;
    @SerializedName("u_of")
    private String numof;
    private Boolean fechado;
    private Integer tecnico,ftecno,ctInj,ctHoras,nopat;
    @SerializedName("u_prdvalid")
    private boolean validProd;
    @SerializedName("u_qldvalid")
    private boolean validQual;
    @SerializedName("u_qldtoval")
    private boolean qualidToVal;
    private boolean u_dblqrp;



    public DataModelPa(JSONObject jsonObject) throws NoSuchFieldException, IllegalAccessException, JSONException {


        for(Iterator<String> iter = jsonObject.keys();iter.hasNext();) {

            String key = iter.next();
            Field field = this.getClass().getDeclaredField(key);

            // System.out.println(key);
            // System.out.println(field.getName());

            if (field.getType().equals(String.class))
            {
                field.set(this,jsonObject.getString(key));
            }

            if (field.getType().equals(Integer.class))
            {
                field.set(this,jsonObject.getInt(key));
            }

            if (field.getType().equals(Boolean.class))
            {
                field.set(this,jsonObject.getBoolean(key));
            }



        }

    }


    public String getPastamp() {
        return pastamp;
    }

    public String getPdata() {
        return pdata;
    }

    public String getPhora() {
        return phora;
    }

    public String getPtipo() {
        return ptipo;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusCode() {
        if (status.length() > 0) {
            return status.substring(0,1);
        }
        else return "";

    }

    public String getTimeopen() {
        return timeopen;
    }

    public String getSerie() {
        return serie;
    }

    public String getProblema() {
        return problema;
    }

    public String getSolucao() {
        return solucao;
    }

    public String getTipo() {
        return tipo;
    }

    public String getFdata() {
        return fdata;
    }

    public String getTecnnm() {
        return tecnnm;
    }

    public String getFhora() {
        return fhora;
    }

    public String getHorat() {
        return horat;
    }

    public String getFquem() {
        return fquem;
    }

    public String getResumo() {
        return resumo;
    }

    public Boolean getFechado() {
        return fechado;
    }

    public Integer getTecnico() {
        return tecnico;
    }

    public String getDatat() {
        return datat;
    }

    public Integer getNopat() {
        return nopat;
    }

    public Integer getCtInj() {
        return ctInj;
    }

    public Integer getCtHoras() {
        return ctHoras;
    }

    public Integer getFtecno() {
        return ftecno;
    }

    public boolean isValidProd() {
        return validProd;
    }

    public boolean isValidQual() {
        return validQual;
    }

    public boolean isQualidToVal() {
        return qualidToVal;
    }

    public String getPsobs() {
        return psobs;
    }

    public String getNref() {
        return nref;
    }

    public boolean isU_dblqrp() {
        return u_dblqrp;
    }
}
