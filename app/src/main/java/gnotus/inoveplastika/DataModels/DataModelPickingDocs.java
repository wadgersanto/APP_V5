package gnotus.inoveplastika.DataModels;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DataModelPickingDocs {

    private String nome,nome2,bostamp,data,dataof ="",numof = "",refprod = "", armazem = "0";

    public DataModelPickingDocs(String nome, String nome2, String bostamp, String data, String numof, String dataof, String refprod, String armazem) {

        this.nome = nome;
        this.nome2 = nome2;
        this.data = data;
        this.bostamp = bostamp;
        this.dataof = dataof;
        this.numof = numof;
        this.refprod = refprod;
        this.armazem = armazem;


    }

    public DataModelPickingDocs(JSONObject jsonObject) {

        try {

            if (jsonObject.has("bostamp"))  this.bostamp = Objects.toString(jsonObject.getString("bostamp")) ;
            if (jsonObject.has("nome"))  this.nome = Objects.toString(jsonObject.getString("nome"));
            if (jsonObject.has("nome2"))  this.nome2 = Objects.toString(jsonObject.getString("nome2"));
            if (jsonObject.has("data"))  this.data = Objects.toString(jsonObject.getString("data"));
            if (jsonObject.has("dataof"))  this.dataof = Objects.toString(jsonObject.getString("dataof"));
            if (jsonObject.has("numof"))  this.numof = Objects.toString(jsonObject.getString("numof"));
            if (jsonObject.has("refprod"))  this.refprod = Objects.toString(jsonObject.getString("refprod"));
            if (jsonObject.has("armazem"))  this.armazem = Objects.toString(jsonObject.getInt("armazem"));

        }

        catch (final JSONException e) {
            Log.e("", "Erro Conversão: " + e.getMessage());
        }

    }



    public String getBostamp() {
        return bostamp;
    }

    public String getData() {
        return data.substring(0,10);
    }

    public String getNome() {
        return nome.trim();
    }

    public String getNome2() {
        return nome2.trim();
    }

    public String getNumof() {
        return numof.trim();
    }

    public String getDataof() {
        if (dataof.length()>= 10) {
            return dataof.substring(0,10);
        }
        else  return dataof;

    }
    public String getRefprod() {
        return refprod.trim();
    }
    public String getArmazem() {
        return armazem.trim();
    }


}
